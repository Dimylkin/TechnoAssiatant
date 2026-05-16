@echo off
setlocal enabledelayedexpansion

set VENV_DIR=.venv

if "%1"=="" goto usage

if "%1"=="install" goto install
if "%1"=="build" goto build
if "%1"=="run" goto run
if "%1"=="clean" goto clean

goto usage

:install
echo [app] Creating virtual environment...
if not exist "%VENV_DIR%" (
    python -m venv "%VENV_DIR%"
)

echo [app] Installing Python dependencies...
"%VENV_DIR%\Scripts\python.exe" -m pip install --upgrade pip
"%VENV_DIR%\Scripts\pip.exe" install -r requirements.txt

echo [app] Building Java project...
mvn clean package

echo [app] Done.
exit /b 0

:build
echo [app] Building Java project...
mvn clean package
exit /b 0

:run
echo [app] Running JavaFX application...
mvn clean javafx:run
exit /b 0

:clean
echo [app] Cleaning project...
if exist target rmdir /s /q target
if exist "%VENV_DIR%" rmdir /s /q "%VENV_DIR%"
exit /b 0

:usage
echo Usage:
echo   app install   ^(create venv, install requirements, mvn clean package^)
echo   app build     ^(mvn clean package^)
echo   app run       ^(mvn clean javafx:run^)
echo   app clean     ^(remove venv + target^)
exit /b 1