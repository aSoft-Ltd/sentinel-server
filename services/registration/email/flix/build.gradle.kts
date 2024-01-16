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
            api(projects.sentinelServicesRegistrationEmailCore)
            api(projects.sentinelServicesReceptionFlix)
            api(libs.raven.api)
            api(libs.raven.markup.html)
            api(libs.raven.config)
            api(libs.krono.kotlinx)
        }

        jvmMain.dependencies {
            implementation(db.mongo)
            implementation(libs.raven.smtp)
            implementation(libs.raven.mock)
            implementation(libs.yeti.core)
        }
    }
}
