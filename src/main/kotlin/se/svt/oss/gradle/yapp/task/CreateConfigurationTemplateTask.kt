package se.svt.oss.gradle.yapp.task

import org.gradle.api.DefaultTask
import org.gradle.api.internal.tasks.userinput.UserInputHandler
import org.gradle.api.tasks.TaskAction
import se.svt.oss.gradle.yapp.extension.PluginExtensionProperties
import se.svt.oss.gradle.yapp.extension.SigningExtension
import se.svt.oss.gradle.yapp.extension.YappPublisherExtension
import se.svt.oss.gradle.yapp.extension.fetchPluginExtensionProperties
import se.svt.oss.gradle.yapp.publishingtarget.PublishingTargetType
import se.svt.oss.gradle.yapp.publishingtarget.fetchPluginExtensionsPropertiesForTarget
import se.svt.oss.gradle.yapp.yappExtension
import java.io.File

abstract class CreateConfigurationTemplateTask : DefaultTask() {
    init {
        group = "yapp publisher"
        description = "Create template for a gradle.properties based on user input"

        val destination: String? = project.properties["destination"] as String?
        val outputFile: File? = if (destination != null) File(destination) else null

        project.logger.lifecycle(
            """
            |
            |This task will create Yapp Publisher configuration.
            |Fill in needed configuration or just create a template for you gradle.properties file.
            |
        """.trimMargin()
        )

        if (outputFile != null) {
            project.logger.lifecycle("Will write configuration to ${outputFile.absoluteFile}.")
        } else {
            project.logger.lifecycle("Will write configuration to console.")
        }
    }

    @TaskAction
    fun createTemplate() {
        val userInput = services.get(org.gradle.api.internal.tasks.userinput.UserInputHandler::class.java)
        val groups: MutableList<Group> = mutableListOf()

        // Yapp config
        val yappPluginExtensionProperties = fetchPluginExtensionProperties(
            "Yapp Publisher Plugin",
            YappPublisherExtension::class,
            project.yappExtension()
        )
        groups.add(
            configureGroup(
                userInput = userInput,
                name = yappPluginExtensionProperties.name,
                displayName = yappPluginExtensionProperties.displayName,
                target = null,
                propPrefix = yappPluginExtensionProperties.propPrefix,
                properties = yappPluginExtensionProperties.properties().filter { p -> p.name != "targets" }
            )
        )

        // Signing config
        if (userInput.askYesNoQuestion("Add signing configuration?", false)) {
            val signingPluginExtensionProperties = fetchPluginExtensionProperties(
                "Signing",
                SigningExtension::class,
                project.yappExtension().signing
            )
            groups.add(
                configureGroup(
                    userInput = userInput,
                    name = signingPluginExtensionProperties.name,
                    displayName = signingPluginExtensionProperties.displayName,
                    target = null,
                    propPrefix = signingPluginExtensionProperties.propPrefix,
                    properties = signingPluginExtensionProperties.properties()
                )
            )
        }

        // Publish target config
        val possiblePublishTargets: MutableList<PublishingTargetType> = mutableListOf(
            PublishingTargetType.MAVEN_CENTRAL,
            PublishingTargetType.GRADLE_PORTAL,
            PublishingTargetType.GITHUB,
            PublishingTargetType.GITLAB,
            PublishingTargetType.ARTIFACTORY
        )
        while (possiblePublishTargets.size > 0) {
            val choice: String = userInput.selectOption(
                "Add target to configuration",
                listOf("No more targets") + possiblePublishTargets.map { t -> t.name },
                null
            )
            if (choice == "No more targets") {
                break
            }
            val targetType = PublishingTargetType.valueOf(choice)
            val pluginExtensionsProperties =
                fetchPluginExtensionsPropertiesForTarget(target = targetType.publishTarget(project))
            groups.add(
                configureGroup(
                    userInput = userInput,
                    name = pluginExtensionsProperties.name,
                    displayName = pluginExtensionsProperties.displayName,
                    target = targetType.name.lowercase(),
                    propPrefix = pluginExtensionsProperties.propPrefix,
                    properties = pluginExtensionsProperties.properties()
                )
            )
            possiblePublishTargets.remove(targetType)
        }
        println("yapp.targets=${groups.filter { g -> g.target != null }.map { g -> g.target }.joinToString(",")}")
        groups.forEach { group ->
            run {
                println("# ${group.name}")
                group.props.forEach { println("${it.key}=${it.value}") }
            }
        }
    }

    private fun configureGroup(
        userInput: UserInputHandler,
        displayName: String,
        name: String,
        propPrefix: String,
        target: String?,
        properties: List<PluginExtensionProperties.ExtensionProperty>
    ): Group {
        val createEmpty = userInput.askYesNoQuestion(
            """

            Configure $displayName later by create an empty template with all properties.

            ${name.lowercase()}> Create empty template?
            """.trimIndent(),
            false
        )
        val onlyRequired = if (!createEmpty) {
            userInput.askYesNoQuestion("${name.lowercase()}> Create template with required fields only?", true)
        } else {
            false
        }
        val group = Group(displayName, target)
        properties
            .filter { props -> if (onlyRequired) props.mandatory else true }
            .forEach { props ->
                run {
                    val answer =
                        if (!createEmpty) {
                            var question = "${name.lowercase()}> ${props.name}"
                            if (props.valueType == PluginExtensionProperties.ExtensionPropertyType.LIST) {
                                question = """Use ',' to create a list. E.g. value1,value2,value3
                                    |$question
                                """.trimMargin()
                            }
                            userInput.askQuestion(question, props.defaultValue)
                        } else {
                            ""
                        }
                    group.props["${propPrefix}${props.name}"] = answer
                }
            }
        return group
    }

    private class Group(val name: String, val target: String?) {
        val props: MutableMap<String, String> = mutableMapOf()
    }
}
