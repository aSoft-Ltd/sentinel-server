import docker.DockateExtension
import docker.DockatePlugin

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
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
//                implementation(libs.lexi.config)
//                implementation(projects.sentinelRegistrationServiceSdk)
            }
        }
    }
}

configure<DockateExtension> {
    val base = "app/root"

    environments("Testing", "Development", "Staging", "Production") { env ->
        file("/$base/config.toml") {
            logging(level = "debug") {
                console(format = if (env in listOf("Testing", "Development")) "simple" else "json")
            }
        }
    }

    val app = image(name = "server") {
        from(OPEN_JDK_22_JDK_SLIM)
        expose(port = 8080)
        source(layout.buildDirectory.dir("install/${project.name}")) {
            dependsOn(tasks.named("installDist"))
        }
        copy("bin", "/app/bin")
        copy("lib", "/app/lib")
        copy("$base/config.toml","/$base/config.toml")
        volume("/app/root")
        cmd("/app/bin/$name")
    }

    compose("sentinel") {
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

        service(name = "app", image = app) {
            restart("always")
            port(80, 8080)
            volume(root to "/app/root")
            if (it.name == "Staging") port(82, 8080)
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

    registry(
        name = "asoft",
        url = "http://65.21.254.230:1030",
        user = "root",
        pass = "bitframe"
    )
}