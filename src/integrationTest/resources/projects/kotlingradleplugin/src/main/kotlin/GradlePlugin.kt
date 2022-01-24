// SPDX-FileCopyrightText: 2021 Sveriges Television AB
//
// SPDX-License-Identifier: Apache-2.0

import org.gradle.api.Plugin
import org.gradle.api.Project

class GradlePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.register("greeting") { task ->
            task.doLast {
                println("Hello from plugin 'hejsan.greeting'")
            }
        }
    }
}
/*plugins {
    id("org.jetbrains.kotlin.jvm") version "1.5.0"

    // Apply the java-library plugin for API and implementation separation.
    `java-library`
}*/
