@echo off

if "%~1"=="" (
    set INVALID_ARGS=true
)
if defined INVALID_ARGS (
    echo Run a command with arguments.
    echo:
    echo Usage: run.bat ^<command^> [arguments]
    echo Example: run.bat echo Hello, World!
    exit /b 1
)

%*

if %ERRORLEVEL% NEQ 0 (
    echo:
    echo Failed to run command with exit code %ERRORLEVEL%.
    echo Failed command: %*
    echo:
    pause
    exit /b %ERRORLEVEL%
)
