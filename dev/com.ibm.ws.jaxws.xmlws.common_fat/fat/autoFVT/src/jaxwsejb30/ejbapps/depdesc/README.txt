LIDB4511.45 JSR 109 Managed JAX-WS client.

Description:
These test cases are checking the functionality of the JSR-109 deployment descriptor's
ability to define a JAX-WS web service using the <service-ref> element inside a web.xml
file and an ejb-jar.xml file. 

This test also tests that the <service-ref> element overrides the values defined in the
WebServiceRef and WebServiceRefs annotations.

Run:
This test is fully automated see: <FVT_TOP>/autoFVT/readme.txt

To run test individually:
../../../../../bin/<nt or unix>/unittest.<bat or sh>/ <path to FVT_TOP> <path to
WAS_HOME> a b setHotInstall buildall uninstall install component-run

Notes:
This test case is covered fairly well but it still needs to test out the WebServiceRefs
annotation. 

Handler chains also need to be tested.

There are also a few additional overrides that need to be tested such as the <wsdl-file>
element to override wsdlLocation.

mappedName, type, and value should also be looked into overriding in the deployment
descriptor (web.xml or ejb-jar.xml)
