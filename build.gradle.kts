plugins {
    kotlin("jvm") version "1.6.20"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "app.revanced"

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        url = uri("https://maven.pkg.github.com/ReVancedTeam/multidexlib2")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR") // DO NOT CHANGE!
            password = project.findProperty("gpr.key")  as String? ?: System.getenv("GITHUB_TOKEN") // DO NOT CHANGE!
        }
    }
    maven {
        url = uri("https://jitpack.io")
    }
}

val patchesDependency = "app.revanced:revanced-patches:1.0.0-dev.4"

dependencies {
    implementation(kotlin("stdlib"))
    implementation("app.revanced:revanced-patcher:+")
    implementation(patchesDependency)
    implementation("info.picocli:picocli:+")
    implementation("org.bouncycastle:bcpkix-jdk15on:+")
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        dependencies {
            exclude(dependency(patchesDependency))
        }
        manifest {
            attributes("Main-Class" to "app.revanced.cli.MainKt")
            attributes("Implementation-Title" to project.name)
            attributes("Implementation-Version" to project.version)
        }
    }
}