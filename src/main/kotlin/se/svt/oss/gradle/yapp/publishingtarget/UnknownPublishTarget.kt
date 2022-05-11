// SPDX-FileCopyrightText: 2022 Sveriges Television AB
//
// SPDX-License-Identifier: Apache-2.0

package se.svt.oss.gradle.yapp.publishingtarget

import org.gradle.api.Project

internal class UnknownPublishTarget(project: Project) :
    BasePublishTarget(project, PublishingTargetType.UNKNOWN) {

    override fun configure() {

        project.logger.error(
            """
                |Yapp-Publisher-Plugin could not identify a PublishTarget.
                |As you added this plugin, it is assumed that you wanted to publish, i.e use a Publish Target.
                |Please see the docs (README or https://github.com/svt/gradle-yapp-publisher-plugin )
                |on how you can add a PublishTarget
            """.trimIndent()
        )
    }
}
