package se.svt.oss.gradle.yapp.publishingtarget

import GradlePluginPortal
import org.gradle.api.Project
import se.svt.oss.gradle.yapp.config.projectType
import se.svt.oss.gradle.yapp.extension.PluginExtensionProperties
import se.svt.oss.gradle.yapp.extension.YappPublisherExtension
import se.svt.oss.gradle.yapp.extension.fetchPluginExtensionProperties
import se.svt.oss.gradle.yapp.projecttype.GradleJavaPlugin
import se.svt.oss.gradle.yapp.projecttype.GradleKotlinPlugin
import se.svt.oss.gradle.yapp.projecttype.JavaLibrary
import se.svt.oss.gradle.yapp.projecttype.JavaProject
import se.svt.oss.gradle.yapp.projecttype.KotlinLibrary

internal object PublishTargetUtil {
    fun identifyPublishTarget(
        project: Project
    ): List<BasePublishTarget> {
        val targets = specifiedPublishTargetType(project)

        return when (targets.isEmpty()) {
            true -> {
                listOf(guessPublishTargetType(project))
            }
            else -> targets
        }
    }

    private fun specifiedPublishTargetType(project: Project): List<BasePublishTarget> =
        project.extensions.getByType(YappPublisherExtension::class.java).targets.getOrElse(emptyList()).map {
            PublishingTargetType.valueOf(it.uppercase().trim()).publishTarget(project)
        }

    private fun guessPublishTargetType(
        project: Project
    ): BasePublishTarget {

        return when (project.projectType()) {
            is GradleKotlinPlugin -> GradlePluginPortal(project, PublishingTargetType.GRADLE_PORTAL)
            is GradleJavaPlugin -> GradlePluginPortal(project, PublishingTargetType.GRADLE_PORTAL)
            is JavaLibrary -> MavenCentralRepository(project, PublishingTargetType.MAVEN_CENTRAL)
            is JavaProject -> MavenCentralRepository(project, PublishingTargetType.MAVEN_CENTRAL)
            is KotlinLibrary -> MavenCentralRepository(project, PublishingTargetType.MAVEN_CENTRAL)
            else -> UnknownPublishTarget(project)
        }
    }

    fun fetchPluginExtensionsPropertiesForTarget(target: BasePublishTarget): PluginExtensionProperties {
        return fetchPluginExtensionProperties(target)
    }
}
fun Project.publishingTargets(): List<BasePublishTarget> = PublishTargetUtil.identifyPublishTarget(this)
fun fetchPluginExtensionsPropertiesForTarget(target: BasePublishTarget): PluginExtensionProperties = PublishTargetUtil.fetchPluginExtensionsPropertiesForTarget(target)
