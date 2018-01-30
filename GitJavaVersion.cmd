@ECHO OFF
REM
REM This script will create a Java class which contains git build info

set TEMPFILE=%TEMP%\%~n0.txt

set GIT_VERSION=???????
git.exe log -1 --pretty=format:%%h > %TEMPFILE%
set /p GIT_VERSION=<%TEMPFILE%

set GIT_STATUS=
git diff --shortstat HEAD > %TEMPFILE%
set /p GIT_STATUS=<%TEMPFILE%

date /t > %TEMPFILE%
set /p BUILD_DATE=<%TEMPFILE%

time /t > %TEMPFILE%
set /p BUILD_TIME=<%TEMPFILE%

echo package org.usfirst.frc1388;
echo public class BuildInfo {
echo   public static final String GIT_VERSION = "%GIT_VERSION%";
echo   public static final String GIT_STATUS = "%GIT_STATUS%";
echo   public static final String BUILD_DATE = "%BUILD_DATE%";
echo   public static final String BUILD_TIME = "%BUILD_TIME%";
echo }
