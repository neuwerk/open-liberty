package annotations.webservice.testdata;

import javax.jws.*;

/**
 * A trivial java file to test @Webservice annotation.
 * 
 * WsdlLoc mismatches war file, should not deploy
 * 
 * @author btiffany
 *
 */

@WebService(name="MyService", targetNamespace="wsfvt.anno", serviceName="valid1", 
            wsdlLocation="WEB-INF/wsdl/elsewhere.wsdl")            
public class WebServiceRuntime {
	@WebMethod
	public String echo (String parm){ return "the server says: "+parm; }

}
