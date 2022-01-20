// SPDX-FileCopyrightText: 2021 Sveriges Television AB
//
// SPDX-License-Identifier: Apache-2.0

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.xmlunit.diff.Diff
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension
import kotlin.io.path.ExperimentalPathApi
import ConfigurationData as conf

@ExperimentalPathApi
@ExtendWith(
    SystemStubsExtension::class
)
class PropertiesIntegrationTest : AbstractIntegrationTest() {

    lateinit var pathConf: PathConf

    @BeforeEach
    fun before() {
        pathConf = PathConf(kotlinLibProjectPath, yappPluginTmpDir())
        copyTemplateBuildFile(pathConf)
    }

    @Test
    fun `properties are read from build file`() {

        val group = "$TLD.$BUILD"
        val version = "0.0.2-SNAPSHOT"

        publishToTmp(
            conf.buildFileData(group, version, buildGradleFile = pathConf.buildFilePath),
            conf.buildFileYappConfData(group, version),
            pathConf = pathConf
        )

        val pomDiff: Diff = diff(resource("pom/buildGradleConf.pom"), generatedPom(pathConf.libraryDirName, "build", version))

        assertFalse(pomDiff.hasDifferences())
    }

    @Test
    fun `properties are read from properties file`() {
        val group = "$TLD.$PROPERTY"
        val version = "0.0.3-SNAPSHOT"

        publishToTmp(
            conf.buildFileData(group, version, buildGradleFile = pathConf.buildFilePath),
            "",
            conf.yappPropertiesConf(pathConf.libraryDirName, group, version),
            pathConf = pathConf
        )

        val pomDiff: Diff = diff(resource("pom/propertyConf.pom"), generatedPom(pathConf.libraryDirName, "property", version))
        println(pomDiff.toString())

        assertFalse(pomDiff.hasDifferences())
    }

    @Test
    fun `properties are read from environment`(environmentVariables: EnvironmentVariables) {

        val group = "$TLD.$ENV"
        val version = "0.0.4-SNAPSHOT"

        conf.systemEnv().entries.forEach {
            environmentVariables.set(it.key, it.value)
        }

        publishToTmp(
            conf.buildFileData(group, version, buildGradleFile = pathConf.buildFilePath),
            pathConf = pathConf
        )

        val pomDiff: Diff = diff(resource("pom/envConf.pom"), generatedPom(pathConf.libraryDirName, "env", version))

        assertFalse(pomDiff.hasDifferences())
    }

    @Test
    fun `properties are read in order build file, properties file, system env`(environmentVariables: EnvironmentVariables) {

        val group = "$TLD.$ORDER"
        val version = "0.0.4-SNAPSHOT"

        environmentVariables.set("YAPP_MAVENPUBLISHING_NAME", "envname")
        environmentVariables.set("YAPP_TARGETS", "maven_central")

        publishToTmp(
            conf.buildFileData(group, version, buildGradleFile = pathConf.buildFilePath),

            conf.buildFileYappConfData(group, version, name = "confname"),
            """yapp.mavenPublishing.name=propertyname
                |yapp.targets=maven_central
            """.trimMargin(),
            pathConf = pathConf
        )

        xpathFieldDiff("m:project/m:name", "confname", "order", version, pathConf.libraryDirName)

        copyTemplateBuildFile(pathConf)
        publishToTmp(
            conf.buildFileData(group, version, buildGradleFile = pathConf.buildFilePath), "",
            """yapp.mavenPublishing.name=propertyname
                |yapp.targets=maven_central
            """.trimMargin(),

            pathConf = pathConf
        )

        xpathFieldDiff("m:project/m:name", "propertyname", "order", version, pathConf.libraryDirName)

        copyTemplateBuildFile(pathConf)
        publishToTmp(
            conf.buildFileData(group, version, buildGradleFile = pathConf.buildFilePath), "", "",

            pathConf = pathConf
        )

        xpathFieldDiff("m:project/m:name", "envname", "order", version, pathConf.libraryDirName)
    }

    @Test
    fun `properties are read in order build file, properties file`() {

        val group = "$TLD.$ORDER"
        val version = "0.0.5-SNAPSHOT"

        publishToTmp(
            conf.buildFileData(group, version, buildGradleFile = pathConf.buildFilePath),
            """

            yapp {
                targets.add("maven_central")
                mavenPublishing {
                    name.set("confname")
                }
            }
            """.trimIndent(),
            """yapp.mavenPublishing.name=propertyname
                |yapp.targets=maven_central
            """.trimMargin(),

            pathConf = pathConf
        )

        xpathFieldDiff("m:project/m:name", "confname", "order", version, pathConf.libraryDirName)

        copyTemplateBuildFile(pathConf)
        publishToTmp(
            conf.buildFileData(group, version, buildGradleFile = pathConf.buildFilePath), "",
            """yapp.mavenPublishing.name=propertyname
                |
                |yapp.targets=maven_central
            """.trimMargin(),

            pathConf = pathConf
        )

        xpathFieldDiff("m:project/m:name", "propertyname", "order", version, pathConf.libraryDirName)
    }

    fun yappConf() = """yapp {
           mavenPublishing {
               name.set("asdf")
           }
       }
       """
}
