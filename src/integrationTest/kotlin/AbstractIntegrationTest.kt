// SPDX-FileCopyrightText: 2021 Sveriges Television AB
//
// SPDX-License-Identifier: Apache-2.0

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
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.appendText
import kotlin.io.path.writeText

@ExperimentalPathApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractIntegrationTest {

    open val buildFileTemplatePath: String = "/src/integrationTest/resources/projects/build.gradle.kts"

    val kotlinLibProjectPath: String = "/src/integrationTest/resources/projects/kotlinlibrary"
    val javaLibProjectPath: String = "/src/integrationTest/resources/projects/javalibrary"
    val javaGradlePluginProjectPath: String = "/src/integrationTest/resources/projects/javagradleplugin"
    val kotlinGradlePluginProjectPath: String = "/src/integrationTest/resources/projects/kotlingradleplugin"
    val unknownLibraryProjectPath: String = "/src/integrationTest/resources/projects/unknown-library"

    lateinit var signingKey: String

    protected val pluginName = "gradle-yapp-publisher-plugin"
    protected val tmpdir: String = System.getProperty("java.io.tmpdir")

    @BeforeAll
    fun beforeAll() {
        publishYappPluginToTmp()
    }

    private fun publishYappPluginToTmp() {

        val pathConf = PathConf("", yappPluginTmpDir())

        signingKey = resource("gpg/sec_signingkey_ascii_newlineliteral.asc").readText()

        val fileArray = File("./").listFiles { file ->
            !file.name.matches(Regex("""build|.gradle|docs|gradle|gradle|.idea|LICENSES|.reuse|.git|DEVELOPMENT.md|LICENSE"""))
        }
        fileArray.forEach {
            println(it.name.toString())
            println("${yappPluginTmpDir()}/${it.name}")
        }

        fileArray.forEach { it.copyRecursively(File("${yappPluginTmpDir()}/${it.name}")) }

        publishToTmp(ConfigurationData.yappBasePlugin(), pathConf = pathConf)
    }

    fun publishToTmp(
        buildFileData: String,
        buildFileAppendData: String = "",
        propertiesData: String = "",
        gradleTask: String = "publishToMavenLocal",
        pathConf: PathConf
    ) {

        pathConf.buildFilePath.writeText(buildFileData)
        pathConf.buildFilePath.appendText(buildFileAppendData)
        pathConf.propertyFilePath.toFile().writeText(propertiesData)

        val buildResult = GradleRunner.create()
            .withProjectDir(pathConf.projectPath.toFile())
            .withArguments("-Dmaven.repo.local=$tmpdir", gradleTask)
            .withPluginClasspath()
            .forwardOutput()
            .build()

        assertEquals(TaskOutcome.SUCCESS, buildResult.task(":$gradleTask")!!.outcome)
    }

    fun resource(resource: String): File = File(javaClass.classLoader.getResource(resource)!!.file)

    fun diff(resourcePom: File, generatedPom: File): Diff {

        val diff = DiffBuilder.compare(Input.fromFile(resourcePom)).withTest(Input.fromFile(generatedPom))
            .checkForSimilar()
            .ignoreWhitespace().build()
        println(diff.differences)
        return diff
    }

    fun generatedPom(name: String, subdir: String, version: String, extension: String = "pom"): File = Paths.get(
        tmpdir, TLD, subdir, name, version,
        "$name-$version.$extension"
    ).toFile()

    fun generatedSignatures(name: String = pluginName, subdir: String, version: String) = Paths.get(
        tmpdir, TLD, subdir, name, version
    ).toFile().walk().filter { it.extension == "asc" }.map { it.name }.toList().sorted()

    fun xpathFieldDiff(
        query: String,
        expectedValue: String,
        subdir: String,
        version: String,
        name: String
    ) {
        val xpath: XPathEngine = JAXPXPathEngine()
        xpath.setNamespaceContext(mapOf(Pair("m", "http://maven.apache.org/POM/4.0.0")))
        val nodes = xpath.selectNodes(query, Input.fromFile(generatedPom(name, subdir, version)).build())

        assertTrue(nodes.count() > 0)

        nodes.forEach {
            assertEquals(expectedValue, it.textContent)
        }
    }

    fun copyTemplateBuildFile(pathConf: PathConf) {
        Files.copy(
            Paths.get(yappPluginTmpDir(), buildFileTemplatePath),
            Paths.get(yappPluginTmpDir(), pathConf.projectDirPath, "build.gradle.kts"), StandardCopyOption.REPLACE_EXISTING
        )
    }

    companion object {
        @JvmStatic
        @TempDir
        lateinit var testDirPath: Path

        const val TLD: String = "se"
        const val SIGNING: String = "signing"
        const val ORDER: String = "order"
        const val PROPERTY: String = "property"
        const val BUILD: String = "build"
        const val ARTIFACTS: String = "artifacts"
        const val ENV: String = "env"

        const val JAVALIB: String = "javalib"
        const val KOTLINLIB: String = "kotlinlib"
        const val JAVAGRADLEPLUG: String = "javagradleplug"
        const val KOTLINGRADLEPLUG: String = "kotlingradleplug"
        const val UNKNOWN: String = "unknown"

        const val MAVEN_CENTRAL = "maven_central"
    }

    fun yappPluginTmpDir() = "$testDirPath/yappinstall"
}

data class PathConf(val projectDirPath: String, private val rootPath: String) {

    val projectPath = Paths.get(rootPath, projectDirPath)
    val settingsFilePath = Paths.get(rootPath, projectDirPath, "settings.gradle.kts")
    val buildFilePath = Paths.get(rootPath, projectDirPath, "build.gradle.kts")
    val propertyFilePath = Paths.get(rootPath, projectDirPath, "gradle.properties")
    val libraryDirName = projectDirPath.substringAfterLast("/")
}
