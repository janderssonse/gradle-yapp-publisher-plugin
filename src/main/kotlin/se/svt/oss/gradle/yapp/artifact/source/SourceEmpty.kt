// SPDX-FileCopyrightText: 2022 Sveriges Television AB
//
// SPDX-License-Identifier: Apache-2.0

package se.svt.oss.gradle.yapp.artifact.source

import org.gradle.jvm.tasks.Jar

open class SourceEmpty : Jar() {
    init {
        archiveClassifier.set("sources")
    }
}
