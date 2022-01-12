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

    var mavenCentralLegacyUrl: Property<Boolean> = propertyBool("mavenCentralLegacyUrl")

    open var artifactId: Property<String> = propertyString("artifactId")
    open var groupId: Property<String> = propertyString("groupId")
    open var version: Property<String> = propertyString("version")
    var name: Property<String> = propertyString("name")

    var description: Property<String> = propertyString("description")
    var url: Property<String> = propertyString("url")
    var inceptionYear: Property<String> = propertyString("inceptionYear")

    var licenseName: Property<String> = propertyString("licenseName")
    var licenseUrl: Property<String> = propertyString("licenseUrl")
    var licenseDistribution: Property<String> =
        propertyString("licenseDistribution")
    var licenseComments: Property<String> = propertyString("licenseComments")

    var developers = listProperty("developer", Developer.stringListToDev)

    var organization: Property<String> = propertyString("organization")
    var organizationUrl: Property<String> = propertyString("organizationUrl")

    var scmUrl: Property<String> = propertyString("scmUrl")

    var scmConnection: Property<String> = propertyString("scmConnection")
    var scmDeveloperConnection: Property<String> = propertyString("scmDeveloperConnection")

    open var user: Property<String> = propertyString("user")
    open var password: Property<String> = propertyString("password")
    open var token: Property<String> = propertyString("token")

    open var directReleaseToMavenCentral: Property<Boolean> = propertyBool("directReleaseToMavenCentral")
}

data class Developer(val id: String, val name: String, val email: String) {

    companion object {
        val stringListToDev: (List<List<String>>) -> List<Developer> =
            { list ->
                list.map {

                    Developer(
                        it.getOrElse(0, { "a" }),
                        it.getOrElse(1, { "b" }),
                        it.getOrElse(2, { "c" })
                    )
                }
            }
    }
}
