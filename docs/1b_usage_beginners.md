## Step 1: Download the ReVanced CLI

Head over to the [ReVanced CLI releases page](https://github.com/ReVanced/revanced-cli/releases) and grab the latest `.jar` file.

Create a new folder somewhere easy to find (something like `revanced` on your Desktop works fine), drop the file in there, and rename it to:

```
revanced-cli.jar
```

Now open a terminal inside that folder:

- **Linux / macOS:** Right-click the folder and choose *Open Terminal Here*, or just `cd` into it
- **Windows:** Open the folder in File Explorer, right-click on an empty spot, and select *Open in PowerShell*

To confirm you're in the right place, run:

```bash
pwd
```

The path it shows should match your folder. Then run `ls` (or `dir` on Windows PowerShell) to list the files in the folder and make sure `revanced-cli.jar` is actually there:

```bash
# Linux / macOS
ls

# Windows PowerShell
dir
```

You should see `revanced-cli.jar` in the output. If you do, you're in the right place and ready to continue.

---

## Step 2: Download the Patch Bundle

The patch bundle is a `.rvp` file that tells the CLI what patches are available and which apps they support.

> **This file is downloaded directly from the official ReVanced API** — the exact same endpoint the ReVanced Manager app on Android uses to fetch patches. The ReVanced GitHub repo is currently unavailable and the GitLab mirror only has source code releases, so this API is the officially recommended source for the latest patch bundle.

**Linux / macOS:**

```bash
wget "https://api.revanced.app/v5/patches.rvp"
```

**Windows:**

Just open this URL in your browser and it'll download automatically. Move the downloaded file into the same folder you've been working in:

```
https://api.revanced.app/v5/patches.rvp
```

---

## Step 3: Find the Right APK Version

You can't patch just any version of an app. The patches are built for specific versions, so you need to find out which one to download. Run this command to see what's supported.

**Linux / macOS (with grep):**

```bash
java -jar revanced-cli.jar list-versions \
  -p patches.rvp \
  -b | grep -A20 -E "com.google.android.youtube|com.google.android.apps.youtube.music"
```

The `|` inside the grep part means OR, so it'll show results for YouTube and YouTube Music at the same time. You can add more apps the same way.

**Windows, or if you don't have grep:**

```bash
java -jar revanced-cli.jar list-versions -p patches.rvp -b
```

This dumps everything, so just scroll through and find your app manually. Write down the latest supported version number you see for it.

Here are the package names to look for:

| App | Package Name |
|-----|-------------|
| YouTube | `com.google.android.youtube` |
| YouTube Music | `com.google.android.apps.youtube.music` |

---

## Step 4: Download the APK

Go to [APKMirror](https://www.apkmirror.com/) and search for the app along with the version number you just noted. For example: `YouTube 20.14.43`.

Before downloading, you need to pick the right file for your phone's CPU architecture. If you're not sure which one your phone uses, install the **CPU-Z** app from the Play Store and it'll tell you right away.

| Architecture | Who it's for |
|---|---|
| `armeabi-v7a` (32-bit) | Older and budget phones, usually with 4 GB RAM or less |
| `arm64-v8a` (64-bit) | Most phones made in the last few years |

You might also see an option to download an "APK bundle" on APKMirror. Skip that and stick to the plain `.apk` file for your architecture.

Once downloaded, move the APK into your working folder and give it a simple name:

| App | Rename to |
|-----|-----------|
| YouTube | `youtube-latest.apk` |
| YouTube Music | `yt-music-latest.apk` |
| Any other app | Anything short and easy to remember |

---

## Step 5: Patch the APK

**YouTube:**

```bash
java -jar revanced-cli.jar patch \
  -p patches.rvp \
  -b \
  -o Patched-YouTube.apk \
  youtube-latest.apk
```

**YouTube Music:**

```bash
java -jar revanced-cli.jar patch \
  -p patches.rvp \
  -b \
  -o Patched-YTMusic.apk \
  yt-music-latest.apk
```

**Any other app** (swap `APK_NAME.apk` for whatever you named your downloaded file):

```bash
java -jar revanced-cli.jar patch \
  -p patches.rvp \
  -b \
  -o Patched.apk \
  APK_NAME.apk
```

---

## Step 6: Install GmsCore (MicroG)

GmsCore is a free and open-source replacement for Google Play Services. It's required for any Google-related app patches to work, including YouTube, YouTube Music, and Google Photos. Without it, the patched apps simply won't function properly.

We're using the version maintained by the ReVanced team themselves, so you know it's legit:

1. Download the latest release from the [ReVanced GmsCore releases page](https://github.com/ReVanced/GmsCore/releases)
2. Transfer it to your phone and install it (see Step 7 below if you hit an "Install blocked" error)
3. Open GmsCore and sign in with your Google account

---

## Step 7: Install the Patched APK

Transfer the patched APK (for example, `Patched-YouTube.apk`) to your phone however you like — Bluetooth, Google Drive, a USB cable, a messaging app, whatever works for you.

Once it's on your phone, open it with a file manager and tap **Install**.

**Getting an "Install blocked" or "Unknown source" message?**

This just means your phone needs permission to install apps from outside the Play Store. Here's how to fix it:

1. In the error dialog, tap **Settings** or **Allow from this source**
2. Turn on the **"Install unknown apps"** permission for whichever app you used to open the APK (your file manager, browser, etc.)
3. Go back and tap Install again

---

## Troubleshooting

**See all supported app versions:**

```bash
java -jar revanced-cli.jar list-versions \
  -p patches.rvp \
  -b
```

**See all available patches:**

```bash
java -jar revanced-cli.jar list-patches \
  -p patches.rvp \
  -b
```

**Force patching an unsupported version:**

If the exact version you need isn't available on APKMirror, you can try forcing the patch with the `-f` flag. It doesn't always work perfectly, but it's worth a shot.

```bash
java -jar revanced-cli.jar patch \
  -f \
  -p patches.rvp \
  -b \
  -o Patched-YouTube.apk \
  youtube-latest.apk
```

---
