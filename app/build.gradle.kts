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
                implementation(libs.lexi.config)
                implementation(libs.raven.config)
                implementation(projects.sentinelRegistrationServiceSdk)
                implementation(projects.sentinelEnterpriseAuthenticationServiceSdk)
            }
        }
    }
}

configure<DockateExtension> {
    environments("Testing") { env ->
        file("/config.toml") {
            logging(level = "debug") {
                console(format = "json")
            }

            mail(sender = "flix")

            verification(
                name = "Sentinel Reception",
                address = "reception@sentinel.com",
                subject = "Sentinel Email Verification",
                template = "/app/root/templates/registration/verification.txt"
            )

            recovery(
                name = "Sentinel Security",
                address = "security@sentinel.com",
                subject = "Sentinel Account Recovery",
                template = "/app/root/templates/authentication/recovery.txt"
            )
        }
    }

    val app = image(name = "server") {
        from(OPEN_JDK_22_JDK_SLIM)
        expose(port = 8080)
        source(layout.buildDirectory.dir("install/${project.name}")) {
            dependsOn(tasks.named("installDist"))
        }
        source(layout.projectDirectory.dir("src/main/resources"))
        copy("bin", "/app/bin")
        copy("lib", "/app/lib")
        copy("templates","/app/root/templates")
        copy("config.toml", "/app/root/config.toml")
        cmd("/app/bin/$name")
    }

    compose("sentinel") {
        version(3.8)

        val (database, root) = volumes("$name-database", "$name-root")

        val mng = mongo(username = "root", password = "pass", port = 8079) {
            volume(database to "/data/db")
        }

        service(name = "app", image = app) {
            restart("always")
            port(8080, 8080)
            volume(root to "/app/root")
            dependsOn(mng)
        }
    }
}