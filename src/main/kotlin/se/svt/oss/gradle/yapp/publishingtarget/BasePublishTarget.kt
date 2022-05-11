// SPDX-FileCopyrightText: 2022 Sveriges Television AB
//
// SPDX-License-Identifier: Apache-2.0

package se.svt.oss.gradle.yapp.publishingtarget

import org.gradle.api.Project

abstract class BasePublishTarget(
    val project: Project,
    val publishingTargetType: PublishingTargetType
) {
    abstract fun configure()

    fun name(): String {
        // return if (project.isSnapShot()) {
        //  "${this.javaClass.simpleName}-SNAPSHOT"
        // } else {
        return this.javaClass.simpleName
        // }
    }
}
