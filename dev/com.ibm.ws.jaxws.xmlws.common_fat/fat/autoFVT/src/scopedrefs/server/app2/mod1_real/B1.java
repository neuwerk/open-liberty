package scopedrefs.server.app2.mod1;
import javax.ejb.Stateless;
import javax.jws.WebService;

@Stateless
@WebService()
public class B1 extends scopedrefs.server.app2.common.HelperMethods {
    public String identify(){ return "a2m1b1";}    
    
    public String lookupAndInvokeAnyJndiRef(String jndiref){
        return super.lookupAndInvokeAnyJndiRef(jndiref);
    }
    
    // statically set http port for app2 endpoints.   
    public void setStaticApp2HttpPort(String port){
       app2HttpPort = port;
    }
    
    
}
