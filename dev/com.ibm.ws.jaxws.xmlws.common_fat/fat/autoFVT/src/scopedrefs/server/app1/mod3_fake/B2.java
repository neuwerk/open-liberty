package scopedrefs.server.app1.mod3;
import javax.ejb.Stateless;
import javax.jws.WebService;


// a fake class so we can generate wsdl 


@Stateless
@WebService
public class B2 {
    
    public String identify(){ return "a1m3b2_fake_bean";}
    
    public String invokeapp1ModxEjb1(){ return "";}


}
