call @was_home@/jaxws/wsimport.bat -target 2.2 -d @dest_dir@ @base_dir@\src\annotations\jaxwstooling\etc\JaxwsToolingService.wsdl
IF ERRORLEVEL 1 (
  echo call failed..!
) ELSE (
  echo call succeeded..!
)
