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
                implementation(projects.sentinelRegistrationServiceSdk)
            }
        }
    }
}

configure<DockateExtension> {
    val app = jvmImage(port = 8080)

    compose {
        version(3.8)

        val (database, root) = volumes("$name-database", "$name-root")

        val mongo = service(name = "mongo", image = "mongo:latest") {
            restart("always")
            port(27017, 2012)
            environment(
                "MONGO_INITDB_ROOT_USERNAME" to "root",
                "MONGO_INITDB_ROOT_PASSWORD" to "pass"
            )
            volume(database to "/data/db")
        }

        service(name = "server", image = app) {
            restart("always")
            port(8080, 2011)
            volume(root to "/app/root")
            dependsOn(mongo)
        }
    }

    // must come after compose block
    registry(
        name = "picortex",
        url = "http://65.21.254.230:1030",
        user = "root",
        pass = "bitframe"
    )
}