package se.svt.oss.gradle.yapp.extension

import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

open class GitLabExtension @Inject constructor(project: Project, objects: ObjectFactory) :
    MavenPublishingExtension(project, objects, "yapp.gitlab.", "YAPP_GITLAB_") {

    var host: Property<String> = propertyString("host")
    var tokenType: Property<String> = propertyString("tokenType")
    var endpointLevel: Property<String> = propertyString("endpointLevel")
    var gitlabGroupId: Property<String> = propertyString("glGroupId")
    var gitlabProjectId: Property<String> = propertyString("glProjectId")
}
