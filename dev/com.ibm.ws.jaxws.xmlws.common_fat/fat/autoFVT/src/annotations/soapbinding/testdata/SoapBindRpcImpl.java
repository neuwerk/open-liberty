package annotations.soapbinding.testdata.serverrpc;
// need separate package so ObjectFactory doesn't get overwritten on client side.

import javax.jws.*;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.*;
import javax.xml.ws.Holder;

// used by SoapBindingRuntimeTestCase 
@SOAPBinding(style=SOAPBinding.Style.RPC, use=SOAPBinding.Use.LITERAL,
        parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)
@WebService()
public class SoapBindRpcImpl {
    
    
    @WebMethod 
    public String echo(String inp){
        return("echoreplies: "+inp);
    }
    
    // this should throw a webservice exception when we call it, as it is illegal
    // to return nulls jsr224 3.6.2.3
    @WebMethod
    public String returnNull(String inp){
        return null;        
    }
    
    // use a holder so we can check inout mode.
    @WebMethod
    public void  echoHolder (@WebParam(mode= WebParam.Mode.INOUT) Holder<Object> o){
        o.value = new String("Holder updated:" +o.value) ;        
    }
}
