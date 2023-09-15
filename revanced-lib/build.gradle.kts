plugins {
    kotlin("jvm") version "1.9.0"
    `maven-publish`
}

dependencies {
    testImplementation(libs.kotlin.test)
}

kotlin { jvmToolchain(11) }

java {
    withSourcesJar()
}

tasks {
    test {
        useJUnitPlatform()
        testLogging {
            events("PASSED", "SKIPPED", "FAILED")
        }
    }
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
                description = "Library for ReVanced"
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