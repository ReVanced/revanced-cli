rootProject.name = "revanced-cli"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        maven {
            name = "githubPackages"
            url = uri("https://maven.pkg.github.com/revanced/cli")
            credentials(PasswordCredentials::class)
        }
    }
}
