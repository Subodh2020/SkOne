plugins {
    alias(libs.plugins.skone.android.library)
    alias(libs.plugins.skone.publish)
}

android {
    namespace = "io.skone.common"
}

dependencies {
    implementation(libs.androidx.core.ktx)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
