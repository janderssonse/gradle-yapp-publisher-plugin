package se.svt.oss.gradle.yapp.task

import org.gradle.api.DefaultTask
import org.gradle.api.internal.tasks.userinput.UserInputHandler
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.TaskAction
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
abstract class CreateConfigurationTemplateTask
@Inject constructor(
    private val clock: Clock,
    private val outputEventListener: OutputEventListener,
) : DefaultTask() {
    init {
        group = "yapp publisher"
        description = "Create template for a gradle.properties based on user input"
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

    @TaskAction
    fun createTemplate() {
        sendOutput(
            listOf(
                StyledTextOutputEvent.Span(
                    Style.Header,
                    "This task will create Yapp Publisher configuration."
                ),
                StyledTextOutputEvent.EOL,
                StyledTextOutputEvent.Span(
                    Style.Description,
                    "Add configuration or just create an empty template for you gradle.properties file."
                ),
                StyledTextOutputEvent.EOL,
                StyledTextOutputEvent.EOL,
                StyledTextOutputEvent.Span(
                    Style.Identifier,
                    "At the end all configuration will be written to console."
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
                    target = targetType.name.lowercase(),
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
        propPrefix: String,
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

        val group = Group(displayName, target)
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

                            if (props.collectionType == PluginExtensionProperties.ExtensionPropertyType.LIST) {
                                spans.add(StyledTextOutputEvent.EOL)
                                spans.add(
                                    StyledTextOutputEvent.Span(
                                        Style.Description,
                                        "Use ',' to create a list, i.e. value1,value2,value3"
                                    )
                                )
                            } else if (props.collectionType == PluginExtensionProperties.ExtensionPropertyType.MAP) {
                                spans.add(StyledTextOutputEvent.EOL)
                                spans.add(
                                    StyledTextOutputEvent.Span(
                                        Style.Description,
                                        "Use 'k:v' and ',' to create a map, i.e. key1:value1,key2:value2,key3:value3"
                                    )
                                )
                            }
                            if (props.valueType == PluginExtensionProperties.ValueType.NUMERIC) {
                                spans.add(StyledTextOutputEvent.EOL)
                                spans.add(StyledTextOutputEvent.Span(Style.Description, "Add a numeric value."))
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
                    group.props["${propPrefix}${props.name}"] = answer.toString()
                }
            }
        return group
    }

    private class Group(val name: String, val target: String?) {
        val props: MutableMap<String, String> = mutableMapOf()
    }
}
