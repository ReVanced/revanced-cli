@echo off

if "%~1"=="" (
    set INVALID_ARGS=true
)
if defined INVALID_ARGS (
    echo Installs a package using winget.
    echo:
    echo Usage: install ^<id^>
    echo Example: install jqlang.jq
    exit /b 1
)

run winget install -e --id=%1
