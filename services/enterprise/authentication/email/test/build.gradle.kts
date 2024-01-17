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
                api(projects.sentinelServicesEnterpriseAuthenticationEmailCore)
                api(projects.sentinelServicesRegistrationEmailCore)
                api(libs.raven.bus)
                api(libs.koncurrent.later.coroutines)
                api(libs.kommander.coroutines)
            }
        }
    }
}