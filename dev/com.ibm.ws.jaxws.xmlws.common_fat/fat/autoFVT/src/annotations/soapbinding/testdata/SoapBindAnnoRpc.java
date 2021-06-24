
package annotations.soapbinding.testdata;

import javax.xml.ws.*;
import javax.jws.*;
import javax.jws.soap.SOAPBinding;

/**
 * A class to test that RPC literal mode works.
 */
@WebService

@SOAPBinding(style=SOAPBinding.Style.RPC, use=SOAPBinding.Use.LITERAL, 
		  parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)
public class SoapBindAnnoRpc {
	
	// should be rpc lit wrap
	public String echorlw(String parm){
		return ("echorlw replies: "+parm);
	}
}
