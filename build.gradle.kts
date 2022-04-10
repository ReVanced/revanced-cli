plugins {
    kotlin("jvm") version "1.6.20"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "app.revanced"
version = "1.0"

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
}

val patchesDependency = "app.revanced:revanced-patches:1.0.0-dev.4"

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.4")

    implementation("app.revanced:revanced-patcher:1.0.0-dev.8")
    implementation(patchesDependency)

    implementation("com.google.code.gson:gson:2.9.0")
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        dependencies {
            // This makes sure we link to the library, but don't include it.
            // So, a "runtime only" dependency.
            exclude(dependency(patchesDependency))
        }
        manifest {
            attributes("Main-Class" to "app.revanced.cli.Main")
            attributes("Implementation-Title" to project.name)
            attributes("Implementation-Version" to project.version)
        }
    }
}