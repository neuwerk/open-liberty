package annotations.webservice.testdata;

import javax.jws.*;

/**
 * A trivial java file to test @Webservice annotation.  
 * It should be converted successfully into wsdl.
 * @author btiffany
 *
 */
@WebService(name="othername", targetNamespace="wsfvt.anno", serviceName="valid1", 
			wsdlLocation="WEB-INF/wsdl/Valid1.wsdl", endpointInterface="",
			portName="")
public class WebServiceRuntime {
	@WebMethod
	public String echo (String parm){ return "the server says: "+parm; }

}
