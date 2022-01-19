package se.svt.oss.gradle.yapp.publishingtarget

import org.gradle.api.Project
import se.svt.oss.gradle.yapp.artifact.ArtifactConfigure
import se.svt.oss.gradle.yapp.plugin.ArtifactoryPlugin
import se.svt.oss.gradle.yapp.plugin.MavenPublishingPlugin

class ArtifactoryRepository(

    project: Project,
    publishingTargetType: PublishingTargetType
) :
    BasePublishTarget(project, publishingTargetType) {

    override fun configure() {
        MavenPublishingPlugin(project)
        ArtifactConfigure(project, publishingTargetType)
            .configure()
    }

    companion object {

        fun artifactory(project: Project) = ArtifactoryPlugin(project)
    }
}
