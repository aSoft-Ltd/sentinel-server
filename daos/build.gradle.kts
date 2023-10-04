plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("tz.co.asoft.library")
}

kotlin {
    jvm { library() }
    if (Targeting.JS) js(IR) { library() }
//    if (Targeting.WASM) wasm { library() }
    if (Targeting.OSX) osxTargets() else listOf()
//    if (Targeting.NDK) ndkTargets() else listOf()
    if (Targeting.LINUX) linuxTargets() else listOf()
    if (Targeting.MINGW) mingwTargets() else listOf()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.sentinel.enterprise.authentication.api.core)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kommander.coroutines)
            }
        }
    }
}
