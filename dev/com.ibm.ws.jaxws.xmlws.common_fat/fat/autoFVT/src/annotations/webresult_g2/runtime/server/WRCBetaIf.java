package annotations.webresult_g2.runtime.server;

import javax.jws.*;
import javax.jws.soap.SOAPBinding;

// used to check that a method with fully specified webresult anno can be invoked
// for beta, we have to use an SEI.  Will try to keep the same client and wsdl. 
@WebService(targetNamespace="http://server.runtime.webresult_g2.annotations/")
            
public interface WRCBetaIf{
    public String echo1(String s);

    // we can't set header param, not supported in beta.
    // force to bare style to work around wsgen prob 402362 to test header
    // looks like runtime can''t cope with bare on method yet.
    @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)   
    @WebResult(name="xecho2",
               partName="xecho2pn",
               targetNamespace="annotations.webresult_g2.runtime.newns"
               )               
    public String echo2(String s);
    
    
    public void void1(String  s);
    
    // this should be illegal, we shall see.
    @WebResult(name="xvoid2",
            partName="xvoid2pn",
            targetNamespace="annotations.webresult_g2.runtime.newns"
            )
    public void void2(String s);
            

    
}
