@echo off
setlocal

for /f "tokens=8 delims=. " %%a in ('java --version 2^>nul ^| findstr /r "build"') do (
    set JAVA_VERSION_MAJOR=%%a
    goto :break
)
:break

if %JAVA_VERSION_MAJOR% LSS 11 (
    echo It looks like Java version is less than 11. Install OpenJDK/ Eclipse Temurin 11 or newer.
)
