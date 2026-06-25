@echo off
set "JAVA_HOME=D:\apps\idea\jdk1.8.0_301"
set "PATH=%JAVA_HOME%\bin;%PATH%"
cd /d "%~dp0backend"
"D:\apps\maven\apache-maven-3.9.9\bin\mvn.cmd" spring-boot:run
