package annotations.soapbinding.testdata.serverimpl;
// need separate package so ObjectFactory doesn't get overwritten on client side.

import javax.jws.*;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.*;

// used by SoapBindingRuntimeTestCase
// customize portType name so we can reuse client 
@WebService(name="SoapBindRtImpl")
@SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL,
            parameterStyle=SOAPBinding.ParameterStyle.BARE)    
public interface SoapBindRtIf { 
 
    @SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL,
            parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)
    public String echodlw(String inp);
    
    public String echodlb(String inp);
}
