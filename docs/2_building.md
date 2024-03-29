# 🔨️ Building

Build ReVanced CLI from source.

## 📝 Requirements

- Java Development Kit 11 (Azul Zulu JRE or OpenJDK)

## 🏗️ Building

To build ReVanced CLI, follow these steps:

1. Clone the repository:

   ```bash
   git clone git@github.com:ReVanced/revanced-cli.git
   cd revanced-cli
   ```

2. Build the project:

   ```bash
    ./gradlew build
   ```

> [!NOTE]
> If the build fails due to authentication, you may need to authenticate to GitHub Packages.
> Create a PAT with the scope `read:packages` [here](https://github.com/settings/tokens/new?scopes=read:packages&description=ReVanced) and add your token to ~/.gradle/gradle.properties.
>
> Example `gradle.properties` file:
>
> ```properties
> gpr.user = user
> gpr.key = key
> ```

After the build succeeds, the built JAR file will be located at `build/libs/revanced-cli-<version>-all.jar`.
