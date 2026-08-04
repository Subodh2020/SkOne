plugins {
    `java-platform`
    alias(libs.plugins.skone.publish)
}

javaPlatform {
    allowDependencies()
}

dependencies {
    constraints {
        api(project(":skone-common"))
        api(project(":skone-plugin"))
        api(project(":skone-theme"))
        api(project(":skone-core"))
        api(project(":skone-compose"))
        api(project(":skone-xml"))
        api(project(":skone-ui"))
        api(project(":skone-forms"))

        // Future modules automatically join the BOM when created + constrained here:
        // api(project(":skone-navigation"))
        // api(project(":skone-feedback"))
        // api(project(":skone-animation"))
        // api(project(":skone-ai-core"))
        // api(project(":skone-ai-ui"))
        // api(project(":skone-camera"))
        // api(project(":skone-map"))
        // api(project(":skone-location"))
        // api(project(":skone-network"))
        // api(project(":skone-auth"))
        // api(project(":skone-storage"))
    }
}
