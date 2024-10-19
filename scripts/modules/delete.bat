@echo off
setlocal

if "%~1"=="" (
    set INVALID_ARGS=true
)
if defined INVALID_ARGS (
    echo Deletes a directory relative to the current directory using the rmdir command.
    echo:
    echo Usage: delete ^<path^>
    echo Example: delete C:/revanced
    exit /b 1
)

set DIRECTORY=%1

if exist %DIRECTORY% (
    echo Confirm deletion of
    run rmdir /s %DIRECTORY%
)
