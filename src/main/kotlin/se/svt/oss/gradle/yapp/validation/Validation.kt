// SPDX-FileCopyrightText: 2022 Sveriges Television AB
//
// SPDX-License-Identifier: Apache-2.0

package se.svt.oss.gradle.yapp.validation

import org.gradle.api.internal.provider.DefaultProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import java.lang.reflect.Member
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaGetter

@Target(AnnotationTarget.PROPERTY)
annotation class StringFormatValidator(val format: String, val message: String = "[NULL]")

class ValidateStringFormat(format: String, val message: String?) : ValueValidator<String> {
    private val pattern = format.toRegex()
    override fun validate(value: String): Boolean {
        if (value.isEmpty()) {
            return true
        }
        return value.matches(pattern)
    }

    override fun message(): String {
        return this.message ?: "Property is not on format '${pattern.pattern}'"
    }
}

fun validate(obj: Any): Validation = validateObj(obj)

private fun validateObj(obj: Any): Validation {
    val validation = Validation()
    val kClass = obj.javaClass.kotlin

    kClass.annotations
        .filterIsInstance<ExtensionRule>()
        .forEach {
            run {
                val valid = validateExtensionRule(it, obj)
                if (!valid) {
                    validation.add(
                        Validation.ValidationWarnings(
                            "ExtensionRule",
                            "${it.validTogether.joinToString(", ")} must be set together"
                        )
                    )
                }
            }
        }
    // Follow declaringClass if one is present. Gradle wraps extension class. We need the original one to find annotations.
    @Suppress("UNCHECKED_CAST")
    val properties: Collection<KProperty1<Any, *>> = if (
        kClass.memberProperties.first().declaringClass()?.kotlin?.declaredMemberProperties != null
    ) kClass.memberProperties.first()
        .declaringClass()!!.kotlin.declaredMemberProperties as Collection<KProperty1<Any, *>>
    else kClass.declaredMemberProperties
    properties.forEach {
        run {
            val warning = validateProperty(it, obj)
            if (warning != null) {
                validation.add(Validation.ValidationWarnings(it.name, message = warning))
            }
        }
    }
    return validation
}

fun KProperty<*>.declaringClass(): Class<*>? {
    return (this.javaField as Member? ?: this.javaGetter)?.declaringClass
}

fun validateExtensionRule(it: ExtensionRule, obj: Any): Boolean {
    // Valid it all are blank or all are not blank
    val isNotBlank = it.validTogether.map { name ->
        (obj.javaClass.kotlin.memberProperties.first { it.name == name }.get(obj) as DefaultProperty<*>)
            .orNull.toString().isNotBlank()
    }
    return isNotBlank.all { it } || isNotBlank.none { it }
}

fun validateProperty(prop: KProperty1<Any, *>, obj: Any): String? {
    var value = prop.get(obj)
    if (value is Property<*>) {
        value = value.get()
    } else if (value is ListProperty<*>) {
        value = value.get()
    }
    val valid: Boolean = prop.getValidator()?.validate(value) ?: true
    return if (valid) null else prop.getValidator()?.message()
}

fun KProperty<*>.getValidator(): ValueValidator<Any?>? {
    val stringFormatValidator = findAnnotation<StringFormatValidator>()
    if (stringFormatValidator != null) {
        @Suppress("UNCHECKED_CAST")
        return ValidateStringFormat(
            format = stringFormatValidator.format,
            message = if (stringFormatValidator.message == "[NULL]") null else stringFormatValidator.message
        ) as ValueValidator<Any?>
    }
    val customValidator = findAnnotation<CustomValidator>() ?: return null
    val validationClass = customValidator.validationClass
    val valueValidator = validationClass.objectInstance ?: validationClass.createInstance()
    @Suppress("UNCHECKED_CAST")
    return valueValidator as ValueValidator<Any?>
}

class Validation {
    private val validationWarnings: MutableList<ValidationWarnings> = mutableListOf()

    fun getValidationWarnings(): List<ValidationWarnings> {
        return validationWarnings.toList()
    }

    fun add(warnings: ValidationWarnings) {
        validationWarnings.add(warnings)
    }

    class ValidationWarnings(val name: String, val message: String)
}
