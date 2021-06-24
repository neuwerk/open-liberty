package annotations.reqrespwrappers.server;
import javax.jws.*;
import javax.xml.ws.*;
@WebService()
public class Jaxws22ImplCheck {
    
    @RequestWrapper(partName="customizedRequestPart")
    public String requestOp( String in) throws Jaxws22ImplException { return in; }
    
    @ResponseWrapper(partName="customizedResponsePart")
    public String responseOp( String in) throws Jaxws22ImplException { return in; }
    
    

}
