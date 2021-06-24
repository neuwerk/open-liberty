#!/bin/sh
#
# 1.1, 5/22/07
#
# THIS PRODUCT CONTAINS RESTRICTED MATERIALS OF IBM
# 5724-I63, 5724-H88 (C) COPYRIGHT International Business Machines Corp., 2005, 2006
# All Rights Reserved * Licensed Materials - Property of IBM
# US Government Users Restricted Rights - Use, duplication or disclosure
# restricted by GSA ADP Schedule Contract with IBM Corp.
#
#
# Date        Defect/Feature       Author         Description
# ------      -----------------    -----------    --------------------
# 02/13/2005  243709               ulbricht       New File
# 05/24/2005  275916.1             ulbricht       Updates for componentization
# 07/20/2005  292226               ulbricht       More updates for componentization
# 08/03/2005  295527               ulbricht       Componentization updates
# 10/11/2005  311801               ulbricht       Componentization updates
# 12/05/2005  328481               ulbricht       Change jar file names
# 03/08/2006  353128               ulbricht       Change jar file names
# 04/27/2006  365687               ulbricht       Add OS390 jar file
#

#!echo "Usage: mp <FVT_HOME> <PROFILE_DIR> <TEST_NAME> <TEST_CLASS> 

FVT_HOME=$1
echo $FVT_HOME
PROFILE_DIR=$2
echo $PROFILE_DIR
TEST_NAME=$3
echo $TEST_NAME
TEST_CLASS=$4
echo $TEST_CLASS

binDir=`dirname $0`
. $binDir/setupCmdLine.sh

isJavaOption=false
for option in "$@" ; do
  if [ "$option" = "-JVMOptions" ] ; then
     isJavaOption=true
  else
     if [ "$isJavaOption" = "true" ] ; then
        javaoption=$option
        break
     fi
  fi
done


ORB_RAS_MGR=-Dcom.ibm.CORBA.RasManager=com.ibm.websphere.ras.WsOrbRasManager 
NAMING_FACTORY=com.ibm.websphere.naming.WsnInitialContextFactory
JMS_PATH=$WAS_HOME/lib/WMQ/java/lib

#Platform specific args...
PLATFORM=`/bin/uname`
case $PLATFORM in
  AIX)
    LIBPATH=$binDir:$LIBPATH
    export LIBPATH ;;
  Linux)
    LD_LIBRARY_PATH=$binDir:$LD_LIBRARY_PATH
    export LD_LIBRARY_PATH ;;
  SunOS)
    LD_LIBRARY_PATH=$binDir:$LD_LIBRARY_PATH
    export LD_LIBRARY_PATH ;;
  HP-UX)
    SHLIB_PATH=$binDir:$SHLIB_PATH
    export SHLIB_PATH ;;
  OS/390)
    PATH="$PATH":$binDir
    ENCODE_ARGS="-Xnoargsconversion -Dfile.encoding=ISO8859-1" 
    export PATH
    X_ARGS="-Xnoargsconversion" ;;
esac

WAS_CLASSPATH=$WAS_CLASSPATH:$FVT_HOME/build/classes
WAS_CLASSPATH=$WAS_CLASSPATH:$WAS_HOME/plugins/com.ibm.ws.runtime_6.1.0.jar
WAS_CLASSPATH=$WAS_CLASSPATH:$WAS_HOME/plugins/com.ibm.ws.runtime.ws390_6.1.0.jar
WAS_CLASSPATH=$WAS_CLASSPATH:$WAS_HOME/plugins/org.eclipse.core.runtime_3.1.2.jar
WAS_CLASSPATH=$WAS_CLASSPATH:$WAS_HOME/plugins/org.eclipse.osgi_3.1.2.jar
WAS_CLASSPATH=$WAS_CLASSPATH:$WAS_HOME/plugins/com.ibm.ws.wccm_6.1.0.jar
WAS_CLASSPATH=$WAS_CLASSPATH:$WAS_HOME/plugins/com.ibm.ws.emf_2.1.0.jar
WAS_CLASSPATH=$WAS_CLASSPATH:$WAS_HOME/plugins/com.ibm.ws.bootstrap_6.1.0.jar
WAS_CLASSPATH=$WAS_CLASSPATH:$WAS_HOME/plugins/com.ibm.wsspi.extension_6.1.0.jar
WAS_CLASSPATH=$WAS_CLASSPATH:$WAS_HOME/lib/j2ee.jar
WAS_CLASSPATH=$WAS_CLASSPATH:$WAS_HOME/lib/bootstrap.jar
WAS_CLASSPATH=$WAS_CLASSPATH:$PROFILE_DIR/installedApps/$WAS_CELL/MP109ServerApp.ear/MP109Ejb.jar
WAS_CLASSPATH=$WAS_CLASSPATH:$PROFILE_DIR/installedApps/$WAS_CELL/MP101App.ear/MP101Ejb.jar
WAS_CLASSPATH=$WAS_CLASSPATH:$PROFILE_DIR/installedApps/$WAS_CELL/MP101ComplexApp.ear/MP101ComplexEjb.jar

echo $WAS_CELL
echo $WAS_CLASSPATH

"$JAVA_HOME/bin/java" \
  $ENCODE_ARGS \
  $CONSOLE_ENCODING \
  $javaoption \
  $WAS_LOGGING \
  -Dibm.websphere.preload.classes=true \
  -Dcom.ibm.ws.client.installedConnectors="$CLIENT_CONNECTOR_INSTALL_ROOT" \
  "$CLIENTSAS" \
  "$CLIENTSSL" \
  "$CLIENTSOAP" \
  $ORB_RAS_MGR \
  $USER_INSTALL_PROP \
  -Dwas.install.root="$WAS_HOME" \
  -Dws.ext.dirs="$WAS_EXT_DIRS:$JMS_PATH" \
  -Djava.security.auth.login.config="$USER_INSTALL_ROOT/properties/wsjaas_client.conf" \
  -Dcom.ibm.CORBA.BootstrapHost=$DEFAULTSERVERNAME \
  -Dcom.ibm.CORBA.BootstrapPort=$SERVERPORTNUMBER \
  -Djava.naming.factory.initial=$NAMING_FACTORY \
  -classpath "$WAS_CLASSPATH" com.ibm.ws.bootstrap.WSLauncher \
  $TEST_CLASS $TEST_NAME $FVT_HOME

