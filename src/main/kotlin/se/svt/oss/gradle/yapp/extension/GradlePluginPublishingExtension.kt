package se.svt.oss.gradle.yapp.extension

import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

open class GradlePluginPublishingExtension @Inject constructor(project: Project, objects: ObjectFactory) :
    PropertyHandler(project, objects, "yapp.gradleplugin.", "YAPP_GRADLEPLUGIN_") {

    var id: Property<String> = propertyString("id")
    var webSite: Property<String> = propertyString("web")
    var vcsUrl: Property<String> = propertyString("vcs")
    var tags: ListProperty<String> = propertyList("tags", toStringList)
    var implementationClass: Property<String> =
        propertyString("class")
    var description: Property<String> =
        propertyString("description")
    var displayName: Property<String> =
        propertyString("displayname")

    var key: Property<String> =
        propertyString("key")
    var keySecret: Property<String> =
        propertyString("keySecret")
}
