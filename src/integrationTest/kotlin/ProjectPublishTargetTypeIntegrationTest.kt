import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.io.path.ExperimentalPathApi

@ExperimentalPathApi
class ProjectPublishTargetTypeIntegrationTest : AbstractIntegrationTest() {

    @Test
    fun `a java library is identified correctly`() {
        val pathConf = PathConf(javaLibProjectPath, yappPluginTmpDir())
        copyTemplateBuildFile(pathConf)

        val group = "$TLD.$JAVALIB"
        val version = "0.0.2"

        publishToTmp(
            ConfigurationData.buildFileData(
                group,
                version,
                "",
                plugin2 = """`java-library`""",
                buildGradleFile = pathConf.buildFilePath
            ),
            ConfigurationData.buildFileYappConfData(group, version),
            pathConf = pathConf
        )
    }

    @Test
    fun `a java library having a snapshot version is identified correctly`() {
        val pathConf = PathConf(javaLibProjectPath, yappPluginTmpDir())
        copyTemplateBuildFile(pathConf)

        val group = "$TLD.$JAVALIB"
        val version = "0.0.1-SNAPSHOT"

        publishToTmp(
            ConfigurationData.buildFileData(
                group,
                version,
                "",
                plugin2 = """`java-library`""",
                buildGradleFile = pathConf.buildFilePath
            ),
            ConfigurationData.buildFileYappConfData(group, version),
            pathConf = pathConf
        )
    }

    @Test
    fun `a kotlin library is identified correctly`() {
        val pathConf = PathConf(kotlinLibProjectPath, yappPluginTmpDir())
        copyTemplateBuildFile(pathConf)

        val group = "$TLD.$KOTLINLIB"
        val version = "0.0.1-SNAPSHOT"

        publishToTmp(
            ConfigurationData.buildFileData(
                group, version, "",
                plugin2 = """kotlin("jvm") version "1.5.21"
                | `java-library`""".trimMargin(),
                buildGradleFile = pathConf.buildFilePath
            ),

            ConfigurationData.buildFileYappConfData(group, version),
            pathConf = pathConf
        )
    }

    @Test
    fun `a kotlin gradle plugin is identified correctly`() {
        val pathConf = PathConf(kotlinGradlePluginProjectPath, yappPluginTmpDir())
        copyTemplateBuildFile(pathConf)

        val group = "$TLD.$KOTLINGRADLEPLUG"
        val version = "0.0.1-SNAPSHOT"

        publishToTmp(
            ConfigurationData.buildFileData(
                group, version, "",
                plugin2 = """id("org.jetbrains.kotlin.jvm") version "1.5.31"
                | `java-gradle-plugin`""".trimMargin(),
                buildGradleFile = pathConf.buildFilePath
            ),

            ConfigurationData.buildFileYappConfData(group, version),
            propertiesData = """yapp.gradleplugin.id=$group""",
            pathConf = pathConf
        )
    }

    @Test
    fun `a java gradle plugin is identified correctly`() {
        val pathConf = PathConf(javaGradlePluginProjectPath, yappPluginTmpDir())
        copyTemplateBuildFile(pathConf)

        val group = "$TLD.$JAVAGRADLEPLUG"
        val version = "0.0.1-SNAPSHOT"

        publishToTmp(
            ConfigurationData.buildFileData(
                group,
                version,
                "",
                plugin1 = "",
                plugin2 = """`java-gradle-plugin`""".trimMargin(),
                buildGradleFile = pathConf.buildFilePath
            ),

            ConfigurationData.buildFileYappConfData(group, version),
            propertiesData = """yapp.gradleplugin.id=$group""",
            pathConf = pathConf
        )
    }

    @Disabled
    fun `could not identify the project type`() {
        val pathConf = PathConf(unknownLibraryProjectPath, yappPluginTmpDir())
        copyTemplateBuildFile(pathConf)

        val group = "$TLD.$UNKNOWN"
        val version = "0.0.1-SNAPSHOT"

        assertThrows<IllegalStateException> {
            publishToTmp(
                ConfigurationData.buildFileData(
                    group,
                    version,
                    "",
                    plugin1 = "",
                    plugin2 = "".trimMargin(),
                    buildGradleFile = pathConf.buildFilePath
                ),

                ConfigurationData.buildFileYappConfData(group, version),
                pathConf = pathConf
            )
        }
    }

    private fun gradlePluginBlock() = """




gradlePlugin {
    plugins {
        create("simplePlugin") {
            id = "org.gradle.sample.simple-plugin"
            implementationClass = "org.gradle.sample.SimplePlugin"
        }
    }
}

    """
}
