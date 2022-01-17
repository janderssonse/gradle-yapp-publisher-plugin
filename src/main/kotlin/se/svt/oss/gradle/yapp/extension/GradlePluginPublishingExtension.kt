package se.svt.oss.gradle.yapp.extension

import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

open class GradlePluginPublishingExtension @Inject constructor(project: Project, objects: ObjectFactory) :
    PropertyHandler(project, objects, "yapp.gradleplugin.", "YAPP_GRADLEPLUGIN_") {

    // TO-DO this be generalized as name
    @ExtensionProperty(
        name = "id",
        description = "The id for the plugin, usually groupId:plugin-name"
    )
    var id: Property<String> = propertyString("id")

    // TO-DO this can generalized
    @ExtensionProperty(
        name = "web",
        description = "Url to the projects web site",
        example = "https://my.web.com/project"
    )
    var webSite: Property<String> = propertyString("web")

    // this can also be generalized
    @ExtensionProperty(
        name = "vcs",
        description = "Url to the projects repository",
        example = "https://github.com/johndoe/GradlePlugins"
    )
    var vcsUrl: Property<String> = propertyString("vcs")

    @ExtensionProperty(
        name = "tags",
        description = "One or more tags to describe the categories the plugin covers",
        example = "publishing,ossrh"
    )
    var tags: ListProperty<String> = propertyList("tags", toStringList)

    @ExtensionProperty(
        name = "class",
        description = "The main class for the extension",
        example = "org.best.plugin.SimplePlugin"
    )
    var implementationClass: Property<String> = propertyString("class")

    @ExtensionProperty(
        name = "description",
        description = "A human readable description describing the intentions of the plugin",
        example = "This is a plugin for doing X, but not Y!"
    )
    var description: Property<String> = propertyString("description")

    @ExtensionProperty(
        name = "displayname",
        description = "The overall general description of the plugin for Gradle Portal",
        example = "The Just Do-It plugin!"
    )
    var displayName: Property<String> =
        propertyString("displayname")

    // TO-DO this is really the same as token so can be generliasdd
    @ExtensionProperty(name = "key")
    var key: Property<String> =
        propertyString("key")

    // TO-Do this is really the same as password (for the token) so can be generalized
    @ExtensionProperty(name = "keySecret", secret = true)
    var keySecret: Property<String> =
        propertyString("keySecret")
}
