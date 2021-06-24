@echo off

@setlocal

rem
rem Run unittest.bat, redirecting output to a file. This version of
rem unittest is run by websvcs.fvt/executeFVT.xml
rem
rem FVT/ws/code/websvcs.fvt/bin/nt/unittest_redirect.bat, WAS.websvcs.fvt, WAS85.FVT, cf011231.01, 1.2.1.1
rem
rem Date         Feature/Defect     Author        Description
rem -----------  -----------------  ------------  ----------------------
rem 12/11/2008   566458             jramos        New File
rem

set command_args=%*

set FVT_TOP=%1%

@call %FVT_TOP%\autoFVT\bin\nt\unittest.bat %command_args% >>out.log 2>&1

@call COPY out.log results

rem Signify that execution has finished
echo Execution Complete >> executionComplete.log

@endlocal
