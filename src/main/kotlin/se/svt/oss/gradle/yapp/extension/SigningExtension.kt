package se.svt.oss.gradle.yapp.extension

import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

open class SigningExtension @Inject constructor(project: Project, objects: ObjectFactory) :
    PropertyHandler(project, objects, "yapp.signing.", "YAPP_SIGNING_") {

    var enabled: Property<Boolean> = propertyBool("enabled")
    var signSnapshot: Property<Boolean> = propertyBool("signSnapshot")
    var keyId: Property<String> = propertyString("keyId")
    var keySecret: Property<String> = propertyString("keySecret")
    var key: Property<String> = propertyString("key")
}
