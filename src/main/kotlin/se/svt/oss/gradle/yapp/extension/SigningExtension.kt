// SPDX-FileCopyrightText: 2022 Sveriges Television AB
//
// SPDX-License-Identifier: Apache-2.0

package se.svt.oss.gradle.yapp.extension

import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

open class SigningExtension @Inject constructor(project: Project, objects: ObjectFactory) :
    PropertyHandler(project, objects, "yapp.signing.", "YAPP_SIGNING_") {

    @ExtensionProperty(
        name = "enabled",
        description = "Enable signing of artifacts",
        example = "true"
    )
    var enabled: Property<Boolean> = propertyBool("enabled")

    @ExtensionProperty(
        name = "signSnapshot",
        description = "Sign snapshots",
        example = "true"
    )
    var signSnapshot: Property<Boolean> = propertyBool("signSnapshot")

    @ExtensionProperty(
        name = "keyId",
        description = "The public keyId, last 8 digits",
        example = "abc12345"
    )
    var keyId: Property<String> = propertyString("keyId")

    @ExtensionProperty(
        name = "keySecret",
        description = "the password for the key",
        secret = true
    )
    var keySecret: Property<String> = propertyString("keySecret")

    @ExtensionProperty(
        name = "key",
        description = "The signing GPG key, absolute path to or in text format",
        example = "/path/to/gpgkey OR in pure textformat"
    )
    var key: Property<String> = propertyString("key")
}
