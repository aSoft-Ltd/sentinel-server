plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("tz.co.asoft.library")
}

kotlin {
    jvm { library() }
    js(IR) { library() }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(projects.sentinelEnterpriseAuthenticationApiCore)
                api(libs.pione.rest)
            }
        }

        val commonTest by getting {
            dependencies {
//                implementation(projects.sentinelEnterpriseAuthenticationApiPione)
                implementation(libs.koncurrent.later.coroutines)
                implementation(libs.koncurrent.later.coroutines)
                implementation(libs.pione.test)
            }
        }
    }
}