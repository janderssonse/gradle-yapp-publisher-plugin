package se.svt.oss.gradle.yapp.extension

import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import se.svt.oss.gradle.yapp.extension.DefaultProperties.Companion.DEFAULT_PROPERTY_BOOLEAN
import se.svt.oss.gradle.yapp.extension.DefaultProperties.Companion.DEFAULT_PROPERTY_INT
import se.svt.oss.gradle.yapp.extension.DefaultProperties.Companion.DEFAULT_PROPERTY_STRING
import javax.inject.Inject

abstract class PropertyHandler @Inject constructor(
    val project: Project,
    private val objects: ObjectFactory,
    val propPrefix: String,
    val envPrefix: String
) {

    internal fun propertyString(property: String, defaultValue: String = DEFAULT_PROPERTY_STRING) =
        simpleProperty(property, defaultValue) { it.first() }

    internal fun propertyBool(property: String, defaultValue: Boolean = DEFAULT_PROPERTY_BOOLEAN) =
        simpleProperty(property, defaultValue) { it.first().toBooleanStrict() }

    internal fun propertyInt(property: String, defaultValue: Int = DEFAULT_PROPERTY_INT): Property<Int> =
        simpleProperty<Int>(property, defaultValue) { it.first().toInt() }

    internal inline fun <reified K, reified V> propertyMap(
        property: String,
        toTypeFunction: (List<String>) -> Map<K, V>,
        defaultValue: Map<K, V> = emptyMap()
    ) = mapProperty(property, defaultValue, toTypeFunction)

    internal inline fun <reified T> propertyList(
        property: String,
        toTypeFunction: (List<List<String>>) -> List<T>,
        defaultValue: List<T> = emptyList()
    ) = listProperty(property, defaultValue, toTypeFunction)

    private inline fun <reified T> simpleProperty(
        property: String,
        defaultValue: T,
        mapToType: (List<String>) -> T
    ): Property<T> {

        val filteredPropertiesMap = findProperties(property, propPrefix, envPrefix, project).values.toList()

        val gradlePropOrEnvProperties: T = if (filteredPropertiesMap.isEmpty()) {
            defaultValue
        } else
            mapToType(filteredPropertiesMap)

        return objects.property(T::class.java)
            .apply { convention(gradlePropOrEnvProperties) }
    }

    private inline fun <reified T> listProperty(
        property: String,
        defaultList: List<T>,
        mapToType: (List<List<String>>) -> List<T>
    ): ListProperty<T> {

        val filteredPropertiesMap = findProperties(property, propPrefix, envPrefix, project)
        val propAsList = convertToList(filteredPropertiesMap)

        val gradlePropOrEnvProperties = if (propAsList.isEmpty()) {
            defaultList
        } else
            mapToType(propAsList)

        return objects.listProperty(T::class.java)
            .apply { convention(gradlePropOrEnvProperties) }
    }

    private inline fun <reified K, reified V> mapProperty(
        property: String,
        defaultMap: Map<K, V>,
        toTypeFunction: (List<String>) -> Map<K, V>
    ): MapProperty<K, V> {

        val filteredPropertiesMap = findProperties(property, propPrefix, envPrefix, project)
        val propAsList = convertToList(filteredPropertiesMap)
        val gradlePropOrEnvProperties = if (propAsList.isEmpty()) {
            defaultMap
        } else
            toTypeFunction(propAsList.first())
        return objects.mapProperty(K::class.java, V::class.java)
            .apply { convention(gradlePropOrEnvProperties) }
    }

    private fun convertToList(propertiesMap: Map<String, String>): List<List<String>> =
        HashMap<String, List<String>>().apply {
            propertiesMap.forEach { this[it.key] = it.value.split(",") }
        }.map { it.value }.ifEmpty { emptyList() }

    private fun findProperties(
        name: String,
        propPrefix: String,
        envPrefix: String,
        project: Project
    ): Map<String, String> {

        val propertiesMap: Map<String, String> = findPropertiesInProject(project, propPrefix, name)

        val environmentMap: Map<String, String> = findPropertiesInEnv(envPrefix, name)

        return environmentMap + propertiesMap
    }

    companion object {
        fun findPropertiesInEnv(envPrefix: String, name: String): Map<String, String> {
            return System.getenv()
                .filter { !it.key.isNullOrBlank() }
                .filter { it.key.matches("""^$envPrefix${name.uppercase()}(\.\d+)?$""".toRegex()) }
        }

        fun findPropertiesInProject(project: Project, propPrefix: String, name: String): Map<String, String> {
            return project.properties.entries
                .filter { !it.key.isNullOrBlank() }
                .filter { it.key.matches("""^$propPrefix$name(\.\d+)?$""".toRegex()) }
                .associate { it.key.uppercase().replace(".", "_") to it.value.toString() }
        }

        val toStringList: (List<List<String>>) -> List<String> =
            { list: List<List<String>> -> list.flatten() }
    }
}
