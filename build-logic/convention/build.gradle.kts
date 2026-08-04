plugins {
    `kotlin-dsl`
}

group = "io.skone.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.detekt.gradlePlugin)
    compileOnly(libs.ktlint.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("skoneAndroidLibrary") {
            id = "skone.android.library"
            implementationClass = "io.skone.buildlogic.AndroidLibraryConventionPlugin"
        }
        register("skoneAndroidApplication") {
            id = "skone.android.application"
            implementationClass = "io.skone.buildlogic.AndroidApplicationConventionPlugin"
        }
        register("skonePublishing") {
            id = "skone.publishing"
            implementationClass = "io.skone.buildlogic.PublishingConventionPlugin"
        }
        register("skoneQuality") {
            id = "skone.quality"
            implementationClass = "io.skone.buildlogic.QualityConventionPlugin"
        }
    }
}
