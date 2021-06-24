package annotations.webservice.testdata;


import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * Illegal use of annotations that should be caught by java2wsdl.
 * Webservice contains a missing endpoint interface.
 *  @author btiffany
 *
 */
@WebService(endpointInterface="something")
public  class WebServiceDefaultsImproper3 {		
	@WebMethod()
	String helloWorld(@WebParam(name="pingstring", targetNamespace="space", 
			mode=WebParam.Mode.IN, header=false) String str){
		return str;
	}
	
}
