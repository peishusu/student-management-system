@echo off
setlocal enabledelayedexpansion

set "ROOT=%~dp0"
set "BACKEND_DIR=%ROOT%backend"
set "JAR=%BACKEND_DIR%\target\student-management-backend-1.0.0.jar"
set "JAVA_HOME=D:\apps\idea\jdk1.8.0_301"
set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
set "JAVAC_EXE=%JAVA_HOME%\bin\javac.exe"
set "MAVEN=D:\apps\maven\apache-maven-3.9.9\bin\mvn.cmd"
set "PATH=%JAVA_HOME%\bin;%PATH%"

cd /d "%BACKEND_DIR%"

if not exist "%JAVA_EXE%" (
  echo Java runtime not found: %JAVA_EXE%
  exit /b 1
)

if not exist "%JAVAC_EXE%" (
  echo JDK compiler not found: %JAVAC_EXE%
  echo Please install a JDK or fix JAVA_HOME in run-backend.cmd.
  exit /b 1
)

if not exist "%MAVEN%" (
  echo Maven not found: %MAVEN%
  exit /b 1
)

for /f "tokens=5" %%p in ('netstat -ano ^| findstr /r /c:":8080 .*LISTENING"') do (
  echo Backend port 8080 is already in use by PID %%p.
  echo Run stop-backend.cmd first, or stop that process manually.
  exit /b 1
)

set "NEED_BUILD=0"
if not exist "%JAR%" (
  set "NEED_BUILD=1"
) else (
  powershell -NoProfile -ExecutionPolicy Bypass -Command "$jar = Get-Item '%JAR%'; $newer = Get-ChildItem -Path '%BACKEND_DIR%\src','%BACKEND_DIR%\pom.xml' -Recurse -File | Where-Object { $_.LastWriteTime -gt $jar.LastWriteTime } | Select-Object -First 1; if ($newer) { exit 1 } else { exit 0 }"
  if errorlevel 1 (
    set "NEED_BUILD=1"
  )
)

if "%NEED_BUILD%"=="1" (
  echo Backend jar is missing or stale. Building once...
  call "%MAVEN%" -q -DskipTests package
  if errorlevel 1 exit /b 1
)

echo Starting backend from packaged jar...
"%JAVA_EXE%" -jar "%JAR%"
exit /b %errorlevel%
