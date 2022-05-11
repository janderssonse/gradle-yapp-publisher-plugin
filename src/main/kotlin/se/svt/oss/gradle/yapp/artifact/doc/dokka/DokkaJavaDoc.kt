// SPDX-FileCopyrightText: 2022 Sveriges Television AB
//
// SPDX-License-Identifier: Apache-2.0

package se.svt.oss.gradle.yapp.artifact.doc.dokka

import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider

open class DokkaJavaDoc :
    DokkaDoc() {

    init {

        archiveClassifier.set("javadoc")

        val dokkaJavaDoc = project.tasks.getByName("dokkaJavadoc")
        dependsOn(dokkaJavaDoc)
        from(dokkaJavaDoc)
    }

    internal companion object {
        internal fun doc(task: String, project: Project): TaskProvider<*> {
            return project.tasks.register("${task}Gen", DokkaJavaDoc::class.java)
        }
    }
}
