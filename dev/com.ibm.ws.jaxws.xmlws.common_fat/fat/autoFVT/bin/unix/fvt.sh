#!/bin/sh
# Web Services FVT Bat File
#
# 1.1, 5/21/07
#
# Date         Feature/Defect    Author        Description
# ----------   --------------    -----------   ---------------------
# 12/11/2003   D185471           ulbricht      build number/release missing on fvt
# 04/19/2004   D199126           ulbricht      Change for Multi-Build dir structure
# 11/11/2004   D243972           ulbricht      Change path name from SERV1 to FVT
# 11/19/2004   D244607           ulbricht      Rename path name
# 03/23/2005   LIDB3525-7.1      ulbricht      Exec thin client
# 10/11/2005   311801            ulbricht      Remove exec thin client
# 05/16/2006   368616            ulbricht      Changes for WSFP

FVT_base_dir=$1
export FVT_base_dir

WAS_base_dir=$2
export WAS_base_dir

buildNumber=$3
export buildNumber

cmvcRelease=$4
export cmvcRelease

sh $FVT_base_dir/WFVT/ws/code/websvcs.fvt/bin/unix/unittest.sh $FVT_base_dir $WAS_base_dir $buildNumber $cmvcRelease "-DreportServer=amy.austin.ibm.com -v"
