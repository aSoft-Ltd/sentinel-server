import dockate.DockateExtension
import dockate.DockatePlugin
import dockate.builders.console
import dockate.builders.database
import dockate.builders.environments
import dockate.builders.logging
import dockate.builders.mail
import dockate.builders.recovery
import dockate.builders.verification
import dockate.models.Isolate

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
                implementation(libs.sanity.flix)
                implementation(libs.sanity.local)
                implementation(libs.grape.mongo)
                implementation(projects.sentinelRegistrationServiceSdk)
                implementation(projects.sentinelEnterpriseAuthenticationServiceSdk)
            }
        }
    }
}


enum class Deployment : Isolate {
    sentinel
}

configure<DockateExtension> {
    val environments = environments(Deployment.values(), "testing")
    image(environments, name = "app", dependsOn = tasks.named("installDist")) {
        from(OPEN_JDK_22_JDK_SLIM)
        expose(port = 8080)
        copy(layout.projectDirectory.dir("src/main/resources"), "/app/conf")
        from(layout.buildDirectory.dir("install/${project.name}")) {
            copy("bin", "/app/bin")
            copy("lib", "/app/lib")
        }
        copy("conf/config.toml") {
            database(
                url = "mongodb://root:pass@mongo:27017/",
                name = it.qualifier.dashed
            )

            blankline()

            logging(level = "debug") {
                console(format = "json")
            }

            mail(sender = "bus")
            mail(sender = "console")
            mail(
                sender = "smtp",
                host = "smtp.sendgrid.net",
                user = "apikey",
                password = "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"
            )

            verification(
                name = "PiCapital Reception",
                address = "reception@picapital.com",
                subject = "PiCapital Email Verification",
                template = "/app/conf/templates/registration/verification.txt"
            )

            recovery(
                name = "PiCapital Security",
                address = "security@picapital.com",
                subject = "PiCapital Account Recovery",
                template = "/app/conf/templates/authentication/recovery.txt"
            )
        }
        volume("/app/root")
        cmd("/app/bin/$name", "/app/conf/config.toml")
    }

    compose(environments) {
        version(3.8)

        val (database, root) = volumes("database", "root")

        mongo(username = "root", password = "pass", port = 27017) {
            volume(database to "/data/db")
        }

        service(name = "app", dependsOn = tasks.named("dockerImageBuildApp${it.qualifier.tasked}")) {
            image("app:$version")
            restart("always")
            port(8080, 8080)
            volume(root to "/app/root")
            dependsOn("mongo")
        }
    }
}