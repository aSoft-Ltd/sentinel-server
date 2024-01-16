plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("tz.co.asoft.library")
}

description = "A kotlin multiplatform sdk registration"

kotlin {
    jvm { library() }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(projects.sentinelEmails)
                api(libs.sentinel.schemes.enterprise.authentication.core)
                api(libs.raven.markup.html)
            }
        }
    }
}
