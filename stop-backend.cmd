@echo off
setlocal

set "FOUND="
for /f "tokens=5" %%p in ('netstat -ano ^| findstr /r /c:":8080 .*LISTENING"') do (
  set "FOUND=1"
  echo Stopping backend process on port 8080, PID %%p...
  taskkill /PID %%p /F
)

if not defined FOUND (
  echo No backend process is listening on port 8080.
)
