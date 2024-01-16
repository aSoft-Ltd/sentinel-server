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
            implementation(libs.kommander.coroutines)
            implementation(libs.koncurrent.later.coroutines)
        }
    }
}
