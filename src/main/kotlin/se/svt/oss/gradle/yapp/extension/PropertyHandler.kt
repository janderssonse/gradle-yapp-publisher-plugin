package se.svt.oss.gradle.yapp.extension

import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

abstract class PropertyHandler @Inject constructor(
    val project: Project,
    val objects: ObjectFactory,
    val propPrefix: String,
    val envPrefix: String
) {

    internal fun propertyString(property: String, default: String = "") =
        simpleProperty(property, default, stringListToString)

    internal fun propertyBool(property: String, default: Boolean = false) =
        simpleProperty(property, default, stringListToBool)

    private val stringListToString: (list: List<String>) -> String = {
        if (it.isEmpty()) {
            ""
        } else it.first()
    }
    private val stringListToBool: (list: List<String>) -> Boolean = {
        if (it.isEmpty()) {
            false
        } else it.first().toBooleanStrict()
    }

    private inline fun <reified T> simpleProperty(
        property: String,
        default: T,
        mapToType: (List<String>) -> T
    ): Property<T> {

        val filteredPropertiesMap = findProperties(property, propPrefix, envPrefix, project).values.toList()
        val gradlePropOrEnvProperties: T = if (filteredPropertiesMap.isEmpty()) {
            default
        } else mapToType(filteredPropertiesMap)

        return objects.property(T::class.java).apply { convention(gradlePropOrEnvProperties) }
    }

    internal inline fun <reified T> listProperty(
        property: String,
        mapToType: (List<List<String>>) -> List<T>
    ): ListProperty<T> {

        val filteredPropertiesMap = findProperties(property, propPrefix, envPrefix, project)
        val propAsList = convertToList(filteredPropertiesMap)
        val gradlePropOrEnvProperties = mapToType(propAsList)

        return objects.listProperty(T::class.java).apply { convention(gradlePropOrEnvProperties) }
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
            .filter { it.key.matches("""^$propPrefix$name\.\d+$""".toRegex()) || it.key.matches("""^$propPrefix$name$""".toRegex()) }
            .associate { it.key.uppercase().replace(".", "_") to it.value.toString() }

        val environmentMap: Map<String, String> = System.getenv()
            .filter { !it.key.isNullOrBlank() }
            .filter { it.key.matches("""^$envPrefix${name.uppercase()}\.\d+$""".toRegex()) || it.key.matches("""^$envPrefix${name.uppercase()}$""".toRegex()) }

        return environmentMap + propertiesMap
    }
}
