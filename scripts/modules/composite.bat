@echo off
setlocal EnableDelayedExpansion

if "%~1"=="" (
    set INVALID_ARGS=true
)
if "%~2"=="" (
    set INVALID_ARGS=true
)
if defined INVALID_ARGS (
    echo Runs curated compositions of scripts.
    echo:
    echo Usage: composite ^<script^> ^<workspace^>
    echo Example: composite patch C:/revanced
    echo:
    echo Available commands:
    echo:   download - Download files
    echo:   patch - Patches an application
    echo:   clean - Cleans the workspace
    echo:   env - Check for a valid environment setup
    exit /b 1
)

call env

set WORKSPACE=%2

if "%~1"=="download" (
   call create %WORKSPACE%

    if not exist %WORKSPACE%/revanced-cli.jar (
        echo Downloading ReVanced CLI...
        call download %CLI_REPO% jar %WORKSPACE%/revanced-cli.jar
    )

    if not exist %WORKSPACE%/patches.rvp (
        echo Downloading ReVanced patches...
        call download %PATCHES_REPO% jar %WORKSPACE%/patches.rvp
    )
)
if "%~1"=="patch" (
    set /p APK="Path to the APK file: "

    call patch !APK! %WORKSPACE%
)
if "%~1"=="clean" (
    call delete %WORKSPACE%
)
if "%~1"=="env" (
    call check-java
)
