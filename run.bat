@echo off
title Faculty Research Publication Repository
cd /d "%~dp0"

echo ===================================================
echo   Faculty Research Publication Repository (2026)
echo ===================================================
echo [1/3] Compiling Java source files...

if not exist bin mkdir bin

javac -d bin -cp "lib\mysql-connector-j-8.3.0.jar;src\main\java" src\main\java\com\facultyrepository\model\*.java src\main\java\com\facultyrepository\db\*.java src\main\java\com\facultyrepository\dao\*.java src\main\java\com\facultyrepository\util\*.java src\main\java\com\facultyrepository\ui\*.java src\main\java\com\facultyrepository\Main.java

if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed! Check if JDK is installed and in PATH.
    pause
    exit /b %errorlevel%
)

echo [2/3] Copying configuration and logo assets...
copy /Y src\main\resources\* bin\ >nul

echo [3/3] Launching Application GUI...
start javaw -cp "bin;lib\mysql-connector-j-8.3.0.jar" com.facultyrepository.Main

echo.
echo Application launched successfully!
echo You can close this window.
ping 127.0.0.1 -n 3 >nul
