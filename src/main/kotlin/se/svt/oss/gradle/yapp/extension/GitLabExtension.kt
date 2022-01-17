package se.svt.oss.gradle.yapp.extension

import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

open class GitLabExtension @Inject constructor(project: Project, objects: ObjectFactory) :
    MavenPublishingExtension(project, objects, "yapp.gitlab.", "YAPP_GITLAB_") {

    @ExtensionProperty(
        name = "host",
        description = "The GitLab Host domain name",
        example = "https://gitlab.com"
    )
    var host: Property<String> = propertyString("host")

    @ExtensionProperty(
        name = "tokenType",
        description = "The GitLab token type (Project-Token or Deploy-Token)",
        example = "Project-Type"
    )
    var tokenType: Property<String> = propertyString("tokenType")

    @ExtensionProperty(
        name = "endpointLevel",
        description = "Publish to project or group (valid: project or group",
        example = "project"
    )
    var endpointLevel: Property<String> = propertyString("endpointLevel", "project")

    @ExtensionProperty(
        name = "glGroupId",
        description = "The GitLab Group id, see your projects home page ",
        example = "23423535235" // TO-Do is this always numeric?
    )
    var gitlabGroupId: Property<String> = propertyString("glGroupId")

    @ExtensionProperty(
        name = "glProjectId",
        description = "The GitLab ProjectId, see your projects homepage",
        example = "23432432" // TO-DO is this alwyas numeric?
    )
    var gitlabProjectId: Property<String> = propertyString("glProjectId")
}
