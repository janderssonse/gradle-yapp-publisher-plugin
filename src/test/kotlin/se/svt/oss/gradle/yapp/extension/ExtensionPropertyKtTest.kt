package se.svt.oss.gradle.yapp.extension

import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.internal.extensibility.DefaultExtraPropertiesExtension
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension

val name = "test"
val propPrefix = "yapp.$name."
val envPrefix = "YAPP_TEST_"
val displayName = "displayName"

@ExtendWith(SystemStubsExtension::class)
internal class ExtensionPropertyKtTest {

    open class TestExtension constructor(project: Project, objects: ObjectFactory) :
        PropertyHandler(project, objects, propPrefix, envPrefix) {

        companion object {
            const val DEFAULT_STRING_VALUE = "defaultString"
            const val DEFAULT_BOOLEAN_VALUE = true
            const val DEFAULT_INT_VALUE = 30
        }

        @ExtensionProperty(name = "stringValueNoDefault")
        var stringValueNoDefault: Property<String> = propertyString("stringValueNoDefault")

        @ExtensionProperty(name = "stringValueWithDefault", defaultValue = DEFAULT_STRING_VALUE)
        var stringValueWithDefault: Property<String> =
            propertyString("stringValueWithDefault", defaultValue = DEFAULT_STRING_VALUE)

        @ExtensionProperty(name = "booleanValueNoDefault")
        var booleanValueNoDefault: Property<Boolean> = propertyBool("booleanValueNoDefault")

        @ExtensionProperty(name = "booleanValueWithDefault", defaultValue = DEFAULT_BOOLEAN_VALUE.toString())
        var booleanValueWithDefault: Property<Boolean> =
            propertyBool("booleanValueWithDefault", defaultValue = DEFAULT_BOOLEAN_VALUE)

        @ExtensionProperty(name = "intValueNoDefault")
        var intValueNoDefault: Property<Int> = propertyInt("intValueNoDefault")

        @ExtensionProperty(name = "intValueWithDefault", defaultValue = DEFAULT_INT_VALUE.toString())
        var intValueWithDefault: Property<Int> = propertyInt("intValueWithDefault", defaultValue = DEFAULT_INT_VALUE)

        @ExtensionProperty(name = "listValueNoDefault")
        var listValueNoDefault: ListProperty<String> = propertyList("listValueNoDefault", toStringList)

        @ExtensionProperty(name = "listValueWithDefault", defaultValue = "1,2")
        var listValueWithDefault: ListProperty<String> =
            propertyList("listValueWithDefault", toStringList, defaultValue = listOf("1", "2"))

        @ExtensionProperty(name = "mapValueNoDefault")
        var mapValueNoDefault: MapProperty<String, String> =
            propertyMap("mapValueNoDefault", { list -> list.associateWith { it } })

        @ExtensionProperty(name = "mapValueWithDefault", defaultValue = "k=v")
        var mapValueWithDefault: MapProperty<String, String> = propertyMap(
            "mapValueWithDefault",
            { list -> list.associateWith { it } },
            defaultValue = mapOf(Pair("k", "v"))
        )
    }

    @Test
    fun `test all property types defaults`() {
        val project = ProjectBuilder.builder().build()
        val extensionProperties = fetchPluginExtensionProperties(
            displayName = displayName,
            clazz = TestExtension::class,
            extension = TestExtension(project = project, project.objects)
        )

        val stringValueNoDefault = extensionProperties.properties().filter { it.name == "stringValueNoDefault" }.first()
        Assertions.assertEquals(
            PluginExtensionProperties.ExtensionPropertyType.SINGLE,
            stringValueNoDefault.collectionType
        )
        Assertions.assertEquals(PluginExtensionProperties.ValueType.STRING, stringValueNoDefault.valueType)
        Assertions.assertNull(stringValueNoDefault.defaultValue)
        Assertions.assertEquals("", stringValueNoDefault.value)

        val stringValueWithDefault =
            extensionProperties.properties().filter { it.name == "stringValueWithDefault" }.first()
        Assertions.assertEquals(
            PluginExtensionProperties.ExtensionPropertyType.SINGLE,
            stringValueWithDefault.collectionType
        )
        Assertions.assertEquals(PluginExtensionProperties.ValueType.STRING, stringValueWithDefault.valueType)
        Assertions.assertEquals(TestExtension.DEFAULT_STRING_VALUE, stringValueWithDefault.defaultValue)
        Assertions.assertEquals(TestExtension.DEFAULT_STRING_VALUE, stringValueWithDefault.value)

        val booleanValueNoDefault =
            extensionProperties.properties().filter { it.name == "booleanValueNoDefault" }.first()
        Assertions.assertEquals(
            PluginExtensionProperties.ExtensionPropertyType.SINGLE,
            booleanValueNoDefault.collectionType
        )
        Assertions.assertEquals(PluginExtensionProperties.ValueType.BOOLEAN, booleanValueNoDefault.valueType)
        Assertions.assertNull(booleanValueNoDefault.defaultValue)
        Assertions.assertEquals("", booleanValueNoDefault.value)

        val booleanValueWithDefault =
            extensionProperties.properties().filter { it.name == "booleanValueWithDefault" }.first()
        Assertions.assertEquals(
            PluginExtensionProperties.ExtensionPropertyType.SINGLE,
            booleanValueWithDefault.collectionType
        )
        Assertions.assertEquals(PluginExtensionProperties.ValueType.BOOLEAN, booleanValueWithDefault.valueType)
        Assertions.assertEquals(TestExtension.DEFAULT_BOOLEAN_VALUE.toString(), booleanValueWithDefault.defaultValue)
        Assertions.assertEquals(TestExtension.DEFAULT_BOOLEAN_VALUE.toString(), booleanValueWithDefault.value)

        val intValueNoDefault = extensionProperties.properties().filter { it.name == "intValueNoDefault" }.first()
        Assertions.assertEquals(
            PluginExtensionProperties.ExtensionPropertyType.SINGLE,
            intValueNoDefault.collectionType
        )
        Assertions.assertEquals(PluginExtensionProperties.ValueType.NUMERIC, intValueNoDefault.valueType)
        Assertions.assertNull(intValueNoDefault.defaultValue)
        Assertions.assertEquals("", intValueNoDefault.value)

        val intValueWithDefault = extensionProperties.properties().filter { it.name == "intValueWithDefault" }.first()
        Assertions.assertEquals(
            PluginExtensionProperties.ExtensionPropertyType.SINGLE,
            intValueWithDefault.collectionType
        )
        Assertions.assertEquals(PluginExtensionProperties.ValueType.NUMERIC, intValueWithDefault.valueType)
        Assertions.assertEquals(TestExtension.DEFAULT_INT_VALUE.toString(), intValueWithDefault.defaultValue)
        Assertions.assertEquals(TestExtension.DEFAULT_INT_VALUE.toString(), intValueWithDefault.value)

        val listValueNoDefault = extensionProperties.properties().filter { it.name == "listValueNoDefault" }.first()
        Assertions.assertEquals(PluginExtensionProperties.ExtensionPropertyType.LIST, listValueNoDefault.collectionType)
        Assertions.assertEquals(PluginExtensionProperties.ValueType.STRING, listValueNoDefault.valueType)
        Assertions.assertNull(listValueNoDefault.defaultValue)
        Assertions.assertEquals("", listValueNoDefault.value)

        val listValueWithDefault = extensionProperties.properties().filter { it.name == "listValueWithDefault" }.first()
        Assertions.assertEquals(
            PluginExtensionProperties.ExtensionPropertyType.LIST,
            listValueWithDefault.collectionType
        )
        Assertions.assertEquals(PluginExtensionProperties.ValueType.STRING, listValueWithDefault.valueType)
        Assertions.assertEquals("1,2", listValueWithDefault.defaultValue)
        Assertions.assertEquals("1,2", listValueWithDefault.value)

        val mapValueNoDefault = extensionProperties.properties().filter { it.name == "mapValueNoDefault" }.first()
        Assertions.assertEquals(PluginExtensionProperties.ExtensionPropertyType.MAP, mapValueNoDefault.collectionType)
        Assertions.assertEquals(PluginExtensionProperties.ValueType.STRING, mapValueNoDefault.valueType)
        Assertions.assertNull(mapValueNoDefault.defaultValue)
        Assertions.assertEquals("", mapValueNoDefault.value)

        val mapValueWithDefault = extensionProperties.properties().filter { it.name == "mapValueWithDefault" }.first()
        Assertions.assertEquals(PluginExtensionProperties.ExtensionPropertyType.MAP, mapValueWithDefault.collectionType)
        Assertions.assertEquals(PluginExtensionProperties.ValueType.STRING, mapValueWithDefault.valueType)
        Assertions.assertEquals("k=v", mapValueWithDefault.defaultValue)
        Assertions.assertEquals("k=v", mapValueWithDefault.value)
    }

    @Test
    fun `test map with empty project property`() {
        val project = ProjectBuilder.builder().build()

        project.extensions.add("test", TestExtension)
        val ext: DefaultExtraPropertiesExtension = project.properties.get("ext") as DefaultExtraPropertiesExtension
        ext.setProperty("${propPrefix}listValueNoDefault", "")

        val extensionProperties = fetchPluginExtensionProperties(
            displayName = displayName,
            clazz = TestExtension::class,
            extension = TestExtension(project = project, project.objects)
        )

        val listValueNoDefault = extensionProperties.properties().filter { it.name == "listValueNoDefault" }.first()
        Assertions.assertEquals("", listValueNoDefault.value)
        Assertions.assertTrue(listValueNoDefault.inProject)
    }

    @Test
    fun `test propertes from project`() {
        val project = ProjectBuilder.builder().build()

        project.extensions.add("test", TestExtension)
        val ext: DefaultExtraPropertiesExtension = project.properties.get("ext") as DefaultExtraPropertiesExtension
        ext.setProperty("${propPrefix}stringValueNoDefault", "stringValueNoDefault")
        ext.setProperty("${propPrefix}stringValueWithDefault", "stringValueWithDefault")
        ext.setProperty("${propPrefix}booleanValueNoDefault", true)
        ext.setProperty("${propPrefix}booleanValueWithDefault", true)
        ext.setProperty("${propPrefix}intValueNoDefault", 1)
        ext.setProperty("${propPrefix}intValueWithDefault", 2)
        ext.setProperty("${propPrefix}listValueNoDefault", "10,11")
        ext.setProperty("${propPrefix}listValueWithDefault", "20,21")

        val extensionProperties = fetchPluginExtensionProperties(
            displayName = displayName,
            clazz = TestExtension::class,
            extension = TestExtension(project = project, project.objects)
        )
        val stringValueNoDefault = extensionProperties.properties().filter { it.name == "stringValueNoDefault" }.first()
        Assertions.assertEquals("stringValueNoDefault", stringValueNoDefault.value)
        Assertions.assertTrue(stringValueNoDefault.inProject)
        val stringValueWithDefault =
            extensionProperties.properties().filter { it.name == "stringValueWithDefault" }.first()
        Assertions.assertEquals("stringValueWithDefault", stringValueWithDefault.value)
        Assertions.assertTrue(stringValueWithDefault.inProject)

        val booleanValueNoDefault =
            extensionProperties.properties().filter { it.name == "booleanValueNoDefault" }.first()
        Assertions.assertEquals("true", booleanValueNoDefault.value)
        Assertions.assertTrue(booleanValueNoDefault.inProject)
        val booleanValueWithDefault =
            extensionProperties.properties().filter { it.name == "booleanValueWithDefault" }.first()
        Assertions.assertEquals("true", booleanValueWithDefault.value)
        Assertions.assertTrue(booleanValueWithDefault.inProject)

        val intValueNoDefault = extensionProperties.properties().filter { it.name == "intValueNoDefault" }.first()
        Assertions.assertEquals("1", intValueNoDefault.value)
        Assertions.assertTrue(intValueNoDefault.inProject)
        val intValueWithDefault = extensionProperties.properties().filter { it.name == "intValueWithDefault" }.first()
        Assertions.assertEquals("2", intValueWithDefault.value)
        Assertions.assertTrue(intValueWithDefault.inProject)

        val listValueNoDefault = extensionProperties.properties().filter { it.name == "listValueNoDefault" }.first()
        Assertions.assertEquals("10,11", listValueNoDefault.value)
        Assertions.assertTrue(listValueNoDefault.inProject)
        val listValueWithDefault = extensionProperties.properties().filter { it.name == "listValueWithDefault" }.first()
        Assertions.assertEquals("20,21", listValueWithDefault.value)
        Assertions.assertTrue(listValueWithDefault.inProject)
    }

    @Test
    fun `test properties from system env`(environmentVariables: EnvironmentVariables) {
        val project = ProjectBuilder.builder().build()

        environmentVariables.set("${envPrefix}STRINGVALUENODEFAULT", "stringValueNoDefault")
        environmentVariables.set("${envPrefix}STRINGVALUEWITHDEFAULT", "stringValueWithDefault")
        environmentVariables.set("${envPrefix}BOOLEANVALUENODEFAULT", "true")
        environmentVariables.set("${envPrefix}BOOLEANVALUEWITHDEFAULT", "true")
        environmentVariables.set("${envPrefix}INTVALUENODEFAULT", "1")
        environmentVariables.set("${envPrefix}INTVALUEWITHDEFAULT", "2")
        environmentVariables.set("${envPrefix}LISTVALUENODEFAULT", "10,11")
        environmentVariables.set("${envPrefix}LISTVALUEWITHDEFAULT", "20,21")

        val extensionProperties = fetchPluginExtensionProperties(
            displayName = displayName,
            clazz = TestExtension::class,
            extension = TestExtension(project = project, project.objects)
        )
        val stringValueNoDefault = extensionProperties.properties().filter { it.name == "stringValueNoDefault" }.first()
        Assertions.assertEquals("stringValueNoDefault", stringValueNoDefault.value)
        val stringValueWithDefault =
            extensionProperties.properties().filter { it.name == "stringValueWithDefault" }.first()
        Assertions.assertEquals("stringValueWithDefault", stringValueWithDefault.value)

        val booleanValueNoDefault =
            extensionProperties.properties().filter { it.name == "booleanValueNoDefault" }.first()
        Assertions.assertEquals("true", booleanValueNoDefault.value)
        val booleanValueWithDefault =
            extensionProperties.properties().filter { it.name == "booleanValueWithDefault" }.first()
        Assertions.assertEquals("true", booleanValueWithDefault.value)

        val intValueNoDefault = extensionProperties.properties().filter { it.name == "intValueNoDefault" }.first()
        Assertions.assertEquals("1", intValueNoDefault.value)
        val intValueWithDefault = extensionProperties.properties().filter { it.name == "intValueWithDefault" }.first()
        Assertions.assertEquals("2", intValueWithDefault.value)

        val listValueNoDefault = extensionProperties.properties().filter { it.name == "listValueNoDefault" }.first()
        Assertions.assertEquals("10,11", listValueNoDefault.value)
        val listValueWithDefault = extensionProperties.properties().filter { it.name == "listValueWithDefault" }.first()
        Assertions.assertEquals("20,21", listValueWithDefault.value)
    }

    @Test
    fun `test properties from extension`() {
        val project = ProjectBuilder.builder().build()

        project.extensions.add("test", TestExtension)
        val ext: DefaultExtraPropertiesExtension = project.properties.get("ext") as DefaultExtraPropertiesExtension
        ext.setProperty("${propPrefix}stringValue", "stringValue")

        val extensionProperties = fetchPluginExtensionProperties(
            displayName = displayName,
            clazz = TestExtension::class,
            extension = TestExtension(project = project, project.objects)
        )
        Assertions.assertNotNull(extensionProperties)
        Assertions.assertEquals(propPrefix, extensionProperties.propPrefix)
        Assertions.assertEquals(envPrefix, extensionProperties.envPrefix)
        Assertions.assertEquals(displayName, extensionProperties.displayName)
        Assertions.assertEquals(name, extensionProperties.name)
    }

    @Test
    fun `test description and example`() {
        open class TestExtension constructor(project: Project, objects: ObjectFactory) :
            PropertyHandler(project, objects, propPrefix, envPrefix) {

            @ExtensionProperty(name = "withDescriptionAndExample", example = "example", description = "description")
            var withDescriptionAndExample: Property<String> = propertyString("withDescriptionAndExample")

            @ExtensionProperty(name = "withoutDescriptionAndExample")
            var withoutDescriptionAndExample: Property<String> = propertyString("withoutDescriptionAndExample")
        }

        val project = ProjectBuilder.builder().build()
        val extensionProperties = fetchPluginExtensionProperties(
            displayName = displayName,
            clazz = TestExtension::class,
            extension = TestExtension(project = project, project.objects)
        )
        val withDescriptionAndExample =
            extensionProperties.properties().filter { it.name == "withDescriptionAndExample" }.first()
        Assertions.assertEquals("description", withDescriptionAndExample.description)
        Assertions.assertEquals("example", withDescriptionAndExample.example)
        val withoutDescriptionAndExample =
            extensionProperties.properties().filter { it.name == "withoutDescriptionAndExample" }.first()
        Assertions.assertNull(withoutDescriptionAndExample.description)
        Assertions.assertNull(withoutDescriptionAndExample.example)
    }

    @Test
    fun `test properties defaults`() {
        open class TestExtension constructor(project: Project, objects: ObjectFactory) :
            PropertyHandler(project, objects, propPrefix, envPrefix) {

            var stringValue: Property<String> = propertyString("stringValue")
            var booleanValue: Property<Boolean> = propertyBool("booleanValue")
            var intValue: Property<Int> = propertyInt("intValue")
            var listValue: ListProperty<String> = propertyList("listValue", toStringList)
            var mapValue: MapProperty<String, String> = propertyMap("mapValue", { list -> list.associateWith { it } })
        }

        val project = ProjectBuilder.builder().build()
        val extension = TestExtension(project = project, project.objects)
        Assertions.assertEquals("", extension.stringValue.get())
        Assertions.assertEquals(false, extension.booleanValue.get())
        Assertions.assertEquals(0, extension.intValue.get())
        Assertions.assertTrue(extension.listValue.get().isEmpty())
        Assertions.assertTrue(extension.mapValue.get().isEmpty())
    }
}
