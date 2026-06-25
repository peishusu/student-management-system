@echo off
setlocal

set "ROOT=%~dp0"

cd /d "%ROOT%"
call "%ROOT%run-backend.cmd"
if errorlevel 1 (
  echo.
  echo Backend failed to start. Please check the message above.
  pause
)
