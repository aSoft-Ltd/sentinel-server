plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("tz.co.asoft.library")
}

description = "A kotlin multiplatform service implementation for the flix pattern"

kotlin {
    jvm { library() }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(projects.sentinelEnterpriseAuthenticationServiceCore)
                api(projects.sentinelReceptionServiceFlix)
                api(libs.kase.response.core)
                api(libs.raven.config)
                api(libs.lexi.api)
            }
        }

        val jvmMain by getting {
            dependencies {
                api(db.mongo)
                api(libs.raven.smtp)
                api(libs.lexi.console)
                api(libs.raven.mock)
                api(libs.yeti.core)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kommander.coroutines)
            }
        }
    }
}
