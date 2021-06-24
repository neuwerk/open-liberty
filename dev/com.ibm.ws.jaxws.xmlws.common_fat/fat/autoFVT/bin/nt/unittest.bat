@echo off

@setlocal

rem
rem Web Services FVT Bat File
rem
rem Usage: unittest.bat <FVT_TOP> <WAS_TOP> <build number> <CMVC Release> [Optional: ANT_HOME or Ant Target]
rem        unittest.bat C:\test\development C:\WebSphere\AppServer a0539.01 WASX.IBASE [C:\tools\ant | component-run]
rem
rem FVT/ws/code/websvcs.fvt/bin/nt/unittest.bat, WAS.websvcs.fvt, WAS85.FVT, cf011231.01, 1.8.1.2
rem
rem Date         Feature/Defect     Author        Description
rem -----------  -----------------  ------------  ----------------------
rem 08/15/2003   D174374            ulbricht      Ant needs properties in diff format
rem 08/20/2003   D174754            ulbricht      Using too much memory; AxisFvt
rem                                                  test needs to fork
rem 09/09/2003   CVS_TD_M4          ulbricht      Allow for running single test
rem 10/05/2003   D178846            ulbricht      More changes to allow single test
rem 02/03/2004   D189349            ulbricht      JVM changed location
rem 02/23/2004   D191791            ulbricht      Look for JVM in java also for BobCat
rem 03/23/2004   D195681            ulbricht      Fix Ant optional jar version mismatch
rem 04/19/2004   D198971            ulbricht      Change dir for Multi-Build
rem 04/19/2004   D199126            ulbricht      Support Ant 1.6.1
rem 06/30/2004   D213500            ulbricht      Change Ant version detection
rem 11/11/2004   D243972            ulbricht      Change path name from SERV1 to FVT
rem 11/19/2004   D244607            ulbricht      Rename path name
rem 05/13/2005   D275916            ulbricht      Remove support Ant 1.5
rem 09/28/2005   D308997            ulbricht      Remove -Djava.protocol.handler.pkgs property
rem 10/27/2005   D317728            ulbricht      Allow fifth arg of Ant home
rem 05/18/2006   368616             ulbricht      Changes for WSFP
rem 09/05/2006   381622             smithd        Add ND support (JSTAF, setenvtask)
rem 10/24/2006   395172             smithd        Improve usability of STAF
rem 03/12/2007   423999             smithd        Remove STAF check
rem 05/22/2007   440922             jramos        Changes for Pyxis
rem 06/13/2007   445309             btiffany      Add usage statement, fix reports on java6
rem 08/21/2007   461165             btiffany      fix occasional aliasing between ant_home and ant_targets
rem 09/16/2007   467494             btiffany      fix case where usage statement wasn't shown
rem 05/23/2008   523472             btiffany      prepend drive letter if missing from directory parameters
rem 09/30/2008   554594             btiffany      don't break if fvt and WAS on different drives.
rem 11/07/2008   559143             jramos        Add Simplicity props to ANT_OPTS, jars to classpath
rem 09/21/2010   671018             btiffany      Add endorsed dirs to ANT_OPTS
rem 09/26/2011   726664             syed          Added jsse.jar and js.jar for SAML Web SSO

set all_args=%*
set FVT_TOP=%1%
shift
set WAS_TOP=%1%
shift
set buildNumber=%1%
shift
set cmvcRelease=%1%
shift

if not "%1%"=="" goto cl_arg_1
goto cl_arg_2

rem The cross platform team will not be setting the ANT_HOME in the
rem environment.  These statements will allow the ANT_HOME to be
rem added as a fifth arg to the unittest.bat.  However, we still
rem want the ability for the fifth arg to be an Ant target so check
rem to see if the arg is a directory.
rem sometimes it's both a target and a directory, so look for ant_home/bin to disambiguate -bt
:cl_arg_1

if not exist %1%\bin goto cl_arg_2
set ANT_HOME=%1%
shift
goto cl_arg_2

:cl_arg_2
set EXTRA_ANT_OPTS=%1 %2 %3 %4 %5 %6 %7 %8 %9

rem Make sure the directories are defined and exist
if not defined WAS_TOP set ARG=WAS_TOP& goto arg_not_defined
if not exist %WAS_TOP% set DIR=%WAS_TOP%& set ARG=WAS_TOP& goto dir_not_exist

if not defined FVT_TOP set ARG=FVT_TOP& goto arg_not_defined
if not exist %FVT_TOP% set DIR=%FVT_TOP%& set ARG=FVT_TOP& goto dir_not_exist

if not defined ANT_HOME set ARG=ANT_HOME& goto arg_not_defined
if not exist %ANT_HOME% set DIR=%ANT_HOME%& set ARG=ANT_HOME& goto dir_not_exist

rem Edit for buildNumber and cmvcRelease
if not defined buildNumber set ARG=buildNumber& goto arg_not_defined
if not defined cmvcRelease set ARG=cmvcRelease& goto arg_not_defined

rem some tests MUST have the drive letter in the path, put it there if missing.
set start_dir=%cd%
cd /d %FVT_TOP% 
set FVT_TOP=%cd%
cd /d %WAS_TOP%
set WAS_TOP=%cd%
cd /d %start_dir%

echo unittest.bat is invoked with arguments: %all_args%

rem The location of JUnit included in tests extracted from CMVC
set JUNIT_HOME=%FVT_TOP%\autoFVT\common\jars
if not exist %JUNIT_HOME% set DIR=%JUNIT_HOME%& set ARG=JUNIT_HOME& goto dir_not_exist

set TEST_base_dir=WSFVT

set FVT_base_dir=%FVT_TOP%\autoFVT

rem Add ANT build tools to PATH
set PATH=%ANT_HOME%\bin;%PATH%

rem Add JDK to PATH
set JAVA_HOME=%WAS_TOP%\_jvm

if not exist %JAVA_HOME% goto try_java_dir
goto add_jvm_to_path

:try_java_dir
set JAVA_HOME=%WAS_TOP%\java
if not exist %JAVA_HOME% goto cant_find_jvm
goto add_jvm_to_path

:add_jvm_to_path
set PATH=%JAVA_HOME%\jre\bin;%PATH%
set PATH=%JAVA_HOME%\bin;%PATH%

rem Clear out any stuff that may be in the classpath!
set CLASSPATH=

rem Setup CLASSPATH - WAS jar files
set CLASSPATH=%CLASSPATH%;%WAS_TOP%\lib\urlprotocols.jar;%FVT_base_dir%\common\jars\JSTAF.jar;%FVT_base_dir%\common\jars\setenvtask.jar;%FVT_base_dir%\common\jars\public.api.jar;%FVT_base_dir%\common\jars\provider.api.jar;%FVT_base_dir%\common\jars\provider.wsadmin.jar;%FVT_base_dir%\common\jars\provider.rxa.jar;%FVT_base_dir%\common\jars\jiiws.jar;%FVT_base_dir%\common\jars\jlanclient.jar;%FVT_base_dir%\common\jars\remoteaccess.jar;%FVT_base_dir%\common\jars\ssh.jar;%JUNIT_HOME%\junit.jar;%FVT_base_dir%\common\jars\jsse.jar;%FVT_base_dir%\common\jars\js.jar

rem Complete ANT_OPTS
set ANT_OPTS=-DWAS.base.dir=%WAS_TOP%  -Djavax.xml.transform.TransformerFactory=org.apache.xalan.processor.TransformerFactoryImpl -DFVT.base.dir=%FVT_base_dir% -DsimplicityConfigProps=%FVT_base_dir%\simplicityConfig.props -DbootstrappingPropsFile=%FVT_base_dir%\bootstrapping.properties -Djava.endorsed.dirs=%WAS_TOP%\endorsed_apis 

rem This will setup anything necessary in the files due to testing
rem with BobCat.
@call ant -DANT.base.dir=%ANT_HOME% -DFVT.base.dir=%FVT_base_dir% -DWAS.base.dir=%WAS_TOP% -f %FVT_TOP%\autoFVT\preBuildTest.xml

rem
rem The greater than signs must be added right after the variables
rem to prevent spaces from appearing after the variables.
rem
echo ^<property name="FVT.base" value="%FVT_TOP%"/^>> %FVT_base_dir%\instance.xml
echo ^<property name="WAS.base.dir" value="%WAS_TOP%"/^>>> %FVT_base_dir%\instance.xml
echo ^<property name="TEST.base.dir" value="%TEST_base_dir%"/^>>> %FVT_base_dir%\instance.xml
echo ^<property name="reportBuild" value="%buildNumber%"/^>>> %FVT_base_dir%\instance.xml
echo ^<property name="ANT.base.dir" value="%ANT_HOME%"/^>>> %FVT_base_dir%\instance.xml
echo ^<property name="reportRelease" value="%cmvcRelease%"/^>>> %FVT_base_dir%\instance.xml

rem Start running the FVTs
rem To accommodate for the AxisFvt tests that have a build.xml file instead of a buildTest.xml, I've
rem added a conditional statement here.  This is mainly for running a single test from the AxisFvt
rem directories.
if not exist buildTest.xml goto try_build_xml
ant -DFVT.base.dir=%FVT_base_dir% -DWAS.base.dir=%WAS_TOP% -f buildTest.xml %EXTRA_ANT_OPTS%
goto end

:try_build_xml
ant -DFVT.base.dir=%FVT_base_dir% -DWAS.base.dir=%WAS_TOP% -f build.xml %EXTRA_ANT_OPTS%
goto end

:arg_not_defined
echo The argument %ARG% was not defined.
call :usage
goto end

:dir_not_exist
echo The directory %DIR% specified in argument %ARG% does not exist.
call :usage
goto end

:cant_find_jvm
echo Could not find a JVM to run tests.
call :usage
goto end

goto end
:usage
        rem print a usage statement
        echo Usage: unittest.bat FVT_TOP WAS_TOP build_number CMVC_Release [ANT_HOME or Ant Targets]
        echo.
        echo  build_number and CMVC_Release are only meaningful when publishing reports.
        echo.
        echo  Mode 1: If invoked from .../fvt directory, will build and run all tests.
        echo      example:  bin\nt\unittest.bat c:\test c:\Progra~1\IBM\WebSphere\AppServer xxx yyy
        echo  Mode 2: If invoked from some source subdirectory beneath webservices.fvt, will build those tests.
        echo      example:  ..\..\..\bin\nt\unitest.bat c:\test c:\Progra~1\IBM\WebSphere\AppServer xxx yyy
        echo.
        echo  Useful Ant Targets: (to use, set ANT_HOME as an environment variable first)
        echo  For Mode 1:
        echo      Generate html reports:   report
        echo      example: bin\nt\unittest.bat c:\test c:\was\AppServer xx yy report
        echo.
        echo      Reinstall and repeat tests: fvtBase
        echo.
        echo   Ant targets For Mode 2:
        echo      re-run some tests:  component-run
        echo      Reinstall and repeat some tests: stop-server uninstall install start-server component-run
        echo.
goto :eof


:end

@endlocal
