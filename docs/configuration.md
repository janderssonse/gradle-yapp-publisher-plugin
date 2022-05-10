# Configuration

You can put your configuration in a

- Build File (build.gradle.kts etc)
- Property File (gradle.properties etc)
- System Environment Variable

This is also the order in which they will be picked up.

* NOTE: System Environments are always in CAPITAL, i.e YAPP_POM_ARTIFACTID, and so on.

* **Yapp Publisher General Configuration**

| File          | Type                      |                   |
|---------------|---------------------------|-------------------|
| Property file | gradle.properties-format: | yapp.property     |
| Build file    | build.gradle.kts-format   | yapp { property } |
| N/A           | environment variable      | YAPP_PROPERTY     |

| property | description | example value | (maps to)                                     |
|----------|-------------|---------------|-----------------------------------------------|
| targets  |             | *             | A publishing target (see README.md)) or empty |

**General MavenPublishing Configuration/Maven Central**

| File          | Type                      |                                         |
|---------------|---------------------------|-----------------------------------------|
| Property file | gradle.properties-format: | yapp.mavenPublishing.property           |
| Build file    | build.gradle.kts-format   | yapp { mavenPublishing { property } } } |
| N/A           | environment variable      | YAPP_MAVENPUBLISHING_PROPERTY           |

| property               | description | example value | (maps to) |
|------------------------|-------------|---------------|-----------|
| mavenCentralLegacyUrl  | *           | *             |           |
| artifactId             | *           | *             |           |
| groupId                | *           | *             |           |
| version                | *           | *             |           |
| name                   | *           | *             |           |
| description            | *           | *             |           |
| url                    | *           | *             |           |
| inceptionYear          | *           | *             |           |
| licenseName            | *           | *             |           |
| licenseUrl             | *           | *             |           |
| licenseDistribution    | *           | *             |           |
| licenseComments        | *           | *             |           |
| developerId            | *           | *             |           |
| developerName          | *           | *             |           |
| developerEmail         | *           | *             |           |
| organization           | *           | *             |           |
| organizationUrl        | *           | *             |           |
| scmUrl                 | *           | *             |           |
| scmConnection          | *           | *             |           |
| scmDeveloperConnection | *           | *             |           |
| user                   | *           | *             |           |
| password               | *           | *             |           |

**Signing**

| File          | Type                      |                                 |
|---------------|---------------------------|---------------------------------|
| Property file | gradle.properties-format: | yapp.signing.property           |
| Build file    | build.gradle.kts-format   | yapp { signing { property } } } |
| N/A           | environment variable      | YAPP_SIGNING_PROPERTY           |

| property     | description            | example value                 | (maps to)                                                                          |
|--------------|------------------------|-------------------------------|------------------------------------------------------------------------------------|
| enabled      | signing enabled        | true                          |                                                                                    |
| signSnapshot | sign snapshot          | true                          |                                                                                    |
| keySecret    | signing key password   | <YOUR-SECRET-PW>              | [password](https://docs.gradle.org/current/userguide/signing_plugin.html)          |
| keyId        | public key id (last 8) | abc12345                      | [keyId](https://docs.gradle.org/current/userguide/signing_plugin.html)             |
| key          | signing key            | /path/to/gpgkey or textformat | [secretKeyRingFile](https://docs.gradle.org/current/userguide/signing_plugin.html) |

Note: The signing key can be either a path to an gpg-key or in text format with literal newlines as \n. See the gpg
folder under the Project test resources for examples.

**Gradle Plugin and publishing**

| File          | Type                      |                                      |
|---------------|---------------------------|--------------------------------------|
| Property file | gradle.properties-format: | yapp.gradleplugin.property           |
| Build file    | build.gradle.kts-format   | yapp { gradleplugin { property } } } |
| N/A           | environment variable      | YAPP_GRADLEPLUGIN_PROPERTY           |

| property    | description                                        | example value                                | (maps to)                                                                                                   |
|-------------|----------------------------------------------------|----------------------------------------------|-------------------------------------------------------------------------------------------------------------|
| web         | The project weburl                                 | "https[]://my.web.com/project"               | [web](https://plugins.gradle.org/docs/publish-plugin)                                                       |
| vcs         | The projects repo url                              | "https[]://github.com/johndoe/GradlePlugins" | [vcs](https://plugins.gradle.org/docs/publish-plugin)                                                       |
| tags        | Tags to describe the categories the plugin covers  | listOf("publishing", "ossrh")                | [tags](https://plugins.gradle.org/docs/publish-plugin)                                                      |
| id          | groupid.plugin-name                                | org.best.myplugin                            | [id](https://docs.gradle.org/current/userguide/java_gradle_plugin.html#java_gradle_plugin)                  |
| class       | The extensions main class                          | org.best.plugin.SimplePlugin                 | [implementationClass](https://docs.gradle.org/current/userguide/java_gradle_plugin.html#java_gradle_plugin) |
| description | A description specifying the intent of the plugin. | A plugin for doing x!                        | [description](https://plugins.gradle.org/docs/publish-plugin)                                               |
| displayName | Overall plugin display name                        | Gradle Do-it Plugin                          | [displayName](https://plugins.gradle.org/docs/publish-plugin)                                               |
| token       | Plugin Portal token                                | @£234234efkkdk                               | [API Key](https://plugins.gradle.org/docs/publish-plugin)                                                   |
| password    | Plugin Portal password                             | <YOUR-SECRET-LOGIN-PASS                      | [Account password](https://plugins.gradle.org/docs/publish-plugin)                                          |

**GitLab Package Registry Publishing**

| File          | Type                      |                                |
|---------------|---------------------------|--------------------------------|
| Property file | gradle.properties-format: | yapp.gitlab.property           |
| Build file    | build.gradle.kts-format   | yapp { gitlab { property } } } |
| N/A           | environment variable      | YAPP_GITLAB_PROPERTY           |

| property      | description                                                                                                                                                 | example value        | (maps to)                                                                                |
|---------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------|------------------------------------------------------------------------------------------|
| host          | gitlab host                                                                                                                                                 | <https://gitlab.com> | [web](https://plugins.gradle.org/docs/publish-plugin)                                    |
| token         | [personal access, deploy or CI token](https://docs.gitlab.com/ee/user/packages/maven_repository/index.html#authenticate-to-the-package-registry-with-maven) | <YOUR-GITLAB-TOKEN>  | [credentialsvalue](https://docs.gitlab.com/ee/user/packages/maven_repository/index.html) |
| tokenType     | One of GitLabs token types                                                                                                                                  | Project-Token        | [credentialsname](https://docs.gitlab.com/ee/user/packages/maven_repository/index.html)  |
| endpointLevel | One of [GitLabs endpoint types](https://docs.gitlab.com/ee/user/packages/maven_repository/index.html#use-the-gitlab-endpoint-for-maven-packages)            | project              | [GItlabEndPoint](https://docs.gitlab.com/ee/user/packages/maven_repository/index.html)   |
| glProjectId   | Gitlab Project or GroupID                                                                                                                                   | 24234242             | [projectOrGroupId](https://docs.gitlab.com/ee/user/packages/maven_repository/index.html) |

**GitHub Package Registry Publishing**

| File          | Type                      |                                |
|---------------|---------------------------|--------------------------------|
| Property file | gradle.properties-format: | yapp.github.property           |
| Build file    | build.gradle.kts-format   | yapp { github { property } } } |
| N/A           | environment variable      | YAPP_GITHUB_PROPERTY           |

| property  | description                   | example value | (maps to)                                                                                  |
|-----------|-------------------------------|---------------|--------------------------------------------------------------------------------------------|
| user      | your github user              | johndoe       | [web](https://plugins.gradle.org/docs/publish-plugin)                                      |
| token     | your github token             | secret token  | [vcs](https://plugins.gradle.org/docs/publish-plugin)                                      |
| namespace | github-organisation/namespace | acmecorp      | [tags](https://plugins.gradle.org/docs/publish-plugin)                                     |
| repo      | github repo                   | pluginrep     | [id](https://docs.gradle.org/current/userguide/java_gradle_plugin.html#java_gradle_plugin) |

