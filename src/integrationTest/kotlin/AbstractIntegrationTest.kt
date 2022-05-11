// SPDX-FileCopyrightText: 2022 Sveriges Television AB
//
// SPDX-License-Identifier: Apache-2.0

import ConfigurationData.Companion.yappPluginTestBaseBuildFile
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.io.TempDir
import org.xmlunit.builder.DiffBuilder
import org.xmlunit.builder.Input
import org.xmlunit.diff.Diff
import org.xmlunit.xpath.JAXPXPathEngine
import org.xmlunit.xpath.XPathEngine
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import kotlin.io.path.writeText
import kotlin.random.Random

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractIntegrationTest {

    open val buildFileTemplatePath: String = "/src/integrationTest/resources/projects/build_template_kts"

    lateinit var signingKey: String

    protected val pluginName = "gradle-yapp-publisher-plugin"
    protected val tmpdir: String = System.getProperty("java.io.tmpdir")

    protected open var pathDict: PathDict = PathDict(JAVALIB_PROJECTPATH)

    @BeforeAll
    fun beforeAll() {
        publishYappPluginToTmp()
    }

    protected fun publishToTmp(
        buildFileData: String,
        propertiesFileData: String = "",
        gradleTask: String = "publishToMavenLocal",
        pathDict: PathDict
    ) {

        pathDict.buildFilePath.writeText(buildFileData)
        pathDict.propertyFilePath.toFile().writeText(propertiesFileData)

        val buildResult = GradleRunner.create()
            .withProjectDir(pathDict.projectPath.toFile())
            .withArguments("-Dmaven.repo.local=$tmpdir", gradleTask)
            .withPluginClasspath()
            .forwardOutput()
            .build()

        assertEquals(TaskOutcome.SUCCESS, buildResult.task(":$gradleTask")!!.outcome)
    }

    protected fun resource(resource: String): File = File(javaClass.classLoader.getResource(resource)!!.file)

    protected fun diff(referencePom: String, generatedPom: File): Diff {

        val diff = DiffBuilder
            .compare(referencePom)
            .withTest(Input.fromFile(generatedPom))
            .checkForSimilar()
            .ignoreWhitespace().build()
        println(diff.differences)
        return diff
    }

    protected fun publishedPomPath(
        name: String,
        group: String,
        version: String,
        extension: String = "pom"
    ): File =
        Paths.get(tmpdir, TLD, group, name, version, "$name-$version.$extension").toFile()

    protected fun publishedSignatures(
        name: String,
        group: String,
        version: String
    ) =
        Paths.get(tmpdir, TLD, group, name, version)
            .toFile().walk()
            .filter { it.extension == "asc" }
            .map { it.name }
            .toList()
            .sorted()

    protected fun xpathFieldDiff(
        query: String,
        expectedValue: String,
        group: String,
        version: String,
        name: String
    ) {
        val xpath: XPathEngine = JAXPXPathEngine()
        xpath.setNamespaceContext(mapOf(Pair("m", "http://maven.apache.org/POM/4.0.0")))
        val nodes = xpath.selectNodes(query, Input.fromFile(publishedPomPath(name, group, version)).build())

        assertTrue(nodes.count() > 0)

        nodes.forEach {
            assertEquals(expectedValue, it.textContent)
        }
    }

    protected fun copyBuildFileTemplate(pathDict: PathDict) =
        Files.copy(
            Paths.get(yappPluginTmpDir(), buildFileTemplatePath),
            Paths.get(yappPluginTmpDir(), pathDict.projectDirPath, "build.gradle.kts"),
            StandardCopyOption.REPLACE_EXISTING
        )

    private fun publishYappPluginToTmp() {

        val pathDict = PathDict("")

        signingKey = resource("gpg/sec_signingkey_ascii_newlineliteral.asc").readText()

        copyProjectFilesToTestTmpDir()

        publishToTmp(yappPluginTestBaseBuildFile(), pathDict = pathDict)
    }

    private fun copyProjectFilesToTestTmpDir() {
        val projectFiles = File("./").listFiles { file ->
            !file.name.matches(
                Regex(
                    """build|.gradle|docs|gradle|gradle|.idea|LICENSES|.reuse|.git|DEVELOPMENT.md|LICENSE"""
                )
            )
        }
        /*fileArray.forEach {
            println(it.name.toString())
            println("${yappPluginTmpDir()}/${it.name}")
        }*/

        projectFiles.forEach {
            it.copyRecursively(File("${yappPluginTmpDir()}/${it.name}"))
        }
    }

    companion object {
        @JvmStatic
        @TempDir
        var testDirPath: Path? = null

        // group test categories
        const val TLD: String = "se"
        const val SIGNING: String = "signing"
        const val ORDER: String = "order"
        const val PROPERTY: String = "property"
        const val BUILD: String = "build"
        const val ARTIFACTS: String = "artifacts"
        const val ENV: String = "env"

        // Test projects naming
        const val JAVALIB: String = "javalibrary"
        const val KOTLINLIB: String = "kotlinlibrary"
        const val JAVAGRADLEPLUG: String = "javagradleplugin"
        const val KOTLINGRADLEPLUG: String = "kotlingradleplugin"
        const val UNKNOWN: String = "unknown"

        private const val TESTPROJECT_BASE = "src/integrationTest/resources/projects"
        const val KOTLINLIB_PROJECTPATH: String = "$TESTPROJECT_BASE/$KOTLINLIB"
        const val JAVALIB_PROJECTPATH: String = "$TESTPROJECT_BASE/$JAVALIB"
        const val JAVA_GRADLEPLUG_PROJECTPATH: String = "$TESTPROJECT_BASE/$JAVAGRADLEPLUG"
        const val KOTLIN_GRADLEPLUG_PROJECTPATH: String = "$TESTPROJECT_BASE/$KOTLINGRADLEPLUG"
        const val UNKNOWN_PROJECTPATH: String = "$TESTPROJECT_BASE/$UNKNOWN"

        fun yappPluginTmpDir() = "$testDirPath/yappinstall"
    }

    fun version() = "0.0.1-alpha.${Random.nextInt()}"
}

data class PathDict(val projectDirPath: String) {

    private val rootPath: String = AbstractIntegrationTest.yappPluginTmpDir()

    val projectPath = Paths.get(rootPath, projectDirPath)
    val settingsFilePath = Paths.get(rootPath, projectDirPath, "settings.gradle.kts")
    val buildFilePath = Paths.get(rootPath, projectDirPath, "build.gradle.kts")
    val propertyFilePath = Paths.get(rootPath, projectDirPath, "gradle.properties")
    val libraryDirName = projectDirPath.substringAfterLast("/")
}
