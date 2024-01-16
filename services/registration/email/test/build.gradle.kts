plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("tz.co.asoft.library")
}

description = "A kotlin multiplatform sdk registration"

kotlin {
    jvm { library() }

    sourceSets {
        commonMain.dependencies {
            api(projects.sentinelEmails)
            api(projects.sentinelServicesRegistrationEmailCore)
            api(libs.raven.mock)
            api(libs.kommander.coroutines)
            api(libs.koncurrent.later.coroutines)
        }
    }
}
