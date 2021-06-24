#!/bin/sh
#
# Web Services FVT Script File
#
# autoFVT/bin/unix/unittest.sh, WAS.websvcs.fvt, WAS85.FVT, cf011231.01
#
# Date         Feature/Defect    Author        Description
# ----------   --------------    -----------   ---------------------
# 08/15/2003   D174374           ulbricht      Ant needs properties in diff format
# 08/20/2003   D174754           ulbricht      Switch format back
# 10/05/2003   D178846           ulbricht      Allow for running single test
# 01/20/2004   D187712           ulbricht      instance.xml needs to be ASCII for z/OS
# 02/03/2004   D189349           ulbricht      JVM changed locations
# 02/23/2004   D191791           ulbricht      BobCat JVM still in old location
# 03/23/2004   D195681           ulbricht      Ant optional jar version mismatch
# 04/19/2004   D198971           ulbricht      Change dir for Multi-Build
# 04/19/2004   D199126           ulbricht      Add support for Ant 1.6.1
# 06/24/2004   D213500           ulbricht      Change detection of Ant version
# 07/24/2004   D219005           ulbricht      Handle encoding on z/OS
# 07/30/2004   D220461           ulbricht      Only add no args conversion for z/OS
# 08/09/2004   D222729           ulbricht      Modify execution for z/OS
# 08/17/2004   D224836           ulbricht      JVM in different location on z/OS EZWas
# 08/19/2004   D225528           ulbricht      Ant 1.5 key file name changed
# 11/11/2004   D243972           ulbricht      Change path name from SERV1 to FVT
# 11/19/2004   D244607           ulbricht      Rename path name
# 01/10/2005   D249479           ulbricht      Need LIBPATH for z/OS
# 02/08/2005   D254081           ulbricht      JDK changed location on z/OS
# 02/15/2005   D243709           ulbricht      a2e mp.sh for MultiProtocol tests
# 02/25/2005   D257672           ulbricht      Only run a2e once on mp.sh
# 04/25/2005   D269183.4         ulbricht      Put iSeries JDK in Path
# 05/13/2005   D275916           ulbricht      Remove Ant 1.5 support
# 08/11/2005   D294431           ulbricht      iSeries needs xerces jar
# 08/24/2005   D299172           vgoswami      Change OS400 to use Ant 1.5
# 09/28/2005   D308997           ulbricht      Remove -Djava.protocol.handler.pkgs property
# 10/27/2005   D317728           ulbricht      Allow fifth arg to be Ant home
# 11/16/2005   D324398           ulbricht      Change jdk for z/OS
# 03/07/2006   D353128           ulbricht      Remove a2e from commands
# 05/16/2006   368616            ulbricht      Changes for WSFP
# 09/05/2006   381622            smithd        Add ND support (JSTAF, setenvtask)
# 10/24/2006   395172            smithd        Improve usability of STAF
# 01/03/2007   412838            ulbricht      instance.xml in wrong encoding for OS400
# 03/12/2007   423999            smithd        Remove STAF check
# 05/22/2007   440922            jramos        Changes for Pyxis
# 07/17/2007   452504            btiffany      Add usage statement
# 08/21/2007   461165            btiffany      fix aliasing between ant_home and a target of same name
# 09/28/2007   470778            ulbricht      Change location of Java 6 for i5/OS
# 06/19/2008   526980            dheeks	       Fix gen heap space in HP
# 06/20/2008   527487            btiffany      Change ant_opts for 64 bit hp.
# 07/09/2008   535300            gkuo          set <property environment="env" /> into instance.xml
# 07/11/2008   534937            bluk          Add java.endorsed.dirs to ANT_OPTs so will pick up TransformerFactoryImpl
# 07/16/2008   532767		      dheeks	       Exporting ANT_HOME
# 07/18/2008   537591            bluk          Only add java.endorsed.dirs to ANT_OPTs for iSeries
# 07/22/2008   535544            btiffany      Change to j9 jvm to be consistent with jax-rpc bucket
# 08/07/2008   541401            btiffany      Even more permanent heap memory for hp, jms tests failing to compile
# 08/20/2008   545297            btiffany      hp jvm needs different flags between pa-risc and itanium
# 9/04/2008   545966              whsu           updates for solaris64 
# 9/08/2008   545966.1            whsu           solaris update needed for target.xml and solaris32 
# 9/10/2008   545966.2            whsu           solaris64 update needed :add a flag run.buildtestcase.fork
# 11/07/2008   559143            jramos        Add Simplicity props to ANT_OPTS, jars to classpath
# 03/13/2009  568794		     dheeks         Update for AIX 32 and 64 bit JVM ops, different for each.
# 05/22/2009  592239            jramos         Change how ANT_HOME is set when passed in to unittest
# 06/05/2009  595214            btiffany       un-regress hp64 flag.  May not need without staf, but we'll be consistent.
# 06/20/2009  596146            btiffany       increase max heap for solaris on amd 64.
# 09/21/2010  670108            btiffany       add endorsed dirs to ANT_OPTS 
# 12/13/2010  670291.1          btiffany       update endorsed dirs for hp, solaris
# 03/30/2011  699868            jtnguyen       change java_home to java6.2.6 on iSeries for V8 requirement
# 04/01/2010  699378            syed           Need to update LIBPATH for zOS
# 01/25/2012  726664            syed           Added jsse.jar and js.jar for SAML Web SSO tests




function usage
{
        # print a usage statement.
        echo "Usage: unittest.sh FVT_TOP WAS_TOP build_number CMVC_Release [JAVA_HOME or Ant Targets]"
        echo ""
        echo "build_number and CMVC_Release are only meaningful when publishing reports."
        echo ""
        echo "Mode 1: If invoked from websvcs.fvt directory, will build and run all tests."
        echo "     example:  bin/unix/unittest.sh /test /opt/IBM/WebSphere/AppServer xxx yyy"
        echo "Mode 2: If invoked from some source subdirectory beneath websvcs.fvt, will build those tests."
        echo "     example:  ../../../bin/nt/unitest.sh /test /opt/IBM/WebSphere/AppServer xxx yyy"
        echo ""
        echo "Useful Ant Targets: (to use, set ANT_HOME as an environment variable first)"
        echo " For Mode 1:"
        echo "     Generate html reports:   report "
        echo "     example: bin/unix/unittest.sh /test /was/AppServer xx yy report "
        echo ""
        echo "     Reinstall and repeat tests: fvtBase"
        echo ""
        echo " Ant targets For Mode 2:"
        echo "     re-run some tests:  component-run"
        echo "     Reinstall and repeat some tests: stop-server uninstall install start-server component-run"
        echo ""
}

echo unittest.sh is invoked with arguments: $*
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
shift


EXTRA_ANT_OPTS="$1 $2 $3 $4 $5 $6 $7 $8 $9"

TEST_base_dir=WSFVT
export TEST_base_dir

platform=`uname`

COMMON_JARS=$FVT_TOP/autoFVT/common/jars

if [ -z "$FVT_TOP" ]; then
    echo "FVT_TOP was not defined"
    usage
    exit 1
fi
if [ ! -d $FVT_TOP ]; then
   echo "Specified FVT_TOP Directory, $FVT_TOP does not exist"
   usage
   exit 1
fi

if [ -z "$WAS_TOP" ]; then
    echo "WAS_TOP was not defined"
        usage
    exit 1
fi
if [ ! -d $WAS_TOP ]; then
   echo "Specified WAS_TOP Directory, $WAS_TOP does not exist"
   usage
   exit 1
fi


if [ -z "$JAVA_HOME" ]; then
   echo "JAVA_HOME was not defined"
   usage
   exit 1
fi
if [ ! -d $ANT_HOME ]; then
   echo "ANT_HOME Directory, $ANT_HOME does not exist"
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
    touch -C 819 $FVT_TOP/autoFVT/instance.xml
    echo "<property name=\"FVT.base\" value=\"$FVT_TOP\"/>" >> $FVT_TOP/autoFVT/instance.xml
else
    echo "<property name=\"FVT.base\" value=\"$FVT_TOP\"/>" > $FVT_TOP/autoFVT/instance.xml
fi

echo "<property name=\"WAS.base.dir\" value=\"$WAS_TOP\"/>" >> $FVT_TOP/autoFVT/instance.xml
echo "<property name=\"TEST.base.dir\" value=\"$TEST_base_dir\"/>" >> $FVT_TOP/autoFVT/instance.xml
echo "<property name=\"reportBuild\" value=\"$buildNumber\"/>" >> $FVT_TOP/autoFVT/instance.xml
echo "<property name=\"ANT.base.dir\" value=\"$ANT_HOME\"/>" >> $FVT_TOP/autoFVT/instance.xml
echo "<property name=\"reportRelease\" value=\"$cmvcRelease\"/>" >> $FVT_TOP/autoFVT/instance.xml
if [ "$platform" = "HP-UX" ]; then
   # HP need to get the 64bit falg (defect527487)
   # but it caused trouble in zOS, because the EBCDIC and ASCII encoding (we might want to create a task instead of tag property )
   echo "<property environment=\"env\"/>" >> $FVT_TOP/autoFVT/instance.xml
fi

# Add JDK to PATH
export JAVA_HOME
PATH=$ANT_HOME/bin:$PATH
PATH=$JAVA_HOME/jre/bin:$PATH
PATH=$JAVA_HOME/bin:$PATH
export PATH

# SPECIAL_ANT_OPTS can be used to pass in os-specific ANT Defines.
SPECIAL_ANT_OPTS=" "

ENDORSED_DIRS="$FVT_TOP/autoFVT/common/jars/endorsed"
ANT_OPTS="-DFVT.base=$FVT_TOP -DWAS.base.dir=$WAS_TOP -Djava.endorsed.dirs=$ENDORSED_DIRS -Djavax.xml.transform.TransformerFactory=org.apache.xalan.processor.TransformerFactoryImpl"
if [ "$platform" = "SunOS" ]; then
    ANT_OPTS="$ANT_OPTS -XX:MaxPermSize=128m"
    #for defect 545966
    kernel_bits=`isainfo -b`
    if  [ "$kernel_bits" = "64" ]; then
      if [ -d "$JAVA_HOME/bin/sparcv9" ] || [ -d "$JAVA_HOME/bin/amd64" ] ; then 
        ANT_OPTS="$ANT_OPTS -XX:MaxPermSize=512m -d64 "
        SPECIAL_ANT_OPTS="-Dcompile.foreach.fork=no"
   echo "<property name=\"hp64StafFlag\" value=\"-d64\" />" >> $FVT_TOP/autoFVT/instance.xml
   echo "<property name=\"junit.jvm.xmx\"" value=\"-Xmx1024m\"" />" >> $FVT_TOP/autoFVT/instance.xml
   echo "<property name=\"compile.foreach.fork\" value=\"no\" />" >> $FVT_TOP/autoFVT/instance.xml
   echo "<property name=\"run.buildtestcase.fork\" value=\"no\" />" >> $FVT_TOP/autoFVT/instance.xml
      fi    
    fi
 

elif [ "$platform" = "OS/390" ]; then
    ANT_OPTS="$ANT_OPTS -Dfile.encoding=ISO8859-1 -Xnoargsconversion"
elif [ "$platform" = "OS400" ]; then
    #535544
	#ANT_OPTS="$ANT_OPTS -Djava.endorsed.dirs=$WAS_TOP/java/endorsed"
	ANT_OPTS="$ANT_OPTS -Djava.endorsed.dirs=$WAS_TOP/java/endorsedj9:$ENDORSED_DIRS"
elif [ "$platform" = "AIX" ]; then
     #DJH 568794 32 bit AIX needs default JVM properites, this block only sets up JVM props for 64bit JVM
     export PPC64="$(java -version 2>&1 | grep -c ppc64)"
     if [ $PPC64 -ne 0 ]; then
        ANT_OPTS="$ANT_OPTS -Xoss256m -Xms500m -Xmx1200m"
        echo "Java is 64 bit"
      fi
elif [ "$platform" = "HP-UX" ]; then
    ANT_OPTS="$ANT_OPTS -Xms500m -Xmx2000m"
	# need this next one so staf will correctly load 64 bit native libs 
    # but on pa-risc, jvm is 32bit only, -d64 does not work there. 
    is_itanium=`getconf MACHINE_MODEL |grep -i ia`
    if [ -n "$is_itanium" ]; then
    	kernel_bits=`getconf KERNEL_BITS`
    	if [ "$kernel_bits" = "64" ]; then
            # staf can't find it's own libs without a hint.
    		ANT_OPTS="$ANT_OPTS -XX:MaxPermSize=512m -d64 "
    		SPECIAL_ANT_OPTS="-Dcompile.foreach.fork=no"     		
			#595187 -  527487 fixed z but broke this, so we'll use echo line below
    		#export HP64BIT=true  
			echo "<property name=\"hp64StafFlag\" value=\"-d64\" />" >> $FVT_TOP/autoFVT/instance.xml
    	fi	
    fi
fi

FVT_base_dir="$FVT_TOP/autoFVT"
export FVT_base_dir

JPDA_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,address=5006,server=y,suspend=n"
ANT_OPTS="$JPDA_OPTS $ANT_OPTS -DFVT.base.dir=$FVT_base_dir -DsimplicityConfigProps=$FVT_base_dir/simplicityConfig.props -DbootstrappingPropsFile=$FVT_base_dir/bootstrapping.properties"

echo the ANT_OPTS environment variable is $ANT_OPTS
export ANT_OPTS

CMD="ant -DANT.base.dir=$ANT_HOME $SPECIAL_ANT_OPTS -DFVT.base.dir=$FVT_TOP/autoFVT -DWAS.base.dir=$WAS_TOP -f $FVT_TOP/autoFVT/preBuildTest.xml"
$CMD

if [ "$platform" = "OS/390" ] &&
   [ ! -f $FVT_TOP/autoFVT/setup-zos ]; then
    # Don't run a2e more than once on the same file
    a2e $FVT_TOP/autoFVT/bin/unix/mp.sh
    chmod 777 $FVT_TOP/autoFVT/bin/unix/mp.sh
    CMD="ant -DANT.base.dir=$ANT_HOME -DFVT.base.dir=$FVT_TOP/autoFVT -DWAS.base.dir=$WAS_TOP -f $FVT_TOP/autoFVT/preBuildTest.xml setupZOS"
    $CMD
fi

if [ "$platform" = "OS/390" ]; then

LIBPATH=$WAS_TOP/lib/s390-31:$LIBPATH
export LIBPATH

fi

# Set JUNIT_HOME
JUNIT_HOME=$FVT_TOP/autoFVT/common/jars
export JUNIT_HOME

CLASSPATH=
CLASSPATH=$FVT_TOP/autoFVT/acute.jar:$FVT_base_dir/common/jars/twas/urlprotocols.jar:$COMMON_JARS/JSTAF.jar:$COMMON_JARS/setenvtask.jar:$JUNIT_HOME/junit.jar:$FVT_base_dir/common/jars/fattest.simplicity.jar:$FVT_base_dir/common/jars/public.api.jar:$FVT_base_dir/common/jars/provider.api.jar:$FVT_base_dir/common/jars/provider.wsadmin.jar:$FVT_base_dir/common/jars/provider.rxa.jar:$FVT_base_dir/common/jars/jiiws.jar:$FVT_base_dir/common/jars/jlanclient.jar:$FVT_base_dir/common/jars/remoteaccess.jar:$FVT_base_dir/common/jars/ssh.jar:$FVT_base_dir/common/jars/jsse.jar:$FVT_base_dir/common/jars/js.jar

export CLASSPATH

# Start running the FVTs
CMD="ant -f"
if [ -f buildTest.xml ]; then
    CMD="$CMD buildTest.xml"
else
    CMD="$CMD build.xml"
fi
CMD="$CMD $EXTRA_ANT_OPTS"
$CMD
