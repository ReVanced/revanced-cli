@echo off

echo Check, if OpenJDK/ Eclipse Temurin 11 or newer is installed.
pause
echo:

cd modules
call run java -version
cd ..

echo:
set /P INSTALLED_JAVA=Can you see OpenJDK/ Eclipse Temurin 11 or newer? (y/n)

if "%INSTALLED_JAVA%"=="y" (
    echo The environment is set up.
    pause
    exit
)

cls
echo Install Eclipse Temurin JRE 21.
pause

cd modules
call install-java
cd ..

echo The environment is set up. Rerun this script to check your environment.
pause
