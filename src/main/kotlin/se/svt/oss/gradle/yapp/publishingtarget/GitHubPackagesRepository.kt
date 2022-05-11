// SPDX-FileCopyrightText: 2022 Sveriges Television AB
//
// SPDX-License-Identifier: Apache-2.0

package se.svt.oss.gradle.yapp.publishingtarget

import org.gradle.api.Project
import se.svt.oss.gradle.yapp.artifact.ArtifactConfigure
import se.svt.oss.gradle.yapp.plugin.MavenPublishingPlugin
import se.svt.oss.gradle.yapp.yappExtension
import java.net.URI

internal class GitHubPackagesRepository(
    project: Project,
    publishingTargetType: PublishingTargetType
) :
    BasePublishTarget(project, publishingTargetType) {

    override fun configure() {
        MavenPublishingPlugin(project).configure(credentials(), publishingTargetType)
        ArtifactConfigure(project, publishingTargetType).configure()
    }

    private fun credentials(): RepositoryConfiguration {
        val uri =
            URI(
                "https://maven.pkg.github.com/${project.yappExtension().gitHub.namespace.get()}/" +
                    project.yappExtension().gitHub.repoName.get()
            )
        val credential = RepositoryCredential(
            project.yappExtension().gitHub.user.get(), project.yappExtension().gitHub.token.get()
        )
        return RepositoryConfiguration(uri, uri, "GitHubPackages", credential)
    }
}
