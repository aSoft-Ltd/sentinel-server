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
    "sentinel-core", "raven-core", "raven-client", "raven-server", "cinematic", "yeti",
    "sanity", "kiota", "grape"
).forEach { includeBuild("../$it") }

rootProject.name = "sentinel-server"

includeSubs("sentinel-services-reception", "services/reception", "flix")

includeSubs("sentinel-services-registration", "services/registration", "core")
includeSubs("sentinel-services-registration-email", "services/registration/email", "core", "flix", "test")
includeSubs("sentinel-services-registration-phone", "services/registration/phone", "core")
includeSubs("sentinel-services-enterprise-authentication", "services/enterprise/authentication", "core", "email")

includeSubs("sentinel-reception-service", "reception", "flix", "sdk")
includeSubs("sentinel-registration-service", "registration", "core", "flix", "sdk")
includeSubs("sentinel-enterprise-authentication-service", "enterprise/authentication", "core", "flix", "sdk")
includeSubs("sentinel", ".", "emails", "app")
