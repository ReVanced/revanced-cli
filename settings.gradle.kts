rootProject.name = "revanced-cli"

buildCache {
    local {
        isEnabled = !System.getenv().containsKey("CI")
    }
}
