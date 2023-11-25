plugins {
    kotlin("jvm") version "1.9.10"
    alias(libs.plugins.shadow)
}

group = "app.revanced"

repositories {
    mavenCentral()
    mavenLocal()
    google()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation(libs.revanced.patcher)
    implementation(libs.revanced.library)
    implementation(libs.kotlinx.coroutines.core)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
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

    /*
    Dummy task to hack gradle-semantic-release-plugin to release this project.

    Explanation:
    SemVer is a standard for versioning libraries.
    For that reason the semantic-release plugin uses the "publish" task to publish libraries.
    However, this subproject is not a library, and the "publish" task is not available for this subproject.
    Because semantic-release is not designed to handle this case, we need to hack it.

    RE: https://github.com/KengoTODA/gradle-semantic-release-plugin/issues/435
     */

    register<DefaultTask>("publish") {
        group = "publishing"
        description = "Dummy task to hack gradle-semantic-release-plugin to release ReVanced CLI"
        dependsOn(build)
    }
}
