LIDB4511.46 JSR 109 Server-side deployment descriptors.

Description:
These test cases are checking the functionality of the JSR-109 webservices.xml
on an EJB3-based JAX-WS service and a standard WAR-based JAX-WS service. 

Run:
This test is fully automated see: <FVT_TOP>/autoFVT/readme.txt

To run test individually:
../../../../bin/<nt or unix>/unittest.<bat or sh>/ <path to FVT_TOP> <path to
WAS_HOME> a b setHotInstall buildall uninstall install component-run

Notes:
Currently, there are only some basic test cases for the webservices.xml file.
Handler chains need to be tested as well as the <enable-mtom> element of the
webservices.xml file.
