plugins {
    `java-platform`
    `maven-publish`
}

group = "io.skone"
version = "1.1.0-SNAPSHOT"

javaPlatform {
    allowDependencies()
}

dependencies {
    constraints {
        // Foundation + Design System artifacts
        api(project(":skone-common"))
        api(project(":skone-plugin"))
        api(project(":skone-theme"))
        api(project(":skone-core"))
        api(project(":skone-compose"))
        api(project(":skone-xml"))

        // Reserved future coordinates (documented; modules created in later phases):
        // io.skone:skone-ui
        // io.skone:skone-forms
        // io.skone:skone-navigation
        // io.skone:skone-feedback
        // io.skone:skone-validation
        // io.skone:skone-animation
        // io.skone:skone-ai-core
        // io.skone:skone-ai-ui
        // io.skone:skone-ai-chat
        // io.skone:skone-ai-voice
        // io.skone:skone-ai-image
        // io.skone:skone-camera
        // io.skone:skone-location
        // io.skone:skone-map
        // io.skone:skone-network
        // io.skone:skone-socket
        // io.skone:skone-auth
        // io.skone:skone-storage
    }
}

publishing {
    publications {
        create<MavenPublication>("bom") {
            from(components["javaPlatform"])
            artifactId = "skone-bom"
            pom {
                name.set("skone-bom")
                description.set("SKOne Bill of Materials")
                url.set("https://github.com/skone-io/skone")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
            }
        }
    }
}
