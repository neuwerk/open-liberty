package scopedrefs.server.app2.mod1;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.naming.InitialContext;
import javax.xml.ws.WebServiceRef;
@Stateless
@WebService
public class B2 {
    
    public String identify(){ return "a2m1b2";}

}
