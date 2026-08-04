plugins {
    alias(libs.plugins.skone.android.library)
    alias(libs.plugins.skone.publishing)
}

android {
    namespace = "io.skone"
}

dependencies {
    api(project(":skone-common"))
    api(project(":skone-plugin"))
    api(project(":skone-theme"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.kotlinx.coroutines.test)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
