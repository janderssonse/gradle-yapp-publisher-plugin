package se.svt.oss.gradle.yapp.artifact

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.TaskProvider
import se.svt.oss.gradle.yapp.artifact.doc.JavaDoc
import se.svt.oss.gradle.yapp.artifact.doc.JavaDocEmpty
import se.svt.oss.gradle.yapp.artifact.doc.dokka.DokkaHtml
import se.svt.oss.gradle.yapp.artifact.doc.dokka.DokkaJavaDoc
import se.svt.oss.gradle.yapp.artifact.source.AndroidSource
import se.svt.oss.gradle.yapp.artifact.source.Source
import se.svt.oss.gradle.yapp.artifact.source.SourceEmpty
import se.svt.oss.gradle.yapp.publishingtarget.PublishingTargetType
import se.svt.oss.gradle.yapp.yappExtension

data class ArtifactConfigure(
    val project: Project,
    val publishTarget: PublishingTargetType
) {

    fun configure() {
        configureDocumentationArtifact()
        configureSourceArtifact()
    }

    private fun configureDocumentationArtifact() {

        when {
            project.yappExtension().emptyDocArtifact.get() -> addEmptyDocArtifact()
            project.yappExtension().withDocArtifact.get() -> addDocArtifact()
        }
    }

    private fun configureSourceArtifact() {
        when {
            project.yappExtension().emptySourceArtifact.get() -> addEmptySourceArtifact()
            project.yappExtension().withDocArtifact.get() -> addSourceArtifact()
        }
    }

    private fun addEmptyDocArtifact() {
        val emptyDoc = project.tasks.register("javadocEmpty", JavaDocEmpty::class.java)
        addArtifact(emptyDoc)
    }

    private fun addDocArtifact() {
        if (project.plugins.hasPlugin("org.jetbrains.dokka")) {
            if (project.yappExtension().dokkaPublishings.get().contains("javadoc"))
                addArtifact(DokkaJavaDoc.doc("dokkaJavadocGen", project))
            if (project.yappExtension().dokkaPublishings.get().contains("html"))
                addArtifact(DokkaHtml.doc("dokkaHtmlGen", project))
            if (project.yappExtension().dokkaPublishings.get().contains("gfm"))
                addArtifact(DokkaHtml.doc("dokkaGfmGen", project))
            if (project.yappExtension().dokkaPublishings.get().contains("jekyll")) println()
            addArtifact(DokkaHtml.doc("dokkaJekyllGen", project))
        } else {
            JavaDoc(project)
        }
    }

    private fun addEmptySourceArtifact() {
        val emptySource = project.tasks.register("sourcesEmpty", SourceEmpty::class.java)
        addArtifact(emptySource)
    }

    private fun addSourceArtifact() {
        when {
            project.plugins.hasPlugin("com.android.library") -> {
                project.afterEvaluate {
                    addArtifact(AndroidSource.source("androidSourcesGen", project))
                }
            }
            else -> Source(project)
        }
    }

    private fun addArtifact(provider: TaskProvider<*>) {
        project.extensions.configure(PublishingExtension::class.java) { pe ->

            pe.publications { publications ->
                publications.forEach { project.logger.warn("PUBLICATIONS " + it.name) }

                try {
                    val p = publications.getByName(publishTarget.name) as MavenPublication
                    // p.from(project.components.getByName("java"))
                    p.artifact(provider)
                } catch (e: Exception) {

                    publications.forEach { project.logger.warn("ERRPUBLICATIONS " + it.name) }
                }
            }
        }
    }
}

/*if (project.plugins.hasPlugin("com.android.library")) {
project.afterEvaluate {
    addArtifact(AndroidDoc.doc("androidDocGen", project))
}

} else */
/*
project.extensions.configure(PublishingExtension::class.java) { pe ->

    pe.publications { publications ->
        val p = publications.getByName(publishTarget.name) as MavenPublication
        p.from(project.components.getByName("java"))

        val source = project.tasks.findByPath(sources)
        val docs = project.tasks.findByPath(javaDoc)

        when (docs) {
            null -> {
                val d = project.tasks.register(javaDoc, JavaDoc::class.java)
                p.artifact(d)
            }
        }
        when (source) {
            null -> {
                val s = project.tasks.register(sources, Sources::class.java)
                p.artifact(s)
            }
        }
           */
