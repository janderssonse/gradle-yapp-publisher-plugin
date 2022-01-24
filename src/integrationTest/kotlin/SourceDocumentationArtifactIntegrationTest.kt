import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import se.svt.oss.gradle.yapp.publishingtarget.PublishingTargetType.MAVEN_CENTRAL
import java.nio.file.Path
import java.nio.file.Paths
import java.util.jar.JarEntry
import java.util.jar.JarFile

class SourceDocumentationArtifactIntegrationTest : AbstractIntegrationTest() {

    @BeforeEach
    fun before() {
        pathDict = PathDict(JAVALIB_PROJECTPATH)
        copyBuildFileTemplate(pathDict)
    }

    @Test
    fun `source artifact and documentation artifacts are published by default`() {
        val group = "$TLD.$ARTIFACTS"
        val version = version()

        val artifacts = artifacts(version, pathDict)

        publishToTmp(
            ConfigurationData.buildFileData(
                group, version,
                ConfigurationData.buildFileYappSection(
                    listOf(MAVEN_CENTRAL.lowercase()),
                    mavenpublishingsection = ConfigurationData.buildFileMavenPublishingSection(group, version)
                ),
                buildGradleFile = pathDict.buildFilePath
            ),
            pathDict = pathDict
        )

        assertIterableEquals(
            generatedArtifacts(pathDict.libraryDirName, ARTIFACTS, version)
                .map { it.name }.toList().sorted(),
            artifacts
        )

        val mainPath = Paths.get(tmpdir, TLD, ARTIFACTS, pathDict.libraryDirName, version)

        val htmlFiles =
            readJar(Paths.get(mainPath.toString(), "${pathDict.libraryDirName}-$version-javadoc.jar"), ".html")
        val sourceFiles =
            readJar(Paths.get(mainPath.toString(), "${pathDict.libraryDirName}-$version-sources.jar"), ".java")

        assertTrue(htmlFiles.isNotEmpty())
        assertTrue(sourceFiles.isNotEmpty())
    }

    @Test
    fun `empty source artifact and empty documentation artifacts are published`() {
        val group = "$TLD.$ARTIFACTS"
        val version = version()

        val artifacts = artifacts(version, pathDict)

        publishToTmp(
            ConfigurationData.buildFileData(
                group, version,

                ConfigurationData.buildFileYappSection(
                    listOf(MAVEN_CENTRAL.lowercase()),
                    mavenpublishingsection = ConfigurationData.buildFileMavenPublishingSection(group, version),
                    emptyDocArtifact = true,
                    emptySourceArtifact = true
                ),
                buildGradleFile = pathDict.buildFilePath
            ),
            pathDict = pathDict
        )

        val mainPath = Paths.get(tmpdir, TLD, ARTIFACTS, pathDict.libraryDirName, version)

        assertIterableEquals(
            generatedArtifacts(pathDict.libraryDirName, ARTIFACTS, version)
                .map { it.name }.toList().sorted(),
            artifacts
        )

        val htmlFiles =
            readJar(Paths.get(mainPath.toString(), "${pathDict.libraryDirName}-$version-javadoc.jar"), ".html")
        val sourceFiles =
            readJar(Paths.get(mainPath.toString(), "${pathDict.libraryDirName}-$version-sources.jar"), ".java")

        assertTrue(htmlFiles.isEmpty())
        assertTrue(sourceFiles.isEmpty())
    }

    @Test
    fun `no source artifact and no documentation artifacts are published`() {
        val group = "$TLD.${ARTIFACTS}Empty"
        val version = version()

        publishToTmp(
            ConfigurationData.buildFileData(
                group, version,
                ConfigurationData.buildFileYappSection(
                    listOf(MAVEN_CENTRAL.lowercase()),
                    mavenpublishingsection = ConfigurationData.buildFileMavenPublishingSection(group, version),
                    withDocArtifact = false,
                    withSourceArtifact = false
                ),

                buildGradleFile = pathDict.buildFilePath
            ),
            pathDict = pathDict
        )

        assertIterableEquals(
            generatedArtifacts(pathDict.libraryDirName, ARTIFACTS + "Empty", version)
                .map { it.name }.toList().sorted(),
            listOf("${pathDict.libraryDirName}-$version.jar")
        )
    }

    private fun readJar(path: Path, extensionName: String): List<JarEntry> {
        val file = JarFile(path.toFile())
        return file.entries().asSequence().filter { it.name.endsWith(extensionName) }.toList()
    }

    private fun generatedArtifacts(name: String, subdir: String, version: String) = Paths.get(
        tmpdir, TLD, subdir, name, version
    ).toFile().walk().filter { it.extension == "jar" }

    private fun artifacts(version: String, pathDict: PathDict): List<String> = listOf(
        "${pathDict.libraryDirName}-$version-javadoc.jar",
        "${pathDict.libraryDirName}-$version-sources.jar",
        "${pathDict.libraryDirName}-$version.jar"
    ).sorted()
}
