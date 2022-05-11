// SPDX-FileCopyrightText: 2022 Sveriges Television AB
//
// SPDX-License-Identifier: Apache-2.0

package se.svt.oss.gradle.yapp.validation

import kotlin.reflect.KClass

/**
 * All validator needs to implement with interface.
 */
interface ValueValidator<T> {
    /**
     * Validate function. Returns true if value is valid otherwise false.
     */
    fun validate(value: T): Boolean

    /**
     * Message to describe validation error.
     */
    fun message(): String
}

/**
 * If no parameter are needed CustomValidator could be used to add a validator to a extension property
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class CustomValidator(val validationClass: KClass<out ValueValidator<*>>)

/**
 * Add ExtensionRule to group properties to make sure they are all specified.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Repeatable
annotation class ExtensionRule(val validTogether: Array<String>)
