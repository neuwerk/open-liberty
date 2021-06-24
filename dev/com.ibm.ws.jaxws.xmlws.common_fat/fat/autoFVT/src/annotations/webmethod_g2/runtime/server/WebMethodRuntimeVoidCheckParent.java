package annotations.webmethod_g2.runtime.server;
import javax.jws.*;

// used to check @WebMethod(exclude) in WebMethodRuntimeVoidCheck
// I think having to put @WebService on superclasses is suspect, but that's
// what the current consensus is.
@WebService
public class WebMethodRuntimeVoidCheckParent {    
    @WebMethod
    public String echoInherited(String s){
        return("echoInherited replies: "+s);
    }
    
}
