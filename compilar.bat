@echo off
setlocal
cd /d "%~dp0"
set "JDK_BIN="
for /d %%d in ("%ProgramFiles%\Java\jdk-*") do if exist "%%d\bin\javac.exe" set "JDK_BIN=%%d\bin\"
if defined JAVA_HOME if exist "%JAVA_HOME%\bin\javac.exe" set "JDK_BIN=%JAVA_HOME%\bin\"
if not exist "target\classes-manual" mkdir "target\classes-manual"
> "target\fontes-java.txt" (
    for %%f in (src\main\java\com\mycompany\alemdaurna\*.java) do echo %%f
)
"%JDK_BIN%javac.exe" --release 17 -encoding UTF-8 -d "target\classes-manual" "@target\fontes-java.txt"
if errorlevel 1 goto erro
"%JDK_BIN%jar.exe" --create --file "target\AlemDaUrna-1.0-SNAPSHOT.jar" --main-class com.mycompany.alemdaurna.AlemDaUrna -C "target\classes-manual" .
if errorlevel 1 goto erro
echo Compilacao concluida. Abra executar.bat.
pause
exit /b 0
:erro
echo Falha na compilacao. Confira se o JDK 17 ou superior esta disponivel.
pause
exit /b 1
