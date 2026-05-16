@echo off
setlocal enabledelayedexpansion

set PYTHON_DIR=PythonAI
set VENV_DIR=%PYTHON_DIR%\.venv
set REQUIREMENTS_FILE=%PYTHON_DIR%\requirements.txt

if "%1"=="" goto usage

if "%1"=="install" goto install
if "%1"=="build" goto build
if "%1"=="run" goto run
if "%1"=="clean" goto clean

goto usage

:install
if not exist "%PYTHON_DIR%" (
    echo ERROR: %PYTHON_DIR% directory not found
    exit /b 1
)

echo [app] Creating virtual environment...
if not exist "%VENV_DIR%" (
    python -m venv "%VENV_DIR%"
)

if not exist "%REQUIREMENTS_FILE%" (
    echo ERROR: %REQUIREMENTS_FILE% not found
    exit /b 1
)

echo [app] Installing Python dependencies...
"%VENV_DIR%\Scripts\python.exe" -m pip install --upgrade pip
"%VENV_DIR%\Scripts\pip.exe" install -r "%REQUIREMENTS_FILE%"

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

for /d /r "%PYTHON_DIR%" %%d in (__pycache__) do @if exist "%%d" rmdir /s /q "%%d"
del /s /q "%PYTHON_DIR%\*.pyc" 2>nul

exit /b 0

:usage
echo Usage:
echo   app install   ^(create PythonAI\.venv, install PythonAI\requirements.txt, mvn clean package^)
echo   app build     ^(mvn clean package^)
echo   app run       ^(mvn clean javafx:run^)
echo   app clean     ^(remove PythonAI\.venv + target^)
exit /b 1