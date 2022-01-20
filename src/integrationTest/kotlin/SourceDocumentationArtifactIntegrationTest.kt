
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Path
import java.nio.file.Paths
import java.util.jar.JarEntry
import java.util.jar.JarFile
import kotlin.io.path.ExperimentalPathApi

@ExperimentalPathApi
class SourceDocumentationArtifactIntegrationTest : AbstractIntegrationTest() {

    lateinit var pathConf: PathConf

    @BeforeEach
    fun before() {
        pathConf = PathConf(javaLibProjectPath, yappPluginTmpDir())
        copyTemplateBuildFile(pathConf)
    }

    @Test
    fun `source artifact and documentation artifacts are published by default`() {
        val group = "$TLD.$ARTIFACTS"
        val version = "0.0.1-SNAPSHOT"

        val artifacts = artifacts(version, pathConf)

        publishToTmp(
            ConfigurationData.buildFileData(group, version, buildGradleFile = pathConf.buildFilePath),
            ConfigurationData.buildFileYappConfData(group, version),
            pathConf = pathConf
        )

        assertIterableEquals(
            generatedArtifacts(pathConf.libraryDirName, ARTIFACTS, version)
                .map { it.name }.toList().sorted(),
            artifacts
        )

        val mainPath = Paths.get(tmpdir, TLD, ARTIFACTS, pathConf.libraryDirName, version)

        val htmlFiles =
            readJar(Paths.get(mainPath.toString(), "${pathConf.libraryDirName}-$version-javadoc.jar"), ".html")
        val sourceFiles =
            readJar(Paths.get(mainPath.toString(), "${pathConf.libraryDirName}-$version-sources.jar"), ".java")

        assertTrue(htmlFiles.isNotEmpty())
        assertTrue(sourceFiles.isNotEmpty())
    }

    @Test
    fun `empty source artifact and empty documentation artifacts are published`() {
        val group = "$TLD.$ARTIFACTS"
        val version = "0.0.2-SNAPSHOT"

        val artifacts = artifacts(version, pathConf)

        publishToTmp(
            ConfigurationData.buildFileData(group, version, buildGradleFile = pathConf.buildFilePath),
            ConfigurationData.buildFileYappConfData(
                group,
                version,
                emptyDocArtifact = true,
                emptySourceArtifact = true
            ),
            pathConf = pathConf
        )

        val mainPath = Paths.get(tmpdir, TLD, ARTIFACTS, pathConf.libraryDirName, version)

        assertIterableEquals(
            generatedArtifacts(pathConf.libraryDirName, ARTIFACTS, version)
                .map { it.name }.toList().sorted(),
            artifacts
        )

        val htmlFiles =
            readJar(Paths.get(mainPath.toString(), "${pathConf.libraryDirName}-$version-javadoc.jar"), ".html")
        val sourceFiles =
            readJar(Paths.get(mainPath.toString(), "${pathConf.libraryDirName}-$version-sources.jar"), ".java")

        assertTrue(htmlFiles.isEmpty())
        assertTrue(sourceFiles.isEmpty())
    }

    @Test
    fun `no source artifact and no documentation artifacts are published`() {
        val group = "$TLD.${ARTIFACTS}Empty"
        val version = "0.0.3-SNAPSHOT"

        publishToTmp(
            ConfigurationData.buildFileData(group, version, buildGradleFile = pathConf.buildFilePath),
            ConfigurationData.buildFileYappConfData(
                group,
                version,
                withDocArtifact = false,
                withSourceArtifact = false
            ),
            pathConf = pathConf
        )

        assertIterableEquals(
            generatedArtifacts(pathConf.libraryDirName, ARTIFACTS + "Empty", version)
                .map { it.name }.toList().sorted(),
            listOf("${pathConf.libraryDirName}-$version.jar")
        )
    }

    private fun readJar(path: Path, extensionName: String): List<JarEntry> {
        val file = JarFile(path.toFile())
        return file.entries().asSequence().filter { it.name.endsWith(extensionName) }.toList()
    }

    private fun generatedArtifacts(name: String, subdir: String, version: String) = Paths.get(
        tmpdir, TLD, subdir, name, version
    ).toFile().walk().filter { it.extension == "jar" }

    private fun artifacts(version: String, pathConf: PathConf): List<String> = listOf(
        "${pathConf.libraryDirName}-$version-javadoc.jar",
        "${pathConf.libraryDirName}-$version-sources.jar",
        "${pathConf.libraryDirName}-$version.jar"
    ).sorted()
}
