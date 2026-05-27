@echo off
set "MAVEN_HOME=%TEMP%\maven\apache-maven-3.9.6"
"%MAVEN_HOME%\bin\mvn.cmd" %*
