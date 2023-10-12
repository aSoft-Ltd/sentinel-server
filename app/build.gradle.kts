import docker.DockateExtension
import docker.DockatePlugin

plugins {
    kotlin("jvm")
    id("tz.co.asoft.library")
    application
}

apply<DockatePlugin>()

application {
    mainClass.set("sentinel.MainKt")
}

kotlin {
    target {
        application()
    }

    sourceSets {
        val main by getting {
            dependencies {
                implementation(ktor.server.cio)
                implementation(ktor.server.cors)
                // implementation(projects.sentinelRegistrationServiceSdk)
            }
        }

        val test by getting {
            dependencies {
                // implementation(libs.kommander.coroutines)
            }
        }
    }
}

configure<DockateExtension> {
    addDockerImageTasksForJvmApp(name = "app", port = 8080)

    dockerCompose {
        version(3.8)
        services {
            val mongo = service("mongo") {
//                image("mongodb/mongodb-community-server:7.0.0-ubuntu2204")
                image("mongo:latest")
                restart("always")
                port(27017, 27017)
                environment(
                    "MONGO_INIT_ROOT_USERNAME" to "root",
                    "MONGO_INIT_ROOT_PASSWORD" to "pass"
                )
                volumes(
                    "./data/logs" to "/data/logs",
                    "./data/db" to "/data/db",
                    "./data/configdb" to "/data/configdb",
                )
            }

            service("server") {
                image("$name:$version")
                restart("always")
                port(8080, 8080)
                dependsOn(mongo)
            }
        }
    }
}
