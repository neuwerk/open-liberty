call @was_home@/jaxws/wsgen.bat -keep  -d @build_work@ -s @build_work@ -r @test_dir@ -wsdl[soap1.1] -verbose -extension -cp @classpath@;. ThereIsNoSuchSEI  -servicename {http://localhost:9090/some/}someServicename -portname {http://localhost:9090/some/}somePortname
IF ERRORLEVEL 1 (
  echo call failed..!
) ELSE (
  echo call succeeded..!
)
