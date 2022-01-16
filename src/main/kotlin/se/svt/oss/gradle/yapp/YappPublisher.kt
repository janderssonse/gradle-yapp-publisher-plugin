// SPDX-FileCopyrightText: 2021 Sveriges Television AB
//
// SPDX-License-Identifier: Apache-2.0
package se.svt.oss.gradle.yapp

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.util.GradleVersion
import se.svt.oss.gradle.yapp.config.projectType
import se.svt.oss.gradle.yapp.extension.YappPublisherExtension
import se.svt.oss.gradle.yapp.publishingtarget.publishingTargets
import se.svt.oss.gradle.yapp.task.CreateConfigurationTemplateTask
import se.svt.oss.gradle.yapp.task.PublishArtifactTask
import se.svt.oss.gradle.yapp.task.PublishArtifactToLocalRepoTask
import se.svt.oss.gradle.yapp.task.YappConfigurationTask

class YappPublisher : Plugin<Project> {

    companion object {
        private val MIN_GRADLE: GradleVersion = GradleVersion.version("7.1")
    }

    override fun apply(project: Project) {
        isMinSupportedGradleVersion(project)
        buildExtensions(project)

        project.afterEvaluate {
            configurePublishingTargets(project)
            registerTasks(project)

            projectInfo(project)
        }
    }

    private fun isMinSupportedGradleVersion(project: Project) {
        require(GradleVersion.version(project.gradle.gradleVersion) >= MIN_GRADLE) {
            "This plugin is tested with $MIN_GRADLE and higher"
        }
    }

    private fun buildExtensions(project: Project) {
        project.extensions.create(
            "yapp", YappPublisherExtension::class.java, project
        )
    }

    private fun configurePublishingTargets(project: Project) =
        project.publishingTargets().forEach { it.configure() }

    private fun registerTasks(
        project: Project
    ) {
        project.tasks.register("yappConfiguration", YappConfigurationTask::class.java)

        project.tasks.register(
            "publishArtifactToLocalRepo",
            PublishArtifactToLocalRepoTask::class.java

        )
        project.tasks.register(
            "publishArtifact",
            PublishArtifactTask::class.java

        )
        project.tasks.register(
            "createConfigurationTemplate",
            CreateConfigurationTemplateTask::class.java
        )
    }

    private fun projectInfo(project: Project) {
        project.logger.info(
            "Yapp Publisher Plugin: Name: {}, Type: {}, Target: {}",
            project.name, project.projectType().javaClass.simpleName, project.publishingTargets().forEach { it.name() }
        )
    }
}

fun Project.isSnapShot(): Boolean = version.toString().contains("SNAPSHOT")
fun Project.yappExtension(): YappPublisherExtension = project.extensions.getByType(YappPublisherExtension::class.java)
fun Project.isRootProject(): Boolean = rootProject == project
