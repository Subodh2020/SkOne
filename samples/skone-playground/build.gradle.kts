plugins {
    alias(libs.plugins.skone.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "io.skone.playground"

    defaultConfig {
        applicationId = "io.skone.playground"
        versionCode = 1
        versionName = "1.3.1"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":skone-core"))
    implementation(project(":skone-compose"))
    implementation(project(":skone-ui"))
    implementation(project(":skone-forms"))
    implementation(project(":skone-xml"))
    implementation(project(":skone-theme"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.material3)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.jupiter.engine)

    debugImplementation(libs.androidx.ui.tooling)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
