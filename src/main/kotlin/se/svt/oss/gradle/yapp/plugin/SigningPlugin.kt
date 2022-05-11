// SPDX-FileCopyrightText: 2022 Sveriges Television AB
//
// SPDX-License-Identifier: Apache-2.0

package se.svt.oss.gradle.yapp.plugin

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.plugins.signing.SigningExtension
import org.gradle.plugins.signing.SigningPlugin
import se.svt.oss.gradle.yapp.isSnapShot
import java.io.File

class SigningPlugin(project: Project) : BasePlugin(project) {
    fun configure() {

        val extension = yappExtension()

        project.plugins.apply(SigningPlugin::class.java)

        project.afterEvaluate { // (the signing plugin old api with conventions, non lazy properties etc so have to do this)

            val isReleaseVersion = !project.isSnapShot()

            project.extensions.configure(SigningExtension::class.java) {

                if (extension.signing.enabled.get() && (isReleaseVersion || extension.signing.signSnapshot.get())) {

                    if (isValidFilePath(extension.signing.key.get())) {

                        project.extensions.extraProperties.set("signing.keyId", extension.signing.keyId.get())
                        project.extensions.extraProperties.set(
                            "signing.password",
                            extension.signing.keySecret.get()
                        )
                        project.extensions.extraProperties.set(
                            "signing.secretKeyRingFile",
                            extension.signing.key.get()
                        )
                        println("sign by file")
                        it.sign(project.extensions.findByType(PublishingExtension::class.java)?.publications)
                    } else {
                        println("sign by mem")
                        it.useInMemoryPgpKeys(
                            extension.signing.keyId.get(),
                            extension.signing.key.get(),
                            extension.signing.keySecret.get()
                        )
                        it.sign(project.extensions.findByType(PublishingExtension::class.java)?.publications)
                    }
                }
            }
        }
    }

    fun isValidFilePath(path: String): Boolean = try {
        File(path).canonicalPath
        true
    } catch (e: Exception) {
        false
    }
}
