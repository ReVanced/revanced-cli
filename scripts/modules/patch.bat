@echo off
setlocal

if "%~1"=="" (
    set INVALID_ARGS=true
)
if "%~2"=="" (
    set INVALID_ARGS=true
)
if defined INVALID_ARGS (
    echo Patches an application using the specified patches.
    echo:
    echo Usage: patch ^<apk^> ^<workspace^>
    echo Example: patch C:/app.apk C:/workspace
    exit /b 1
)

set APK=%1
set WORKSPACE=%2

call run java -jar %WORKSPACE%/revanced-cli.jar patch ^
    --patch-bundle %WORKSPACE%/patches.rvp ^
    --temporary-files-path %WORKSPACE%/temporary-files ^
    --out %WORKSPACE%/patched.apk ^
    --purge ^
    %APK%

mv %WORKSPACE%/patched.apk
