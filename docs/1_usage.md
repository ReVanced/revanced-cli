# 🛠️ Using ReVanced CLI

Learn how to ReVanced CLI.

## ⚡ Setup (optional)

1. Make sure your device is connected

   ```bash
   adb shell exit
   ```

   If you plan to use the root variant, check if you have root access

   ```bash
   adb shell su -c exit
   ```

2. Copy the ADB device name

   ```bash
   adb devices
   ```

## 🔨 ReVanced CLI Usage

- ### Show all available options for ReVanced CLI

  ```bash
  java -jar revanced-cli.jar -h
  ```

- ### List all available patches from supplied patch bundles

  ```bash
  java -jar revanced-cli.jar \
      -b revanced-patches.jar \
      -l
  ```

- ### Use ReVanced CLI without root permissions

  ```bash
  java -jar revanced-cli.jar \
   -a input.apk \
   -o patched-output.apk \
   -b revanced-patches.jar
  ```

- ### Mount the patched application with root permissions over the installed application

  ```bash
  adb install input.apk # make sure the same version is installed
  java -jar revanced-cli.jar \
      -a input.apk \
      -d device-name \
      -o patched-output.apk \
      -b revanced-patches.jar \
      -e vanced-microg-support \
      --mount
  ```

> **Note**:
>
> - If you want to exclude patches, you can use the option `-e`. In the case of YouTube, you can exclude
    the `vanced-microg-support` patch from [ReVanced Patches](https://github.com/revanced/revanced-patches) with the
    option `-e vanced-microg-support` when mounting for example.
>
> - Some patches from [ReVanced Patches](https://github.com/revanced/revanced-patches) also might require
    [ReVanced Integrations](https://github.com/revanced/revanced-integrations). Supply them with the option `-m`.
    > The integrations will be merged, if necessary automatically, if supplied.
>
> - If you supplied a device with the option `-d`, the patched application will be automatically installed on the
    device.
