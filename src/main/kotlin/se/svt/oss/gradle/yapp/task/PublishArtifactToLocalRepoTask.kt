// SPDX-FileCopyrightText: 2022 Sveriges Television AB
//
// SPDX-License-Identifier: Apache-2.0

package se.svt.oss.gradle.yapp.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import se.svt.oss.gradle.yapp.config.projectType

abstract class PublishArtifactToLocalRepoTask : DefaultTask() {
    init {

        group = "yapp publisher"
        description =
            "Publish ${project.name} as a ${project.projectType().javaClass.simpleName} to the local repository"
        dependsOn(project.tasks.getByName("publishToMavenLocal"))
        doLast {

            println(
                "Publish ${project.name} as a " +
                    "${project.projectType().javaClass.simpleName} to the local repository"
            )
        }
    }

    @TaskAction
    fun publishToLocal() {
    }
}
