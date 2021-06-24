package annotations.webservice.testdata; 
// namespace customization should work.
import javax.jws.*;

@WebService(targetNamespace="customized")
public class WebServicePackageMapping4 {
	public String sayHello(){ return "Hello";}

}
