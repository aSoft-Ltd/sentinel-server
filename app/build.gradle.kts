plugins {
    kotlin("jvm")
    id("tz.co.asoft.library")
    application
}

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
                implementation(projects.sentinelRegistrationServiceSdk)
            }
        }

        val test by getting {
            dependencies {
                implementation(libs.kommander.coroutines)
            }
        }
    }

}
