package annotations.webservice_g2.inheritance.server;
import javax.jws.*;
import javax.jws.soap.SOAPBinding;
@WebService
/*
@SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL, 
        parameterStyle=SOAPBinding.ParameterStyle.BARE)
*/        
public class BottomImpl extends Impl2{
    
// should expose Impl1.impl1echo, Impl1.impl1echo2,
// and Impl2.impl2echo,
//    and override Impl1.impl1echo2 with IMpl2.impl1echo2.
// nothing named "surprise" should appear in the wsdl.     
    
    public String echoBottom( String s ){ return "echoBottom:"+s; }
    
    // inherited from impl1 but we exclude it here
    @WebMethod(exclude=true) public String impl1surpriseexclude(String s){ return s; }

}
