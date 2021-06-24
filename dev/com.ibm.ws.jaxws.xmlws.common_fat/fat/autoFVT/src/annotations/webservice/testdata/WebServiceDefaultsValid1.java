package annotations.webservice.testdata;

import javax.jws.*;

/**
 * A trivial java file to test @Webservice annotation.  
 * It should be converted successfully into wsdl.
 * @author btiffany
 *
 */
@WebService(name="MyService", targetNamespace="myns", serviceName="valid1", 
			wsdlLocation="somewhere", endpointInterface="",
			portName="")
public class WebServiceDefaultsValid1 {
	@WebMethod
	public String echo (String parm){ return "the server says: "+parm; }

}
