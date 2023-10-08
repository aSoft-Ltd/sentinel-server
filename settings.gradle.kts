import java.io.File

pluginManagement {
    includeBuild("../build-logic")
}

plugins {
    id("multimodule")
}

fun includeSubs(base: String, path: String = base, vararg subs: String) {
    subs.forEach {
        include(":$base-$it")
        project(":$base-$it").projectDir = File("$path/$it")
    }
}

listOf(
    "lexi", "neat", "kash-api", "geo-api", "kase", "keep",
    "kronecker", "epsilon-api", "krono-core", "hormone", "identifier-api",
    "kommerce", "kollections", "koncurrent", "kommander", "cabinet-api",
    "sentinel-core", "raven-client", "cinematic", "yeti"
).forEach { includeBuild("../$it") }

rootProject.name = "sentinel-server"

includeSubs("sentinel-registration-service", "registration", "core", "flix")
includeSubs("sentinel-server", ".", "app")