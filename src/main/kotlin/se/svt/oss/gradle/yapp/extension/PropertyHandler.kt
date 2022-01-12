package se.svt.oss.gradle.yapp.extension

import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

abstract class PropertyHandler @Inject constructor(
    val project: Project,
    private val objects: ObjectFactory,
    private val propPrefix: String,
    private val envPrefix: String
) {

    internal fun propertyString(property: String, defaultValue: String = "") =
        simpleProperty(property, defaultValue, { it.first() })

    internal fun propertyBool(property: String, defaultValue: Boolean = false) =
        simpleProperty(property, defaultValue, { it.first().toBooleanStrict() })

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

        val propertiesMap: Map<String, String> = project.properties.entries
            .filter { !it.key.isNullOrBlank() }
            .filter { it.key.matches("""^$propPrefix$name(\.\d+)?$""".toRegex()) }
            .associate { it.key.uppercase().replace(".", "_") to it.value.toString() }

        val environmentMap: Map<String, String> = System.getenv()
            .filter { !it.key.isNullOrBlank() }
            .filter { it.key.matches("""^$envPrefix${name.uppercase()}(\.\d+)?$""".toRegex()) }

        return environmentMap + propertiesMap
    }

    companion object {

        val toStringList: (List<List<String>>) -> List<String> =
            { list: List<List<String>> -> list.flatten() }
    }
}
