#!/bin/sh
#
# 1.1, 5/22/07
#
# Description: This file was created because some tests were failing
#              on Unix platforms because the classpath was not being
#              used from the test case.
#
# Date         Feature/Defect    Author        Description
# ----------   --------------    -----------   ---------------------
# 10/06/2004   D221507.1         vgoswami      New file

FVT_TOP=$1
shift
CMD=$*
                             
CLASSPATH=$CLASSPATH:$FVT_TOP/build/classes
export CLASSPATH

$CMD
