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
            api(projects.sentinelServicesRegistrationCore)
            api(libs.sentinel.dtos.registration.email)
            api(libs.sentinel.schemes.registration.email)
            api(libs.raven.api)
        }

        jvmMain.dependencies {
            implementation(db.mongo)
            implementation(libs.raven.smtp)
            implementation(libs.raven.mock)
            implementation(libs.yeti.core)
        }
    }
}
