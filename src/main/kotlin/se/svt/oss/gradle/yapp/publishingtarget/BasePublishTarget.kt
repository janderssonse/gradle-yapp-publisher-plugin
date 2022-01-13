// SPDX-FileCopyrightText: 2021 Sveriges Television AB
//
// SPDX-License-Identifier: Apache-2.0

package se.svt.oss.gradle.yapp.publishingtarget

import org.gradle.api.Project
import se.svt.oss.gradle.yapp.isSnapShot

abstract class BasePublishTarget(
    val project: Project,
    val publishingTargetType: PublishingTargetType
) {
    abstract fun configure()

    fun name(): String {
        return if (project.isSnapShot()) {
            "${this.javaClass.simpleName}-SNAPSHOT"
        } else {
            this.javaClass.simpleName
        }
    }
}
