// SPDX-FileCopyrightText: 2021 Sveriges Television AB
//
// SPDX-License-Identifier: Apache-2.0

import ConfigurationData.Companion.buildFileMavenPublishingSection
import ConfigurationData.Companion.buildFileYappSection
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.xmlunit.diff.Diff
import se.svt.oss.gradle.yapp.publishingtarget.PublishingTargetType.MAVEN_CENTRAL
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension
import ConfigurationData as conf

@ExtendWith(SystemStubsExtension::class)
class PropertiesIntegrationTest : AbstractIntegrationTest() {

    @BeforeEach
    fun before() {
        pathDict = PathDict(KOTLINLIB_PROJECTPATH)
        copyBuildFileTemplate(pathDict)
    }

    @Test
    fun `properties are read from build file`() {

        val group = "$TLD.$BUILD"
        val version = version()

        publishToTmp(
            conf.buildFileData(
                group, version,
                buildFileYappSection(
                    listOf(MAVEN_CENTRAL.lowercase()),
                    buildFileMavenPublishingSection(group, version)
                ),

                buildGradleFile = pathDict.buildFilePath
            ),
            pathDict = pathDict
        )

        val referencePom = resource("pom/publishingtargetverification/${MAVEN_CENTRAL.lowercase()}.pom").readText()
            .replace("{GROUPID}", group)
            .replace("{ARTIFACTID}", KOTLINLIB)
            .replace("{VERSION}", version)

        val pomDiff: Diff = diff(referencePom, publishedPomPath(pathDict.libraryDirName, "build", version))

        assertFalse(pomDiff.hasDifferences())
    }

    @Test
    fun `properties are read from properties file`() {
        val group = "$TLD.$PROPERTY"
        val version = version()

        publishToTmp(
            conf.buildFileData(
                group, version,
                buildGradleFile = pathDict.buildFilePath
            ),
            propertiesFileData = conf.propertiesFileData(pathDict.libraryDirName, group, version),
            pathDict = pathDict
        )
        val referencePom = resource("pom/property.pom").readText()
            .replace("{GROUPID}", group)
            .replace("{ARTIFACTID}", KOTLINLIB)
            .replace("{VERSION}", version)

        val pomDiff: Diff =
            diff(referencePom, publishedPomPath(pathDict.libraryDirName, PROPERTY, version))
        println(pomDiff.toString())

        assertFalse(pomDiff.hasDifferences())
    }

    @Test
    fun `properties are read from environment`(environmentVariables: EnvironmentVariables) {

        val group = "$TLD.$ENV"
        val version = version()

        conf.systemEnvironmentMavenPublishing(version).entries.forEach {
            environmentVariables.set(it.key, it.value)
        }

        val referencePom = resource("pom/environment.pom").readText()
            .replace("{GROUPID}", group)
            .replace("{ARTIFACTID}", KOTLINLIB)
            .replace("{VERSION}", version)

        publishToTmp(
            conf.buildFileData(group, version, buildGradleFile = pathDict.buildFilePath),
            pathDict = pathDict
        )

        val pomDiff: Diff = diff(referencePom, publishedPomPath(pathDict.libraryDirName, ENV, version))

        assertFalse(pomDiff.hasDifferences())
    }

    @Test
    fun `properties are read in order build file, properties file, system env`(
        environmentVariables: EnvironmentVariables
    ) {

        val group = "$TLD.$ORDER"
        val version = version()

        environmentVariables.set("YAPP_MAVENPUBLISHING_NAME", "envname")
        environmentVariables.set("YAPP_TARGETS", "maven_central")

        publishToTmp(
            conf.buildFileData(
                group, version,
                buildFileYappSection(
                    listOf(MAVEN_CENTRAL.lowercase()),
                    buildFileMavenPublishingSection(group, version, name = "confname")
                ),
                buildGradleFile = pathDict.buildFilePath
            ),

            propertiesFileData = """yapp.mavenPublishing.name=propertyname
                |yapp.targets=maven_central
            """.trimMargin(),
            pathDict = pathDict
        )

        xpathFieldDiff("m:project/m:name", "confname", "order", version, pathDict.libraryDirName)

        copyBuildFileTemplate(pathDict)
        publishToTmp(
            conf.buildFileData(group, version, buildGradleFile = pathDict.buildFilePath),
            propertiesFileData = """yapp.mavenPublishing.name=propertyname
                |yapp.targets=maven_central
            """.trimMargin(),

            pathDict = pathDict
        )

        xpathFieldDiff("m:project/m:name", "propertyname", "order", version, pathDict.libraryDirName)

        copyBuildFileTemplate(pathDict)
        publishToTmp(
            conf.buildFileData(group, version, buildGradleFile = pathDict.buildFilePath), "",

            pathDict = pathDict
        )

        xpathFieldDiff("m:project/m:name", "envname", "order", version, pathDict.libraryDirName)
    }

    @Test
    fun `properties are read in order build file, properties file`() {

        val group = "$TLD.$ORDER"
        val version = version()

        publishToTmp(
            conf.buildFileData(
                group, version,
                buildFileYappSection(
                    listOf(MAVEN_CENTRAL.lowercase()),
                    buildFileMavenPublishingSection(group, version, "confname")
                ),
                buildGradleFile = pathDict.buildFilePath
            ),
            propertiesFileData = """yapp.mavenPublishing.name=propertyname
                |yapp.targets=maven_central
            """.trimMargin(),

            pathDict = pathDict
        )

        xpathFieldDiff("m:project/m:name", "confname", "order", version, pathDict.libraryDirName)

        copyBuildFileTemplate(pathDict)
        publishToTmp(
            conf.buildFileData(group, version, buildGradleFile = pathDict.buildFilePath),
            propertiesFileData =
            """yapp.mavenPublishing.name=propertyname
                |
                |yapp.targets=maven_central
            """.trimMargin(),

            pathDict = pathDict
        )

        xpathFieldDiff("m:project/m:name", "propertyname", "order", version, pathDict.libraryDirName)
    }

    fun yappConf() = """yapp {
           mavenPublishing {
               name.set("asdf")
           }
       }
       """
}
