package se.svt.oss.gradle.yapp.task

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import se.svt.oss.gradle.yapp.publishingtarget.fetchPluginExtensionsForTarget
import se.svt.oss.gradle.yapp.publishingtarget.publishingTargets
import se.svt.oss.gradle.yapp.validation.validate

open class ValidateConfigurationTask : DefaultTask() {

    init {
        group = "yapp publisher"
        description = "Validate configuration values"
        outputs.upToDateWhen { true }
    }

    @Input
    fun getYappPropertyNames(): Map<String, Any?> {
        return project.properties.filter { (k, _) -> k.startsWith("yapp.") }.toMap() +
            System.getenv().filter { (k, _) -> k.startsWith("YAPP_") }.toMap()
    }

    @TaskAction
    fun validateConfiguration() {
        val validations = project.publishingTargets()
            .map { fetchPluginExtensionsForTarget(it) }
            .map { validate(it) }
            .map { validation ->
                run {
                    validation.getValidationWarnings().forEach { warning ->
                        run {
                            logger.info("${warning.name} -> ${warning.message}")
                        }
                    }
                    validation.getValidationWarnings().isEmpty()
                }
            }
        if (validations.none { it }) {
            throw GradleException("Invalid configuration.")
        }
    }
}
