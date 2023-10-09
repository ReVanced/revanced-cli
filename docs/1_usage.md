# 🛠️ Using ReVanced CLI

Learn how to ReVanced CLI.

## 🔨 Usage

ReVanced CLI is divided into the following fundamental commands:

- ### ⚙️ Show all available options for ReVanced CLI

  ```bash
  java -jar revanced-cli.jar -h
  ```

- ### 📃 List patches

  ```bash
  java -jar revanced-cli.jar list-patches \
   --with-packages \
   --with-versions \
   --with-options \
   revanced-patches.jar [<patch-bundle> ...]
  ```

- ### ⚙️ Generate options

  This will generate an `options.json` file for the patches from a list of supplied patch bundles.
  The file can be supplied to ReVanced CLI later on.
 
  ```bash
  java -jar revanced-cli.jar options \
   --path options.json \
   --overwrite \
   revanced-patches.jar [<patch-bundle> ...]
  ```

  > [!NOTE]  
  > A default `options.json` file will be automatically created, if it does not exist 
  without any need for intervention when using the `patch` command.

- ### 💉 Patch apps

  You can patch apps by supplying patch bundles and the APK file to patch.
  After patching, ReVanced CLI can install the patched app on your device using two methods:

  > [!NOTE]  
  > For ReVanced CLI to be able to install the patched app on your device, make sure ADB is working:
  >
  > ```bash
  > adb shell exit
  > ```
  >
  > To get your device's serial, run the following command:
  >
  > ```bash
  > adb devices
  > ```
  >
  > If you want to mount the patched app on top of the un-patched app, make sure you have root permissions:
  >
  > ```bash
  > adb shell su -c exit
  > ```
  >

  > [!WARNING]  
  > Some patches may require integrations
  > such as [ReVanced Integrations](https://github.com/revanced/revanced-integrations).
  > Supply them with the option `--merge`. ReVanced Patcher will automatically determine if they are necessary.

  - #### 👾 Patch an app and install it on your device regularly

    ```bash
    java -jar revanced-cli.jar patch \
     --patch-bundle revanced-patches.jar \
     --out patched-app.apk \
     --device-serial <device-serial> \
     input.apk
    ```

  - #### 👾 Patch an app and mount it on top of the un-patched app with root permissions
  
    > [!IMPORTANT]  
    > Ensure sure the same app you are patching is installed on your device:
    > 
    > ```bash
    > adb install app.apk
    > ```

    Patch and install the app on your device by mounting it on top of the un-patched app with root permissions:

    ```bash
    java -jar revanced-cli.jar patch \
     --patch-bundle revanced-patches.jar \
     --include "Some patch" \
     --exclude "Some other patch" \
     --out patched-app.apk \
     --device-serial <device-serial> \
     --mount \
     app.apk
    ```

- ### 🗑️ Uninstall an app

  ```bash
  java -jar revanced-cli.jar utility uninstall \
   --package-name <package-name> \
   <device-serial>
  ```

  > [!NOTE]  
  > You can unmount an APK file
  by adding the option `--unmount`.

- ### ️ ⚙️ Install an app

  ```bash
  java -jar revanced-cli.jar utility install \
   -a input.apk \
   <device-serial>
  ```

  > [!NOTE]  
  > You can mount an APK file 
  > by supplying the package name of the app to mount the supplied APK file to over the option `--mount`.
