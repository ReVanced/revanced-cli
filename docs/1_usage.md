# 🛠️ Using ReVanced CLI

Learn how to use ReVanced CLI.
The following examples will show you how to perform basic operations.
You can list patches, patch an app, uninstall, and install an app.

## 🚀 Show all commands

```bash
java -jar revanced-cli.jar -h
```

## 📃 List patches

```bash
java -jar revanced-cli.jar list-patches --with-descriptions --with-packages --with-versions --with-options --with-universal-patches revanced-patches.rvp
```

## 💉 Patch an app with the default list of patches

```bash
java -jar revanced-cli.jar patch -b revanced-patches.rvp input.apk
```

You can also use multiple patch bundles:

```bash
java -jar revanced-cli.jar patch -b revanced-patches.rvp -b another-patches.rvp input.apk
```

To manually include or exclude patches, use the options `-i` and `-e`.
Keep in mind the name of the patch must be an exact match.
You can also use the options `--ii` and `--ie` to include or exclude patches by their index
if two patches have the same name.
To know the indices of patches, use the option `--with-indices` when listing patches:

```bash
java -jar revanced-cli.jar list-patches --with-indices revanced-patches.rvp
```

Then you can use the indices to include or exclude patches:

```bash
java -jar revanced-cli.jar patch -b revanced-patches.rvp --ii 123 --ie 456 input.apk
```

> [!TIP]
> You can use the option `-d` to automatically install the patched app after patching.
> Make sure ADB is working:
>
> ```bash
>  adb shell exit
> ```


> [!TIP]
> You can use the option `--mount` to mount the patched app on top of the un-patched app.
> Make sure you have root permissions and the same app you are patching and mounting over is installed on your device:
>
> ```bash
> adb shell su -c exit
> adb install input.apk
> ```

## 📦 Install an app manually

```bash
java -jar revanced-cli.jar utility install -a input.apk
```

> [!TIP]
> You can use the option `--mount` to mount the patched app on top of the un-patched app.
> Make sure you have root permissions and the same app you are patching and mounting over is installed on your device:
>
> ```bash
> adb shell su -c exit
> adb install input.apk
> ```

## 🗑️ Uninstall an app manually

Here `<package-name>` is the package name of the app you want to uninstall:

```bash
java -jar revanced-cli.jar utility uninstall --package-name <package-name>
```

If the app is mounted, you need to unmount it by using the option `--unmount`:

```bash
java -jar revanced-cli.jar utility uninstall --package-name <package-name> --unmount
```

> [!TIP]
> By default, the app is installed or uninstalled to the first connected device.
> You can append one or more devices by their serial to install or uninstall an app on your selected choice of devices:
>
> ```bash
> java -jar revanced-cli.jar utility uninstall --package-name <package-name> [<device-serial> ...]
> ```
