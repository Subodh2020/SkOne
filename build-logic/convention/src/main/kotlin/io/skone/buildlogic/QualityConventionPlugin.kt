package io.skone.buildlogic

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jlleitschuh.gradle.ktlint.KtlintExtension

/**
 * Applies Detekt and Ktlint with conservative defaults.
 * Full rule tuning lands in a later quality hardening pass.
 */
class QualityConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("io.gitlab.arturbosch.detekt")
            pluginManager.apply("org.jlleitschuh.gradle.ktlint")

            extensions.configure<DetektExtension> {
                buildUponDefaultConfig = true
                allRules = false
                parallel = true
                ignoreFailures = true
                config.setFrom(rootProject.files("config/detekt/detekt.yml"))
            }

            extensions.configure<KtlintExtension> {
                android.set(true)
                ignoreFailures.set(true)
                filter {
                    exclude("**/generated/**")
                    exclude("**/build/**")
                }
            }
        }
    }
}
