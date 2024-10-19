@echo off

echo Clean the workspace.
pause

cd modules
call composite clean %~dp0\workspace
cd ..

echo Cleaned workspace.
pause
