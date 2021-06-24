package annotations.webservice.testdata;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * Missing required public constructor 
 * Should be caught by java2wsdl.  
 * @author btiffany
 *
 */
@WebService()
public class WebServiceDefaultsImproper_NoPublicConstructor {	
	private WebServiceDefaultsImproper_NoPublicConstructor(){
		super();
	}
	
	@WebMethod()
	String helloWorld(@WebParam(name="pingstring", targetNamespace="space", 
			mode=WebParam.Mode.IN, header=false) String str){
		return str;
	}
	
}
