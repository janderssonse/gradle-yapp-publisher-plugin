// SPDX-FileCopyrightText: 2022 Sveriges Television AB
//
// SPDX-License-Identifier: Apache-2.0

package se.svt.oss.gradle.yapp.plugin

import org.gradle.api.Project
import se.svt.oss.gradle.yapp.extension.YappPublisherExtension

abstract class BasePlugin(val project: Project) {

    open fun yappExtension(): YappPublisherExtension = project.extensions.getByType(YappPublisherExtension::class.java)
}
