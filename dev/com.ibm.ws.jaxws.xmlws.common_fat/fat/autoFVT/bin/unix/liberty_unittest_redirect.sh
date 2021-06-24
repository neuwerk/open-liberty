#!/bin/sh
#
# Run unittest.sh, redirecting output to a file. This version of
# unittest is run by websvcs.fvt/executeFVT.xml
#
# FVT/ws/code/websvcs.fvt/bin/unix/unittest_redirect.sh, WAS.websvcs.fvt, WAS85.FVT, cf011231.01, 1.2.1.1
#
# Date         Feature/Defect    Author        Description
# ----------   --------------    -----------   ---------------------
# 12/11/2008   566458            jramos        New File
# 

FVT_TOP=$1
export FVT_TOP
shift
WAS_TOP=$1
export WAS_TOP
shift
buildNumber=$1
shift
cmvcRelease=$1
shift
JAVA_HOME=$1
export JAVA_HOME
shift

EXTRA_ANT_OPTS="$1 $2 $3 $4 $5 $6 $7 $8 $9"

$FVT_TOP/autoFVT/bin/unix/liberty_unittest.sh $FVT_TOP $WAS_TOP $buildNumber $cmvcRelease $JAVA_HOME $EXTRA_ANT_OPTS | tee out.log

cp out.log results

echo Execution Complete >> executionComplete.log
