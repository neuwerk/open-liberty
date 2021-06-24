package annotations.webservice_g2.inheritance.server;

import javax.jws.*;
import javax.jws.soap.SOAPBinding;

@WebService
/*
@SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL, 
       parameterStyle=SOAPBinding.ParameterStyle.BARE)
*/
        
public interface If2 extends If1{   
    String impl2echo (String s);
    String echoBottom (String s);
    // it makes no sense to exclude something in an interface - 
    // the definition of an interface is you have to imp ALL of it.
    //@WebMethod(exclude=true) public String impl1surpriseexclude(String s);

}
