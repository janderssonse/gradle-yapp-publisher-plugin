package se.svt.oss.gradle.yapp.extension

import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

open class SigningExtension @Inject constructor(project: Project, objects: ObjectFactory) :
    PropertyHandler(project, objects, "yapp.signing.", "YAPP_SIGNING_") {

    @ExtensionProperty(name = "enabled")
    var enabled: Property<Boolean> = propertyBool("enabled")
    @ExtensionProperty(name = "signSnapshot")
    var signSnapshot: Property<Boolean> = propertyBool("signSnapshot")
    @ExtensionProperty(name = "keyId")
    var keyId: Property<String> = propertyString("keyId")
    @ExtensionProperty(name = "keySecret", secret = true)
    var keySecret: Property<String> = propertyString("keySecret")
    @ExtensionProperty(name = "key")
    var key: Property<String> = propertyString("key")
}
