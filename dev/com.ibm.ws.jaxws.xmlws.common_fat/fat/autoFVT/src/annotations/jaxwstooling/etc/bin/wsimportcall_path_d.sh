# check wsimport.sh. The test that runs this will look at console output
# as well as return code to decide if wsimport is behaving properly.
# it should complain about the -d path.


# echo on
set -x

if [ -z "$FVT_TOP" ]; then
    echo "FVT_TOP was not defined"
    exit 12
fi
if [ ! -d $FVT_TOP ]; then
   echo "Directory $FVT_TOP does not exist"
   exit 12
fi

if [ -z "$WAS_TOP" ]; then
    echo "WAS_TOP was not defined"
    exit 12
fi
if [ ! -d $WAS_TOP ]; then
   echo "Directory $WAS_TOP does not exist"
   exit 12
fi

platform=`uname`
echo $platform
if [ "$platform" = "OS400" ]; then
  unset ext
else
  # Liberty wsgen and wsimport does not have file extension.
  #ext=".sh"
  ext=""
fi

WAS_BIN=$WAS_TOP/bin
BUILD_WORK=$FVT_TOP/autoFVT/build/work
BASE_DIR=$FVT_TOP/autoFVT
BUILD_CLASSES=$FVT_TOP/autoFVT/build/classes
TEST_DIR=$BUILD_WORK/annotations/jaxwstooling


sh $WAS_BIN/jaxws/wsimport$ext -target 2.2 -d $BUILD_WORK/INVALIDPATH/jaxwstooling \
 $BASE_DIR/src/annotations/jaxwstooling/etc/JaxwsToolingService.wsdl


if [ "$?" -ne "0" ]; then
   echo "call failed..!"
   exit 1
else
   echo "call succeeded..!"
   exit 0
fi
