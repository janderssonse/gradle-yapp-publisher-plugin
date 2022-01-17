package se.svt.oss.gradle.yapp.extension

import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

open class MavenPublishingExtension @Inject constructor(
    project: Project,
    objects: ObjectFactory,
    propPrefix: String = "yapp.mavenPublishing.",
    envPrefix: String = "YAPP_MAVENPUBLISHING_"
) :
    PropertyHandler(project, objects, propPrefix, envPrefix) {

    @ExtensionProperty(name = "mavenCentralLegacyUrl")
    var mavenCentralLegacyUrl: Property<Boolean> = propertyBool("mavenCentralLegacyUrl")

    @ExtensionProperty(name = "artifactId")
    open var artifactId: Property<String> = propertyString("artifactId")
    @ExtensionProperty(name = "groupId")
    open var groupId: Property<String> = propertyString("groupId")
    @ExtensionProperty(name = "version")
    open var version: Property<String> = propertyString("version")
    @ExtensionProperty(name = "name")
    var name: Property<String> = propertyString("name")

    @ExtensionProperty(name = "description")
    var description: Property<String> = propertyString("description")
    @ExtensionProperty(name = "url")
    var url: Property<String> = propertyString("url")
    @ExtensionProperty(name = "inceptionYear")
    var inceptionYear: Property<String> = propertyString("inceptionYear")

    @ExtensionProperty(name = "licenseName")
    var licenseName: Property<String> = propertyString("licenseName")
    @ExtensionProperty(name = "licenseUrl")
    var licenseUrl: Property<String> = propertyString("licenseUrl")
    @ExtensionProperty(name = "licenseDistribution")
    var licenseDistribution: Property<String> = propertyString("licenseDistribution")
    @ExtensionProperty(name = "licenseComments")
    var licenseComments: Property<String> = propertyString("licenseComments")

    @ExtensionProperty(name = "developer")
    @ExtensionPropertyExtractor(extractor = ExtensionPropertyDeveloperExtractor::class)
    var developers = propertyList("developer", Developer.toDevelopers)

    @ExtensionProperty(name = "organization")
    var organization: Property<String> = propertyString("organization")
    @ExtensionProperty(name = "organizationUrl")
    var organizationUrl: Property<String> = propertyString("organizationUrl")

    @ExtensionProperty(name = "scmUrl")
    var scmUrl: Property<String> = propertyString("scmUrl")

    @ExtensionProperty(name = "scmConnection")
    var scmConnection: Property<String> = propertyString("scmConnection")
    @ExtensionProperty(name = "scmDeveloperConnection")
    var scmDeveloperConnection: Property<String> = propertyString("scmDeveloperConnection")

    @ExtensionProperty(name = "user")
    open var user: Property<String> = propertyString("user")
    @ExtensionProperty(name = "password", secret = true)
    open var password: Property<String> = propertyString("password")
    @ExtensionProperty(name = "token", secret = true)
    open var token: Property<String> = propertyString("token")

    @ExtensionProperty(name = "directReleaseToMavenCentral")
    open var directReleaseToMavenCentral: Property<Boolean> = propertyBool("directReleaseToMavenCentral")
}

data class Developer(val id: String, val name: String, val email: String) {

    companion object {
        val toDevelopers: (List<List<String>>) -> List<Developer> =
            { list ->
                list.filter { it.size == 3 }
                    .map {
                        Developer(it[0], it[1], it[2])
                    }
            }
    }
}
