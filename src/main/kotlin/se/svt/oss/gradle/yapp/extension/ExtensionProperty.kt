package se.svt.oss.gradle.yapp.extension

import org.gradle.api.GradleException
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import se.svt.oss.gradle.yapp.publishingtarget.BasePublishTarget
import se.svt.oss.gradle.yapp.publishingtarget.PublishingTargetType
import se.svt.oss.gradle.yapp.yappExtension
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

const val NULL_VALUE = "[null]"

/**
 * Annotation to add metadata to a Yapp Plugin extension.
 * @property name the name of the property
 *
 *
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class ExtensionProperty(
    val name: String,
    val description: String = NULL_VALUE,
    val example: String = NULL_VALUE,
    val defaultValue: String = NULL_VALUE,
    val secret: Boolean = false,
    val mandatory: Boolean = false,
)

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class ExtensionPropertyExtractor(val extractor: KClass<out ExtensionPropertyValueExtractor>)

class ExtensionPropertyDeveloperExtractor : ExtensionPropertyValueExtractor {
    override fun asString(value: Any): String {
        if (value is Developer) {
            return "${value.id}, ${value.name} <${value.email}>, <${value.organization}> <${value.organizationUrl}>"
        }
        throw GradleException("ExtensionPropertyDeveloperExtractor only works for Developer class")
    }

    override fun parser(): (String) -> Any {
        return { it.split("|").toList() }
    }

    override fun extraDescription(): String {
        return "Use '|' to create a list of developers, i.e. id1, name1, email1|id2, name2, email2"
    }
}

interface ExtensionPropertyValueExtractor {
    fun asString(value: Any): String
    fun parser(): (String) -> Any
    fun extraDescription(): String
}

abstract class PluginExtensionPropertyFormatter {
    abstract fun format(value: String): String
}

private class AnnotationWrapper<T : Any>(
    val propertyName: String,
    val annotation: ExtensionProperty?,
    val extractor: ExtensionPropertyExtractor?,
    val clazz: KClass<T>,
    val list: Boolean = false,
)

class PluginExtensionProperties(
    val displayName: String,
    val propPrefix: String,
    val envPrefix: String,
    val name: String
) {
    private val properties: MutableList<ExtensionProperty> = mutableListOf()

    fun addProperty(
        name: String,
        value: String,
        valueType: ValueType,
        collectionType: ExtensionPropertyType,
        defaultValue: String? = null,
        formatter: PluginExtensionPropertyFormatter? = null,
        inProject: Boolean = false,
        inEnv: Boolean = false,
        description: String? = null,
        example: String? = null,
        mandatory: Boolean = false,
        extraDescription: String = "",
        parser: ((String) -> Any)? = null,
    ) {
        properties.add(
            ExtensionProperty(
                name = name,
                value = value,
                valueType = valueType,
                collectionType = collectionType,
                defaultValue = defaultValue,
                formatter = formatter,
                inProject = inProject,
                inEnv = inEnv,
                description = description,
                example = example,
                mandatory = mandatory,
                extraDescription = extraDescription,
                parser = parser,
            )
        )
    }

    fun properties(): List<ExtensionProperty> = properties.toList()

    fun prettyPrint() {
        println("#### ${this.displayName} ###")
        println("Properties prefix: ${this.propPrefix}")
        println("Env prefix: ${this.envPrefix}")
        println("-".repeat(70))
        println("|%-5s|%-5s|%-30s|%s".format("Props", "Env", "Property", "Value"))
        println("-".repeat(70))
        this.properties().forEach { p ->
            p.prettyPrint()
        }
    }

    enum class ValueType {
        STRING,
        NUMERIC,
        BOOLEAN,
    }

    enum class ExtensionPropertyType {
        SINGLE,
        LIST,
        MAP,
    }

    class ExtensionProperty(
        val name: String,
        value: String,
        val valueType: ValueType,
        val collectionType: ExtensionPropertyType,
        val defaultValue: String? = null,
        val formatter: PluginExtensionPropertyFormatter? = null,
        val inProject: Boolean = false,
        val inEnv: Boolean = false,
        val description: String? = null,
        val example: String? = null,
        val mandatory: Boolean = false,
        val extraDescription: String = "",
        val parser: ((String) -> Any)? = null,
    ) {
        val value: String = value
            // Overrides value to handle formatters (secrets)
            get() {
                // Always return empty string as no value
                if (!inProject && !inEnv && defaultValue == null) {
                    return ""
                }
                var tmp: String = field
                if (formatter != null) {
                    tmp = formatter.format(tmp)
                }
                return tmp
            }

        fun prettyPrint() {
            val inProject = if (this.inProject) "  x  " else "     "
            val inEnv = if (this.inEnv) {
                if (this.inProject) {
                    " (x) "
                } else {
                    "  x  "
                }
            } else {
                "     "
            }
            println("|%5s|%5s|%-30s|%s".format(inProject, inEnv, this.name, this.value))
        }
    }
}

fun <T : Any> fetchPluginExtensionProperties(
    displayName: String,
    clazz: KClass<T>,
    extension: PropertyHandler
): PluginExtensionProperties {
    val props = fetchExtensionPropertiesAnnotations(clazz)
    return buildExtensionProperties(displayName, props, extension)
}

fun fetchPluginExtensionProperties(target: BasePublishTarget): PluginExtensionProperties {
    val pluginExtensionProperties: PluginExtensionProperties = when (target.publishingTargetType) {
        PublishingTargetType.ARTIFACTORY -> {
            val props =
                fetchExtensionPropertiesAnnotations(ArtifactoryExtension::class) +
                    fetchExtensionPropertiesAnnotations(MavenPublishingExtension::class, openOnly = true)
            val extension = target.project.yappExtension().artifactoryPublishing

            buildExtensionProperties("Artifactory Plugin", props, extension)
        }
        PublishingTargetType.GRADLE_PORTAL -> {
            fetchPluginExtensionProperties(
                "Gradle Portal Plugin",
                GradlePluginPublishingExtension::class,
                target.project.yappExtension().gradlePortalPublishing
            )
        }
        PublishingTargetType.MAVEN_CENTRAL -> {
            fetchPluginExtensionProperties(
                "Maven Central Plugin",
                MavenPublishingExtension::class,
                target.project.yappExtension().mavenPublishing
            )
        }
        PublishingTargetType.GITLAB -> {
            val props =
                fetchExtensionPropertiesAnnotations(GitLabExtension::class) +
                    fetchExtensionPropertiesAnnotations(MavenPublishingExtension::class, openOnly = true)
            val extension = target.project.yappExtension().gitLab

            buildExtensionProperties("Gitlab Plugin", props, extension)
        }
        PublishingTargetType.GITHUB -> {
            val props =
                fetchExtensionPropertiesAnnotations(GitHubExtension::class) +
                    fetchExtensionPropertiesAnnotations(MavenPublishingExtension::class, openOnly = true)
            val extension = target.project.yappExtension().gitHub
            buildExtensionProperties("Github Plugin", props, extension)
        }
        else -> {
            PluginExtensionProperties("Unknown Plugin", "", "", "")
        }
    }
    return pluginExtensionProperties
}

private fun <T : Any> fetchExtensionPropertiesAnnotations(
    clazz: KClass<T>,
    openOnly: Boolean = false,
): List<AnnotationWrapper<*>> {
    return clazz.declaredMemberProperties
        .filter { p -> if (openOnly) p.isOpen else true }
        .map { p ->
            AnnotationWrapper(
                propertyName = p.name,
                annotation = p.findAnnotation(),
                extractor = p.findAnnotation(),
                clazz = clazz,
            )
        }
        .filter { p -> p.annotation != null }
}

private fun buildExtensionProperties(
    display_name: String,
    props: List<AnnotationWrapper<*>>,
    extension: PropertyHandler
): PluginExtensionProperties {
    val name = if (extension.propPrefix == "yapp.") {
        extension.propPrefix.slice(0 until (extension.propPrefix.length - 1))
    } else {
        extension.propPrefix.slice(5 until (extension.propPrefix.length - 1))
    }
    val extensionProperties = PluginExtensionProperties(
        displayName = display_name,
        name = name,
        propPrefix = extension.propPrefix,
        envPrefix = extension.envPrefix,
    )
    props.forEach { annotationWrapper ->
        run {
            val propertyAnnotation = annotationWrapper.annotation
            if (propertyAnnotation != null) {
                val formatter = if (propertyAnnotation.secret) PluginExtensionPropertySecretFormatter() else null
                val valueWithType = getValueAsString(annotationWrapper, extension)
                val inProject = PropertyHandler.findPropertiesInProject(
                    extension.project,
                    extension.propPrefix,
                    propertyAnnotation.name
                ).isNotEmpty()
                val inEnv = PropertyHandler.findPropertiesInEnv(extension.envPrefix, propertyAnnotation.name)
                    .isNotEmpty()
                extensionProperties.addProperty(
                    name = propertyAnnotation.name,
                    value = valueWithType.first,
                    valueType = valueWithType.second,
                    collectionType = valueWithType.third,
                    defaultValue = if (propertyAnnotation.defaultValue == NULL_VALUE) null else propertyAnnotation.defaultValue,
                    formatter = formatter,
                    inProject = inProject,
                    inEnv = inEnv,
                    description = if (propertyAnnotation.description == NULL_VALUE) null else propertyAnnotation.description,
                    example = if (propertyAnnotation.example == NULL_VALUE) null else propertyAnnotation.example,
                    mandatory = propertyAnnotation.mandatory,
                    extraDescription = valueWithType.fourth,
                    parser = valueWithType.fifth,
                )
            }
        }
    }
    return extensionProperties
}

data class ValueObject<out A, out B, out C, out D, out E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
) {
    override fun toString(): String = "($first, $second, $third, $fourth, $fifth)"
}

private fun getValueAsString(
    annotationWrapper: AnnotationWrapper<*>,
    extension: PropertyHandler
): ValueObject<String, PluginExtensionProperties.ValueType, PluginExtensionProperties.ExtensionPropertyType, String, ((String) -> Any)?> {
    var value = annotationWrapper.clazz.java.kotlin.memberProperties
        .first { it.name == annotationWrapper.propertyName }
        .getter.call(extension)
    var valueType: PluginExtensionProperties.ValueType = PluginExtensionProperties.ValueType.STRING
    val collectionType: PluginExtensionProperties.ExtensionPropertyType?
    val extractor: ExtensionPropertyValueExtractor? = annotationWrapper.extractor?.extractor?.createInstance()
    val parser = extractor?.parser()
    var extraDescription = ""
    if (value is Property<*>) {
        if (extractor != null) {
            value = extractor.asString(value.get())
        } else {
            value = value.orNull
            if (value != null) {
                if (value is Boolean) {
                    valueType = PluginExtensionProperties.ValueType.BOOLEAN
                    value = value.toString()
                } else {
                    if (value is Int) {
                        valueType = PluginExtensionProperties.ValueType.NUMERIC
                        value = value.toString()
                        extraDescription = extractor?.extraDescription() ?: "Add a numeric value."
                    }
                }
            }
        }
        collectionType = PluginExtensionProperties.ExtensionPropertyType.SINGLE
    } else if (value is ListProperty<*>) {
        value = value.getOrElse(emptyList())
        value = if (extractor != null) {
            value.map { v -> extractor.asString(v) }
        } else {
            value.map { v -> v.toString() }
        }
        value = value.joinToString(",")
        collectionType = PluginExtensionProperties.ExtensionPropertyType.LIST
        extraDescription = extractor?.extraDescription() ?: "Use ',' to create a list, i.e. value1,value2,value3"
    } else if (value is MapProperty<*, *>) {
        value = value.orNull
        if (value != null) {
            if (value.isNotEmpty()) {
                value.mapKeys { (k, _) -> k.toString() }
                value = if (extractor != null) {
                    value.mapValues { (_, v) -> extractor.asString(v) }
                } else {
                    value.mapValues { (_, v) -> v.toString() }
                }
                value = value.map { (k, v) -> "$k=$v" }.joinToString(",")
            } else {
                value = ""
            }
            collectionType = PluginExtensionProperties.ExtensionPropertyType.MAP
            extraDescription = extractor?.extraDescription()
                ?: "Use 'k:v' and ',' to create a map, i.e. key1:value1,key2:value2,key3:value3"
        } else {
            throw GradleException("MapProperty is null")
        }
    } else {
        throw GradleException("Unknown value object ${if (value != null) value::class.java.simpleName else null}")
    }

    return ValueObject(value as String, valueType, collectionType, extraDescription, parser)
}

private class PluginExtensionPropertySecretFormatter() : PluginExtensionPropertyFormatter() {
    override fun format(value: String): String {
        if (value.isNotEmpty()) {
            return "********"
        }
        return ""
    }
}
