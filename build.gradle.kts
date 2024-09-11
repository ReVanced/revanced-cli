import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.shadow)
    application
    `maven-publish`
    signing
}

group = "app.revanced"

application {
    mainClass = "app.revanced.cli.command.MainCommandKt"
}

repositories {
    mavenCentral()
    google()
    maven {
        // A repository must be specified for some reason. "registry" is a dummy.
        url = uri("https://maven.pkg.github.com/revanced/registry")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation(libs.revanced.patcher)
    implementation(libs.revanced.library)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.picocli)

    testImplementation(libs.kotlin.test)
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

java {
    targetCompatibility = JavaVersion.VERSION_11
}

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
        exclude("/prebuilt/linux/aapt", "/prebuilt/windows/aapt.exe", "/prebuilt/*/aapt_*")
        minimize {
            exclude(dependency("org.bouncycastle:.*"))
            exclude(dependency("app.revanced:revanced-patcher"))
        }
    }

    publish {
        dependsOn(shadowJar)
    }
}

// Needed by gradle-semantic-release-plugin.
// Tracking: https://github.com/KengoTODA/gradle-semantic-release-plugin/issues/435

// The maven-publish is also necessary to make the signing plugin work.
publishing {
    repositories {
        mavenLocal()
    }

    publications {
        create<MavenPublication>("revanced-cli-publication") {
            from(components["java"])
        }
    }
}

signing {
    useGpgCmd()

    sign(publishing.publications["revanced-cli-publication"])
}
