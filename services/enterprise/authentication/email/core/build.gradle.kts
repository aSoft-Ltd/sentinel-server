plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("tz.co.asoft.library")
}

description = "A kotlin multiplatform sdk registration"

kotlin {
    jvm { library() }
    js(IR) { library() }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(projects.sentinelServicesEnterpriseAuthenticationCore)
                api(libs.sentinel.schemes.enterprise.authentication.email)
                api(libs.raven.markup.html)
            }
        }
    }
}
