plugins {
    kotlin("jvm") version "1.9.0"
    alias(libs.plugins.binary.compatibility.validator)
    `maven-publish`
}

dependencies {
    implementation(libs.revanced.patcher)
    implementation(libs.kotlin.reflect)
    implementation(libs.jadb) // Updated fork
    implementation(libs.apksig)
    implementation(libs.bcpkix.jdk15on)
    implementation(libs.jackson.module.kotlin)

    testImplementation(libs.revanced.patcher)
    testImplementation(libs.kotlin.test)
}

tasks {
    test {
        useJUnitPlatform()
        testLogging {
            events("PASSED", "SKIPPED", "FAILED")
        }
    }
}

kotlin { jvmToolchain(11) }

java {
    withSourcesJar()
}

publishing {
    repositories {
        mavenLocal()
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
        create<MavenPublication>("gpr") {
            from(components["java"])

            version = project.version.toString()

            pom {
                name = "ReVanced Library"
                description = "Library containing common utilities for ReVanced"
                url = "https://revanced.app"

                licenses {
                    license {
                        name = "GNU General Public License v3.0"
                        url = "https://www.gnu.org/licenses/gpl-3.0.en.html"
                    }
                }
                developers {
                    developer {
                        id = "ReVanced"
                        name = "ReVanced"
                        email = "contact@revanced.app"
                    }
                }
                scm {
                    connection = "scm:git:git://github.com/revanced/revanced-cli.git"
                    developerConnection = "scm:git:git@github.com:revanced/revanced-cli.git"
                    url = "https://github.com/revanced/revanced-cli"
                }
            }
        }
    }
}