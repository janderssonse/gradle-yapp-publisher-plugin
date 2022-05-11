// SPDX-FileCopyrightText: 2022 Sveriges Television AB
//
// SPDX-License-Identifier: Apache-2.0

package se.svt.oss.gradle.yapp.publishingtarget

import org.gradle.api.Project
import se.svt.oss.gradle.yapp.artifact.ArtifactConfigure
import se.svt.oss.gradle.yapp.plugin.MavenPublishingPlugin
import se.svt.oss.gradle.yapp.yappExtension
import java.net.URI

internal class GitLabRepository(
    project: Project,
    publishingTargetType: PublishingTargetType
) :
    BasePublishTarget(project, publishingTargetType) {

    override fun configure() {
        // val tokenTypes = listOf("Private-Token", "Deploy-Token", "Job-Token")

        MavenPublishingPlugin(project).configure(credentials(), publishingTargetType)
        ArtifactConfigure(project, publishingTargetType).configure()
    }

    private fun gitLabUri(): URI {
        return when (project.yappExtension().gitLab.endpointLevel.get()) {
            "project" -> URI(
                "${project.yappExtension().gitLab.host.get()}" +
                    "/api/v4/projects/${project.yappExtension().gitLab.gitlabProjectId.get()}/packages/maven"
            )

            "group" -> URI(
                "${project.yappExtension().gitLab.host.get()}" +
                    "/api/v4/groups/${project.yappExtension().gitLab.gitlabGroupId.get()}/-/packages/maven"
            )
            else -> {
                throw IllegalArgumentException("Submit at valid endpoint level - project, group")
            }
        }
    }

    private fun credentials(): RepositoryConfiguration {
        val credential = RepositoryCredential(
            project.yappExtension().gitLab.tokenType.get(), project.yappExtension().gitLab.token.get()
        )
        return RepositoryConfiguration(gitLabUri(), gitLabUri(), "GitLab", credential)
    }
}
