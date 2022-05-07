plugins {
    kotlin("jvm") version "1.6.20"
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
    implementation(kotlin("stdlib"))
    implementation("app.revanced:revanced-patcher:+")
    implementation("info.picocli:picocli:+")

    implementation("me.tongfei:progressbar:+")
    implementation("com.github.li-wjohnson:jadb:master-SNAPSHOT") // using a fork instead.
    implementation("org.bouncycastle:bcpkix-jdk15on:+")
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