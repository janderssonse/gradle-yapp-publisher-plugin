package se.svt.oss.gradle.yapp.artifact.doc.dokka

import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider

open class DokkaHtml :

    DokkaDoc() {

    init {
        archiveClassifier.set("html-doc")
        val dokkaHtml = project.tasks.getByName("dokkaHtml")
        dependsOn(dokkaHtml)
        from(dokkaHtml)
    }
    internal companion object {
        internal fun doc(task: String, project: Project): TaskProvider<*> {
            return project.tasks.register("${task}Gen", DokkaHtml::class.java)
        }
    }
}
