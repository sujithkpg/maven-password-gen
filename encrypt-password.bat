@echo off
setlocal

REM Check if required parameters are provided
if "%~1"=="" (
    echo Usage: %0 "MAVEN_PATH" "JAVA_PATH" "MASTER_PASSWORD_FILE"
    exit /b 1
)

if "%~2"=="" (
    echo Usage: %0 "MAVEN_PATH" "JAVA_PATH" "MASTER_PASSWORD_FILE"
    exit /b 1
)

if "%~3"=="" (
    echo Usage: %0 "MAVEN_PATH" "JAVA_PATH" "MASTER_PASSWORD_FILE"
    exit /b 1
)

REM Set parameters
set "MAVEN_PATH=%~1"
set "JAVA_PATH=%~2"
set "MASTER_PASSWORD_FILE=%~3"
set "PASSWORD=sdfmslf"

REM Ensure paths are quoted
set "MAVEN_PATH=%MAVEN_PATH:"="%"
set "JAVA_PATH=%JAVA_PATH:"="%"
set "MASTER_PASSWORD_FILE=%MASTER_PASSWORD_FILE:"="%"

REM Check if the master password file exists
if not exist "%MASTER_PASSWORD_FILE%" (
    echo Master password file does not exist: %MASTER_PASSWORD_FILE%
    exit /b 1
)

REM Ensure the Maven executable is available
if not exist "%MAVEN_PATH%\bin\mvn.cmd" (
    echo Maven executable not found at %MAVEN_PATH%\bin\mvn.cmd
    exit /b 1
)

REM Ensure the Java compiler is available
if not exist "%JAVA_PATH%\bin\javac.exe" (
    echo Java compiler not found at %JAVA_PATH%\bin\javac.exe
    exit /b 1
)

REM Create a temporary file to hold the password
set "TEMP_PASSWORD_FILE=password.txt"
type "%MASTER_PASSWORD_FILE%" > "%TEMP_PASSWORD_FILE%"

REM Execute the Maven command and pass the password file
"%MAVEN_PATH%\bin\mvn.cmd" --encrypt-master-password -Dmaven.compiler.executable="%JAVA_PATH%\bin\javac.exe" < "%TEMP_PASSWORD_FILE%"

REM Clean up the temporary file
del "%TEMP_PASSWORD_FILE%"

endlocal