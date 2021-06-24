/*
 * This file is not used in any tests.  If needed, it can be used to generate
 * or regenerate something equivalent to w2jwrappercheck.wsdl
 */

package annotations.reqrespwrappers.server;

import javax.xml.ws.*;
import javax.jws.*;
import javax.jws.soap.SOAPBinding;

/* in sun, doclitbare style just ignores the second method.
 * however it throws exception in doclitwrapped style. 
 */


@WebService
// this is the default soapbinding, just restated here in case someone wants to 
// experiment with other bindings. 
@SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL, 
			  parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)
/**
 * test class to 
 * verify that we can pass all these parameters out to doclitwrapped
 * wsdl properly.  This class should generate something very close to
 * W2JWrapperCheck.wsdl
 */			  
public class _W2JWrapperCheck {
	public String echo1(String parm){
		return ("you passed me a string: "+parm);
	}
	
	/* adding this would allow echo1 name to be overloaded, 
	 * wsdl to be generated, and maybe even service to work:
	 * @WebMethod(operationName="echoObject")
	 * We're also supposed to be able to overload by specifying classnames,
	 * but that does not work.  However it's not required by the standard. 
	 */
	@RequestWrapper(localName="notarg0",
					targetNamespace="annotations.reqrespwrappers.server2req",
					className="echo2input")
	@ResponseWrapper(localName="notresponse",
					targetNamespace="annotations.reqrespwrappers.server2resp",
					className="echo2output")
	public String echo2(Object parm){
		return ("you passed me an object:"+parm.toString());
	}
	

}
