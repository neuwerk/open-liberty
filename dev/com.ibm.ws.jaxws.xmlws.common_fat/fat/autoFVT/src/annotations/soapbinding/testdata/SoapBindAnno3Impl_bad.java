package annotations.soapbinding.testdata.server2;
import javax.jws.*;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.*;
import annotations.soapbinding.testdata.server.*;
/**
 * a modified class with soapbinding anno modified. 
 * @author btiffany
 *
 */
// binding types are reversed on this client.  Shouldn't work, but it does,
// and correctly too.
// probably because the bindings are being read off the interface. so, don't us it.

//@WebService(endpointInterface="annotations.soapbinding.testdata.SoapBindAnno3")

@WebService
@SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL, 
		  parameterStyle=SOAPBinding.ParameterStyle.BARE)
public class SoapBindAnno3Impl {
	
	@WebResult 
    public String echodlw(String inp){
        return("echodlw replies: "+inp);
    }
    
 
	@SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL, 
			  parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)
	@WebResult 
    public String echodlb(String inp){
        return("echodlb replies: "+inp);
    }
}
