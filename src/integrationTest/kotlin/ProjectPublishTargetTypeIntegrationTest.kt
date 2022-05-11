// SPDX-FileCopyrightText: 2022 Sveriges Television AB
//
// SPDX-License-Identifier: Apache-2.0

import ConfigurationData.Companion.buildFileData
import ConfigurationData.Companion.buildFileMavenPublishingSection
import ConfigurationData.Companion.buildFileYappSection
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import se.svt.oss.gradle.yapp.publishingtarget.PublishingTargetType.MAVEN_CENTRAL

class ProjectPublishTargetTypeIntegrationTest : AbstractIntegrationTest() {

    @Test
    fun `a java library is identified correctly`() {

        val pathDict = PathDict(JAVALIB_PROJECTPATH)
        copyBuildFileTemplate(pathDict)

        val group = "$TLD.$JAVALIB"
        val version = version()

        publishToTmp(
            buildFileData(
                group,
                version,
                buildFileYappSection(
                    listOf(MAVEN_CENTRAL.lowercase()),
                    buildFileMavenPublishingSection(group, version)
                ),
                plugin2 = """`java-library`""",
                buildGradleFile = pathDict.buildFilePath
            ),
            pathDict = pathDict
        )
    }

    @Test
    fun `a java library having a snapshot version is identified correctly`() {

        val pathDict = PathDict(JAVALIB_PROJECTPATH)
        copyBuildFileTemplate(pathDict)

        val group = "$TLD.$JAVALIB"
        val version = version()

        publishToTmp(
            buildFileData(
                group,
                version,
                buildFileYappSection(
                    listOf(MAVEN_CENTRAL.lowercase()),
                    buildFileMavenPublishingSection(group, version)
                ),
                "",
                plugin2 = """`java-library`""",
                buildGradleFile = pathDict.buildFilePath
            ),
            pathDict = pathDict
        )
    }

    @Test
    fun `a kotlin library is identified correctly`() {
        val pathDict = PathDict(KOTLINLIB_PROJECTPATH)
        copyBuildFileTemplate(pathDict)

        val group = "$TLD.$KOTLINLIB"
        val version = version()

        publishToTmp(
            buildFileData(
                group, version,
                buildFileYappSection(
                    listOf(MAVEN_CENTRAL.lowercase()),
                    buildFileMavenPublishingSection(group, version)
                ),
                plugin2 = """kotlin("jvm") version "1.5.21"
                | `java-library`
                """.trimMargin(),
                buildGradleFile = pathDict.buildFilePath
            ),

            pathDict = pathDict
        )
    }

    @Test
    fun `a kotlin gradle plugin is identified correctly`() {
        val pathDict = PathDict(KOTLIN_GRADLEPLUG_PROJECTPATH)
        copyBuildFileTemplate(pathDict)

        val group = "$TLD.$KOTLINGRADLEPLUG"
        val version = version()

        publishToTmp(
            buildFileData(
                group, version,
                buildFileYappSection(
                    listOf(MAVEN_CENTRAL.lowercase()),
                    buildFileMavenPublishingSection(group, version)
                ),
                plugin2 = """id("org.jetbrains.kotlin.jvm") version "1.5.31"
                | `java-gradle-plugin`
                """.trimMargin(),
                buildGradleFile = pathDict.buildFilePath
            ),

            propertiesFileData = """yapp.gradleplugin.id=$group""",
            pathDict = pathDict
        )
    }

    @Test
    fun `a java gradle plugin is identified correctly`() {
        val pathDict = PathDict(JAVA_GRADLEPLUG_PROJECTPATH)
        copyBuildFileTemplate(pathDict)

        val group = "$TLD.$JAVAGRADLEPLUG"
        val version = version()

        publishToTmp(
            buildFileData(
                group,
                version,
                buildFileYappSection(
                    listOf(MAVEN_CENTRAL.lowercase()),
                    buildFileMavenPublishingSection(group, version)
                ),
                plugin1 = "",
                plugin2 = """`java-gradle-plugin`""".trimMargin(),
                buildGradleFile = pathDict.buildFilePath
            ),

            propertiesFileData = """yapp.gradleplugin.id=$group""",
            pathDict = pathDict
        )
    }

    @Disabled
    fun `could not identify the project type`() {
        val pathDict = PathDict(UNKNOWN_PROJECTPATH)
        copyBuildFileTemplate(pathDict)

        val group = "$TLD.$UNKNOWN"
        val version = version()

        assertThrows<IllegalStateException> {
            publishToTmp(
                buildFileData(
                    group,
                    version,
                    buildFileYappSection(
                        listOf(MAVEN_CENTRAL.lowercase()),
                        buildFileMavenPublishingSection(group, version)
                    ),
                    plugin1 = "",
                    plugin2 = "".trimMargin(),
                    buildGradleFile = pathDict.buildFilePath
                ),

                pathDict = pathDict
            )
        }
    }
}
