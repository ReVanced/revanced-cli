@echo off
setlocal

if "%~1"=="" (
    set INVALID_ARGS=true
)
if "%~2"=="" (
    set INVALID_ARGS=true
)
if "%~3"=="" (
    set INVALID_ARGS=true
)
if defined INVALID_ARGS (
    echo Downloads a file from a GitHub repository release.
    echo:
    echo Usage: download ^<repo^> ^<asset-extension^> ^<out^>
    echo Example: download revanced/revanced-cli jar C:/revanced/revanced-cli.jar
    exit /b 1
)

set REPO=%1
set ASSET_EXTENSION=%2
set OUT=%3

set URL=https://api.github.com/repos/%REPO%/releases/latest
for /f "delims=" %%i in ('curl -s %URL% ^| jq -r ".assets[] | select(.name | endswith(\"%ASSET_EXTENSION%\")) | .browser_download_url"') do (
    set JAR_URL=%%i
)

run curl --silent --location --output %OUT% %JAR_URL%
