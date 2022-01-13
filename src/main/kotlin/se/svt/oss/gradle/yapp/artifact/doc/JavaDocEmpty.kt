package se.svt.oss.gradle.yapp.artifact.doc

import org.gradle.jvm.tasks.Jar

open class JavaDocEmpty : Jar() {
    init {
        archiveClassifier.set("javadoc")
    }
}
