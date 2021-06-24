package scopedrefs.server.app2.mod1;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.naming.InitialContext;

// a dummy class so we can generate wsdl

@Stateless
@WebService()
public class B1 {
    public String identify(){ return "a2m1b1_fake_bean";}
    
    
    public String lookupAndInvokeAnyJndiRef(String jndiref){ return "";}
    public void setStaticApp2HttpPort(String port){}

}
