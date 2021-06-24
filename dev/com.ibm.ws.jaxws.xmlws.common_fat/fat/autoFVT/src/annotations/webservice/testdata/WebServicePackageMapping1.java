
 
// no package statement on purpose
import javax.jws.*;

@WebService(targetNamespace="foo.bah")
public class WebServicePackageMapping1 {
    @WebMethod
	public String sayHello(){ return "Hello";}

}
