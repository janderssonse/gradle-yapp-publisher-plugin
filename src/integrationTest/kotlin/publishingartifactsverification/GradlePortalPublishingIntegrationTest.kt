// SPDX-FileCopyrightText: 2022 Sveriges Television AB
//
// SPDX-License-Identifier: Apache-2.0

package publishingartifactsverification

import AbstractIntegrationTest
import ConfigurationData
import PathDict
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.xmlunit.diff.Diff
import se.svt.oss.gradle.yapp.publishingtarget.PublishingTargetType.GRADLE_PORTAL
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension
import java.nio.file.Paths
import java.util.stream.Stream
import ConfigurationData as conf

@ExtendWith(
    SystemStubsExtension::class
)
class GradlePortalPublishingIntegrationTest : AbstractIntegrationTest() {

    @ParameterizedTest
    @MethodSource("projecttype")
    fun `a fully configured yapp buildfile deliver expected artifacts and pom data on publication`(
        projectPath: String,
        projectType: String
    ) {

        pathDict = PathDict(projectPath)
        copyBuildFileTemplate(pathDict)

        val group = "$TLD.${GRADLE_PORTAL.lowercase()}"
        val version = version()

        publishToTmp(
            ConfigurationData.buildFileData(
                group, version,
                conf.buildFileYappSection(
                    listOf(GRADLE_PORTAL.lowercase()),
                    gradleportalsection = conf.buildFileGradlePortalSection("$group.$projectType")
                ),
                plugin2 = """id("org.jetbrains.kotlin.jvm") version "1.5.31"
                | `java-gradle-plugin`
                """.trimMargin(),
                buildGradleFile = pathDict.buildFilePath
            ),
            pathDict = pathDict
        )

        var referencePom = resource("pom/publishingtargetverification/${GRADLE_PORTAL.lowercase()}.pom").readText()
            .replace("{ARTIFACTID}", projectType)
            .replace("{VERSION}", version)
            .replace("{GROUPID}", group)

        val pomDiff: Diff =
            diff(
                referencePom,
                publishedPomPath(pathDict.libraryDirName, GRADLE_PORTAL.lowercase(), version)
            )

        Assertions.assertFalse(pomDiff.hasDifferences())

        Assertions.assertIterableEquals(
            generatedArtifacts(pathDict.libraryDirName, GRADLE_PORTAL.lowercase(), version),
            artifactsGradlePlugin(version, pathDict)
        )

        Assertions.assertIterableEquals(
            generatedMarkerArtifacts(pathDict.libraryDirName, GRADLE_PORTAL.lowercase(), version),
            markerArtifactsGradlePlugin(version, pathDict)

        )
    }

    private fun generatedArtifacts(name: String = pluginName, subdir: String, version: String) = Paths.get(
        tmpdir, TLD, subdir, name, version
    ).toFile().walk().filter { it.isFile }.onEach { println(it.name) }.map { it.name }.toList().sorted()

    private fun generatedMarkerArtifacts(name: String = pluginName, subdir: String, version: String) = Paths.get(
        tmpdir, TLD, subdir, name, "$TLD.${GRADLE_PORTAL.lowercase()}.$name.gradle.plugin", version
    ).toFile().walk().filter { it.isFile }.onEach { println(it.name) }.map { it.name }.toList().sorted()

    private fun artifactsGradlePlugin(version: String, pathDict: PathDict) = listOf(
        "${pathDict.libraryDirName}-$version-javadoc.jar",
        "${pathDict.libraryDirName}-$version.jar",
        "${pathDict.libraryDirName}-$version.module",
        "${pathDict.libraryDirName}-$version.pom",
        "${pathDict.libraryDirName}-$version-sources.jar",
    ).sorted()

    private fun markerArtifactsGradlePlugin(version: String, pathDict: PathDict) = listOf(
        "se.gradle_portal.${pathDict.libraryDirName}.gradle.plugin-$version.pom",
    ).sorted()

    companion object {
        @JvmStatic
        fun projecttype(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(JAVA_GRADLEPLUG_PROJECTPATH, JAVAGRADLEPLUG),
                Arguments.of(KOTLIN_GRADLEPLUG_PROJECTPATH, KOTLINGRADLEPLUG)
            )
        }
    }
}
