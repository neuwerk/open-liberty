/* a class to test rpc-lit-wrap on an sei */
package annotations.soapbinding.testdata.serverrpc;
// need separate package so ObjectFactory doesn't get overwritten on client side.

import javax.jws.*;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.*;
import javax.xml.ws.Holder;

// used by SoapBindingRuntimeTestCase 
@SOAPBinding(style=SOAPBinding.Style.RPC, use=SOAPBinding.Use.LITERAL,
        parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)
        
// customize portType name so we can reuse SoapbindRpcImpl client 
@WebService(name="SoapBindRpcImpl" )
public interface SoapBindRpcIf {
    public String echo(String inp);
    
    // should throw exception 
    public String returnNull(String inp);
    
    // use a holder so we can check inout mode.
    public void  echoHolder (@WebParam(mode= WebParam.Mode.INOUT) Holder<Object> o);
    
}
