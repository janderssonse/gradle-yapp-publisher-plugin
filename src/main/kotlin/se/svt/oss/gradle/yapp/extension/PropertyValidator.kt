package se.svt.oss.gradle.yapp.extension

import org.gradle.api.Project

class PropertyMetaDataA(
    val extension: String,
    val project: Project,
    val name: String,
    val value: String,
    val disallows: List<String> = emptyList(),
    val mustHave: List<String> = emptyList(),
    vararg val rules: () -> Boolean
) {

    fun validate(): String {

        project.logger.warn("$name $value")

        if (disallows.contains(name))
            return "Property $name should not be set with ${disallows.joinToString("")}"

        if (mustHave.contains(name))
            return "Property $name should be set with ${mustHave.joinToString("")}"

        rules.forEach {
            val result = !it()
            if (!result) {
                return "Property $name failed with rule $it"
            }
        }
        return "Validated OK!"
    }
}

class PropertyRule(val disallows: List<String>, val allows: List<String>, vararg val rules: () -> Boolean)
