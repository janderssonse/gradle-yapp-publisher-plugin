package se.svt.oss.gradle.yapp.extension

import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

open class ArtifactoryExtension @Inject constructor(project: Project, objects: ObjectFactory) :
    MavenPublishingExtension(project, objects, "yapp.artifactory.", "YAPP_ARTIFACTORY_") {

    @ExtensionProperty(
        name = "repoKey",
        description = "The artifactory repository key"
    )
    var repoKey: Property<String> = propertyString("repoKey")

    @ExtensionProperty(name = "publishArtifacts") // do we need this?, we have publish to local repo already
    var publishArtifacts: Property<Boolean> = propertyBool("publishArtifacts", true)

    @ExtensionProperty(
        name = "publishPom",
    )
    var publishPom: Property<Boolean> = propertyBool("publishPom", true)

   /* @ExtensionProperty( //TO-DO: fix map support in the annotation parser
        name = "properties",
        description = "Optional map of properties to attach to all published artifacts"
    )*/
    var properties: MapProperty<String, String> = propertyMap("properties", { list -> list.associateWith { it } })

    @ExtensionProperty(name = "includeEnvVars")
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

   /* @ExtensionProperty( //TO-DO fix int support annotation parser
        name = "timeout",
        description = "The artifactory repository key"
    )*/
    var timeout: Property<Int> = propertyInt("timeout", 300)
}
