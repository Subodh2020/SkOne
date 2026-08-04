plugins {
    alias(libs.plugins.skone.android.library)
    alias(libs.plugins.skone.publish)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.paparazzi)
}

android {
    namespace = "io.skone.compose"

    buildFeatures {
        compose = true
    }

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    api(project(":skone-theme"))
    api(project(":skone-core"))
    api(project(":skone-ui"))
    api(project(":skone-forms"))

    implementation(libs.androidx.core.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.runtime)
    implementation(libs.androidx.material3)

    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.11.4")

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    debugImplementation(libs.androidx.ui.test.manifest)
    debugImplementation(libs.androidx.ui.tooling)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
