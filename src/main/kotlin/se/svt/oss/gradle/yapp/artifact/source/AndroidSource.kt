package se.svt.oss.gradle.yapp.artifact.source

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar

open class AndroidSource : Jar() {
    init {

        archiveClassifier.set("sources")
    }

    internal companion object {

        internal fun source(task: String, project: Project): TaskProvider<*> {

            return project.tasks.register("${task}Gen", AndroidSource::class.java) {
                val androidExtension = project.extensions.getByType(LibraryExtension::class.java)

                it.from(androidExtension.sourceSets.getByName("main").java.srcDirs)
            }
        }
    }
}
