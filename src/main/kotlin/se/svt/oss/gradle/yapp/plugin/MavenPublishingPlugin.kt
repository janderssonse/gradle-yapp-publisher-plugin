package se.svt.oss.gradle.yapp.plugin

import io.github.gradlenexus.publishplugin.NexusPublishExtension
import io.github.gradlenexus.publishplugin.NexusPublishPlugin
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.api.credentials.HttpHeaderCredentials
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.authentication.http.HttpHeaderAuthentication
import se.svt.oss.gradle.yapp.extension.YappPublisherExtension
import se.svt.oss.gradle.yapp.publishingtarget.PublishingTargetType
import se.svt.oss.gradle.yapp.publishingtarget.RepositoryConfiguration

class MavenPublishingPlugin(project: Project) : BasePlugin(project) {
    fun configure(
        repositoryConf: RepositoryConfiguration,
        publishingTargetType: PublishingTargetType
    ) {

        val ext = yappExtension()
        println(ext.mavenPublishing.name.get())
        project.plugins.apply(MavenPublishPlugin::class.java)

        project.extensions.configure(PublishingExtension::class.java) { p ->

            p.publications { publications ->
                publications.register(publishingTargetType.name, MavenPublication::class.java) { publication ->
                    project.afterEvaluate { // These values does not seem to use the newer api, i.e Lazy properties,
                        // so we cant get rid of the afterEvalute

                        softwareCompontent(publication)
                        publication.groupId = ext.mavenPublishing.groupId.get().ifEmpty {
                            project.group.toString()
                        }
                        publication.artifactId = ext.mavenPublishing.artifactId.get().ifEmpty {
                            project.name.toString()
                        }
                        publication.version = ext.mavenPublishing.version.get().ifEmpty {
                            project.version.toString()
                        }

                        with(publication) {

                            pom.apply {
                                name.set(ext.mavenPublishing.name)
                                description.set(ext.mavenPublishing.description)
                                inceptionYear.set(ext.mavenPublishing.inceptionYear)
                                url.set(ext.mavenPublishing.url)

                                licenses { l ->
                                    l.license { license ->
                                        license.name.set(ext.mavenPublishing.licenseName)
                                        license.url.set(ext.mavenPublishing.licenseUrl)
                                        license.comments.set(ext.mavenPublishing.licenseComments)
                                        license.distribution.set(ext.mavenPublishing.licenseDistribution)
                                    }
                                }

                                println(ext.mavenPublishing.licenseComments)

                                ext.mavenPublishing.developers.get().forEach { devList ->
                                    developers { d ->

                                        d.developer { dev ->

                                            dev.apply {
                                                id.set(devList.id)
                                                name.set(devList.name)
                                                organization.set(devList.organization)
                                                organizationUrl.set(devList.organizationUrl)
                                                email.set(devList.email)
                                            }
                                        }
                                    }
                                }

                                scm { scm ->
                                    scm.url.set(ext.mavenPublishing.scmUrl)
                                    scm.connection.set(ext.mavenPublishing.scmConnection)
                                    scm.developerConnection.set(ext.mavenPublishing.scmDeveloperConnection)
                                }
                            }
                        }
                    }
                }

                p.repositories { repository ->

                    repository.maven { r ->
                        r.apply {
                            name = repositoryConf.name
                            url = repositoryConf.uri

                            if (publishingTargetType == PublishingTargetType.GITLAB) {
                                credentials(HttpHeaderCredentials::class.java) {
                                    it.name = repositoryConf.credential.name
                                    it.value = repositoryConf.credential.value
                                }
                                authentication {

                                    it.create("header", HttpHeaderAuthentication::class.java)
                                }
                            } else {
                                credentials(PasswordCredentials::class.java) {
                                    credentials.username = repositoryConf.credential.name
                                    credentials.password = repositoryConf.credential.value
                                }
                            }
                        }
                    }
                }
            }
        }

        directRelease(publishingTargetType, ext, repositoryConf)
    }

    private fun softwareCompontent(publication: MavenPublication) {
        when (val component = project.components.findByName("release")) {
            null -> publication.from(project.components.getByName("java"))
            else -> publication.from(component)
        }
    }

    private fun directRelease(
        publishTargetType: PublishingTargetType,
        ext: YappPublisherExtension,
        repositoryConf: RepositoryConfiguration
    ) {
        println(publishTargetType)
        if (publishTargetType == PublishingTargetType.MAVEN_CENTRAL && ext.mavenPublishing.directReleaseToMavenCentral.get()) {
            project.plugins.apply(NexusPublishPlugin::class.java)
            project.extensions.configure(NexusPublishExtension::class.java) { extension ->
                extension.repositories.forEach { println(it.name) }
                if (extension.repositories.isNullOrEmpty()) {
                    extension.repositories.sonatype { nexus ->
                        nexus.snapshotRepositoryUrl.set(repositoryConf.snapShotUri)
                        nexus.nexusUrl.set(repositoryConf.uri)
                        nexus.username.set(repositoryConf.credential.name)
                        nexus.password.set(repositoryConf.credential.value)
                    }
                    extension.repositories.forEach { println("$it.name") }
                }
            }
        }
    }
}
