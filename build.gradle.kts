plugins {
    //`maven-publish`
    `java-gradle-plugin`
    signing
    idea
    kotlin("jvm") version "1.5.31"
    id("org.jetbrains.dokka") version "1.5.31"
    id("se.ascp.gradle.gradle-versions-filter") version "0.1.10"
    id("org.jmailen.kotlinter") version "3.7.0"
    id("org.owasp.dependencycheck") version "6.5.0.1"
   // id("com.jfrog.artifactory") version "4.26.1"
    //id("com.gradle.plugin-publish") version "0.18.0"
    id("pl.allegro.tech.build.axion-release") version "1.13.6"
    id("se.svt.oss.gradle-yapp-publisher-plugin") version "0.1.33"
}

group = "se.svt.oss"
//version = scmVersion.version
version = "0.1.33"
description = "Yet another plugin that manages publishing for Gradle projects"

repositories {
    gradlePluginPortal()
    mavenLocal()
    mavenCentral()
    google()
}

dependencies {
    api("com.gradle.publish:plugin-publish-plugin:0.18.0")
    compileOnly("com.android.tools.build:gradle:7.0.4")
    api("org.jfrog.artifactory.client:artifactory-java-client-services:2.9.2")
//    implementation("org.jfrog.buildinfo:build-info-extractor-gradle:4.26.1")
//    implementation("com.jfrog.artifactory:4.26.1")
    implementation("io.github.gradle-nexus:publish-plugin:1.1.0")
//    implementation("com.google.guava:guava:31.0.1-android")//needed by artifactorys tasks propertity
    testImplementation("org.xmlunit:xmlunit-core:2.8.4")
    testImplementation("org.xmlunit:xmlunit-matchers:2.8.4")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testImplementation("uk.org.webcompere:system-stubs-jupiter:1.2.0")
}

tasks {
    test {
        useJUnitPlatform()
    }
}
yapp {

}
/* pluginBundle {
    website = "https://github.com/svt/gradle-yapp-publisher-plugin"
    vcsUrl = "https://github.com/svt/gradle-yapp-publisher-plugin.git"
    tags = listOf("maven central", "gradle portal", "publish")
}
gradlePlugin {
    plugins {
        create("yappPlugin") {
            id = "${project.group}.${project.name}"
            displayName = "Gradle Yapp Publisher Plugin"
            implementationClass = "se.svt.oss.gradle.yapp.YappPublisher"
            description = project.description
        }
    }
}*/


kotlin {

    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(11))
    }
}

java {

    //withSourcesJar()
    //withJavadocJar()
}

tasks.named<Wrapper>("wrapper") {
    distributionType = Wrapper.DistributionType.ALL
    gradleVersion = "7.3"
}
