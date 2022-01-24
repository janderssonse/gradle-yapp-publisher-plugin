package se.svt.oss.gradle.yapp.plugin

import com.gradle.publish.PluginBundleExtension
import com.gradle.publish.PublishPlugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.internal.publication.MavenPublicationInternal
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension
import org.gradle.plugin.devel.plugins.JavaGradlePluginPlugin
import org.gradle.plugin.use.resolve.internal.ArtifactRepositoriesPluginResolver

internal class GradlePortalPublishingPlugin(project: Project) : BasePlugin(project) {

    fun configure() {
        val extensions = project.extensions
        val ext = yappExtension()

        project.plugins.apply(MavenPublishPlugin::class.java)
        project.plugins.apply(JavaGradlePluginPlugin::class.java)
        project.plugins.apply(PublishPlugin::class.java)

        project.afterEvaluate {
            extensions.configure(GradlePluginDevelopmentExtension::class.java) {
                it.plugins.create(project.name) { pd ->
                    with(pd) {
                        description = ext.gradlePortalPublishing.description.get() ?: project.description
                        displayName = ext.gradlePortalPublishing.displayName.get()
                        id = ext.gradlePortalPublishing.id.get() ?: "${project.group}.${project.name}"
                        implementationClass = ext.gradlePortalPublishing.implementationClass.get()
                    }
                }

                project.extensions.extraProperties.set(
                    "gradle.publish.key",
                    ext.gradlePortalPublishing.key.get()
                )
                project.extensions.extraProperties.set(
                    "gradle.publish.secret",
                    ext.gradlePortalPublishing.keySecret.get()
                )
            }

            project.extensions.configure(PublishingExtension::class.java) { p ->
                for (declaration in extensions.getByType(GradlePluginDevelopmentExtension::class.java).plugins) {
                    val coordinates: MavenPublication = p.publications.getByName("pluginMaven")
                        as MavenPublication
                    val pluginId = declaration.id
                    val pluginGroupId = coordinates.groupId
                    val pluginArtifactId = project.rootProject.name

                    println("$pluginGroupId $pluginId $pluginArtifactId")
                    val pluginVersion = coordinates.version
                    val publication = p.publications.create(
                        declaration.name + "PluginMarkerMaven",
                        MavenPublication::class.java
                    ) as MavenPublicationInternal
                    publication.isAlias = true
                    publication.artifactId = pluginId + ArtifactRepositoriesPluginResolver.PLUGIN_MARKER_SUFFIX
                    publication.groupId = pluginId
                    println("${publication.artifactId} ${declaration.name}${declaration.displayName}")
                    publication.pom.withXml { xmlProvider ->
                        val root = xmlProvider.asElement()
                        val document = root.ownerDocument
                        val dependencies = root.appendChild(document.createElement("dependencies"))
                        val dependency = dependencies.appendChild(document.createElement("dependency"))
                        val groupId = dependency.appendChild(document.createElement("groupId"))
                        groupId.textContent = pluginGroupId
                        val artifactId = dependency.appendChild(document.createElement("artifactId"))
                        artifactId.textContent = pluginArtifactId
                        val version = dependency.appendChild(document.createElement("version"))
                        version.textContent = pluginVersion
                    }
                    publication.pom.name.set(declaration.displayName)
                    publication.pom.description.set(declaration.description)
                }
            }
        }
        project.extensions.configure(PluginBundleExtension::class.java) {
            with(it) {
                website = ext.gradlePortalPublishing.webSite.get()
                vcsUrl = ext.gradlePortalPublishing.vcsUrl.get()
                tags = ext.gradlePortalPublishing.tags.get()
            }
        }
    }
}
