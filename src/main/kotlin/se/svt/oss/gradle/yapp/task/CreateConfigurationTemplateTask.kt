// SPDX-FileCopyrightText: 2022 Sveriges Television AB
//
// SPDX-License-Identifier: Apache-2.0

package se.svt.oss.gradle.yapp.task

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.internal.tasks.userinput.UserInputHandler
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.api.tasks.options.OptionValues
import org.gradle.internal.logging.events.OutputEventListener
import org.gradle.internal.logging.events.StyledTextOutputEvent
import org.gradle.internal.logging.text.StyledTextOutput.Style
import org.gradle.internal.time.Clock
import org.gradle.work.DisableCachingByDefault
import se.svt.oss.gradle.yapp.extension.PluginExtensionProperties
import se.svt.oss.gradle.yapp.extension.SigningExtension
import se.svt.oss.gradle.yapp.extension.YappPublisherExtension
import se.svt.oss.gradle.yapp.extension.fetchPluginExtensionProperties
import se.svt.oss.gradle.yapp.publishingtarget.PublishingTargetType
import se.svt.oss.gradle.yapp.publishingtarget.fetchPluginExtensionsPropertiesForTarget
import se.svt.oss.gradle.yapp.yappExtension
import javax.inject.Inject

@DisableCachingByDefault(because = "Not worth caching")
abstract class CreateConfigurationTemplateTask @Inject constructor(
    private val clock: Clock,
    private val outputEventListener: OutputEventListener,
) : DefaultTask() {

    init {
        group = "yapp publisher"
        description = "Create Yapp Publisher config template based on user input"
    }

    private var format: String? = null

    companion object {
        val AVAILABLE_FORMATS = listOf("properties", "environment")
    }

    @Input
    @Optional
    open fun getFormat(): String {
        return if (format.isNullOrEmpty()) "properties" else format!!
    }

    @Option(option = "format", description = "Set output format of the generated configuration.")
    open fun setFormat(format: String) {
        this.format = format
    }

    @OptionValues("format")
    open fun getAvailableFormats(): List<String> {
        return AVAILABLE_FORMATS
    }

    @TaskAction
    fun createTemplate() {
        if (format.isNullOrBlank()) {
            format = getFormat()
        }

        if (!getAvailableFormats().contains(format)) {
            throw GradleException(
                "Not a valid format. Available formats are ${getAvailableFormats()
                    .joinToString(",")}"
            )
        }

        sendOutput(
            listOf(
                StyledTextOutputEvent.Span(
                    Style.Header,
                    "This task will create Yapp Publisher configuration."
                ),
                StyledTextOutputEvent.EOL,
                StyledTextOutputEvent.Span(
                    Style.Description,
                    "Add configuration for all publisher endpoints you need in the project."
                ),
                StyledTextOutputEvent.EOL,
                StyledTextOutputEvent.EOL,
                StyledTextOutputEvent.Span(
                    Style.Identifier,
                    "At the end all configuration will be written to console as ${if (format == "properties")
                        "properties" else "environment variables"}."
                ),
                StyledTextOutputEvent.EOL
            )
        )

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
                displayName = yappPluginExtensionProperties.displayName,
                propPrefix = yappPluginExtensionProperties.propPrefix,
                envPrefix = yappPluginExtensionProperties.envPrefix,
                target = null,
                properties = yappPluginExtensionProperties.properties().filter { p -> p.name != "targets" }
            )
        )

        // Signing config
        sendOutput(
            listOf(
                StyledTextOutputEvent.Span(Style.Header, "Signing configuration"),
                StyledTextOutputEvent.EOL
            )
        )
        if (userInput.askYesNoQuestion("Add signing configuration to project?", false)) {
            val signingPluginExtensionProperties = fetchPluginExtensionProperties(
                "Signing",
                SigningExtension::class,
                project.yappExtension().signing
            )
            groups.add(
                configureGroup(
                    userInput = userInput,
                    displayName = signingPluginExtensionProperties.displayName,
                    propPrefix = signingPluginExtensionProperties.propPrefix,
                    envPrefix = signingPluginExtensionProperties.envPrefix,
                    target = null,
                    properties = signingPluginExtensionProperties.properties(),
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
                listOf("No more targets (exit configuration)") + possiblePublishTargets.map { t -> t.name },
                "No more targets (exit configuration)"
            )
            if (choice == "No more targets (exit configuration)") {
                break
            }
            val targetType = PublishingTargetType.valueOf(choice)
            val pluginExtensionsProperties =
                fetchPluginExtensionsPropertiesForTarget(target = targetType.publishTarget(project))
            groups.add(
                configureGroup(
                    userInput = userInput,
                    displayName = pluginExtensionsProperties.displayName,
                    propPrefix = pluginExtensionsProperties.propPrefix,
                    envPrefix = pluginExtensionsProperties.envPrefix,
                    target = targetType.name.lowercase(),
                    properties = pluginExtensionsProperties.properties()
                )
            )
            possiblePublishTargets.remove(targetType)
        }
        if (format == "properties") {
            println(
                "yapp.targets=${groups
                    .filter { g -> g.target != null }
                    .map { g -> g.target }
                    .joinToString(",")}"
            )

            groups.forEach { group ->
                run {
                    println("# ${group.name}")
                    group.props.forEach { println("${group.propPrefix}${it.key}=${it.value}") }
                }
            }
        } else {
            println(
                "YAPP_TARGETS=${groups
                    .filter { g -> g.target != null }
                    .map { g -> g.target }
                    .joinToString(",")}"
            )

            groups.forEach { group ->
                run {
                    println("# ${group.name}")
                    group.props.forEach {
                        println(
                            "${group.envPrefix}${it.key.replace(".", "_").uppercase()}=${it.value}"
                        )
                    }
                }
            }
        }
    }

    private fun sendOutput(spans: List<StyledTextOutputEvent.Span>) {
        outputEventListener.onOutput(
            StyledTextOutputEvent(
                clock.currentTime,
                "QUIET",
                LogLevel.QUIET,
                null,
                spans
            )
        )
    }

    private fun configureGroup(
        userInput: UserInputHandler,
        displayName: String,
        propPrefix: String,
        envPrefix: String,
        target: String?,
        properties: List<PluginExtensionProperties.ExtensionProperty>
    ): Group {
        sendOutput(
            listOf(
                StyledTextOutputEvent.EOL,
                StyledTextOutputEvent.Span(Style.Header, "Configure $displayName"),
                StyledTextOutputEvent.EOL,
                StyledTextOutputEvent.EOL,
            )
        )

        val createEmpty = userInput.askYesNoQuestion("Create empty template?", false)

        val group = Group(displayName, target, propPrefix, envPrefix)
        properties
            .forEach { props ->
                run {
                    val answer: Any =
                        if (!createEmpty) {
                            val description = props.description
                            val example = props.example

                            val spans = mutableListOf(
                                StyledTextOutputEvent.EOL,
                                StyledTextOutputEvent.Span(Style.Header, props.name)
                            )
                            if (!description.isNullOrBlank()) {
                                spans.add(StyledTextOutputEvent.EOL)
                                spans.add(StyledTextOutputEvent.Span(Style.Description, description))
                            }
                            if (!example.isNullOrBlank()) {
                                spans.add(StyledTextOutputEvent.EOL)
                                spans.add(StyledTextOutputEvent.Span(Style.Description, "Example: $example"))
                            }

                            if (props.extraDescription.isNotBlank()) {
                                spans.add(StyledTextOutputEvent.EOL)
                                spans.add(
                                    StyledTextOutputEvent.Span(
                                        Style.Description,
                                        props.extraDescription
                                    )
                                )
                            }
                            spans.add(StyledTextOutputEvent.EOL)
                            sendOutput(spans)
                            if (props.valueType == PluginExtensionProperties.ValueType.BOOLEAN) {
                                userInput.askYesNoQuestion(props.name, props.defaultValue.toBoolean())
                            } else {
                                userInput.askQuestion(props.name, props.defaultValue ?: "")
                            }
                        } else {
                            ""
                        }
                    if (props.parser != null) {
                        val parsed = props.parser.let { it(answer.toString()) }
                        if (parsed is List<*>) {
                            parsed.forEachIndexed { idx, value ->
                                run {
                                    group.props["${props.name}.${idx + 1}"] = value.toString()
                                }
                            }
                        }
                    } else {
                        group.props[props.name] = answer.toString()
                    }
                }
            }
        return group
    }

    private class Group(val name: String, val target: String?, val propPrefix: String, val envPrefix: String) {
        val props: MutableMap<String, String> = mutableMapOf()
    }
}
