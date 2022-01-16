package se.svt.oss.gradle.yapp.publishingtarget

import org.gradle.api.Project
import se.svt.oss.gradle.yapp.artifact.ArtifactConfigure
import se.svt.oss.gradle.yapp.isRootProject
import se.svt.oss.gradle.yapp.plugin.Artifactory
import se.svt.oss.gradle.yapp.yappExtension
import java.lang.IllegalArgumentException
import java.net.URI

class ArtifactoryRepository(

    project: Project,
    publishingTargetType: PublishingTargetType
) :
    BasePublishTarget(project, publishingTargetType) {

    override fun configure() {
        if (project.isRootProject()) {
            Artifactory(project, publishingTargetType).configure()
            ArtifactConfigure(project, publishingTargetType)
                .configure()
        } else {
            throw IllegalArgumentException(
                """Sorry, due to the original JFrog Artifactory Plugin implementation target type $publishingTargetType " +
                    "configuration can only be set for a rootproject.
                     It is possible that might change in the future, " +
                    "if the Base Plugin used allows this, but as for now..no :(
                """.trimIndent()
            )
        }
    }

    private fun credentials(): RepositoryConfiguration {
        val uri =
            URI("https://maven.pkg.github.com/${project.yappExtension().gitHub.namespace.get()}/${project.yappExtension().gitHub.repoName.get()}")
        val credential = RepositoryCredential(
            project.yappExtension().gitHub.user.get(), project.yappExtension().gitHub.token.get()
        )
        return RepositoryConfiguration(uri, uri, "GitHubPackages", credential)
    }
}
