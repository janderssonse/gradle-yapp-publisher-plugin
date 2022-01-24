// SPDX-FileCopyrightText: 2021 Sveriges Television AB
//
// SPDX-License-Identifier: Apache-2.0
package se.svt.oss.gradle.yapp.extension

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import se.svt.oss.gradle.yapp.extension.DefaultProperties.Companion.DEFAULT_WITH_DOC_ARTIFACT
import se.svt.oss.gradle.yapp.extension.DefaultProperties.Companion.DEFAULT_WITH_SOURCE_ARTIFACT
import javax.inject.Inject

open class YappPublisherExtension @Inject constructor(project: Project, objects: ObjectFactory) :
    PropertyHandler(project, objects, "yapp.", "YAPP_") {

    val mavenPublishing =
        objects.newInstance(MavenPublishingExtension::class.java, "yapp.mavenPublishing.", "YAPP_MAVENPUBLISHING_")
    val gitLab = objects.newInstance(GitLabExtension::class.java)
    val gitHub = objects.newInstance(GitHubExtension::class.java)
    val signing = objects.newInstance(SigningExtension::class.java)
    val gradlePortalPublishing = objects.newInstance(GradlePluginPublishingExtension::class.java)
    val artifactoryPublishing = objects.newInstance(ArtifactoryExtension::class.java)

    fun mavenPublishing(action: Action<in MavenPublishingExtension>) = action.execute(mavenPublishing)
    fun gitLab(action: Action<in GitLabExtension>) = action.execute(gitLab)
    fun gitHub(action: Action<in GitHubExtension>) = action.execute(gitHub)
    fun signing(action: Action<in SigningExtension>) = action.execute(signing)
    fun gradlePortalPublishing(action: Action<in GradlePluginPublishingExtension>) =
        action.execute(gradlePortalPublishing)

    fun artifactoryPublishing(action: Action<in ArtifactoryExtension>) = action.execute(artifactoryPublishing)

    @ExtensionProperty(
        name = "withSourceArtifact",
        description = "Generate a source code artifact",
        defaultValue = DEFAULT_WITH_SOURCE_ARTIFACT.toString()
    )
    var withSourceArtifact: Property<Boolean> = propertyBool("withSourceArtifact", DEFAULT_WITH_SOURCE_ARTIFACT)

    @ExtensionProperty(
        name = "withDocArtifact",
        description = "Generate a doc artifact",
        defaultValue = DEFAULT_WITH_DOC_ARTIFACT.toString()
    )
    var withDocArtifact: Property<Boolean> = propertyBool("withDocArtifact", DEFAULT_WITH_DOC_ARTIFACT)

    @ExtensionProperty(
        name = "emptySourceArtifact",
        description = "Generate an empty source artifact (overrides withSourceArtifact)"
    )
    var emptySourceArtifact: Property<Boolean> = propertyBool("emptySourceArtifact")

    @ExtensionProperty(
        name = "emptyDocArtifact",
        description = "Generates an empty doc artifact (overrides withDocArtifact)"
    )
    var emptyDocArtifact: Property<Boolean> = propertyBool("emptyDocArtifact")

    @ExtensionProperty(
        name = "dokkaPublishings",
        description = "Generates doc artifacts using dokka. Only viable for Kotlin based projects",
        example = "javadoc", "html"
    )
    var dokkaPublishings: ListProperty<String> = propertyList("dokkaPublishings", toStringList)

    @ExtensionProperty(
        name = "targets",
        description = "List of targets to publish to",
        example = "maven_central, github"
    )
    var targets: ListProperty<String> = propertyList("targets", toStringList)
}
