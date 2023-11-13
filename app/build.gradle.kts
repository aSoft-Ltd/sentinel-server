//import docker.DockateExtension
//import docker.DockatePlugin

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("tz.co.asoft.library")
    application
}

//apply<DockatePlugin>()

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

//configure<DockateExtension> {
//    environments("Testing") { env ->
//        file("/config.toml") {
//            logging(level = "debug") {
//                console(format = "json")
//            }
//
//            database(
//                url = "mongodb://root:pass@mongo:27017",
//                name = "sentinel-${env.lowercase()}"
//            )
//
//            mail(sender = "console")
//            mail(sender = "bus")
//
//            verification(
//                name = "Sentinel Reception",
//                address = "reception@sentinel.com",
//                subject = "Sentinel Email Verification",
//                template = "/app/conf/templates/registration/verification.txt"
//            )
//
//            recovery(
//                name = "Sentinel Security",
//                address = "security@sentinel.com",
//                subject = "Sentinel Account Recovery",
//                template = "/app/conf/templates/authentication/recovery.txt"
//            )
//        }
//    }
//
//    val app = image(name = "sentinel") {
//        from(OPEN_JDK_22_JDK_SLIM)
//        expose(port = 8080)
//        source(layout.buildDirectory.dir("install/${project.name}")) {
//            dependsOn(tasks.named("installDist"))
//        }
//        source(layout.projectDirectory.dir("src/main/resources"))
//        copy("bin", "/app/bin")
//        copy("lib", "/app/lib")
//        copy("templates", "/app/conf/templates")
//        copy("config.toml", "/app/conf/config.toml")
//        cmd("/app/bin/$name","/app/conf/config.toml")
//    }
//
//    compose("sentinel") {
//        version(3.8)
//
//        val (database, root) = volumes("$name-database", "$name-root")
//
//        val mng = mongo(username = "root", password = "pass", port = 8079) {
//            volume(database to "/data/db")
//        }
//
//        service(name = "app", image = app) {
//            restart("always")
//            port(8080, 8080)
//            volume(root to "/app/root")
//            dependsOn(mng)
//        }
//    }
//
//    registry(
//        name = "legacy",
//        url = "http://192.168.1.109:1030",
//        user = "andylamax",
//        pass = "andymamson",
//        workdir = "/testing"
//    )
//}