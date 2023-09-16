plugins {
    kotlin("jvm") version "1.9.0"
    alias(libs.plugins.shadow)
}

dependencies {
    implementation(project(":revanced-lib"))
    implementation(libs.revanced.patcher)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.picocli)

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
