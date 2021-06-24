package annotations.webservice.testdata;

import javax.jws.*;

/**
 * A trivial java file to test @Webservice annotation.  
 * It should be converted successfully into wsdl.
 * @author btiffany
 *
 */


@WebService(wsdlLocation="WEB-INF/wsdl/WebServiceBasicRuntimeService.wsdl")           
public class WebServiceBasicRuntime {
	@WebMethod
	public String echo (String parm){ return "the server says: "+parm; }

}
