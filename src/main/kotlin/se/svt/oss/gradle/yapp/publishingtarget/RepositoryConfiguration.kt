package se.svt.oss.gradle.yapp.publishingtarget

import java.net.URI

data class RepositoryConfiguration(
    val uri: URI,
    val snapShotUri: URI,
    val name: String,
    val credential: RepositoryCredential
)

data class RepositoryCredential(val name: String, val value: String)
