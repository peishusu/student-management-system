@echo off
setlocal

set "ROOT=%~dp0"
set "LOG_DIR=%ROOT%logs"

if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"

call "%ROOT%run-backend.cmd" > "%LOG_DIR%\backend.log" 2> "%LOG_DIR%\backend.err.log"
