plugins {
    kotlin("jvm") version "1.8.20"
    alias(libs.plugins.shadow)
}

group = "app.revanced"

dependencies {
    implementation(libs.revanced.patcher)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.picocli)
    implementation(libs.jadb) // Updated fork
    implementation(libs.apksig)
    implementation(libs.bcpkix.jdk15on)
    implementation(libs.jackson.module.kotlin)
    testImplementation(libs.kotlin.test)
}

kotlin { jvmToolchain(11) }

tasks {
    test {
        useJUnitPlatform()
        testLogging {
            events("PASSED", "SKIPPED", "FAILED")
        }
    }

    processResources {
        expand("projectVersion" to project.version)
    }

    shadowJar {
        manifest {
            attributes("Main-Class" to "app.revanced.cli.command.MainCommandKt")
        }
        minimize {
            exclude(dependency("org.jetbrains.kotlin:.*"))
            exclude(dependency("org.bouncycastle:.*"))
            exclude(dependency("app.revanced:.*"))
        }
    }

    build {
        dependsOn(shadowJar)
    }

    // Dummy task to fix the Gradle semantic-release plugin.
    // Remove this if you forked it to support building only.
    // Tracking issue: https://github.com/KengoTODA/gradle-semantic-release-plugin/issues/435
    register<DefaultTask>("publish") {
        group = "publish"
        description = "Dummy task"
        dependsOn(build)
    }
}
