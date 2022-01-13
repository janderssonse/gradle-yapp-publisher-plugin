package se.svt.oss.gradle.yapp.extension

import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

open class GitHubExtension @Inject constructor(project: Project, objects: ObjectFactory) :
    MavenPublishingExtension(project, objects, "yapp.github.", "YAPP_GITHUB_") {
    @ExtensionProperty(name = "namespace")
    var namespace: Property<String> = propertyString("namespace")
    @ExtensionProperty(name = "reponame")
    var repoName: Property<String> = propertyString("reponame")
}
