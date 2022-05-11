// SPDX-FileCopyrightText: 2022 Sveriges Television AB
//
// SPDX-License-Identifier: Apache-2.0

import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import se.svt.oss.gradle.yapp.publishingtarget.PublishingTargetType.MAVEN_CENTRAL
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension

@ExtendWith(
    SystemStubsExtension::class
)
class SigningPluginIntegrationTest : AbstractIntegrationTest() {

    @BeforeEach
    fun before() {
        pathDict = PathDict(KOTLINLIB_PROJECTPATH)
        copyBuildFileTemplate(pathDict)
    }

    @Test
    fun `signing when signing is enabled and is a release version or overrides snapshot`() {

        val group = "$TLD.${SIGNING.lowercase()}"
        var version = "0.0.7"
        // val signingKey = resource("gpg/sec_signingkey.gpg").canonicalPath

        var signatures = signatures(version, pathDict)

        publishToTmp(
            ConfigurationData.buildFileData(
                group, version,
                ConfigurationData.buildFileYappSection(
                    listOf(MAVEN_CENTRAL.lowercase()),
                    ConfigurationData.buildFileMavenPublishingSection(group, version),
                    ConfigurationData.buildFileSigningSection(signingKey, true),
                ),
                buildGradleFile = pathDict.buildFilePath
            ),
            pathDict = pathDict
        )
        assertIterableEquals(publishedSignatures(pathDict.libraryDirName, SIGNING, version), signatures)

        version = "0.0.7-SNAPSHOT"

        copyBuildFileTemplate(pathDict)

        signatures = signatures(version, pathDict)
        publishToTmp(
            ConfigurationData.buildFileData(
                group, version,
                ConfigurationData.buildFileYappSection(
                    listOf(MAVEN_CENTRAL.lowercase()),
                    ConfigurationData.buildFileMavenPublishingSection(group, version),
                    ConfigurationData.buildFileSigningSection(signingKey, true, true)
                ),
                buildGradleFile = pathDict.buildFilePath
            ),
            pathDict = pathDict
        )

        assertIterableEquals(publishedSignatures(pathDict.libraryDirName, "signing", version), signatures)

        copyBuildFileTemplate(pathDict)

        version = "0.0.8-SNAPSHOT"
        publishToTmp(
            ConfigurationData.buildFileData(
                group, version,
                ConfigurationData.buildFileYappSection(
                    listOf(MAVEN_CENTRAL.lowercase()),
                    mavenpublishingsection = ConfigurationData.buildFileMavenPublishingSection(group, version),
                    signingsection = ConfigurationData.buildFileSigningSection(signingKey, true)
                ),
                buildGradleFile = pathDict.buildFilePath
            ),
            pathDict = pathDict

        )

        assertTrue(publishedSignatures(pathDict.libraryDirName, "signing", version).isEmpty())
    }

    @Test
    fun `not signing when signing is disabled or a snapshot version`() {

        copyBuildFileTemplate(pathDict)
        val group = "$TLD.${SIGNING.lowercase()}"
        var version = "0.0.9"

        publishToTmp(
            ConfigurationData.buildFileData(
                group, version,
                ConfigurationData.buildFileYappSection(
                    listOf(MAVEN_CENTRAL.lowercase()),
                    mavenpublishingsection = ConfigurationData.buildFileMavenPublishingSection(group, version),
                    signingsection = ConfigurationData.buildFileSigningSection(signingKey, false)
                ),
                buildGradleFile = pathDict.buildFilePath
            ),
            pathDict = pathDict

        )
        assertTrue(publishedSignatures(pathDict.libraryDirName, "signing", version).isEmpty())

        copyBuildFileTemplate(pathDict)

        version = "0.0.10-SNAPSHOT"
        publishToTmp(
            ConfigurationData.buildFileData(
                group, version,
                ConfigurationData.buildFileYappSection(
                    listOf(MAVEN_CENTRAL.lowercase()),
                    mavenpublishingsection = ConfigurationData.buildFileMavenPublishingSection(group, version),
                    signingsection = ConfigurationData.buildFileSigningSection(signingKey, true)
                ),
                buildGradleFile = pathDict.buildFilePath
            ),

            pathDict = pathDict
        )
    }

    @Test
    fun `same signing artifacts regardless if key is binary format or text format`() {
        var group = "$TLD.${SIGNING.lowercase()}"
        val version = version()

        val signatures = signatures(version, pathDict)

        publishToTmp(
            ConfigurationData.buildFileData(
                group, version,
                ConfigurationData.buildFileYappSection(
                    listOf(MAVEN_CENTRAL.lowercase()),
                    mavenpublishingsection = ConfigurationData.buildFileMavenPublishingSection(group, version),
                    signingsection = ConfigurationData.buildFileSigningSection(signingKey, true)
                ),
                buildGradleFile = pathDict.buildFilePath
            ),
            pathDict = pathDict
        )

        assertIterableEquals(publishedSignatures(pathDict.libraryDirName, "signing", version), signatures)
        val generatedSignatures = publishedSignatures(pathDict.libraryDirName, "signing", version)

        signingKey = resource("gpg/sec_signingkey.gpg").canonicalPath

        group = "$TLD.${SIGNING.lowercase()}2"

        copyBuildFileTemplate(pathDict)
        publishToTmp(
            ConfigurationData.buildFileData(
                group, version,
                ConfigurationData.buildFileYappSection(
                    listOf(MAVEN_CENTRAL.lowercase()),
                    mavenpublishingsection = ConfigurationData.buildFileMavenPublishingSection(group, version),
                    signingsection = ConfigurationData.buildFileSigningSection(signingKey, true)
                ),
                buildGradleFile = pathDict.buildFilePath
            ),
            pathDict = pathDict

        )
        assertIterableEquals(publishedSignatures(pathDict.libraryDirName, "signing2", version), generatedSignatures)
    }

    private fun signatures(version: String, pathDict: PathDict): List<String> {
        val signatures = listOf(
            "${pathDict.libraryDirName}-$version.pom.asc",
            "${pathDict.libraryDirName}-$version-javadoc.jar.asc",
            "${pathDict.libraryDirName}-$version.jar.asc",
            "${pathDict.libraryDirName}-$version.module.asc",
            "${pathDict.libraryDirName}-$version-sources.jar.asc"
        )
        return signatures.sorted()
    }
}
