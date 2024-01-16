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
            api(libs.sentinel.dtos.registration.phone)
            api(libs.sentinel.schemes.registration.phone)
        }

        jvmMain.dependencies {
            implementation(db.mongo)
        }
    }
}
