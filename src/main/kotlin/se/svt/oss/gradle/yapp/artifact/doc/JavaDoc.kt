// SPDX-FileCopyrightText: 2022 Sveriges Television AB
//
// SPDX-License-Identifier: Apache-2.0

package se.svt.oss.gradle.yapp.artifact.doc

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension

class JavaDoc(val project: Project) {
    init {
        project.extensions.configure(JavaPluginExtension::class.java) { java ->
            java.withJavadocJar()
        }
    }
    /*  init {
          archiveClassifier.set("javadoc")

          val javadocTask = project.tasks.getByName("javadoc") as Javadoc
          dependsOn(javadocTask)
          from(javadocTask)
      }*/
}
