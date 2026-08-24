import java.time.Duration

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
    alias(libs.plugins.nmcp.aggregation)
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

/**
 * Aggregates all skone.publish modules into one Central Portal deployment (zip + Publisher API).
 *
 * Credentials are read from the environment. Tasks that upload fail fast if missing.
 */
nmcpAggregation {
    publishAllProjectsProbablyBreakingProjectIsolation()
    centralPortal {
        username.set(
            providers.environmentVariable("CENTRAL_PORTAL_USERNAME")
                .orElse(providers.gradleProperty("CENTRAL_PORTAL_USERNAME"))
                .orElse(""),
        )
        password.set(
            providers.environmentVariable("CENTRAL_PORTAL_PASSWORD")
                .orElse(providers.gradleProperty("CENTRAL_PORTAL_PASSWORD"))
                .orElse(""),
        )
        // AUTOMATIC = upload → validate → publish. USER_MANAGED = stop at VALIDATED.
        publishingType.set(
            providers.environmentVariable("CENTRAL_PUBLISHING_TYPE").orElse("AUTOMATIC"),
        )
        publicationName.set(
            providers.provider {
                "SKOne ${findProperty("VERSION_NAME") ?: "unknown"}"
            },
        )
        validationTimeout.set(Duration.ofMinutes(15))
        // publishingTimeout is only valid for AUTOMATIC; CI uses USER_MANAGED + central-portal.sh.
    }
}

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

tasks.register("printPublishingInfo") {
    group = "publishing"
    description = "Prints publication coordinates for every publishable module"
    dependsOn(publishableProjects.map { "$it:printPublishingInfo" })
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

tasks.register("assembleDocsSite") {
    group = "documentation"
    description = "Assembles docs-site plus browsable Dokka API reference for static hosting"
    dependsOn("dokkaHtmlAll")

    val dokkaModules = listOf(
        "skone-common",
        "skone-plugin",
        "skone-theme",
        "skone-core",
        "skone-ui",
        "skone-forms",
        "skone-compose",
        "skone-xml",
    )

    doLast {
        val deployDir = layout.buildDirectory.dir("docs-site").get().asFile
        if (deployDir.exists()) {
            deployDir.deleteRecursively()
        }
        deployDir.mkdirs()

        copy {
            from("docs-site")
            into(deployDir)
            exclude("api-index.html", "README.md")
        }

        val version = findProperty("VERSION_NAME")?.toString() ?: "unknown"
        val apiRoot = deployDir.resolve("api")
        apiRoot.mkdirs()

        dokkaModules.forEach { module ->
            val source = file("$module/build/dokka/html")
            require(source.isDirectory) {
                "Missing Dokka HTML for $module at ${source.absolutePath}. Run dokkaHtmlAll first."
            }
            copy {
                from(source)
                into(apiRoot.resolve(module))
            }
        }

        val apiIndexTemplate = file("docs-site/api-index.html")
        require(apiIndexTemplate.isFile) {
            "Missing API index template at ${apiIndexTemplate.absolutePath}"
        }
        val apiIndex = apiIndexTemplate.readText().replace("@VERSION@", version)
        apiRoot.resolve("index.html").writeText(apiIndex)

        logger.lifecycle("Assembled documentation site at: ${deployDir.absolutePath}")
        logger.lifecycle("API reference landing page: ${apiRoot.resolve("index.html").absolutePath}")
    }
}

tasks.register("requireCentralSecrets") {
    group = "publishing"
    description = "Fails if Maven Central / signing secrets are missing"
    doLast {
        val required = listOf(
            "SIGNING_KEY",
            "SIGNING_PASSWORD",
            "CENTRAL_PORTAL_USERNAME",
            "CENTRAL_PORTAL_PASSWORD",
        )
        val missing = required.filter { name ->
            System.getenv(name).isNullOrBlank() && findProperty(name)?.toString().isNullOrBlank()
        }
        if (missing.isNotEmpty()) {
            throw GradleException(
                "Missing required publishing secrets: ${missing.joinToString()}. " +
                    "Set them as environment variables before Central publish.",
            )
        }
        logger.lifecycle("✓ Required Central publishing secrets are present")
    }
}
