![REUSE Compliance](https://img.shields.io/reuse/compliance/github.com/svt/gradle-yapp-publisher-plugin)
![GitHub tag (latest SemVer)](https://img.shields.io/github/v/tag/jandersson-svt/gradle-yapp-publisher-plugin)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Conventional Commits](https://img.shields.io/badge/Conventional%20Commits-1.0.0-yellow.svg)](https://conventionalcommits.org)

# Gradle Yapp Publisher Plugin - or Yet Another Publisher Plugin

## What is it?

A Gradle plugin for publishing and optionally signing JVM-based projects (currently Java/Kotlin) and libraries packages
to Maven Central, Gradle Portal, GitHub, GitLab, Artifactory (basic support).

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

Beside adding this plugin

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

Allowed target values are:
- 

- artifactory
- maven_central
- maven_central_snapshot
- gitlab
- github
- gradle_portal
- custom TO-DO

If you leave this empty, the plugin will make a guess based on your added plugins and version.

### In summary, The plugin needs to know about:

* A [Publishing Target](#publishing-target)
* A few properties, depending on your configuration target
* A project type (for example, a java-library)

* If project type and publish target are not configured explicitly, _they are chosen on a best-effort guess_, depending
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

You can put your configuration in a

- Build File (build.gradle.kts etc)
- Property File (gradle.properties etc)
- System Environment Variable

This is also the order in which they will be picked up.

* NOTE: System Environments are always in CAPITAL, i.e YAPP_POM_ARTIFACTID, and so on.

* **Yapp Publisher General Configuration **

| File             | Type                         |                                        |
| ---------------- | --------------------------   | -------------------                    |
| Property file    | gradle.properties-format:    | yapp.property             |
| Build file       | build.gradle.kts-format      | yapp { property }   |
| N/A              | environment variable         | YAPP_PROPERTY             |

| property            | description           |   example value               |  (maps to)                                                                         |
| --------------     | ----------------- | ---------------------- | ----------------------------- |
| targets             |                  | *                      |  A [publishing target](#publishing-target) or empty|

**General MavenPublishing Configuration/Maven Central**

| File             | Type                         |                                        |
| ---------------- | --------------------------   | -------------------                    |
| Property file    | gradle.properties-format:    | yapp.mavenPublishing.property             |
| Build file       | build.gradle.kts-format      | yapp { mavenPublishing { property } } }   |
| N/A              | environment variable         | YAPP_MAVENPUBLISHING_PROPERTY             |

| property            | description           |   example value               |  (maps to)                                                                         |
| ---------------------------  | ----------------------- | ------------------------- | ----------- |
| mavenCentralLegacyUrl        | *                       | *                         |             |
| artifactId                   | *                       | *                         |             |
| groupId                      | *                       | *                         |             | 
| version                      | *                       | *                         |             | 
| name                         | *                       | *                         |             | 
| description                  | *                       | *                         |             | 
| url                          | *                       | *                         |             | 
| inceptionYear                | *                       | *                         |             | 
| licenseName                  | *                       | *                         |             | 
| licenseUrl                   | *                       | *                         |             | 
| licenseDistribution          | *                       | *                         |             | 
| licenseComments              | *                       | *                         |             | 
| developerId                  | *                       | *                         |             | 
| developerName                | *                       | *                         |             | 
| developerEmail               | *                       | *                         |             | 
| organization                 | *                       | *                         |             | 
| organizationUrl              | *                       | *                         |             | 
| scmUrl                       | *                       | *                         |             | 
| scmConnection                | *                       | *                         |             | 
| scmDeveloperConnection       | *                       | *                         |             | 
| user                          | *                 | *                      |              |
| password                      | *                 | *                      |              |

**Signing**

| File             | Type                         |                                        |
| ---------------- | --------------------------   | -------------------                    |
| Property file    | gradle.properties-format:    | yapp.signing.property             |
| Build file       | build.gradle.kts-format      | yapp { signing { property } } }   |
| N/A              | environment variable         | YAPP_SIGNING_PROPERTY             |

| property            | description           |   example value               |  (maps to)                                                                         |
| ------------------- | ----------------------| ----------------------------  | -------------------------------------------------------------------------------- |
| enabled             | signing enabled       | true                          |                                                                                  |
| signSnapshot        | sign snapshot         | true                          |                                                                                  |
| keySecret           | signing key password  | <YOUR-SECRET-PW>              | [password](https://docs.gradle.org/current/userguide/signing_plugin.html)           |
| keyId               | public key id (last 8)| abc12345                      | [keyId](https://docs.gradle.org/current/userguide/signing_plugin.html)             |
| key                 | signing key           | /path/to/gpgkey or textformat | [secretKeyRingFile](https://docs.gradle.org/current/userguide/signing_plugin.html)            |

Note: The signing key can be either a path to an gpg-key or in text format with literal newlines as \n. See the gpg
folder under the Project test resources for examples.

**Gradle Plugin and publishing**

| File             | Type                         |                                        |
| ---------------- | --------------------------   | -------------------                    |
| Property file    | gradle.properties-format:    | yapp.gradleplugin.property             |
| Build file       | build.gradle.kts-format      | yapp { gradleplugin { property } } }   |
| N/A              | environment variable         | YAPP_GRADLEPLUGIN_PROPERTY             |

| property            | description                                         |  example value                              |  (maps to)    |
| ------------------- | --------------------------------------------------- | ------------------------------------------  | ------------------------ |
| web                 | The project weburl                                  | https://my.web.com/project                 |  [web](https://plugins.gradle.org/docs/publish-plugin)        |
| vcs                 | The projects repo url                               | https://github.com/johndoe/GradlePlugins   |  [vcs](https://plugins.gradle.org/docs/publish-plugin)           |
| tags                | Tags to describe the categories the plugin covers   | listOf("publishing", "ossrh")              |  [tags](https://plugins.gradle.org/docs/publish-plugin)           |
| id                  | groupid.plugin-name                                 | org.best.myplugin                          |  [id](https://docs.gradle.org/current/userguide/java_gradle_plugin.html#java_gradle_plugin)           |
| class               | The extensions main class                           | org.best.plugin.SimplePlugin               |  [implementationClass](https://docs.gradle.org/current/userguide/java_gradle_plugin.html#java_gradle_plugin)          |
| description         | A description specifying the intent of the plugin.  | A plugin for doing x!                       |  [description](https://plugins.gradle.org/docs/publish-plugin)           |
| displayName         | Overall plugin display name                         | Gradle Do-it Plugin                        |  [displayName](https://plugins.gradle.org/docs/publish-plugin)           |
| token               | Plugin Portal token                                 | @£234234efkkdk                             |  [API Key](https://plugins.gradle.org/docs/publish-plugin)           |
| password            | Plugin Portal password                              | <YOUR-SECRET-LOGIN-PASS                    |   [Account password](https://plugins.gradle.org/docs/publish-plugin)          |

**GitLab Package Registry Publishing**

| File             | Type                         |                                        |
| ---------------- | --------------------------   | -------------------                    |
| Property file    | gradle.properties-format:    | yapp.gitlab.property             |
| Build file       | build.gradle.kts-format      | yapp { gitlab { property } } }   |
| N/A              | environment variable         | YAPP_GITLAB_PROPERTY             |

| property            | description                             |  example value                       |  (maps to)            |
| ------------------- | --------------------------------------- | ------------------------------------ | ------------------------ |
| host                | gitlab host                                         | https://gitlab.com         |  [web](https://plugins.gradle.org/docs/publish-plugin)        |
| token               | [personal access, deploy or CI token](https://docs.gitlab.com/ee/user/packages/maven_repository/index.html#authenticate-to-the-package-registry-with-maven) |  <YOUR-GITLAB-TOKEN> |  [credentialsvalue](https://docs.gitlab.com/ee/user/packages/maven_repository/index.html) |
| tokenType           | One of GitLabs token types                          |  Project-Token              |  [credentialsname](https://docs.gitlab.com/ee/user/packages/maven_repository/index.html)           |
| endpointLevel       | One of [GitLabs endpoint types](https://docs.gitlab.com/ee/user/packages/maven_repository/index.html#use-the-gitlab-endpoint-for-maven-packages) | project                    |  [GItlabEndPoint](https://docs.gitlab.com/ee/user/packages/maven_repository/index.html)           |
| glProjectId         | Gitlab Project or GroupID                           | 24234242                   |  [projectOrGroupId](https://docs.gitlab.com/ee/user/packages/maven_repository/index.html)  |

**GitHub Package Registry Publishing**

| File             | Type                         |                                        |
| ---------------- | --------------------------   | -------------------                    |
| Property file    | gradle.properties-format:    | yapp.github.property             |
| Build file       | build.gradle.kts-format      | yapp { github { property } } }   |
| N/A              | environment variable         | YAPP_GITHUB_PROPERTY             |

| property            | description                  |  example value                             |  (maps to)    |
| ------------------- | ---------------------------- | ------------------------------------------ | ------------------------ |
| user               | your github user             | johndoe                 |  [web](https://plugins.gradle.org/docs/publish-plugin)        |
| token               | your github token            | secret token   |  [vcs](https://plugins.gradle.org/docs/publish-plugin)           |
| namespace           | github-organisation/namespace | acmecorp               |  [tags](https://plugins.gradle.org/docs/publish-plugin)           |
| repo                | github repo                  | pluginrep                         |  [id](https://docs.gradle.org/current/userguide/java_gradle_plugin.html#java_gradle_plugin)           |

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

# F.A.Q

* Why are there tasks called generateMetaDataFileForPluginMavenPublication and more, all relating to "pluginMaven"

It is a design choice (or a bug) from Gradle, these tasks are autogenerated for the Maven Publish plugin and can't be
disabled. Look att [https://github.com/gradle/gradle/issues/12394](https://github.com/gradle/gradle/issues/12394)

* Why another publisher plugin ?

At the time of starting this plugin there was none found that had all the features this one has. I wanted an easy way to
just drop in some configurations and publish to many places. And then it grew.

## The plugin abstracts:

* [Maven Publish](https://docs.gradle.org/current/userguide/publishing_maven.html)
* [Java Gradle Plugin Development Plugin](https://docs.gradle.org/current/userguide/java_gradle_plugin.html#java_gradle_plugin)
* [Gradle Portal Publishing Plugin](https://plugins.gradle.org/docs/publish-plugin)
* [Gradle Signing Plugin](https://docs.gradle.org/current/userguide/publishing_signing.html)

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

Josef Andersson https://github.com/jandersson-svt

## Creators

Josef Andersson https://github.com/jandersson-svt
Rickard Andersson https://github.com/rickard-svti 

