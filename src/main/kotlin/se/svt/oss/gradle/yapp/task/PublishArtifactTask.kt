package se.svt.oss.gradle.yapp.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import se.svt.oss.gradle.yapp.config.projectType
import se.svt.oss.gradle.yapp.publishingtarget.PublishingTargetType.GITHUB
import se.svt.oss.gradle.yapp.publishingtarget.PublishingTargetType.GITLAB
import se.svt.oss.gradle.yapp.publishingtarget.PublishingTargetType.GRADLE_PORTAL
import se.svt.oss.gradle.yapp.publishingtarget.PublishingTargetType.MAVEN_CENTRAL
import se.svt.oss.gradle.yapp.publishingtarget.PublishingTargetType.UNKNOWN
import se.svt.oss.gradle.yapp.publishingtarget.publishingTargets
import se.svt.oss.gradle.yapp.yappExtension

abstract class PublishArtifactTask( // @Inject constructor(
//    private val projectType: ProjectType,
) : DefaultTask() {
    init {
        group = "yapp publisher"
        description =
            "Publish ${project.name} as a ${project.projectType().javaClass.simpleName} to ${project.publishingTargets().forEach { it.name() }}"

        project.publishingTargets().forEach {
            when (it.publishingTargetType) {
                GRADLE_PORTAL -> {
                    dependsOn(project.tasks.getByName("publishPlugins"))
                }
                MAVEN_CENTRAL -> {
                    if (project.yappExtension().mavenPublishing.directReleaseToMavenCentral.get()) {
                        dependsOn(project.tasks.getByName("publishToSonatype"))
                        dependsOn(project.tasks.getByName("closeAndReleaseSonatypeStagingRepository"))
                    } else {
                        dependsOn(project.tasks.getByName("publish${MAVEN_CENTRAL}PublicationTo${it.name()}"))
                    }
                }
                GITHUB -> {
                    dependsOn(project.tasks.getByName("publish${GITHUB}PublicationTo${it.name()}"))
                }
                GITLAB -> {
                    dependsOn(project.tasks.getByName("publish${GITLAB}PublicationTo${it.name()}"))
                }
                UNKNOWN -> {}
            }
        }
        doLast {

            println("Published ${project.name} as a ${project.projectType().javaClass.simpleName} to ${project.publishingTargets().forEach { it.name() }}")
        }
    }

    @TaskAction
    fun publishToPublishTarget() {
    }
}
