package se.svt.oss.gradle.yapp.artifact.source

import org.gradle.jvm.tasks.Jar

open class SourceEmpty : Jar() {
    init {
        archiveClassifier.set("sources")
    }
}
