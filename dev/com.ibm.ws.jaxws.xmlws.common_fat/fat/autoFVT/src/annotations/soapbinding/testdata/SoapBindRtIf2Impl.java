package annotations.soapbinding.testdata.serverimpl;
// need separate package so ObjectFactory doesn't get overwritten on client side.

import javax.jws.*;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.*;

// used by SoapBindingRuntimeTestCase 
//customize names so we can reuse rtimpl client 
// checks class anno on Ifc, method anno on impl
@WebService(endpointInterface="annotations.soapbinding.testdata.serverimpl.SoapBindRtIf2",
        serviceName="SoapBindRtImplService",
        portName="SoapBindRtImplPort")
public class SoapBindRtIf2Impl {
    
    @SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL,
            parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)
    public String echodlw(String inp){
        return("echodlw replies: "+inp);
    }


    public String echodlb(String inp){
        return("echodlb replies: "+inp);
    }
}
