package annotations.webservice.testdata;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * Missing required @WebService annotation. 
 * Should be caught by java2wsdl.  
 * @author btiffany
 *
 */
public class WebServiceDefaultsImproper1 {		
	@WebMethod()
	String helloWorld(@WebParam(name="pingstring", targetNamespace="space", 
			mode=WebParam.Mode.IN, header=false) String str){
		return str;
	}
	
}
