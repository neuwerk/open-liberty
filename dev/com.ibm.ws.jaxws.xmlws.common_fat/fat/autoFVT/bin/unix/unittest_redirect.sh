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

if [ ! -z "$1" ]; then
    # look for ANT_HOME/bin directory, in case there's both a target and directory of same name present.
    if [ -d $1/bin ]; then
        ANT_HOME=$1
        shift
    fi
fi

EXTRA_ANT_OPTS="$1 $2 $3 $4 $5 $6 $7 $8 $9"

$FVT_TOP/FVT/ws/code/websvcs.fvt/bin/unix/unittest.sh $FVT_TOP $WAS_TOP $buildNumber $cmvcRelease $ANT_HOME $EXTRA_ANT_OPTS | tee out.log

cp out.log results

echo Execution Complete >> executionComplete.log
