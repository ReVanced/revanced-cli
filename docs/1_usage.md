# 🛠️ Using the ReVanced CLI

Lean how to use the ReVanced CLI.

## ⚡ Setup

1. Download Java 19: https://jdk.java.net/19/ then unzip it
2. Download latest release (.jar) of revanced CLI: https://github.com/revanced/revanced-cli/releases
3. Download latest release (.jar) of revanced patches: https://github.com/revanced/revanced-patches/releases
4. Download latest release (.apk) of revanced integrations: https://github.com/revanced/revanced-integrations/releases
5. Download the appropriate version of the Youtube APK: https://www.apkmirror.com/apk/google-inc/youtube/youtube-18-03-36-release/ (replace the version in the URL if necessary; make sure to download the APK and not the APK bundle)

Put all of that in the same directory for easier usage.

## 🔨 ReVanced CLI Usage

You may need to slightly modify the commands to adapt to your specific versions of Java and/or Revanced.

- ### Show all available options for the ReVanced CLI

  ```bash
  .\openjdk-19.0.2_windows-x64_bin\jdk-19.0.2\bin\java.exe -jar .\revanced-cli-2.20.0-all.jar -h
  ```

- ### List all available patches from supplied patch bundles

  ```bash
  .\openjdk-19.0.2_windows-x64_bin\jdk-19.0.2\bin\java.exe -jar .\revanced-cli-2.20.0-all.jar -b .\revanced-patches-2.161.1.jar -l --apk '.\com.google.android.youtube_18.03.36-1535632832_minAPI26(arm64-v8a,armeabi-v7a,x86,x86_64)(nodpi)_apkmirror.com.apk' --with-packages --with-versions
  ```

- ### Generate the APK with the patches you want

```bash
<java> -jar <revanced-cli.jar> -b <revanced-patches.jar> --apk <com.example.apk> -m <revanced-integrations.apk> --exclusive --out <output.apk> -i <patch1> [-i patchX]
```

For example:

  ```bash
  .\openjdk-19.0.2_windows-x64_bin\jdk-19.0.2\bin\java.exe -jar .\revanced-cli-2.20.0-all.jar -b .\revanced-patches-2.161.1.jar --apk '.\com.google.android.youtube_18.03.36-1535632832_minAPI26(arm64-v8a,armeabi-v7a,x86,x86_64)(nodpi)_apkmirror.com.apk' -m .\revanced-integrations-0.96.1.apk --exclusive --out yt-revanced.apk -i video-ads -i general-ads -i hide-my-mix -i client-spoof -i sponsorblock -i hide-watermark -i custom-branding -i hide-info-cards -i seekbar-tapping -i hide-album-cards -i hide-artist-card -i hide-cast-button -i hide-watch-in-vr -i spoof-app-version -i custom-video-speed -i hide-shorts-button -i minimized-playback -i old-quality-layout -i open-links-directly -i disable-zoom-haptics -i hide-endscreen-cards -i hide-crowdfunding-box -i return-youtube-dislike -i hide-breaking-news-shelf -i disable-auto-player-popup-panels -i disable-fullscreen-panels -i disable-startup-shorts-player -i remove-player-button-background -i enable-wide-searchbar -i microg-support
  ```
  
  You can modify the above command to include the patches you want. (The above patches are what you will most likely want, if patching Youtube)
  
  Each instance of `-i xxxxxx-xxxxx` is a patch to be included. For example, if you want YT Shorts, remove `-i hide-shorts-button`. If you want swipe controls, add `-i swipe-controls`.
  
  This will then generate a file `yt-revanced.apk` in your current directory. Simply transfer the APK to your phone, then install it. You may need to install MicroG if it redirects you to the Github page of Vanced MicroG.

> **Note**:
>
> - If you want to exclude patches, you can use the option `-e`. In the case of YouTube, you may have to exclude
    the `microg-support` patch from  [ReVanced Patches](https://github.com/revanced/revanced-patches) with the
    option `-e microg-support`.
>
> - Some patches from [ReVanced Patches](https://github.com/revanced/revanced-patches) also might require
    [ReVanced Integrations](https://github.com/revanced/revanced-integrations). Supply them with the option `-m`.
>
> - If you supplied a device with the option `-d`, the patched application will be automatically installed on the
    device.
