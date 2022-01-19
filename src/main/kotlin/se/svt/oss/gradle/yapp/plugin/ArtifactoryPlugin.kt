package se.svt.oss.gradle.yapp.plugin

import org.gradle.api.Project
import org.jfrog.artifactory.client.Artifactory
import org.jfrog.artifactory.client.ArtifactoryClientBuilder
import se.svt.oss.gradle.yapp.extension.YappPublisherExtension
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

data class ArtifactoryPlugin(val project: Project) {

    fun yappExtension(): YappPublisherExtension = project.extensions.getByType(YappPublisherExtension::class.java)

    fun upload(artifactPaths: List<Path>) {
        artifactPaths.forEach { path ->
            Files.newInputStream(path).use { stream ->
                val artifactoryTargetPath = artifactoryTargetBasePath().resolve(path.fileName).toString()
                artifactory()
                    .repository(yappExtension().artifactoryPublishing.repoKey.get())
                    .upload(artifactoryTargetPath, stream)
                    .doUpload()
            }
        }
    }

    fun collectArtifacts(artifactBasePath: String): List<Path> {

        val artifacts = Paths.get(
            artifactBasePath,
            project.group.toString().replace(".", "/"),
            project.name,
            project.version.toString()
        )

        return artifacts.toFile().walk()
            .filter { !it.isDirectory }
            .map { it.absolutePath }
            .map { Paths.get(it) }
            .toList()
    }

    private fun artifactory(): Artifactory = ArtifactoryClientBuilder.create()
        .setUrl(yappExtension().artifactoryPublishing.host.get())
        .setUsername(yappExtension().artifactoryPublishing.user.get())
        .setPassword(yappExtension().artifactoryPublishing.password.get())
        .addInterceptorLast { request, context ->
            project.logger.warn("Artifactory request: " + request.requestLine)
            project.logger.warn("Context: $context")
        }
        .build()

    private fun artifactoryTargetBasePath() =
        Paths.get("${project.group}", project.name, project.version.toString())
}
