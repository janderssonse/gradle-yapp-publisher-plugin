package se.svt.oss.gradle.yapp.extension

import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

open class GradlePluginPublishingExtension @Inject constructor(project: Project, objects: ObjectFactory) :
    PropertyHandler(project, objects, "yapp.gradleplugin.", "YAPP_GRADLEPLUGIN_") {

    @ExtensionProperty(name = "id")
    var id: Property<String> = propertyString("id")
    @ExtensionProperty(name = "web")
    var webSite: Property<String> = propertyString("web")
    @ExtensionProperty(name = "vcs")
    var vcsUrl: Property<String> = propertyString("vcs")
    @ExtensionProperty(name = "tags")
    var tags: ListProperty<String> = propertyList("tags", toStringList)
    @ExtensionProperty(name = "class")
    var implementationClass: Property<String> =
        propertyString("class")
    @ExtensionProperty(name = "description")
    var description: Property<String> =
        propertyString("description")
    @ExtensionProperty(name = "displayname")
    var displayName: Property<String> =
        propertyString("displayname")
    @ExtensionProperty(name = "key")
    var key: Property<String> =
        propertyString("key")
    @ExtensionProperty(name = "keySecret", secret = true)
    var keySecret: Property<String> =
        propertyString("keySecret")
}
