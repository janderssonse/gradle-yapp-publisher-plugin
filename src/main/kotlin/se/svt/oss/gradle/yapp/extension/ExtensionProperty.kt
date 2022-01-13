package se.svt.oss.gradle.yapp.extension

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import se.svt.oss.gradle.yapp.publishingtarget.BasePublishTarget
import se.svt.oss.gradle.yapp.publishingtarget.PublishingTargetType
import se.svt.oss.gradle.yapp.yappExtension
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

/**
 * Annotation to add metadata to a Yapp Plugin extension.
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class ExtensionProperty(
    val name: String,
    val description: String = "[null]",
    val defaultValue: String = "[null]",
    val secret: Boolean = false,
)

abstract class PluginExtensionPropertyFormatter {
    abstract fun format(value: String): String
}

private class AnnotationWrapper<T : Any>(
    val propertyName: String,
    val annotation: ExtensionProperty?,
    val clazz: KClass<T>,
    val list: Boolean = false,
)

class PluginExtensionProperties(val displayName: String) {
    private val properties: MutableList<ExtensionProperty> = mutableListOf()

    fun addProperty(
        name: String,
        value: String? = null,
        defaultValue: String? = null,
        formatter: PluginExtensionPropertyFormatter? = null,
    ) {
        properties.add(ExtensionProperty(name, value, defaultValue, formatter))
    }

    fun properties(): List<ExtensionProperty> = properties.toList()

    fun prettyPrint() {
        println("#### ${this.displayName} ###")
        println("-".repeat(70))
        println("%-30s|%s".format("Property", "Value"))
        println("-".repeat(70))
        this.properties().forEach { p ->
            p.prettyPrint()
        }
    }

    class ExtensionProperty(
        val name: String,
        value: String? = null,
        val defaultValue: String? = null,
        val formatter: PluginExtensionPropertyFormatter? = null,
    ) {
        private val value: String? = value
            // Overrides value to handle defaults and formatters (secrets)
            get() {
                var tmp: String? = field ?: this.defaultValue
                if (formatter != null && tmp != null) {
                    tmp = formatter.format(tmp)
                }
                return tmp
            }

        fun prettyPrint() {
            println("%-30s|%s".format(this.name, this.value))
        }
    }
}

fun fetchPluginExtensionProperties(target: BasePublishTarget): PluginExtensionProperties {
    lateinit var pluginExtensionProperties: PluginExtensionProperties

    when (target.publishingTargetType) {
        PublishingTargetType.GRADLE_PORTAL -> {
            val props = fetchExtensionPropertiesAnnotations(GradlePluginPublishingExtension::class)
            val extension = target.project.yappExtension().gradlePortalPublishing
            pluginExtensionProperties =
                buildExtensionProperties("Gradle Portal Plugin", props, extension)
        }
        PublishingTargetType.MAVEN_CENTRAL -> {
            val props = fetchExtensionPropertiesAnnotations(MavenPublishingExtension::class)
            val extension = target.project.yappExtension().mavenPublishing
            pluginExtensionProperties =
                buildExtensionProperties("Maven Central Plugin", props, extension)
        }
        PublishingTargetType.GITLAB -> {
            val props =
                fetchExtensionPropertiesAnnotations(GitLabExtension::class) +
                    fetchExtensionPropertiesAnnotations(MavenPublishingExtension::class, openOnly = true)
            val extension = target.project.yappExtension().gitLab

            pluginExtensionProperties = buildExtensionProperties("Gitlab Plugin", props, extension)
        }
        PublishingTargetType.GITHUB -> {
            val props =
                fetchExtensionPropertiesAnnotations(GitHubExtension::class) +
                    fetchExtensionPropertiesAnnotations(MavenPublishingExtension::class, openOnly = true)
            val extension = target.project.yappExtension().gitHub
            pluginExtensionProperties = buildExtensionProperties("Github Plugin", props, extension)
        }
        else -> {
            pluginExtensionProperties = PluginExtensionProperties("Unknown Plugin")
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
                p.name,
                p.findAnnotation(),
                clazz,
            )
        }
        .filter { p -> p.annotation != null }
}

private fun buildExtensionProperties(
    display_name: String,
    props: List<AnnotationWrapper<*>>,
    extension: Any
): PluginExtensionProperties {
    val extensionProperties = PluginExtensionProperties(display_name)
    props.forEach { annotationWrapper ->
        run {
            val propertyAnnotation = annotationWrapper.annotation
            if (propertyAnnotation != null) {
                var value =
                    annotationWrapper.clazz.java.kotlin.memberProperties.first { it.name == annotationWrapper.propertyName }.getter.call(
                        extension
                    )
                val f = if (propertyAnnotation.secret) PluginExtensionPropertySecretFormatter() else null
                if (value is Property<*>) {
                    value = value.orNull
                    if (value is Boolean) {
                        value = value.toString()
                    }
                } else {
                    if (value is ListProperty<*>) {
                        value = value.getOrElse(emptyList()).joinToString(",")
                    }
                }
                extensionProperties.addProperty(
                    name = propertyAnnotation.name,
                    value = value as String?,
                    defaultValue = propertyAnnotation.defaultValue,
                    formatter = f
                )
            }
        }
    }
    return extensionProperties
}

private class PluginExtensionPropertySecretFormatter() : PluginExtensionPropertyFormatter() {
    override fun format(value: String): String {
        if (value.isNotEmpty()) {
            return "********"
        }
        return ""
    }
}
