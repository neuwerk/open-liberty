/**
 * methods with @WebM.. should be inhertied by BottomImpl, which we will call for testing. 
 */

package annotations.webservice_g2.inheritance.server;
import javax.jws.*;
import javax.jws.soap.SOAPBinding;
// without the webservice annotation, wsgen fails - 400720
@WebService
/*
@SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL, 
        parameterStyle=SOAPBinding.ParameterStyle.BARE)
*/        
public class Impl2 extends Impl1{
    @WebMethod public String impl2echo (String s) { return "impl2echo:"+s; }
    
    // should override impl1
    @WebMethod public String impl1echo2(String s) {return "Impl2.impl1echo2:"+s;}
    
    // overriding impl1, should not be in wsdl
    @WebMethod(exclude=true) public String impl1surpriseexclude2(String s){ return s;  }

}
