plugins {
    kotlin("jvm") version "1.8.20"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "app.revanced"

val githubUsername: String = project.findProperty("gpr.user") as? String ?: System.getenv("GITHUB_ACTOR")
val githubPassword: String = project.findProperty("gpr.key") as? String ?: System.getenv("GITHUB_TOKEN")

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        url = uri("https://maven.pkg.github.com/revanced/revanced-patcher")
        credentials {
            username = githubUsername
            password = githubPassword
        }
    }
    maven { url = uri("https://jitpack.io") }
    google()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.20")

    implementation("app.revanced:revanced-patcher:12.0.0")
    implementation("info.picocli:picocli:4.7.3")
    implementation("com.github.revanced:jadb:2531a28109") // updated fork
    implementation("com.android.tools.build:apksig:8.1.0")
    implementation("org.bouncycastle:bcpkix-jdk15on:1.70")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.3")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.8.20-RC")
}

kotlin {
    jvmToolchain(11)
}

tasks {
    test {
        useJUnitPlatform()
        testLogging {
            events("PASSED", "SKIPPED", "FAILED")
        }
    }
    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        manifest {
            attributes("Main-Class" to "app.revanced.cli.main.MainKt")
        }
        minimize {
            exclude(dependency("org.jetbrains.kotlin:.*"))
            exclude(dependency("org.bouncycastle:.*"))
            exclude(dependency("app.revanced:.*"))
        }
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
