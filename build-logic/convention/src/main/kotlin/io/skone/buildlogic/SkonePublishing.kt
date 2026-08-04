package io.skone.buildlogic

import org.gradle.api.Project

/**
 * Centralized publishing metadata and helpers.
 *
 * Version & group are read from the root [gradle.properties] at configuration time
 * so every module stays aligned without duplicated constants.
 */
internal object SkonePublishing {
    const val DEFAULT_GROUP: String = "com.thesubodhgupta.skone"
    const val DEFAULT_VERSION: String = "1.3.2-alpha01"

    const val PROJECT_URL: String = "https://skone.thesubodhgupta.com"
    const val SCM_URL: String = "https://github.com/Subodh2020/SkOne"
    const val SCM_CONNECTION: String = "scm:git:git://github.com/Subodh2020/SkOne.git"
    const val SCM_DEV_CONNECTION: String = "scm:git:ssh://git@github.com/Subodh2020/SkOne.git"
    const val ISSUE_URL: String = "https://github.com/Subodh2020/SkOne/issues"

    const val LICENSE_NAME: String = "The Apache License, Version 2.0"
    const val LICENSE_URL: String = "https://www.apache.org/licenses/LICENSE-2.0.txt"

    const val DEVELOPER_ID: String = "subodhgupta"
    const val DEVELOPER_NAME: String = "Subodh Kumar Gupta"

    const val DESCRIPTION: String = "Open Android Developer Platform"

    /** Maven Central Portal Publisher API (zip upload) — not OSSRH. */
    const val CENTRAL_PORTAL_API: String = "https://central.sonatype.com/api/v1/publisher"
    const val CENTRAL_SNAPSHOT_URL: String =
        "https://central.sonatype.com/repository/maven-snapshots/"
    const val MAVEN_CENTRAL_REPO: String = "https://repo.maven.apache.org/maven2"

    fun groupId(project: Project): String =
        project.findProperty("GROUP")?.toString()
            ?: project.findProperty("skone.group")?.toString()
            ?: DEFAULT_GROUP

    fun versionName(project: Project): String =
        project.findProperty("VERSION_NAME")?.toString()
            ?: project.findProperty("skone.version")?.toString()
            ?: DEFAULT_VERSION

    fun displayName(artifactId: String): String {
        val rest = artifactId.removePrefix("skone-")
        if (rest.isEmpty() || rest == artifactId) return "SKOne"
        val pretty = rest.split("-").joinToString(" ") { part ->
            when (part.lowercase()) {
                "bom" -> "BOM"
                "ui" -> "UI"
                "xml" -> "XML"
                "ai" -> "AI"
                else -> part.replaceFirstChar { it.uppercase() }
            }
        }
        return "SKOne $pretty"
    }

    fun isSnapshot(version: String): Boolean =
        version.endsWith("-SNAPSHOT", ignoreCase = true)

    fun isPreRelease(version: String): Boolean {
        val lower = version.lowercase()
        return lower.contains("-alpha") ||
            lower.contains("-beta") ||
            lower.contains("-rc") ||
            isSnapshot(version)
    }

    fun envOrProperty(project: Project, envName: String, propertyName: String = envName): String? =
        System.getenv(envName)?.takeIf { it.isNotBlank() }
            ?: project.findProperty(propertyName)?.toString()?.takeIf { it.isNotBlank() }
}

/**
 * SDK level constants (not published coordinates).
 */
internal object SkoneVersions {
    const val MIN_SDK = 24
    const val COMPILE_SDK = 35
    const val TARGET_SDK = 35
}
