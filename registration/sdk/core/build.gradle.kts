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
                api(projects.sentinelReceptionSdkClientCore)
                api(libs.sentinel.registration.api.core)
                api(libs.cinematic.scene.core)
                api(libs.hormone.core)
                api(libs.lexi.api)
                api(libs.identifier.fields)
            }
        }

        val commonTest by getting {
            dependencies {
                api(libs.cinematic.live.test)
                api(libs.kommander.coroutines)
            }
        }
    }
}
