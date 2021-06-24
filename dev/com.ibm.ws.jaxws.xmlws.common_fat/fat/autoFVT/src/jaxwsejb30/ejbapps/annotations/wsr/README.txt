LIDB4511.45 JSR 109 Managed JAX-WS client.

Description:
These test cases are checking the functionality of the JSR-109 annotations @WebServiceRef
and @WebServiceRefs. The main class in this test case is the ReferenceChecker class, it is
what takes the services and uses the @WebServiceRef and @WebServiceRefs annotations to
have the classes injected.

Run:
This test is fully automated see: <FVT_TOP>/autoFVT/readme.txt

To run test individually:
../../../../../bin/<nt or unix>/unittest.<bat or sh>/ <path to FVT_TOP> <path to
WAS_HOME> a b setHotInstall buildall uninstall install component-run

Notes:
The variations are fairly well covered in this test but it is missing a WAR-based
reference checker to check that the @WebServiceRef/s annotations function properly in a
WAR based service.

The wsdlLocation element of @WebServiceRef/s could possibly be a challenge since there is
no easy way to redefine that location in the annotation itself. We don't want to override
it in the deployment descriptor because that defeats the purpose of the test. It might be
a good idea to only have a small few of these @WebServiceRef/s annotations actually use
the wsdlLocation element and leave the others to find the WSDL file themselves.
