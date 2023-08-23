# 🛠️ Using ReVanced CLI

Learn how to ReVanced CLI.

## ⚡ Setup ADB

1. Ensure that ADB is working

   ```bash
   adb shell exit
   ```

   If you want to install the patched APK file on your device by mounting it on top of the original APK file, you will need root access. This is optional.

   ```bash
   adb shell su -c exit
   ```

2. Get the name of your device

   ```bash
   adb devices
   ```

## 🔨 Using ReVanced CLI

- ### ⚙️ Show all available options for ReVanced CLI

  ```bash
  java -jar revanced-cli.jar -h
  ```

- ### 📃 List patches from supplied patch bundles

  ```bash
  java -jar revanced-cli.jar list-patches \
   --with-packages \
   --with-versions \
   --with-options \
   revanced-patches.jar
  ```

- ### ⚙️ Supply options to patches using ReVanced CLI

  Some patches provide options. Currently, ReVanced CLI will generate and consume an `options.json` file at the location that is specified in `-o`. If the option is not specified, the options file will be generated in the current working directory.

  The options file contains all options from supplied patch bundles.

  > **Note**: The `options.json` file will be generated at the first time you use ReVanced CLI to patch an APK file for now. This will be changed in the future.

- ### 💉 Use ReVanced CLI to patch an APK file but install without root permissions

  This will install the patched APK file regularly on your device.

  ```bash
  java -jar revanced-cli.jar patch \
   -b revanced-patches.jar \
   -o patched-output.apk \
   -d device-serial \
   input-apk
  ```

- ### 👾 Use ReVanced CLI to patch an APK file but install with root permissions

  This will install the patched APK file on your device by mounting it on top of the original APK file.

  ```bash
  adb install input.apk
  java -jar revanced-cli.jar patch \
   -o patched-output.apk \
   -b revanced-patches.jar \
   -e some-patch \
   -d device-serial \
   --mount \
   input-apk
  ```

  > **Note**: Some patches from [ReVanced Patches](https://github.com/revanced/revanced-patches) also require [ReVanced Integrations](https://github.com/revanced/revanced-integrations). Supply them with the option `-m`. ReVanced Patcher will merge ReVanced Integrations automatically, depending on if the supplied patches require them.
  package

- ### 🗑️ Uninstall a patched 
  ```bash
  java -jar revanced-cli.jar uninstall \
   -p package-name \
   device-serial
  ```
