package se.svt.oss.gradle.yapp.extension

import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

/**
 * Note: This started as a configuration of the artifactory gradle plugin, and turned to using the rest api
 * facilites. Commented properties are things that might be implemented in the feature.
 */
open class ArtifactoryExtension @Inject constructor(project: Project, objects: ObjectFactory) :
    MavenPublishingExtension(project, objects, "yapp.artifactory.", "YAPP_ARTIFACTORY_") {

    @ExtensionProperty(
        name = "host",
        description = "The Artifactory Host domain url",
        example = "https://yapp.jfrog.io/artifactory"
    )
    var host: Property<String> = propertyString("host")

    @ExtensionProperty(
        name = "repoKey",
        description = "The artifactory repository key",
        example = "default-gradle-dev-local"
    )
    var repoKey: Property<String> = propertyString("repoKey")

    @ExtensionProperty(
        name = "publications",
        description = "The publications to do, default publishing target type name for now, should be a list in the future",
        example = "mypublication"
    )
    var publications: Property<String> = propertyString("publications", "ARTIFACTORY")

   /* @ExtensionProperty(name = "publishArtifacts") // do we need this?, we have publish to local repo already
    var publishArtifacts: Property<Boolean> = propertyBool("publishArtifacts", true)

    @ExtensionProperty(
        name = "publishPom",
    )
    var publishPom: Property<Boolean> = propertyBool("publishPom", true)
*/
    @ExtensionProperty(
        name = "properties",
        description = "Optional map of properties to attach to all published artifacts"
    )
    var properties: MapProperty<String, String> = propertyMap("properties", { list -> list.associateWith { it } })

 /*   @ExtensionProperty(name = "includeEnvVars")
    var includeEnvVars: Property<Boolean> = propertyBool("includeEnvVars")

    @ExtensionProperty(
        name = "envVarsExcludePatterns",
        description = "The artifactory repository key"
    )
    var envVarsExcludePatterns: Property<String> = propertyString("envVarsExcludePatterns")

    @ExtensionProperty(
        name = "envVarsIncludePatterns"
    )
    var envVarsIncludePatterns: Property<String> = propertyString("envVarsIncludePatterns")

    @ExtensionProperty(
        name = "environmentProperty"
    )
    var environmentProperty: Property<String> = propertyString("environmentProperty")

    @ExtensionProperty(
        name = "buildName"
    )
    var buildName: Property<String> = propertyString("buildName")

    @ExtensionProperty(
        name = "buildNumber"
    )
    var buildNumber: Property<String> = propertyString("buildNumber")

    @ExtensionProperty(
        name = "timeout",
        description = "The artifactory repository key"
    )
    var timeout: Property<Int> = propertyInt("timeout", 300)

  */
}
