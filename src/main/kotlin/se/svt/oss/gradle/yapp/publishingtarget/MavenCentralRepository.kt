package se.svt.oss.gradle.yapp.publishingtarget

import org.gradle.api.Project
import se.svt.oss.gradle.yapp.artifact.ArtifactConfigure
import se.svt.oss.gradle.yapp.plugin.MavenPublishingPlugin
import se.svt.oss.gradle.yapp.plugin.SigningPlugin
import se.svt.oss.gradle.yapp.yappExtension
import java.net.URI

internal open class MavenCentralRepository(
    project: Project,
    publishingTargetType: PublishingTargetType
) :
    BasePublishTarget(project, publishingTargetType) {

    override fun configure() {

        MavenPublishingPlugin(project).configure(ossrhCredential(), publishingTargetType)
        ArtifactConfigure(project, publishingTargetType).configure()
        SigningPlugin(project).configure()
    }

    private fun ossrhCredential(): RepositoryConfiguration {
        val snapShotUri = URI("https://${getUrlPrefix()}oss.sonatype.org/content/repositories/snapshots/")

        val uri = if (project.yappExtension().mavenPublishing.directReleaseToMavenCentral.get()) {
            URI("https://${getUrlPrefix()}oss.sonatype.org/service/local/")
        } else {
            URI("https://${getUrlPrefix()}oss.sonatype.org/service/local/staging/deploy/maven2/")
        }
        val credential = RepositoryCredential(
            project.yappExtension().mavenPublishing.user.get(), project.yappExtension().mavenPublishing.password.get()
        )
        return RepositoryConfiguration(uri, snapShotUri, "MavenCentral", credential)
    }

    private fun getUrlPrefix(): String =
        if (project.yappExtension().mavenPublishing.mavenCentralLegacyUrl.get()) "" else "s01."
}
