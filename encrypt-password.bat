@echo off
setlocal

:: Check if the correct number of arguments are passed
if "%~1"=="" (
    echo Maven path not provided.
    exit /b 1
)
if "%~2"=="" (
    echo Java path not provided.
    exit /b 1
)

set "MAVEN_PATH=%~1"
set "JAVA_PATH=%~2"

:: Ensure paths are quoted to handle spaces
"%MAVEN_PATH%\bin\mvn.cmd" --encrypt-master-password -Dmaven.compiler.executable="%JAVA_PATH%\bin\javac.exe"

endlocal