@echo off
setlocal

echo Patch an APK with the patches in the workspace.
pause

cd modules
call composite patch %~dp0\workspace
cd ..

mv %~dp0\workspace\patched.apk %~dp0\patched.apk

echo Patched APK saved at %~dp0\patched.apk.
pause
