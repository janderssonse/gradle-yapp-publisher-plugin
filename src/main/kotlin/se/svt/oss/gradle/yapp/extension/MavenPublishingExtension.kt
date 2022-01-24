package se.svt.oss.gradle.yapp.extension

import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import se.svt.oss.gradle.yapp.validation.StringFormatValidator
import javax.inject.Inject

open class MavenPublishingExtension @Inject constructor(
    project: Project,
    objects: ObjectFactory,
    propPrefix: String,
    envPrefix: String
) :
    PropertyHandler(project, objects, propPrefix, envPrefix) {

    @ExtensionProperty(
        name = "mavenCentralLegacyUrl",
        description = "When publishing to Maven Central, should the legacy url (before 2021) be used?",
        example = "false"
    )
    var mavenCentralLegacyUrl: Property<Boolean> = propertyBool("mavenCentralLegacyUrl")

    @ExtensionProperty(
        name = "artifactId",
        description = "The unique name for your component",
        example = "my-super-lib"
    )
    @StringFormatValidator(format = "[A-Za-z0-9_\\-.]+")
    open var artifactId: Property<String> = propertyString("artifactId")

    @ExtensionProperty(
        name = "groupId",
        description = "The top level namespace level for your project starting with the reverse domain name",
        example = "com.acme"
    )
    @StringFormatValidator(format = "[A-Za-z0-9_\\-.]+")
    open var groupId: Property<String> = propertyString("groupId")

    @ExtensionProperty(
        name = "version",
        description = "The version string for your component",
        example = "0.1.0"
    )
    open var version: Property<String> = propertyString("version")

    @ExtensionProperty(
        name = "name",
        description = "Human readable description of the project. A common practice is to use groupId:project.artifactId",
        example = "com.acme.my-super-lib"
    )
    var name: Property<String> = propertyString("name")

    @ExtensionProperty(
        name = "description",
        description = "Human readable project description",
        example = "This a library that is just doing a lot!"
    )
    var description: Property<String> = propertyString("description")

    @ExtensionProperty(
        name = "url",
        description = "Pointer to the Projects main website",
        example = "https:com.acme/project"
    )
    var url: Property<String> = propertyString("url")

    @ExtensionProperty(
        name = "inceptionYear",
        description = "The projects inception year",
        example = "2022"
    )
    var inceptionYear: Property<String> = propertyString("inceptionYear")

    @ExtensionProperty( // TO-DO should be a list licenses
        name = "licenseName",
        description = "License for the project (full name prefer SPDX full name, https://spdx.org/licenses"
    )
    var licenseName: Property<String> = propertyString("licenseName")

    @ExtensionProperty(
        name = "licenseUrl",
        description = "License url for the project, prefer SPDX https://spdx.org/licenses"
    )
    var licenseUrl: Property<String> = propertyString("licenseUrl")

    @ExtensionProperty(
        name = "licenseDistribution",
        description = "Describes how the project may be distributed"
    )
    var licenseDistribution: Property<String> = propertyString("licenseDistribution")

    @ExtensionProperty(
        name = "licenseComments",
        description = "Any extra information about the licensing"
    )
    var licenseComments: Property<String> = propertyString("licenseComments")

    @ExtensionProperty(
        name = "developer",
        description = "A list of developer or maintainers of the project"
    )
    @ExtensionPropertyExtractor(extractor = ExtensionPropertyDeveloperExtractor::class)
    var developers = propertyList("developer", Developer.toDevelopers)

    @ExtensionProperty(
        name = "scmUrl",
        description = "Url to web front for SCM version system",
        example = "https://bitbucket.org/simpligility/ossrh-pipeline-demo/src"
    )
    var scmUrl: Property<String> = propertyString("scmUrl")

    @ExtensionProperty(
        name = "scmConnection",
        description = "Read connection SCM",
        example = "scm:git:git://github.com/simpligility/ossrh-demo.git"
    )
    var scmConnection: Property<String> = propertyString("scmConnection")

    @ExtensionProperty(
        name = "scmDeveloperConnection",
        description = "Read and write connection to SCM",
        example = "scm:git:ssh://github.com:simpligility/ossrh-demo.git"
    )
    var scmDeveloperConnection: Property<String> = propertyString("scmDeveloperConnection")

    @ExtensionProperty(
        name = "user",
        description = "User name - (don't store this in the project itself)"
    )
    open var user: Property<String> = propertyString("user")

    @ExtensionProperty(
        name = "password", secret = true,
        description = "User password - (don't store this in the project itself)"
    )
    open var password: Property<String> = propertyString("password")

    @ExtensionProperty(
        name = "token", secret = true,
        description = "Token - (don't store this in the project itself)"
    )
    open var token: Property<String> = propertyString("token")

    @ExtensionProperty(
        name = "directReleaseToMavenCentral",
        description = "For publishing to Maven Central, without manual intervention of release stage",
        example = "true"
    )
    open var directReleaseToMavenCentral: Property<Boolean> = propertyBool("directReleaseToMavenCentral")
}

data class Developer(
    val id: String,
    val name: String,
    val email: String,
    val organization: String,
    val organizationUrl: String
) {

    companion object {
        val toDevelopers: (List<List<String>>) -> List<Developer> =
            { list ->
                list.filter { it.size == 5 }
                    .map {
                        Developer(it[0], it[1], it[2], it[3], it[4])
                    }
            }
    }
}
