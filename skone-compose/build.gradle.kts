plugins {
    alias(libs.plugins.skone.android.library)
    alias(libs.plugins.skone.publishing)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "io.skone.compose"

    buildFeatures {
        compose = true
    }
}

dependencies {
    api(project(":skone-theme"))
    api(project(":skone-core"))

    implementation(libs.androidx.core.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.runtime)
    implementation(libs.androidx.material3)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
