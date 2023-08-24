# 🛠️ Using ReVanced CLI

Learn how to ReVanced CLI.

## ⚡ Setup ADB

1. Ensure that ADB is working

   ```bash
   adb shell exit
   ```

   Optionally, you can install the patched APK file on your device by mounting it on top of the original APK file. 
   You will need root permissions for this. Check if you have root permissions by running the following command:

   ```bash
   adb shell su -c exit
   ```

2. Get your device's serial

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
   revanced-patches.jar [<patch-bundle> ...]
  ```

- ### ⚙️ Generate options from patches using ReVanced CLI

  This will generate an `options.json` file for the patches from a list of supplied patch bundles.
  The file can be supplied to ReVanced CLI later on.
 
- ```bash
  java -jar revanced-cli.jar options \
   --path options.json \
   --overwrite \
   revanced-patches.jar [<patch-bundle> ...]
  ```

  > **Note**: A default `options.json` file will be automatically generated, if it does not exist 
  without any need for intervention when using the `patch` command.

- ### 💉 Use ReVanced CLI to patch an APK file but install without root permissions

  This will install the patched APK file regularly on your device.

  ```bash
  java -jar revanced-cli.jar patch \
   --patch-bundle revanced-patches.jar \
   --out output.apk \
   --device-serial <device-serial> \
   input.apk
  ```

- ### 👾 Use ReVanced CLI to patch an APK file but install with root permissions

  This will install the patched APK file on your device by mounting it on top of the original APK file.

  ```bash
  adb install input.apk
  java -jar revanced-cli.jar patch \
   --patch-bundle revanced-patches.jar \
   --include some-other-patch \
   --exclude some-patch \
   --out patched-output.apk \
   --device-serial <device-serial> \
   --mount \
   input.apk
  ```

  > **Note**: Some patches may require integrations
  such as [ReVanced Integrations](https://github.com/revanced/revanced-integrations). 
  Supply them with the option `--merge`. If any patches accepted by ReVanced Patcher require ReVanced Integrations, 
  they will be merged into the APK file automatically.

- ### 🗑️ Uninstall a patched APK file
  ```bash
  java -jar revanced-cli.jar utility uninstall \
   --package-name <package-name> \
   <device-serial>
  ```

  > **Note**: You can unmount an APK file
  with the option `--unmount`.

- ### ️ ⚙️ Manually install an APK file

  ```bash
  java -jar revanced-cli.jar utility install \
   -a input.apk \
   <device-serial>
  ```

  > **Note**: You can mount an APK file 
  by supplying the package name of the app to mount the supplied APK file to over the option `--mount`.
