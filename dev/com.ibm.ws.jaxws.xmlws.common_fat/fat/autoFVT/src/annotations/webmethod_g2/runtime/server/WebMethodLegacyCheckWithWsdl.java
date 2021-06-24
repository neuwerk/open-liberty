package annotations.webmethod_g2.runtime.server;
import javax.jws.*;

// an app packaged with wsdl, we want to be sure
// echonoanno is not invokable even though it is not in wsdl

@WebService(serviceName="WebMethodLegacyCheckService",
            wsdlLocation="WEB-INF/wsdl/WebMethodLegacyCheckServiceOneOp.wsdl",
            portName="WebMethodLegacyCheckPort"
            )
public class WebMethodLegacyCheckWithWsdl {
    
    @WebMethod()
    public String echoanno(String s){return s;}
    
    // under old jax-ws (default) behavior, legacywm=true, this unannotated method would not be exposed,
    // but under the new rules, it is exposed.
    //@WebMethod()
    public String echonoanno(String s){return s;}
    
    
    

}
