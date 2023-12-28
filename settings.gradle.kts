rootProject.name = "revanced-cli"

buildCache {
    local {
        isEnabled = "CI" !in System.getenv()
    }
}
