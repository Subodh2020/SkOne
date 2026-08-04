plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.paparazzi) apply false
}

val publishableProjects = listOf(
    ":skone-bom",
    ":skone-common",
    ":skone-plugin",
    ":skone-theme",
    ":skone-core",
    ":skone-ui",
    ":skone-forms",
    ":skone-compose",
    ":skone-xml",
)

tasks.register("verifyPublishing") {
    group = "publishing"
    description = "Validates publishing configuration for all publishable modules"
    dependsOn(publishableProjects.map { "$it:verifyPublishing" })
}

tasks.register("verifyPom") {
    group = "publishing"
    description = "Validates POM metadata for all publishable modules"
    dependsOn(publishableProjects.map { "$it:verifyPom" })
}

tasks.register("verifySigning") {
    group = "publishing"
    description = "Validates signing configuration for all publishable modules"
    dependsOn(publishableProjects.map { "$it:verifySigning" })
}

tasks.register("publishToLocalTestRepository") {
    group = "publishing"
    description = "Publishes all SKOne artifacts to build/local-maven-repo"
    dependsOn(publishableProjects.map { "$it:publishToLocalTestRepository" })
}

tasks.register("dokkaHtmlAll") {
    group = "documentation"
    description = "Generates Dokka HTML for every Android library module"
    dependsOn(
        listOf(
            ":skone-common",
            ":skone-plugin",
            ":skone-theme",
            ":skone-core",
            ":skone-ui",
            ":skone-forms",
            ":skone-compose",
            ":skone-xml",
        ).map { "$it:dokkaHtml" },
    )
}
