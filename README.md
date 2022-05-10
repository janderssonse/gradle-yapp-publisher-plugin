# Gradle Yapp Publisher Plugin - or Yet Another Publisher Plugin

![REUSE Compliance](https://img.shields.io/reuse/compliance/github.com/svt/gradle-yapp-publisher-plugin)
![GitHub tag (latest SemVer)](https://img.shields.io/github/v/tag/janderssonse/gradle-yapp-publisher-plugin)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Conventional Commits](https://img.shields.io/badge/Conventional%20Commits-1.0.0-yellow.svg)](https://conventionalcommits.org)

## What is it?

A Gradle plugin for publishing and optionally signing JVM-based projects (currently Java/Kotlin) and libraries packages
to Maven Central, Gradle Portal, GitHub, GitLab, Artifactory (basic support).

It is working for a lot of cases but under construction there will be compatibility breakage between versions
in the 0.x.x series - alpha.

## Why does it exist?

To make life (well, arguably) easier when configuring the plugins needed for these tasks.

To offer a simple, flexible union interface for these tasks, regardless of publishing target.

If you want a coherent configuration for multiple publishing targets this gradle plugin could be for you.

## Features

* Maven Central, Gradle Portal, GitLab, GitHub, Artifactory Publishing
* Signing
* Java, Kotlin, Android Library (aar) support (release variant)
* Build file, Properties or System Environment Configuration - or all of them
* Publish to several targets at once
* Configure autorelease to Maven Central
* Dokka Publish support
* Choose to publish with or without source, docs artifacts or with empty source/doc artifacts
* Mice configuration overview and guide for creating templates
* Semi smart configuration :)
* ... and more

### Quickstart

1. Add this [plugin](#plugin-addition) itself to your plugins block

2. Configure the plugin, see the [examples](#examples). The examples show the most basic settings - there are lots more
   to [configure or override if you need/want to](#configurations).

3. Run [Gradle Task](#tasks) *yappConfiguration* to view the plugins current project type and publish target
   configuration.

4. Run [Gradle Task](#tasks) *publishArtifactToLocalRepo* to publish your task to the local repo for verification.

5. Run [Gradle Task](#tasks) *publish* to publish your plugin.

## Plugin addition

Besides adding this plugin

```kotlin
plugins {
    id("se.svt.oss.gradle-yapp-publisher-plugin") version "x.y.z"
    //...
}
```

You can also add one of the following basic publishing plugins as a placeholder, [Publishing Target](#publishing-target)

*Publishing to Maven Central/GitLab/GitHub/Custom (Java/Kotlin Library)*

```kotlin
plugins {
    `maven-publish`
        //...
}            
```

* Publishing to Gradle Portal (Gradle Plugin)*

```kotlin
plugins {
    `java-gradle-plugin`
        //...
}
```

## Tasks

The plugin offers a few tasks found under "yapp publisher".

* createConfigurationTemplate TO-DO
* publishArtifact - publish the artifact to the [publishing target](#publishing-target)
* publishArtifactToLocalRepo - publish to local repo
* yappConfiguration - show the current configuration - i.e. type of [publishing target](#publishing-target), project
  type and more

## Publishing target

A publishing target defines where to publish the project.


Allowed target values are


- artifactory
- maven_central
- maven_central_snapshot
- gitlab
- github
- gradle_portal
- custom TO-DO

If you leave this empty, the plugin will make a guess based on your added plugins and versions.

### In summary, The plugin needs to know about

* A [Publishing Target](#publishing-target)
* A few properties, depending on your configuration target
* A project type (for example, a java-library)

* If project type and publish target are not configured explicitly, *they are chosen on a best-effort guess*, depending
  on you chosen plugin and version setup.

## Examples

*Publish to Gradle Portal*

With a gradle.properties file and System.ENV

```properties
yapp.targets=gradle_portal  
yapp.gradleplugin.id=se.svt.oss.gradle-yapp-publisher-plugin 
yapp.gradleplugin.description=Yet another plugin that manages publishing for Gradle projects
yapp.gradleplugin.web=https://github.com/svt/gradle-yapp-publisher-plugin
yapp.gradleplugin.vcs=https://github.com/svt/gradle-yapp-publisher-plugin.git
yapp.gradleplugin.tags=maven central, gradle portal, publishing
yapp.gradleplugin.class=se.svt.oss.gradle.yapp.YappPublisher
yapp.gradleplugin.displayname=Gradle Yapp Publisher Plugin
```

*GitHub*

With a gradle.properties file and System.ENV

```properties
yapp.targets= github
yapp.github.actor=<YOUR-GITHUB-USER>
yapp.github.token=<YOUR-GITHUB-PAT> -# put this global gradle.properties, or use system env so you dont commit it
yapp.github.namespace=<YOUR-GITHUB-ORGANISATION/USER>
yapp.github.reponame=<NAME-OF-REPO>
```

*GitLab*

With a gradle.properties file and System.ENV

```properties
yapp.targets = gitlab
yapp.gitlab.host=https://gitlab.com
yapp.gitlab.token=<YOUR-GITLAB-TOKEN> -# put this global gradle.properties, or use system env so you dont commit it
yapp.gitlab.tokenType=<YOUR-GITLAB-TOKEN-TYPE>
yapp.gitlab.endpointLevel=project
yapp.gitlab.glProjectId=<YOUR-GITLAB-PROJECT-ID>
```

*OSSRH/Maven Central*

Build file(build.gradle.kts) configuration, including signing

```kotlin
yapp {

    mavenPublishing {
        groupId.set("$group")
        version.set("$version")

        name.set("pn")
        description.set("pd")
        url.set("http://p.se")
        inceptionYear.set("1999")

        licenseName.set("pln")
        licenseUrl.set("plu")
        licenseDistribution.set("pld")
        licenseComments.set("plc")


        developers.set(listOf(Developer("a","b","c"),Developer("d","e","f")))

        organization.set("pdo")
        organizationUrl.set("pou")

        scmUrl.set("psu")
        scmConnection.set("psc")
        scmDeveloperConnection.set("psd")
    }

    signing {
        enabled.set(true)
        signSnapshot.set(true)
        keyId.set("11111111")
        password.set("signing")
        key.set("signingKey")
    }
}
```

Properties file(gradle.properties) configuration

```properties
yapp.targets= maven_central
yapp.mavenPublishing.name = exampleproject
yapp.mavenPublishing.description = an pom example description
yapp.mavenPublishing.url = https://github.com/myexamplaproject
yapp.mavenPublishing.inceptionYear = 2021

yapp.mavenPublishing.scmConnection = scm:git:github.com/example.git
yapp.mavenPublishing.scmDeveloperConnection = scm:git:ssh://github.com/example.git
yapp.mavenPublishing.scmUrl = https://github.com/example

yapp.mavenPublishing.licenseName = The Apache Software License, Version 2.0
yapp.mavenPublishing.licenseUrl = http://www.apache.org/licenses/LICENSE-2.0.txt

yapp.mavenPublishing.developer = an dev id, an dev name, my@email.com
yapp.mavenPublishing.organization =  my org name
yapp.mavenPublishing.organizationUrl = my org url

yapp.signing.enabled = false
```

## Configurations

See [docs/configuration.md](docs/configuration.md)

## Future

In an early stage, the following features would be a nice roadmap:

* More tests
* More settings can be simplified and auto set
* Better handling of multi project setup
* More pre-configured targets. Improve the existing ones - JitPack? and Custom Maven Publishing
* Better semi smart Plugin identification
* Create sample configurations
* Support other languages than Java/Kotlin (Groovy,Scala)
* Auto add base plugins based on user publishing target choice

## F.A.Q

* Why are there tasks called generateMetaDataFileForPluginMavenPublication and more, all relating to "pluginMaven"

It is a design choice (or a bug) from Gradle, these tasks are autogenerated for the Maven Publish plugin and can't be
disabled. Look att [https://github.com/gradle/gradle/issues/12394](https://github.com/gradle/gradle/issues/12394)

* Why another publisher plugin ?

At the time of starting this plugin there was none found that had all the features this one has. I wanted an easy way to
just drop in some configurations and publish to many places. And then it grew.

* Plugin must be applied to the root project but was applied to :subproject when using directReleaseToMavenCentral

This is limitations in the Nexus Gradle Plugin used <https://github.com/gradle-nexus/publish-plugin/issues/81>
and if using this feature, the project be a root project currently.

## The plugin abstracts

* [Maven Publish](https://docs.gradle.org/current/userguide/publishing_maven.html)
* [Java Gradle Plugin Development Plugin](https://docs.gradle.org/current/userguide/java_gradle_plugin.html#java_gradle_plugin)
* [Gradle Portal Publishing Plugin](https://plugins.gradle.org/docs/publish-plugin)
* [Gradle Signing Plugin](https://docs.gradle.org/current/userguide/publishing_signing.html)
* [Gradle Nexus Publish Plugin](https://github.com/gradle-nexus/publish-plugin)

## Getting help

If you have questions, concerns, bug reports, etc, please file an issue in this repository's Issue Tracker.

## Getting involved

General instructions for contributing [CONTRIBUTING](docs/CONTRIBUTIONS.adoc).

----

## License

The Gradle Yapp Publisher Plugin is released under the:

[Apache License 2.0](LICENSE)

----

## Primary Maintainer

Josef Andersson <https://github.com/janderssonse>

## Creators

Josef Andersson <https://github.com/janderssonse>
Rickard Andersson <https://github.com/rickard-svti>

