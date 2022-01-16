package se.svt.oss.gradle.yapp.plugin

import org.gradle.api.Project
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.jfrog.gradle.plugin.artifactory.ArtifactoryPlugin
import org.jfrog.gradle.plugin.artifactory.dsl.ArtifactoryPluginConvention
import se.svt.oss.gradle.yapp.publishingtarget.PublishingTargetType

internal class Artifactory(
    project: Project,
    val publishingTargetType: PublishingTargetType
) : BasePlugin(project) {

    fun configure() {
        val ext = yappExtension()

        project.project.plugins.apply(MavenPublishPlugin::class.java)
        project.project.plugins.apply(ArtifactoryPlugin::class.java).apply(project)

        project.afterEvaluate {

            val a = project.project.convention.findPlugin(
                ArtifactoryPluginConvention::class.java
            )
            a!!.apply {
                setContextUrl(yappExtension().mavenPublishing.url)
                publish { publishConfig ->
                    // publishConfig.setContextUrl("http://repo.myorg.com/artifactory")   //The base Artifactory URL for the publisher//TO-DO when allowing config publications
                    publishConfig.repository { repository ->
                        repository.setRepoKey(ext.artifactoryPublishing.repoKey)
                        repository.setUsername(ext.mavenPublishing.user)
                        repository.setPassword(ext.mavenPublishing.password)
                    }
                    publishConfig.defaults { task ->

                        // List of Gradle Publications (names or objects) from which to collect the list of artifacts to be deployed to Artifactory.
                        // If you'd like to deploy the artifacts from all the publications defined in the Gradle script, you can set the 'ALL_PUBLICATIONS' string, instead of the publication names.
                        task.publications(publishingTargetType.name) // to-do makes this configurable //to-do makes this configurable //to-do makes this configurable //to-do makes this configurable //to-do makes this configurable //to-do makes this configurable //to-do makes this configurable
                        ext.artifactoryPublishing.properties.get().entries.forEach {
                            task.properties.put(it.key, it.value)
                        }
                        // publishBuildInfo = true   //Publish build-info to Artifactory (true by default)
                        task.setPublishArtifacts(ext.artifactoryPublishing.publishArtifacts.get()) // Publish artifacts to Artifactory (true by default)
                        task.setPublishPom(ext.artifactoryPublishing.publishPom.get()) // Publish generated POM files to Artifactory (true by default).
                    }
                }
                clientConfig.apply {
                    isIncludeEnvVars = ext.artifactoryPublishing.includeEnvVars.get()
                    envVarsExcludePatterns = ext.artifactoryPublishing.envVarsExcludePatterns.get()
                    envVarsIncludePatterns = ext.artifactoryPublishing.envVarsIncludePatterns.get()
                    info.buildName = ext.artifactoryPublishing.buildName.get()
                    info.buildNumber = ext.artifactoryPublishing.buildNumber.get()
                    timeout = 300
                }
            }
        }
    }
}
