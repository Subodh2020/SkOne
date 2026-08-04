package io.skone.buildlogic

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get

class PublishingConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("maven-publish")

            group = SkoneVersions.GROUP
            version = SkoneVersions.VERSION

            // Defer until after Android variants are created
            pluginManager.withPlugin("com.android.library") {
                extensions.configure<LibraryExtension> {
                    publishing {
                        singleVariant("release") {
                            withSourcesJar()
                        }
                    }
                }

                afterEvaluate {
                    extensions.configure<PublishingExtension> {
                        publications {
                            create<MavenPublication>("release") {
                                from(components["release"])
                                groupId = SkoneVersions.GROUP
                                artifactId = project.name
                                version = SkoneVersions.VERSION

                                pom {
                                    name.set(project.name)
                                    description.set("SKOne Android SDK — ${project.name}")
                                    url.set("https://github.com/skone-io/skone")
                                    licenses {
                                        license {
                                            name.set("The Apache License, Version 2.0")
                                            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                                        }
                                    }
                                    developers {
                                        developer {
                                            id.set("skone")
                                            name.set("SKOne Contributors")
                                        }
                                    }
                                    scm {
                                        connection.set("scm:git:git://github.com/skone-io/skone.git")
                                        developerConnection.set("scm:git:ssh://github.com/skone-io/skone.git")
                                        url.set("https://github.com/skone-io/skone")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
