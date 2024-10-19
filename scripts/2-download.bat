@echo off

echo Download necessary files from GitHub.
pause

cd modules
call composite download %~dp0\workspace
cd ..

echo Files downloaded.
pause
