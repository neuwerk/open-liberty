Note on package names and test styles:

Annotation tests were begun with an experimental archtitecture that used an implementation
adapter to switch quickly between IBM and other implementations.   Tests exist in
this type of architecture for oneway, reqrespwrappers, soapbinding, webendpoint,
webservice,  webparam, and webmethod.  In this architecture, most of the setup is done 
in the test case's setup method instead of the ANT script.

The advantage is being able to write a test that is implementation independent.
One just needs to specify which implementation the test is running in 
to invoke the desired adapter. 

While convenient for development, it was found that this architecture imposes a
performance penalty when running within the FVT automation harness, as it has to start separate ant child
processes to use common ant tasks.  It's also tricky to make sure the various implementation 
adapters perform correctly cross-platform. 

For that reason, later tests were developed under the traditional webservices FVT ant-centered
architecture.  Because the way these tests work is so different, annotations using both types of tests
will have their "traditional" tests in a different package name, (name)_g2, for group 2.
So you will see annotations.webservice_g2, etc.   The only significance is the test 
architecture used. 