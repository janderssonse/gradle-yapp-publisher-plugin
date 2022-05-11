// SPDX-FileCopyrightText: 2022 Sveriges Television AB
//
// SPDX-License-Identifier: Apache-2.0

package se.svt.oss.gradle.yapp.artifact.doc.dokka

import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider

open class DokkaJekyll :
    DokkaDoc() {

    init {
        archiveClassifier.set("jekyll-doc")
        val dokkaJekyll = project.tasks.getByName("dokkaJekyll")
        dependsOn(dokkaJekyll)
        from(dokkaJekyll)
    }

    internal companion object {
        internal fun doc(task: String, project: Project): TaskProvider<*> {
            return project.tasks.register("${task}Gen", DokkaJekyll::class.java)
        }
    }
}
