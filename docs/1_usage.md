# 🛠️ Using ReVanced CLI

Learn how to use ReVanced CLI.

## 🔨 Usage

ReVanced CLI is divided into the following fundamental commands:

- ### 🚀 Show all available options for ReVanced CLI

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

  > **ℹ️ Note**  
  > A default `options.json` file will be automatically created if it does not exist 
  without any need for intervention when using the `patch` command.

- ### 💉 Patch an app

  You can patch apps by supplying patch bundles and the app to patch.
  After patching, ReVanced CLI can install the patched app on your device using two methods:
  
  > **💡 Tip**  
  > For ReVanced CLI to be able to install the patched app on your device, make sure ADB is working:
  >
  > ```bash
  > adb shell exit
  > ```
  >
  > If you want to mount the patched app on top of the un-patched app, make sure you have root permissions:
  >
  > ```bash
  > adb shell su -c exit
  > ```
  >

  > **⚠️ Warning**  
  > Some patches may require integrations
  > such as [ReVanced Integrations](https://github.com/revanced/revanced-integrations).
  > Supply them with the option `--merge`. ReVanced Patcher will automatically determine if they are necessary.

  - #### 👾 Patch an app and install it on your device regularly

    ```bash
    java -jar revanced-cli.jar patch \
     --patch-bundle revanced-patches.jar \
     -d \
     input.apk
    ```

  - #### 👾 Patch an app and mount it on top of the un-patched app with root permissions
  
    > **❗ Caution**  
    > Ensure that the same app you are patching and mounting over is installed on your device:
    > 
    > ```bash
    > adb install app.apk
    > ```

    ```bash
    java -jar revanced-cli.jar patch \
     --patch-bundle revanced-patches.jar \
     --include "Some patch" \
     --ii 123 \
     --exclude "Some other patch" \
     -d \
     --mount \
     app.apk
    ```
    
    > **💡 Tip**  
    > You can use the option `--ii` to include or `--ie` to exclude
    > patches by their index in relation to supplied patch bundles,
    > similarly to the option `--include` and `--exclude`.
    > 
    > This is useful in case two patches have the same name, and you must include or exclude one.  
    > The patch index is calculated by the position of the patch in the list of patches
    > from patch bundles supplied using the option `--patch-bundle`.
    > 
    > You can list all patches with their indices using the command `list-patches`.
    > 
    > Keep in mind that the indices can change based on the order of the patch bundles supplied,
    > as well if the patch bundles are updated because patches can be added or removed.

- ### 🗑️ Uninstall an app

  ```bash
  java -jar revanced-cli.jar utility uninstall \
   --package-name <package-name> \
   [<device-serial>]
  ```

  > **💡 Tip**  
  > You can unmount an APK file
  by adding the option `--unmount`.

- ### ️ 📦 Install an app

  ```bash
  java -jar revanced-cli.jar utility install \
   -a input.apk \
   [<device-serial>]
  ```

  > **💡 Tip**  
  > You can mount an APK file 
  > by supplying the app's package name to mount the supplied APK file over the option `-mount`.
