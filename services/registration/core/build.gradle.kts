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
            api(libs.sentinel.schemes.registration.core)
        }
    }
}
