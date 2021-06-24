/**
 * test inheritance.  Since this one has @WebService, all it's methods should be 
 * inherited by BottomImpl, which we will call for testing. 
 */
package annotations.webservice_g2.inheritance.server;
import javax.jws.*;
import javax.jws.soap.SOAPBinding;
@WebService
/*
@SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL, 
        parameterStyle=SOAPBinding.ParameterStyle.BARE)
*/        
public class Impl1 {
    public String impl1echo(String s) {return "impl1echo:"+s;}
    public String impl1echo2(String s) {return "impl1echo2:"+s;}
    
    // should not be in wsdl, because not public.
    String impl1surprise(String s){ return s; }
    
    // should not be in wsdl , because overridden and exlcuded in bottomimpl
    public String impl1surpriseexclude(String s){ return s; }
    
    // should not be in wsdl, because overridden and excluded in impl2    
    public String impl1surpriseexclude2(String s){ return s; }

}
