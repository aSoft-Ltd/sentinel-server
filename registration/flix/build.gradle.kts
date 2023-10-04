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
                api(libs.sentinel.registration.api.core)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(db.mongo)
            }
        }

        val commonTest by getting {
            dependencies {
                api(libs.kommander.coroutines)
            }
        }
    }
}
