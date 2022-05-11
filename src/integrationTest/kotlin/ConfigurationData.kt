// SPDX-FileCopyrightText: 2022 Sveriges Television AB
//
// SPDX-License-Identifier: Apache-2.0

import AbstractIntegrationTest.Companion.TLD
import java.nio.file.Path
import kotlin.io.path.readText

class ConfigurationData {

    companion object {
        private const val pluginName = "gradle-yapp-publisher-plugin"

        fun buildFileData(
            group: String,
            version: String,
            yappConf: String = "",
            plugin1: String = "id(\"maven-publish\")\n",
            plugin2: String = "kotlin(\"jvm\") version \"1.5.30\"",
            buildGradleFile: Path
        ): String {

            return buildGradleFile.readText()
                .replace("{group}", group)
                .replace("{version}", version)
                .replace("_yappConf_", yappConf)
                .replace("_plugin1_", plugin1)
                .replace("_plugin2_", plugin2)
        }

        fun propertiesFileData(name: String = pluginName, group: String, version: String) = """
       |yapp.targets=maven_central
       |yapp.mavenPublishing.groupId=$group
       |yapp.mavenPublishing.version=$version
       |yapp.mavenPublishing.artifactId=$name

       |yapp.mavenPublishing.name=property name
       |yapp.mavenPublishing.description=property description
       |yapp.mavenPublishing.url=http://propertyurl.se
       |yapp.mavenPublishing.inceptionYear=1901

       |yapp.mavenPublishing.licenseName=property licensename
       |yapp.mavenPublishing.licenseUrl=property licenseurl
       |yapp.mavenPublishing.licenseDistribution=property distribution
       |yapp.mavenPublishing.licenseComments=property comments

       |yapp.mavenPublishing.developer=property developerid,property developername,property developeremail,org,orgu
       |yapp.mavenPublishing.developer.2=property developerid2,property developername2,property developeremail2,org2,orgu2
       |yapp.mavenPublishing.organization=property developerorganization
       |yapp.mavenPublishing.organizationUrl=property developerorganizationurl

       |yapp.mavenPublishing.scmUrl=property scmurl
       |yapp.mavenPublishing.scmConnection=property scmconnection
       |yapp.mavenPublishing.scmDeveloperConnection=property scmdeveloperconnection

       |yapp.signing.enabled=false
       |yapp.signing.signSnapshot=false
       |yapp.signing.keyId=propsk
       |yapp.signing.password=propsp
       |yapp.signing.key=propkf

       |yapp.ossrhUser=propu
       |yapp.ossrhPassword=proppw
        """.trimMargin()

        fun buildFileYappSection(
            targets: List<String>,
            mavenpublishingsection: String = "",
            githubsection: String = "",
            gradleportalsection: String = "",
            signingsection: String = "",
            withSourceArtifact: Boolean = true,
            withDocArtifact: Boolean = true,
            emptySourceArtifact: Boolean = false,
            emptyDocArtifact: Boolean = false,
        ) = """

yapp {
    targets.set(listOf(${targets.map { "\"${it}\"" }.joinToString(",")}))
    withSourceArtifact.set($withSourceArtifact)
    withDocArtifact.set($withDocArtifact)
    emptySourceArtifact.set($emptySourceArtifact)
    emptyDocArtifact.set($emptyDocArtifact)

     $mavenpublishingsection

     $githubsection

     $gradleportalsection

     $signingsection


}
        """.trimIndent()

        fun buildFileMavenPublishingSection(
            group: String,
            version: String,
            name: String = "name"
        ) =
            """

        mavenPublishing {
            groupId.set("$group")
            version.set("$version")

            name.set("$name")
            description.set("pd")
            url.set("http://p.se")
            inceptionYear.set("1999")

            licenseName.set("pln")
            licenseUrl.set("plu")
            licenseDistribution.set("pld")
            licenseComments.set("plc")


            developers.set(listOf(
                Developer("pdi", "pdn", "pde","pdo","pou"),
                Developer("pdi2", "pdn2", "pde2","pdo2","pou2")))


            scmUrl.set("psu")
            scmConnection.set("psc")
            scmDeveloperConnection.set("psd")
        }
            """.trimIndent()

        fun buildFileGitHubSection() = """

        gitHub {
         namespace.set("octocat")
         repoName.set("octocatproject")
    }
        """.trimIndent()

        fun buildFileGradlePortalSection(id: String) = """
         gradlePortalPublishing {
       id.set("$id")
        description.set("description")
        webSite.set("http://a")
        vcsUrl.set("http:/gti")
        tags.set(listOf("a","b"))
        implementationClass.set("GradlePlugin")
       displayName.set("displayname")
    }
        """.trimIndent()

        fun buildFileSigningSection(
            signingKey: String = "",
            signingEnabled: Boolean = false,
            signSnapshot: Boolean = false,
        ) = """
         signing {
        enabled.set($signingEnabled)
        signSnapshot.set($signSnapshot)
        keyId.set("3DC10F04")
        keySecret.set("signing")
        key.set("$signingKey")
    }
        """.trimIndent()

        fun systemEnvironmentMavenPublishing(version: String): Map<String, String> {
            val envPrefix = "YAPP_MAVENPUBLISHING_"
            return mapOf(
                Pair("YAPP_TARGETS", "maven_central"),
                Pair("${envPrefix}NAME", "yapp.mavenPublishing.name"),
                Pair("${envPrefix}DESCRIPTION", "yapp.mavenPublishing.description"),
                Pair("${envPrefix}URL", "yapp.mavenPublishing.url"),
                Pair("${envPrefix}INCEPTIONYEAR", "yapp.mavenPublishing.inceptionYear"),
                Pair("${envPrefix}LICENSENAME", "yapp.mavenPublishing.licenseName"),
                Pair("${envPrefix}LICENSEURL", "yapp.mavenPublishing.licenseUrl"),
                Pair("${envPrefix}LICENSEDISTRIBUTION", "yapp.mavenPublishing.licenseDistribution"),
                Pair("${envPrefix}LICENSECOMMENTS", "yapp.mavenPublishing.licenseComments"),
                Pair(
                    "${envPrefix}DEVELOPER",
                    "yapp.mavenPublishing.developerId,yapp.mavenPublishing.developerName, " +
                        "yapp.mavenPublishing.developerEmail, org, orgu"
                ),
                Pair(
                    "${envPrefix}DEVELOPER.2",
                    "yapp.mavenPublishing.developerId2,yapp.mavenPublishing.developerName2," +
                        " yapp.mavenPublishing.developerEmail2, org2, orgu2"
                ),
                Pair("${envPrefix}SCMURL", "yapp.mavenPublishing.scmUrl"),
                Pair("${envPrefix}SCMCONNECTION", "yapp.mavenPublishing.scmConnection"),
                Pair("${envPrefix}SCMDEVELOPERCONNECTION", "yapp.mavenPublishing.scmDeveloperConnection"),
                Pair("${envPrefix}GROUPID", "$TLD.env"),
                Pair("${envPrefix}VERSION", "$version"),
                Pair("${envPrefix}ARTIFACTID", "kotlinlibrary"),
                Pair("YAPP_SIGNING_ENABLED", "false"),
                Pair("YAPP_SIGNING_SIGNSNAPSHOT", "false"),
                Pair("YAPP_SIGNING_KEYID", "yapp.signing.keyId"),
                Pair("YAPP_SIGNING_KEYSECRET", "yapp.signing.keySecret"),
                Pair("YAPP_SIGNING_KEY", "yapp.signing.keyFile"),
                Pair("YAPP_OSSRHUSER", "yapp.ossrhUser"),
                Pair("YAPP_OSSRHPASSWORD", "yapp.ossrhPassword")
            )
        }

        fun yappPluginTestBaseBuildFile(
            group: String = "$TLD.svt.oss",
            version: String = "1.0.0-SNAPSHOT"
        ) = """

plugins {
    `maven-publish`
    `java-gradle-plugin`
    signing
    kotlin("jvm") version "1.6.21"
    id("org.jetbrains.dokka") version "1.6.21"
    id("com.gradle.plugin-publish") version "0.21.0"


}

group = "$group"
version = "$version"
description = "Yet another plugin that manages publishing for Gradle projects"

repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
}

dependencies {
    api("com.gradle.publish:plugin-publish-plugin:0.21.0")


    api("org.jfrog.artifactory.client:artifactory-java-client-services:2.10.0")
    compileOnly("com.android.tools.build:gradle:7.1.3")

    implementation("io.github.gradle-nexus:publish-plugin:1.1.0")
    testImplementation("org.xmlunit:xmlunit-core:2.9.0")
    testImplementation("org.xmlunit:xmlunit-matchers:2.9.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testImplementation("uk.org.webcompere:system-stubs-jupiter:2.0.1")

}

tasks {
    test {
        useJUnitPlatform()
    }
}

kotlin {

    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(11))
    }
}

pluginBundle {
    website = "https://github.com/svt/gradle-yapp-publisher-plugin"
    vcsUrl = "https://github.com/svt/gradle-yapp-publisher-plugin.git"
    tags = listOf("maven central", "gradle portal", "publish")
}
gradlePlugin {
    plugins {
        create("yappPlugin") {
            id = "se.svt.oss.gradle-yapp-publisher-plugin"
            displayName = "Gradle Yapp Publisher Plugin"
            implementationClass = "se.svt.oss.gradle.yapp.YappPublisher"
            description = project.description
        }
    }
}



kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(11))
    }
}

java {

    withSourcesJar()
    withJavadocJar()
}

tasks.named<Wrapper>("wrapper") {
    distributionType = Wrapper.DistributionType.ALL
    gradleVersion = "7.4.2"
}
        """.trimIndent()
    }
}
