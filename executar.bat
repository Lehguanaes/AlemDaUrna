@echo off
setlocal
chcp 65001 >nul
cd /d "%~dp0"
set "JAVA_CMD=java"
for /d %%d in ("%ProgramFiles%\Java\jdk-*") do if exist "%%d\bin\java.exe" set "JAVA_CMD=%%d\bin\java.exe"
if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
if not exist "target\AlemDaUrna-1.0-SNAPSHOT.jar" (
    echo Abra compilar.bat primeiro ou use Limpar e Construir no NetBeans.
    pause
    exit /b 1
)
"%JAVA_CMD%" -jar "target\AlemDaUrna-1.0-SNAPSHOT.jar" %*
if errorlevel 1 (
    echo Confira a instalacao do Java 17 ou superior e a mensagem acima.
    pause
    exit /b 1
)
