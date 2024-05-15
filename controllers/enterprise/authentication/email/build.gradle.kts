plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("tz.co.asoft.library")
}

description = "A kotlin multiplatform sdk registration"

kotlin {
    jvm { library() }
//    js(IR) { library() }

    sourceSets {
        commonMain.dependencies {
            api(projects.sentinelServicesEnterpriseAuthenticationEmailCore)
            api(libs.koncurrent.later.coroutines)
            api(libs.kase.response.ktor.server)
            api(ktor.server.core)
            api(kotlinx.serialization.json)
        }
    }
}
