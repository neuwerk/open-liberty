
 
// no package statement on purpose, and no namespace attribute.
// wsgen should reject this file
import javax.jws.*;

@WebService()
public class WebServicePackageMapping2 {
	public String sayHello(){ return "Hello";}

}
