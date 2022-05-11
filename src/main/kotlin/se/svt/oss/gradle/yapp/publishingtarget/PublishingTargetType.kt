// SPDX-FileCopyrightText: 2022 Sveriges Television AB
//
// SPDX-License-Identifier: Apache-2.0

package se.svt.oss.gradle.yapp.publishingtarget

import GradlePluginPortal
import org.gradle.api.Project

enum class PublishingTargetType {
    ARTIFACTORY {
        override fun publishTarget(project: Project): BasePublishTarget =
            ArtifactoryRepository(project, this)
    },
    GRADLE_PORTAL {
        override fun publishTarget(project: Project): BasePublishTarget =
            GradlePluginPortal(project, this)
    },
    MAVEN_CENTRAL {
        override fun publishTarget(project: Project): BasePublishTarget =
            MavenCentralRepository(project, this)
    },
    GITLAB {
        override fun publishTarget(project: Project): BasePublishTarget =
            GitLabRepository(project, this)
    },
    GITHUB {
        override fun publishTarget(project: Project): BasePublishTarget =
            GitHubPackagesRepository(project, this)
    },
    UNKNOWN {
        override fun publishTarget(project: Project): BasePublishTarget =
            GitHubPackagesRepository(project, this)
    };

    abstract fun publishTarget(project: Project): BasePublishTarget

    fun lowercase() = name.lowercase()
}
