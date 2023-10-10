import com.android.build.gradle.internal.tasks.factory.dependsOn
import docker.DockateExtension
import docker.DockatePlugin
import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest

plugins {
    kotlin("jvm")
    id("tz.co.asoft.library")
    application
}

apply<DockatePlugin>()

application {
    mainClass.set("sentinel.MainKt")
}

kotlin {
    target {
        application()
    }

    sourceSets {
        val main by getting {
            dependencies {
                implementation(ktor.server.cio)
                implementation(ktor.server.cors)
                implementation(projects.sentinelRegistrationServiceSdk)
            }
        }

        val test by getting {
            dependencies {
                implementation(libs.kommander.coroutines)
            }
        }
    }
}

configure<DockateExtension> {
    val network = createNetwork("sentinel")

    val (runMongo, _, removeMongo) = addDockerContainerTasksForMongo(
        image = "mongodb/mongodb-community-server:7.0.0-ubuntu2204",
        network = network,
        username = "root",
        password = "pass",
        port = 27017
    )

    val (createDockerfile, buildAppImage, removeAppImage) = addDockerImageTasksForJvmApp(
        name = "app", port = 8080, directory = layout.buildDirectory.dir("install/$name")
    )

    val (runAppContainer, _, removeAppContainer) = addDockerContainerTasksFor(
        name = "app",
        image = "app",
        network = network,
        args = arrayOf("-p", "8080:8080")
    )

    createDockerfile.configure {
        dependsOn(tasks.named("installDist"))
    }

    runAppContainer.apply {
        dependsOn(runMongo)
        dependsOn(buildAppImage)
    }

    val removeNetwork = remove(network).apply {
        dependsOn(removeMongo,removeAppImage)
    }
    removeAppContainer.configure {
        finalizedBy(removeNetwork)
    }
}