# 🛠️ Using the ReVanced CLI

Lean how to use the ReVanced CLI.

## ⚡ Setup

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

- ### Show all available options for the ReVanced CLI

  ```bash
  java -jar revanced-cli.jar -h
  ```

- ### List all available patches inside supplied bundles

  ```bash
  java -jar revanced-cli.jar \
      -b revanced-patches.jar \
      -l
  ```

- ### Use the ReVanced CLI without root permissions

  ```bash
  java -jar revanced-cli.jar \
   -a input.apk \
   -c \
   -o unpatched-output.apk \
   -b revanced-patches.jar
  ```

- ### Mount the patched application with root permissions over the installed application

  ```bash
  java -jar revanced-cli.jar \
      -a input.apk \
      -c \
      -d device-name \
      -o patched-output.apk \
      -b revanced-patches.jar \
      -e microg-support \
      --mount
  ```

> **Note**:
>
> - If you want to exclude patches, you can use the option `-e`. In the case of YouTube, you have to exclude
    the `microg-support` patch from the [ReVanced Patches](https://github.com/revanced/revanced-patches) with the
    option `-e microg-support`.
>
> - Some patches from the [ReVanced Patches](https://github.com/revanced/revanced-patches) also might require
    the [ReVanced Integrations](https://github.com/revanced/revanced-integrations). Supply them with the option `-m`.
>
> - If you supplied a device with the option `-d`, the patched application will be automatically installed on the
    device.
