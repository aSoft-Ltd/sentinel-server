plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("tz.co.asoft.library")
}

description = "A kotlin multiplatform server registration sdk"

kotlin {
    jvm { library() }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(projects.sentinelEnterpriseAuthenticationServiceFlix)
                api(libs.sentinel.registration.flix)
                api(libs.kase.response.ktor.server)
                api(ktor.server.core)
                api(kotlinx.serialization.json)
            }
        }
    }
}
