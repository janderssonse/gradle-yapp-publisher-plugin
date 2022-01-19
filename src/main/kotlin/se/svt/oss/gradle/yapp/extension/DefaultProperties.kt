package se.svt.oss.gradle.yapp.extension

class DefaultProperties {
    companion object {
        // GitLabExtension
        const val DEFAULT_ENDPOINT_LEVEL = "project"
        // YappPublisherExtension
        const val DEFAULT_WITH_SOURCE_ARTIFACT = true
        const val DEFAULT_WITH_DOC_ARTIFACT = true
        // PropertyHandler
        const val DEFAULT_PROPERTY_STRING = ""
        const val DEFAULT_PROPERTY_BOOLEAN = false
        const val DEFAULT_PROPERTY_INT = 0
    }
}
