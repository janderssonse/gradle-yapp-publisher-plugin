// SPDX-FileCopyrightText: 2021 Sveriges Television AB
//
// SPDX-License-Identifier: Apache-2.0

package se.svt.oss.gradle.yapp.artifact.source

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension

class Source(val project: Project) {
    init {
        project.extensions.configure(JavaPluginExtension::class.java) { java ->
            java.withSourcesJar()
        }
    }

    /* init {
         archiveClassifier.set("sources")

         val javaPlugin = project.extensions.getByType(JavaPluginExtension::class.java)
         from(javaPlugin.sourceSets.getByName("main").allSource)
     }*/
}
