plugins {
    alias(libs.plugins.skone.android.library)
    alias(libs.plugins.skone.publishing)
}

android {
    namespace = "io.skone.forms"
}

dependencies {
    api(project(":skone-common"))
    api(project(":skone-core"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
