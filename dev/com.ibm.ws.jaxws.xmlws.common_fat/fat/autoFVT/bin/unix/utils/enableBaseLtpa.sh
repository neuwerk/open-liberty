#!/bin/sh
#
# Web Services FVT Script File
#
# 1.1, 1/4/08
#
# Date         Feature/Defect    Author        Description
# ----------   --------------    -----------   ---------------------
# 12/07/2007                     gkuo          create

function usage
{
		# print a usage statement.
        echo "Usage: unittest.sh FVT_TOP WAS_TOP build_number CMVC_Release [ANT_HOME or Ant Targets]"
}



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
    if [ -d $1 ]; then
        ANT_HOME=$1
        shift
    fi
fi

EXTRA_ANT_OPTS="$1 $2 $3 $4 $5 $6 $7 $8 $9"

TEST_base_dir=WSFVT
export TEST_base_dir

platform=`uname`

COMMON_JARS=$FVT_TOP/FVT/ws/code/websvcs.fvt/common/jars

if [ -z "$FVT_TOP" ]; then
    echo "FVT_TOP was not defined"
	usage
    exit 1
fi
if [ ! -d $FVT_TOP ]; then
   echo "Directory $FVT_TOP does not exist"
   usage
   exit 1
fi

if [ -z "$WAS_TOP" ]; then
    echo "WAS_TOP was not defined"
	usage
    exit 1
fi
if [ ! -d $WAS_TOP ]; then
   echo "Directory $WAS_TOP does not exist"
   usage
   exit 1
fi


if [ -z "$ANT_HOME" ]; then
   echo "ANT_HOME was not defined"
   usage
   exit 1
fi
if [ ! -d $ANT_HOME ]; then
   echo "Directory $ANT_HOME does not exist"
   usage
   exit 1
fi

if [ -z "$buildNumber" ]; then
   echo "buildNumber was not defined"
   usage
   exit 1
fi
if [ -z "$cmvcRelease" ]; then
   echo "cmvcRelease was not defined"
   usage
   exit 1
fi

if [ "$platform" = "OS400" ]; then
    touch -C 819 $FVT_TOP/FVT/ws/code/websvcs.fvt/instance.xml
    echo "<property name=\"FVT.base\" value=\"$FVT_TOP\"/>" >> $FVT_TOP/FVT/ws/code/websvcs.fvt/instance.xml
else
    echo "<property name=\"FVT.base\" value=\"$FVT_TOP\"/>" > $FVT_TOP/FVT/ws/code/websvcs.fvt/instance.xml
fi

echo "<property name=\"WAS.base.dir\" value=\"$WAS_TOP\"/>" >> $FVT_TOP/FVT/ws/code/websvcs.fvt/instance.xml
echo "<property name=\"TEST.base.dir\" value=\"$TEST_base_dir\"/>" >> $FVT_TOP/FVT/ws/code/websvcs.fvt/instance.xml
echo "<property name=\"reportBuild\" value=\"$buildNumber\"/>" >> $FVT_TOP/FVT/ws/code/websvcs.fvt/instance.xml
echo "<property name=\"ANT.base.dir\" value=\"$ANT_HOME\"/>" >> $FVT_TOP/FVT/ws/code/websvcs.fvt/instance.xml
echo "<property name=\"reportRelease\" value=\"$cmvcRelease\"/>" >> $FVT_TOP/FVT/ws/code/websvcs.fvt/instance.xml

if [ "$platform" = "OS/390" ]; then
    LIBPATH=$WAS_TOP/lib:$LIBPATH
    export LIBPATH
    JAVA_HOME=$WAS_TOP/java
    if [ ! -d $JAVA_HOME ]; then
        echo "Could not find JDK"
        exit 1
    fi
    e2a $FVT_TOP/FVT/ws/code/websvcs.fvt/instance.xml
elif [ "$platform" = "OS400" ]; then
    JAVA_HOME=/QIBM/ProdData/Java400/jdk15
    if [ ! -d $JAVA_HOME ]; then
        echo "Could not find JDK"
        exit 1
    fi
else
    JAVA_HOME=$WAS_TOP/_jvm
    if [ ! -d $JAVA_HOME ]; then
        JAVA_HOME=$WAS_TOP/java
        if [ ! -d $JAVA_HOME ]; then
            echo "Could not find JDK"
            exit 1
        fi
    fi
fi

# Add JDK to PATH
export JAVA_HOME
PATH=$ANT_HOME/bin:$PATH
PATH=$JAVA_HOME/jre/bin:$PATH
PATH=$JAVA_HOME/bin:$PATH
export PATH

CLASSPATH=
CLASSPATH=$FVT_TOP/FVT/ws/code/websvcs.fvt/acute.jar:$WAS_TOP/lib/urlprotocols.jar:$COMMON_JARS/JSTAF.jar:$COMMON_JARS/setenvtask.jar:$JUNIT_HOME/junit.jar

if [ "$platform" = "OS400" ]; then
    CLASSPATH=/QIBM/ProdData/OS400/xml/lib/xerces411.jar:$CLASSPATH
fi

export CLASSPATH

ANT_OPTS="-DFVT.base=$FVT_TOP -DWAS.base.dir=$WAS_TOP -Djavax.xml.transform.TransformerFactory=org.apache.xalan.processor.TransformerFactoryImpl"
if [ "$platform" = "SunOS" ]; then
    ANT_OPTS="$ANT_OPTS -XX:MaxPermSize=128m"
elif [ "$platform" = "OS/390" ]; then
    ANT_OPTS="$ANT_OPTS -Dfile.encoding=ISO8859-1 -Xnoargsconversion"
elif ["$platform" = "AIX"]; then
    ANT_OPTS="$ANT_OPTS -Xmx2000m -Xms20m"
fi
export ANT_OPTS

CMD="ant -DANT.base.dir=$ANT_HOME -DFVT.base.dir=$FVT_TOP/FVT/ws/code/websvcs.fvt -DWAS.base.dir=$WAS_TOP -f $FVT_TOP/FVT/ws/code/websvcs.fvt/bin/unix/utils/ltpaBase.xml"
$CMD
