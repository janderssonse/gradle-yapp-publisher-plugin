// SPDX-FileCopyrightText: 2021 Sveriges Television AB
//
// SPDX-License-Identifier: Apache-2.0
package se.svt.oss.gradle.yapp.extension

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

open class YappPublisherExtension @Inject constructor(project: Project, objects: ObjectFactory) :
    PropertyHandler(project, objects, "yapp.", "YAPP_") {

    val mavenPublishing =
        objects.newInstance(MavenPublishingExtension::class.java, "yapp.mavenPublishing.", "YAPP_MAVENPUBLISHING_")
    val gitLab = objects.newInstance(GitLabExtension::class.java)
    val gitHub = objects.newInstance(GitHubExtension::class.java)
    val signing = objects.newInstance(SigningExtension::class.java)
    val gradlePortalPublishing = objects.newInstance(GradlePluginPublishingExtension::class.java)

    fun mavenPublishing(action: Action<in MavenPublishingExtension>) = action.execute(mavenPublishing)
    fun gitLab(action: Action<in GitLabExtension>) = action.execute(gitLab)
    fun gitHub(action: Action<in GitHubExtension>) = action.execute(gitHub)
    fun signing(action: Action<in SigningExtension>) = action.execute(signing)
    fun gradlePortalPublishing(action: Action<in GradlePluginPublishingExtension>) =
        action.execute(gradlePortalPublishing)

    @ExtensionProperty(name = "withSourceArtifact")
    var withSourceArtifact: Property<Boolean> = propertyBool("withSourceArtifact", true)

    @ExtensionProperty(name = "withDocArtifact")
    var withDocArtifact: Property<Boolean> = propertyBool("withDocArtifact", true)

    @ExtensionProperty(name = "emptySourceArtifact")
    var emptySourceArtifact: Property<Boolean> = propertyBool("emptySourceArtifact")

    @ExtensionProperty(name = "emptyDocArtifact")
    var emptyDocArtifact: Property<Boolean> = propertyBool("emptyDocArtifact")

    @ExtensionProperty(name = "dokkaPublishings")
    var dokkaPublishings: ListProperty<String> = propertyList("dokkaPublishings", toStringList)

    @ExtensionProperty(name = "targets")
    var targets: ListProperty<String> = propertyList("targets", toStringList)
}
