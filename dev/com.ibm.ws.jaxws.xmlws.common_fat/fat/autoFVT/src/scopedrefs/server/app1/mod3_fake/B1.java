package scopedrefs.server.app1.mod3;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.naming.InitialContext;

// a dummy class so we can generate wsdl

@Stateless
@WebService()
public class B1 {
    public String identify(){ return "a1m3b1_fake_bean";}
    
    public String invokeapp1ModxEjb2(){ return "";}
    
    public String lookupAndInvokeAnyJndiRef(String jndiref){ return"";}

}
