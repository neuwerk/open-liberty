package scopedrefs.server.app2.mod1;
import javax.ejb.Stateless;
import javax.jws.WebService;


// a fake class so we can generate wsdl 


@Stateless
@WebService
public class B2 {
    
    public String identify(){ return "a2m1b2_fake_bean";}
    
    public String invokeapp1ModxEjb1(){ return "";}


}
