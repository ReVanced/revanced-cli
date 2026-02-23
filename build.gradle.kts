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

dependencies {
    implementation(libs.bcpg.jdk18on)
    implementation(libs.sigstore.java)
    implementation(libs.revanced.patcher)
    implementation(libs.revanced.library)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.picocli)

    testImplementation(libs.kotlin.test)
}

kotlin {
    compilerOptions {
        jvmToolchain(17)

        freeCompilerArgs.addAll(
            "-Xexplicit-backing-fields",
            "-Xcontext-parameters",
        )
    }
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
            exclude(dependency("app.revanced:patcher"))
            exclude(dependency("commons-logging:commons-logging"))
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
