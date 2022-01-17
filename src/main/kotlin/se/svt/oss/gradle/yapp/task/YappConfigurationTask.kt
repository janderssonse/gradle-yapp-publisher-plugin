package se.svt.oss.gradle.yapp.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import se.svt.oss.gradle.yapp.config.projectType
import se.svt.oss.gradle.yapp.extension.SigningExtension
import se.svt.oss.gradle.yapp.extension.YappPublisherExtension
import se.svt.oss.gradle.yapp.extension.fetchPluginExtensionProperties
import se.svt.oss.gradle.yapp.publishingtarget.fetchPluginExtensionsPropertiesForTarget
import se.svt.oss.gradle.yapp.publishingtarget.publishingTargets
import se.svt.oss.gradle.yapp.yappExtension

abstract class YappConfigurationTask : DefaultTask() {
    init {
        group = "yapp publisher"
        description = "Prints out the current Yapp Publisher Plugin configuration"
    }

    @TaskAction
    fun printConfiguration() {

        println("Yapp Publisher Plugin")
        println("Name: ${project.name}, Type: ${project.projectType().javaClass.simpleName}")

        println("\nConfigurations\n")

        fetchPluginExtensionProperties(
            "Yapp Publisher Plugin",
            YappPublisherExtension::class,
            project.yappExtension()
        ).prettyPrint()
        println("\n")

        fetchPluginExtensionProperties(
            "Signing",
            SigningExtension::class,
            project.yappExtension().signing
        ).prettyPrint()
        println("\n")

        project.publishingTargets().forEach { target ->
            fetchPluginExtensionsPropertiesForTarget(target).prettyPrint()
            println("\n")
        }
    }
}
