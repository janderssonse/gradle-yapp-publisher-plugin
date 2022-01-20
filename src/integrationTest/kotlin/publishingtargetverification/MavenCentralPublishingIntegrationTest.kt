package publishingtarget

import AbstractIntegrationTest
import PathConf
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.xmlunit.diff.Diff
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension
import java.nio.file.Paths
import kotlin.io.path.ExperimentalPathApi
import ConfigurationData as conf

@ExperimentalPathApi
@ExtendWith(
    SystemStubsExtension::class
)
class MavenCentralPublishingIntegrationTest : AbstractIntegrationTest() {

    lateinit var pathConf: PathConf

    @BeforeEach
    fun before() {
        pathConf = PathConf(kotlinLibProjectPath, yappPluginTmpDir())
        copyTemplateBuildFile(pathConf)
    }

    @Test
    fun `a fully configured yapp buildfile generates expected artifacts and pom data on publication`() {

        val group = "$TLD.$MAVEN_CENTRAL"
        val version = "0.0.1-SNAPSHOT"

        publishToTmp(
            conf.buildFileData(group, version, buildGradleFile = pathConf.buildFilePath),
            conf.buildFileYappConfData(group, version),
            pathConf = pathConf
        )

        val pomDiff: Diff =
            diff(resource("pom/publishingtargetverification/$MAVEN_CENTRAL.pom"), generatedPom(pathConf.libraryDirName, MAVEN_CENTRAL, version))

        Assertions.assertFalse(pomDiff.hasDifferences())

        Assertions.assertIterableEquals(
            generatedArtifacts(pathConf.libraryDirName, MAVEN_CENTRAL, version),
            artifacts(version, pathConf)
        )
    }

    fun generatedArtifacts(name: String = pluginName, subdir: String, version: String) = Paths.get(
        tmpdir, TLD, subdir, name, version
    ).toFile().walk().filter { it.isFile }.onEach { println(it.name) }.map { it.name }.toList().sorted()

    private fun artifacts(version: String, pathConf: PathConf): List<String> {
        val signatures = listOf(
            "${pathConf.libraryDirName}-$version-javadoc.jar",
            "${pathConf.libraryDirName}-$version.jar",
            "${pathConf.libraryDirName}-$version.module",
            "${pathConf.libraryDirName}-$version.pom",
            "${pathConf.libraryDirName}-$version-sources.jar",
            "maven-metadata-local.xml"
        )
        return signatures.sorted()
    }
}
