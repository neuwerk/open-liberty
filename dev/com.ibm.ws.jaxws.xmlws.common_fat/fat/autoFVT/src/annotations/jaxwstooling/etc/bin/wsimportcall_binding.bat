call @was_home@/jaxws/wsimport.bat -target 2.2 -d @build_work@\annotations\jaxwstooling -s @build_work@\annotations\jaxwstooling @base_dir@\src\annotations\jaxwstooling\etc\JaxwsToolingService.wsdl -b @inv_binding_path@
IF ERRORLEVEL 1 (
  echo call failed..!
) ELSE (
  echo call succeeded..!
)
