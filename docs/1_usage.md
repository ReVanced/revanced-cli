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
java -jar revanced-cli.jar list-patches --with-packages --with-versions --with-options revanced-patches.rvp
```

## 💉 Patch an app

To patch an app using the default list of patches, use the `patch` command:

```bash
java -jar revanced-cli.jar patch -b revanced-patches.rvp input.apk
```

You can also use multiple patch bundles:

```bash
java -jar revanced-cli.jar patch -b revanced-patches.rvp -b another-patches.rvp input.apk
```

To change the default set of used patches, use the option `-i` or `-e` to use or disuse specific patches.
You can use the `list-patches` command to see which patches are used by default.

To only use specific patches, you can use the option `--exclusive` combined with `-i`.
Remember that the options `-i` and `-e` match the patch's name exactly. Here is an example:

```bash
java -jar revanced-cli.jar patch -b revanced-patches.rvp --exclusive -i "Patch name" -i "Another patch name" input.apk
```

You can also use the options `--ii` and `--ie` to use or disuse patches by their index.
This is useful, if two patches happen to have the same name.
To know the indices of patches, use the command `list-patches`:

```bash
java -jar revanced-cli.jar list-patches revanced-patches.rvp
```

Then you can use the indices to use or disuse patches:

```bash
java -jar revanced-cli.jar patch -b revanced-patches.rvp --ii 123 --ie 456 input.apk
```

You can combine the option `-i`, `-e`, `--ii`, `--ie` and `--exclusive`. Here is an example:

```bash
java -jar revanced-cli.jar patch -b revanced-patches.rvp --exclusive -i "Patch name" --ii 123 input.apk
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

Patches can have options you can set using the option `--set-options`.
To know the options of a patch, use the option `--with-options` when listing patches:

```bash
java -jar revanced-cli.jar list-patches --with-options revanced-patches.rvp
```

Each patch can have multiple options. You can set them using the option `--set-options`.
For example, to set the options for the patch with the name `Patch name`
with the key `key1` and `key2` to `value1` and `value2` respectively, use the following command:

```bash
java -jar revanced-cli.jar patch -b revanced-patches.rvp --set-options "Patch name" -Okey1=value1 -Okey2=value2 input.apk
```

If you want to set a value to `null`, you can omit the value:

```bash
java -jar revanced-cli.jar patch -b revanced-patches.rvp --set-options "Patch name" -Okey1 input.apk
```

> [!WARNING]
> The values of options are typed. If you set a value with the wrong type, the patching process will fail.
> The type of the option value can be seen when listing patches with the option `--with-options`.
> 
> Example values:
>
> String: `string`  
> Boolean: `true`, `false`  
> Integer: `123`  
> Double: `1.0`  
> Float: `1.0f`  
> Long: `1234567890`, `1L`  
> List: `item1,item2,item3`  
>
> In addition to that, you can escape quotes (`\"`, `\'`) and commas (`\,`) to treat values as string literals:
> 
> Integer as string: `\'123\'`  
> List with an integer, an integer as a string and a string with a comma: `123,\'123\',str\,ing`
>
> Example command with escaped quotes:
> 
> ```bash
> java -jar revanced-cli.jar -b revanced-patches.rvp --set-options "Patch name" -OstringKey=\'1\' input.apk
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
