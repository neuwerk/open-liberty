package annotations.webservice.testdata;
// test the webservice annotation with params defined.
// the package name has to match it's target location, or classloader won't be able to load it.
import javax.jws.*;
//@Bogus()
@WebService(name="testname", targetNamespace="testtns", serviceName="testsvc", 
			wsdlLocation="nowhere", portName="testport")
public class WebServiceMatchesWsdl2{
    @WebMethod()
    public String echo(String e){
        return e;
    }
}
