LIDB4511.45 JSR 109 Managed JAX-WS client.

Description:
These test cases are checking the functionality of the JSR-109 annotations @Resource
and @Resources. The main class in this test case is the ResourceRefChecker class, it is
what takes the services and uses the @Resource and @Resourcess annotations to
have the classes injected.

Run:
This test is fully automated see: <FVT_TOP>/autoFVT/readme.txt

To run test individually:
../../../../../bin/<nt or unix>/unittest.<bat or sh>/ <path to FVT_TOP> <path to
WAS_HOME> a b setHotInstall buildall uninstall install component-run

Notes:
The variations are fairly well covered in this test but it is missing a WAR-based
reference checker to check that the @Resource/s annotations function properly in a
WAR-based service.

Currently, only the @Resources annotation is using an @Resource annotation with any
elements defined (the name and type elements). More research should be done and more tests
most likely should be created to test the other elements of the @Resource annotation such
as the mappedName.
