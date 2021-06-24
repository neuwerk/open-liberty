/* a class to test rpc-lit-wrap on an sei */
package annotations.soapbinding.testdata.serverrpc;
// need separate package so ObjectFactory doesn't get overwritten on client side.

import javax.jws.*;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.*;
import javax.xml.ws.Holder;

//change servicename so we can use same client as serverRpcImpl  
@WebService(endpointInterface="annotations.soapbinding.testdata.serverrpc.SoapBindRpcIf",
        serviceName="SoapBindRpcImplService",
        portName="SoapBindRpcImplPort")
public class SoapBindRpcIfImpl {
    
    public String echo(String inp){
        return("echoreplies: "+inp);
    }
    
    // this should throw a webservice exception when we call it, as it is illegal
    // to return nulls jsr224 3.6.2.3 
    public String returnNull(String inp){
        return null;        
    }
    
    // use a holder so we can check inout mode.
    public void  echoHolder ( Holder<Object> o){
        o.value = new String("Holder updated:" +o.value) ;        
    }
}
