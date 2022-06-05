plugins {
    kotlin("jvm") version "1.6.21"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    java
    `maven-publish`
}

group = "app.revanced"

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        url = uri("https://maven.pkg.github.com/revanced/multidexlib2")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR") // DO NOT CHANGE!
            password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN") // DO NOT CHANGE!
        }
    }
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.21")
    implementation("app.revanced:revanced-patcher:1.0.0-dev.18")

    implementation("info.picocli:picocli:4.6.3")

    implementation("com.github.li-wjohnson:jadb:master-SNAPSHOT") // using a fork instead.
    implementation("org.bouncycastle:bcpkix-jdk15on:1.70")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.21")
}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        manifest {
            attributes("Main-Class" to "app.revanced.cli.MainKt")
            attributes("Implementation-Title" to project.name)
            attributes("Implementation-Version" to project.version)
        }
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/revanced/revanced-cli")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}
