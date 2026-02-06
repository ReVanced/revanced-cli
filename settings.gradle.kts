rootProject.name = "revanced-cli"

dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
        google()
        maven {
            name = "githubPackages"
            url = uri("https://maven.pkg.github.com/revanced/cli")
            credentials(PasswordCredentials::class)
        }
    }
}
