package annotations.webresult_g2.runtime.server;

import javax.jws.*;

// used to check that a method with fully specified webresult anno can be invoked
// for beta, we have to use an SEI.  Because the gm test used header attribute,
// we have to change wsdl's, and probably clients.
   
@WebService( wsdlLocation="WEB-INF/wsdl/WRCBetaImplService.wsdl",       
            targetNamespace="http://server.runtime.webresult_g2.annotations/",
            endpointInterface="annotations.webresult_g2.runtime.server.WRCBetaIf")
public class WRCBetaImpl{
   public String echo1(String s) {
       System.out.println("WRCBetaImpl.echo1 invoked with args: "+s);
       return( "echo1 call returns: "+s);       
    }

   public String echo2(String s){
       System.out.println("WRCBetaImpl.echo2 invoked with args: "+s);
       return( "echo2 call returns: "+s);              
    }
   
   
   public void void1(String s){
       System.out.println("WRCBetaImpl.void1 invoked with args: "+s);
   }
   
   public void void2(String s){
       System.out.println("WRCBetaImpl.void2 invoked with args: "+s);
   }
   
}
