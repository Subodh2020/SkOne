package io.skone.buildlogic

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.signing.SigningExtension

/**
 * Production publishing convention for every SKOne library / BOM module.
 *
 * Apply with:
 * ```
 * plugins {
 *     id("skone.publish")
 * }
 * ```
 *
 * Configures Maven Central–ready publications (AAR/JAR, sources, Dokka Javadoc, POM)
 * plus GPG signing. Central uploads use root [nmcpAggregation] (Publisher API) — not OSSRH.
 */
class PublishingConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("maven-publish")
            pluginManager.apply("signing")

            group = SkonePublishing.groupId(this)
            version = SkonePublishing.versionName(this)

            pluginManager.withPlugin("com.android.library") {
                configureAndroidLibraryPublishing()
            }
            pluginManager.withPlugin("java-platform") {
                configureJavaPlatformPublishing()
            }
            pluginManager.withPlugin("java-library") {
                if (!pluginManager.hasPlugin("com.android.library") &&
                    !pluginManager.hasPlugin("java-platform")
                ) {
                    configureJavaLibraryPublishing()
                }
            }
        }
    }

    private fun Project.configureAndroidLibraryPublishing() {
        pluginManager.apply("org.jetbrains.dokka")

        extensions.configure<LibraryExtension> {
            publishing {
                singleVariant("release") {
                    withSourcesJar()
                }
            }
        }

        val dokkaJavadocJar = registerDokkaJavadocJar()

        afterEvaluate {
            extensions.configure<PublishingExtension> {
                publications {
                    create<MavenPublication>("release") {
                        from(components["release"])
                        applyCoordinates(project)
                        artifact(dokkaJavadocJar)
                        configurePom(project)
                    }
                }
            }
            finalizePublishingSetup()
        }
    }

    private fun Project.configureJavaPlatformPublishing() {
        afterEvaluate {
            extensions.configure<PublishingExtension> {
                publications {
                    create<MavenPublication>("release") {
                        from(components["javaPlatform"])
                        applyCoordinates(project)
                        configurePom(project)
                    }
                }
            }
            finalizePublishingSetup()
        }
    }

    private fun Project.configureJavaLibraryPublishing() {
        pluginManager.apply("org.jetbrains.dokka")

        val dokkaJavadocJar = registerDokkaJavadocJar()
        val javaExt = extensions.getByType(org.gradle.api.plugins.JavaPluginExtension::class.java)
        javaExt.withSourcesJar()

        afterEvaluate {
            extensions.configure<PublishingExtension> {
                publications {
                    create<MavenPublication>("release") {
                        from(components["java"])
                        applyCoordinates(project)
                        artifact(dokkaJavadocJar)
                        configurePom(project)
                    }
                }
            }
            finalizePublishingSetup()
        }
    }

    private fun Project.finalizePublishingSetup() {
        configureLocalRepositories()
        configureSigning()
        registerValidationTasks()
        registerPrintPublishingInfo()
    }

    private fun Project.registerDokkaJavadocJar() = tasks.register<Jar>("dokkaJavadocJar") {
        group = "documentation"
        description = "Assembles a jar archive containing Dokka Javadoc"
        archiveClassifier.set("javadoc")
        dependsOn(tasks.named("dokkaJavadoc"))
        from(layout.buildDirectory.dir("dokka/javadoc"))
    }

    private fun MavenPublication.applyCoordinates(project: Project) {
        groupId = SkonePublishing.groupId(project)
        artifactId = project.name
        version = SkonePublishing.versionName(project)
    }

    private fun MavenPublication.configurePom(project: Project) {
        val artifactId = project.name
        pom {
            name.set(SkonePublishing.displayName(artifactId))
            description.set("${SkonePublishing.DESCRIPTION} — ${SkonePublishing.displayName(artifactId)}")
            url.set(SkonePublishing.PROJECT_URL)
            inceptionYear.set("2026")
            licenses {
                license {
                    name.set(SkonePublishing.LICENSE_NAME)
                    url.set(SkonePublishing.LICENSE_URL)
                    distribution.set("repo")
                }
            }
            developers {
                developer {
                    id.set(SkonePublishing.DEVELOPER_ID)
                    name.set(SkonePublishing.DEVELOPER_NAME)
                }
            }
            scm {
                url.set(SkonePublishing.SCM_URL)
                connection.set(SkonePublishing.SCM_CONNECTION)
                developerConnection.set(SkonePublishing.SCM_DEV_CONNECTION)
            }
            issueManagement {
                system.set("GitHub")
                url.set(SkonePublishing.ISSUE_URL)
            }
        }
    }

    /** Local-only repos. Central uploads go through nmcp Portal Publisher API (zip). */
    private fun Project.configureLocalRepositories() {
        extensions.configure<PublishingExtension> {
            repositories {
                maven {
                    name = "LocalTest"
                    url = uri(rootProject.layout.buildDirectory.dir("local-maven-repo"))
                }
            }
        }
    }

    private fun Project.configureSigning() {
        val key = SkonePublishing.envOrProperty(this, "SIGNING_KEY")
        val password = SkonePublishing.envOrProperty(this, "SIGNING_PASSWORD").orEmpty()
        val publishing = extensions.getByType<PublishingExtension>()
        val signing = extensions.getByType<SigningExtension>()

        if (!key.isNullOrBlank()) {
            signing.useInMemoryPgpKeys(key, password)
            signing.sign(publishing.publications)
            signing.isRequired = true
        } else {
            signing.isRequired = false
        }
    }

    private fun Project.registerPrintPublishingInfo() {
        if (tasks.findByName("printPublishingInfo") != null) return

        tasks.register("printPublishingInfo") {
            group = "publishing"
            description = "Prints Maven publication coordinates and signing status"
            doLast {
                val groupId = SkonePublishing.groupId(project)
                val version = SkonePublishing.versionName(project)
                val signing = project.extensions.getByType<SigningExtension>()
                val signed = !SkonePublishing.envOrProperty(project, "SIGNING_KEY").isNullOrBlank() &&
                    signing.isRequired
                val publishing = project.extensions.getByType<PublishingExtension>()
                val pubs = publishing.publications.withType<MavenPublication>()
                    .joinToString(", ") { it.name }
                    .ifBlank { "(none)" }
                val repos = publishing.repositories
                    .map { it.name }
                    .filterNot { it.equals("nmcp", ignoreCase = true) }
                    .joinToString(", ")
                    .ifBlank { "LocalTest (via publishToLocalTestRepository)" }

                logger.lifecycle("----------------------------------------")
                logger.lifecycle("Module : ${project.name}")
                logger.lifecycle("Group  : $groupId")
                logger.lifecycle("Artifact : ${project.name}")
                logger.lifecycle("Version : $version")
                logger.lifecycle("Publications : $pubs")
                logger.lifecycle("Local repositories : $repos")
                logger.lifecycle("Repository : Maven Central Portal")
                logger.lifecycle("Signed : $signed")
                logger.lifecycle("----------------------------------------")
            }
        }
    }

    private fun Project.registerValidationTasks() {
        if (tasks.findByName("verifyPom") != null) return

        tasks.register("verifyPom") {
            group = "publishing"
            description = "Validates Maven POM coordinates and required metadata"
            doLast {
                val groupId = SkonePublishing.groupId(project)
                val version = SkonePublishing.versionName(project)
                require(groupId.startsWith("com.thesubodhgupta.skone") || groupId == SkonePublishing.DEFAULT_GROUP) {
                    "Unexpected groupId='$groupId'. Expected ${SkonePublishing.DEFAULT_GROUP}"
                }
                require(version.isNotBlank()) { "VERSION_NAME must not be blank" }
                require(project.name.startsWith("skone-")) {
                    "Publishable modules must be named skone-* (was '${project.name}')"
                }
                val publishing = project.extensions.getByType<PublishingExtension>()
                require(publishing.publications.isNotEmpty()) {
                    "No Maven publications registered for ${project.path}"
                }
                publishing.publications.withType<MavenPublication>().forEach { pub ->
                    require(pub.groupId == groupId) { "Publication ${pub.name} groupId mismatch" }
                    require(pub.artifactId == project.name) { "Publication ${pub.name} artifactId mismatch" }
                    require(pub.version == version) { "Publication ${pub.name} version mismatch" }
                }
                logger.lifecycle("verifyPom OK: $groupId:${project.name}:$version")
            }
        }

        tasks.register("verifySigning") {
            group = "publishing"
            description = "Validates that signing is configured when SIGNING_KEY is present"
            doLast {
                val key = SkonePublishing.envOrProperty(project, "SIGNING_KEY")
                if (!key.isNullOrBlank()) {
                    val signing = project.extensions.getByType<SigningExtension>()
                    require(signing.isRequired) {
                        "SIGNING_KEY is set but signing.required is false"
                    }
                    logger.lifecycle("verifySigning OK: in-memory PGP signing enabled")
                } else {
                    logger.lifecycle("verifySigning SKIP: SIGNING_KEY not set (local/dev mode)")
                }
            }
        }

        tasks.register("verifyPublishing") {
            group = "publishing"
            description = "Runs all publishing validation checks for this module"
            dependsOn("verifyPom", "verifySigning", "printPublishingInfo")
        }

        tasks.register("publishToLocalTestRepository") {
            group = "publishing"
            description = "Publishes this module to build/local-maven-repo"
            dependsOn("publishReleasePublicationToLocalTestRepository")
        }
    }
}
