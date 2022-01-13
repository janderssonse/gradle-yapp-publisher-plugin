package se.svt.oss.gradle.yapp.extension

import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

open class GitLabExtension @Inject constructor(project: Project, objects: ObjectFactory) :
    MavenPublishingExtension(project, objects, "yapp.gitlab.", "YAPP_GITLAB_") {

    @ExtensionProperty(name = "host")
    var host: Property<String> = propertyString("host")
    @ExtensionProperty(name = "tokenType")
    var tokenType: Property<String> = propertyString("tokenType")
    @ExtensionProperty(name = "endpointLevel")
    var endpointLevel: Property<String> = propertyString("endpointLevel")
    @ExtensionProperty(name = "glGroupId")
    var gitlabGroupId: Property<String> = propertyString("glGroupId")
    @ExtensionProperty(name = "glProjectId")
    var gitlabProjectId: Property<String> = propertyString("glProjectId")
}
