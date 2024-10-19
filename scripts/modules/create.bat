@echo off
setlocal

if "%~1"=="" (
    set INVALID_ARGS=true
)
if defined INVALID_ARGS (
    echo Creates a directory using the mkdir command.
    echo:
    echo Usage: create ^<path^>
    echo Example: create C:/revanced
    exit /b 1
)

set DIRECTORY=%1

if not exist %DIRECTORY% (
    run "mkdir.exe" -p %DIRECTORY%
)
