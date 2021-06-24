call @was_home@/jaxws/wsimport.bat -target 2.2 -d @build_work@ -s @build_work@ -p @inv_package_name@ @base_dir@\src\annotations\jaxwstooling\etc\JaxwsToolingService.wsdl
IF ERRORLEVEL 1 (
  echo call failed..!
) ELSE (
  echo call succeeded..!
)
