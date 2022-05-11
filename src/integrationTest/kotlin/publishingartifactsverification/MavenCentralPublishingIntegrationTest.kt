// SPDX-FileCopyrightText: 2022 Sveriges Television AB
//
// SPDX-License-Identifier: Apache-2.0

package publishingartifactsverification

import AbstractIntegrationTest
import PathDict
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.xmlunit.diff.Diff
import se.svt.oss.gradle.yapp.publishingtarget.PublishingTargetType.MAVEN_CENTRAL
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension
import java.nio.file.Paths
import java.util.stream.Stream
import ConfigurationData as conf

@ExtendWith(SystemStubsExtension::class)
open class MavenCentralPublishingIntegrationTest : AbstractIntegrationTest() {

    @ParameterizedTest
    @MethodSource("projecttype")
    fun `a fully configured yapp buildfile generates expected artifacts and pom data on publication`(
        projectPath: String,
        projectType: String
    ) {

        pathDict = PathDict(projectPath)
        copyBuildFileTemplate(pathDict)

        val group = "$TLD.${MAVEN_CENTRAL.lowercase()}"
        val version = version()

        publishToTmp(
            conf.buildFileData(
                group, version,
                conf.buildFileYappSection(
                    listOf(MAVEN_CENTRAL.lowercase()),
                    conf.buildFileMavenPublishingSection(group, version)
                ),
                buildGradleFile = pathDict.buildFilePath
            ),
            pathDict = pathDict
        )
        val referencePom = resource("pom/publishingtargetverification/${MAVEN_CENTRAL.lowercase()}.pom").readText()
            .replace("{ARTIFACTID}", projectType)
            .replace("{VERSION}", version)
            .replace("{GROUPID}", group)

        val pomDiff: Diff =
            diff(
                referencePom,
                publishedPomPath(pathDict.libraryDirName, MAVEN_CENTRAL.lowercase(), version)
            )

        assertFalse(pomDiff.hasDifferences())

        assertIterableEquals(
            generatedArtifacts(pathDict.libraryDirName, MAVEN_CENTRAL.lowercase(), version),
            artifacts(version, pathDict)
        )
    }

    private fun generatedArtifacts(name: String = pluginName, subdir: String, version: String) = Paths.get(
        tmpdir, TLD, subdir, name, version
    ).toFile().walk().filter { it.isFile }.onEach { println(it.name) }.map { it.name }.toList().sorted()

    private fun artifacts(version: String, pathDict: PathDict) = listOf(
        "${pathDict.libraryDirName}-$version-javadoc.jar",
        "${pathDict.libraryDirName}-$version.jar",
        "${pathDict.libraryDirName}-$version.module",
        "${pathDict.libraryDirName}-$version.pom",
        "${pathDict.libraryDirName}-$version-sources.jar" // ,
        // "maven-metadata-local.xml"
    ).sorted()

    companion object {
        @JvmStatic
        fun projecttype(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(KOTLINLIB_PROJECTPATH, KOTLINLIB),
                Arguments.of(JAVALIB_PROJECTPATH, JAVALIB)
            )
        }
    }
}
