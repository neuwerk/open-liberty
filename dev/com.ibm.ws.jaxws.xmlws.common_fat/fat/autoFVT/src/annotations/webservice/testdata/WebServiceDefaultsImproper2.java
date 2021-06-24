package annotations.webservice.testdata;


import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * Illegal use of annotations that should be caught by java2wsdl.
 * Webservice contains an endpoint interface with various illegals.
 * class is final.
 * method is not public.
 *  @author btiffany
 *
 */
@WebService(name="improper2", targetNamespace="fvt", wsdlLocation="somewhere",
		endpointInterface="something")
public  final class WebServiceDefaultsImproper2 {		
	@WebMethod()
	String helloWorld(@WebParam(name="pingstring", targetNamespace="space", 
			mode=WebParam.Mode.IN, header=false) String str){
		return str;
	}
	
}
