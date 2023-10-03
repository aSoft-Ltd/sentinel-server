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
    "cinematic", "keep", "lexi", "captain", "neat", "kash-api", "geo-api", "kase",
    "kash-client", "geo-client",
    "kronecker", "epsilon-api", "epsilon-client", "krono", "hormone", "identifier",
    "kommerce", "kollections", "koncurrent", "kommander", "cabinet-api", "cabinet-picortex", "pione", "snitch"
).forEach { includeBuild("../$it") }

rootProject.name = "sentinel-server"

includeSubs(base = "sentinel-reception-api", path = "../sentinel/reception/api", "core")
includeSubs(base = "sentinel-registration-api", path = "../sentinel/registration/api", "core", "pione")
includeSubs(base = "sentinel-enterprise-authentication-api", path = "../sentinel/enterprise/authentication/api", "core", "pione")
includeSubs(base = "sentinel-enterprise-profile-api", path = "../sentinel/enterprise/profile/api", "core", "pione")
