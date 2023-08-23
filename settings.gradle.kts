val githubUsername: String = providers.gradleProperty("gpr.user").orNull ?: System.getenv("GITHUB_ACTOR")
val githubPassword: String = providers.gradleProperty("gpr.key").orNull ?: System.getenv("GITHUB_TOKEN")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        mavenLocal()
        google()
        maven { url = uri("https://jitpack.io") }
        listOf("revanced-patcher", "jadb").forEach { repo ->
            maven {
                url = uri("https://maven.pkg.github.com/revanced/$repo")
                credentials {
                    username = githubUsername
                    password = githubPassword
                }
            }
        }
    }
}

rootProject.name = "revanced-cli"