package annotations.soapbinding.testdata.serverimpl;
// need separate package so ObjectFactory doesn't get overwritten on client side.

import javax.jws.*;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.*;

// used by SoapBindingRuntimeTestCase 
@WebService()
@SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL,
            parameterStyle=SOAPBinding.ParameterStyle.BARE)    
public class SoapBindRtImpl {
    
    @SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL,
            parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)
    @WebMethod
    public String echodlw(String inp){
        return("echodlw replies: "+inp);
    }
 
    @WebMethod
    public String echodlb(String inp){
        return("echodlb replies: "+inp);
    }
}
