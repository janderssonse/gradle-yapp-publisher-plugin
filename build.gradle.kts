plugins {
    // `maven-publish`
    `java-gradle-plugin`
    signing
    idea
    kotlin("jvm") version "1.6.21"
    id("org.jetbrains.dokka") version "1.6.21"
    id("se.ascp.gradle.gradle-versions-filter") version "0.1.13"
    id("org.jmailen.kotlinter") version "3.10.0"
    id("org.owasp.dependencycheck") version "7.1.0.1"
    // id("com.jfrog.artifactory") version "4.26.1"
    // id("com.gradle.plugin-publish") version "0.18.0"
    id("pl.allegro.tech.build.axion-release") version "1.13.6"
    id("se.svt.oss.gradle-yapp-publisher-plugin") version "0.1.33"
}

group = "se.svt.oss"
// version = "0.0.35" // scmVersion.version
description = "Yet another plugin that manages publishing for Gradle projects"

repositories {
    gradlePluginPortal()
    mavenLocal()
    mavenCentral()
    google()
}

dependencies {
    api("com.gradle.publish:plugin-publish-plugin:0.21.0")
    implementation("org.jfrog.artifactory.client:artifactory-java-client-httpClient:2.12.0")
    implementation("org.jfrog.artifactory.client:artifactory-java-client-api:2.12.0")
    compileOnly("com.android.tools.build:gradle:7.1.3")
    implementation("org.jfrog.artifactory.client:artifactory-java-client-services:2.12.0")
//    implementation("org.jfrog.buildinfo:build-info-extractor-gradle:4.26.1")
//    implementation("com.jfrog.artifactory:4.26.1")
    implementation("io.github.gradle-nexus:publish-plugin:1.1.0")
//    implementation("com.google.guava:guava:31.0.1-android")//needed by artifactorys tasks propertity
    testImplementation("org.xmlunit:xmlunit-core:2.9.0")
    testImplementation("org.xmlunit:xmlunit-matchers:2.9.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testImplementation("uk.org.webcompere:system-stubs-jupiter:1.2.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.2")
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

    // withSourcesJar()
    // withJavadocJar()
}

tasks.named<Wrapper>("wrapper") {
    distributionType = Wrapper.DistributionType.ALL
    gradleVersion = "7.4.2"
}

/* NOTE: The following is to separate the integrationtests and run the after regular tests
We should be able to replace this conf with the jvm-test-suite plugin that is incubating in gradle
However, that was unusable for reasons at the time, but return to it in next version or so
*/
sourceSets {
    create("integrationTest") {
        kotlin {
            compileClasspath += main.get().output + configurations.testRuntimeClasspath
            runtimeClasspath += output + compileClasspath
        }
    }
}

val integrationTest = task<Test>("integrationTest") {
    description = "Runs the integration tests"
    group = "verification"
    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath
    mustRunAfter(tasks["test"])
    useJUnitPlatform()
}

tasks.check {
    dependsOn(integrationTest)
}
// END of IntegrationTest setup
