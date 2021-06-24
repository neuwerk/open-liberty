#!/bin/sh
# Web Services FVT Bat File

# !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
# The directory path where CMVC files extracted
# The installation root of WAS
# The place where ANT installed
# !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

# The location of JUnit

export FVT_base_dir=$1
export WAS_base_dir=$2
export TEST_base_dir=`echo $WAS_base_dir | awk -F"\/" '{print $3}'`

if [ ! -d $FVT_base_dir ]; then
   echo "Directory $FVT_base_dir does not exist"
   exit 1
fi

export JUNIT_HOME=$FVT_base_dir/ws/code/webservices/common/jars

if [ ! -d $JUNIT_HOME ]; then
   echo "Directory $JUNIT_HOME does not exist"
   exit 1
fi

if [ ! -d $WAS_base_dir ]; then
   echo "Directory $WAS_base_dir does not exist" 
   exit 1
fi 

export ANT_OPTS=-DWAS.base.dir=$WAS_base_dir

if [ -z $ANT_HOME ]; then
   echo "Directory $ANT_HOME does not exist"
   exit 1
fi

# Add JDK to PATH
export JAVA_HOME=$WAS_base_dir/java
export PATH=$PATH:$JAVA_HOME/bin
export PATH=$PATH:$JAVA_HOME/jre/bin

# Add ANT build tools to PATH
export PATH=$PATH:$ANT_HOME/bin

# Clear out any stuff that may be in the classpath!
export CLASSPATH=

# Setup CLASSPATH - WAS jar files
export CLASSPATH=$CLASSPATH:$WAS_base_dir/lib/webservices.jar
export CLASSPATH=$CLASSPATH:$WAS_base_dir/lib/axis.jar
export CLASSPATH=$CLASSPATH:$WAS_base_dir/lib/qname.jar
export CLASSPATH=$CLASSPATH:$WAS_base_dir/lib/commons-discovery.jar
export CLASSPATH=$CLASSPATH:$WAS_base_dir/lib/wsdl4j.jar
export CLASSPATH=$CLASSPATH:$WAS_base_dir/lib/jaxrpc.jar
export CLASSPATH=$CLASSPATH:$WAS_base_dir/lib/commons-logging-api.jar
export CLASSPATH=$CLASSPATH:$WAS_base_dir/lib/ws-commons-logging.jar
export CLASSPATH=$CLASSPATH:$WAS_base_dir/lib/j2ee.jar
export CLASSPATH=$CLASSPATH:$WAS_base_dir/lib/xerces.jar
export CLASSPATH=$CLASSPATH:$WAS_base_dir/lib/xalan.jar

# Setup CLASSPATH - Misc files
export CLASSPATH=$CLASSPATH:$ANT_HOME/lib/ant.jar
export CLASSPATH=$CLASSPATH:$JUNIT_HOME/junit.jar
export CLASSPATH=$CLASSPATH:$FVT_base_dir/ws/code/webservices/fvt/build/classes

# Start running the FVTs                            
ant -DFVT.base.dir=$FVT_base_dir/ws/code/webservices/fvt -DWAS.base.dir=$WAS_base_dir -buildfile buildTest-efvt.xml
