package se.svt.oss.gradle.yapp.artifact.doc.dokka

import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider

open class DokkaGfm :
    DokkaDoc() {

    init {
        archiveClassifier.set("gfm-doc")
        val dokkaGfm = project.tasks.getByName("dokkaGfm")
        dependsOn(dokkaGfm)
        from(dokkaGfm)
    }

    internal companion object {
        internal fun doc(task: String, project: Project): TaskProvider<*> {
            return project.tasks.register("${task}Gen", DokkaGfm::class.java)
        }
    }
}
