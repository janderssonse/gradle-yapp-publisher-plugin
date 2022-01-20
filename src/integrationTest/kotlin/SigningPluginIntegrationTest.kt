// SPDX-FileCopyrightText: 2021 Sveriges Television AB
//
// SPDX-License-Identifier: Apache-2.0

import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension
import kotlin.io.path.ExperimentalPathApi

@ExperimentalPathApi
@ExtendWith(
    SystemStubsExtension::class
)
class SigningPluginIntegrationTest : AbstractIntegrationTest() {

    lateinit var pathConf: PathConf

    @BeforeEach
    fun before() {
        pathConf = PathConf(kotlinLibProjectPath, yappPluginTmpDir())
        copyTemplateBuildFile(pathConf)
    }

    @Test
    fun `signing when signing is enabled and is a release version or overrides snapshot`() {
        val group = "$TLD.$SIGNING"
        var version = "0.0.7"
        // val signingKey = resource("gpg/sec_signingkey.gpg").canonicalPath

        var signatures = signatures(version, pathConf)

        publishToTmp(
            ConfigurationData.buildFileData(group, version, buildGradleFile = pathConf.buildFilePath),
            ConfigurationData.yappBuildGradleConfSigning(signingKey, true),
            pathConf = pathConf
        )
        assertIterableEquals(generatedSignatures(pathConf.libraryDirName, SIGNING, version), signatures)

        version = "0.0.7-SNAPSHOT"

        copyTemplateBuildFile(pathConf)

        signatures = signatures(version, pathConf)
        publishToTmp(
            ConfigurationData.buildFileData(group, version, buildGradleFile = pathConf.buildFilePath),
            ConfigurationData.yappBuildGradleConfSigning(signingKey, true, true),
            pathConf = pathConf
        )

        assertIterableEquals(generatedSignatures(pathConf.libraryDirName, "signing", version), signatures)

        copyTemplateBuildFile(pathConf)

        version = "0.0.8-SNAPSHOT"
        publishToTmp(
            ConfigurationData.buildFileData(group, version, buildGradleFile = pathConf.buildFilePath),
            ConfigurationData.buildFileYappConfData(group, version, signingKey, true, false),
            pathConf = pathConf

        )

        assertTrue(generatedSignatures(pathConf.libraryDirName, "signing", version).isEmpty())
    }

    @Test
    fun `not signing when signing is disabled or a snapshot version`() {

        copyTemplateBuildFile(pathConf)
        val group = "$TLD.$SIGNING"
        var version = "0.0.9"

        publishToTmp(
            ConfigurationData.buildFileData(group, version, buildGradleFile = pathConf.buildFilePath),
            ConfigurationData.buildFileYappConfData(group, version, signingKey, false),
            pathConf = pathConf

        )
        assertTrue(generatedSignatures(pathConf.libraryDirName, "signing", version).isEmpty())

        copyTemplateBuildFile(pathConf)

        version = "0.0.10-SNAPSHOT"
        publishToTmp(
            ConfigurationData.buildFileData(group, version, buildGradleFile = pathConf.buildFilePath),
            ConfigurationData.buildFileYappConfData(group, version, signingKey, true),

            pathConf = pathConf
        )
    }

    @Test
    fun `same signing artifacts regardless if key is binary format or text format`() {
        var group = "$TLD.$SIGNING"
        val version = "0.0.7"

        val signatures = signatures(version, pathConf)

        publishToTmp(
            ConfigurationData.buildFileData(group, version, buildGradleFile = pathConf.buildFilePath),
            ConfigurationData.buildFileYappConfData(group, version, signingKey, true),
            pathConf = pathConf
        )

        assertIterableEquals(generatedSignatures(pathConf.libraryDirName, "signing", version), signatures)
        val generatedSignatures = generatedSignatures(pathConf.libraryDirName, "signing", version)

        signingKey = resource("gpg/sec_signingkey.gpg").canonicalPath

        group = "$TLD.${SIGNING}2"

        copyTemplateBuildFile(pathConf)
        publishToTmp(
            ConfigurationData.buildFileData(group, version, buildGradleFile = pathConf.buildFilePath),
            ConfigurationData.buildFileYappConfData(group, version, signingKey, true),
            pathConf = pathConf

        )
        assertIterableEquals(generatedSignatures(pathConf.libraryDirName, "signing2", version), generatedSignatures)
    }

    private fun signatures(version: String, pathConf: PathConf): List<String> {
        val signatures = listOf(
            "${pathConf.libraryDirName}-$version.pom.asc",
            "${pathConf.libraryDirName}-$version-javadoc.jar.asc",
            "${pathConf.libraryDirName}-$version.jar.asc",
            "${pathConf.libraryDirName}-$version.module.asc",
            "${pathConf.libraryDirName}-$version-sources.jar.asc"
        )
        return signatures.sorted()
    }
}
