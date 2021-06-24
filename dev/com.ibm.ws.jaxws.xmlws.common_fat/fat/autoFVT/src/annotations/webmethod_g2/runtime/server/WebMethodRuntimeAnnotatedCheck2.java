package annotations.webmethod_g2.runtime.server;
import javax.jws.*;

// annotated, other public methods should be hidden from view
@WebService(wsdlLocation="WEB-INF/wsdl/WebMethodRuntimeAnnotatedCheckService.wsdl")
public class WebMethodRuntimeAnnotatedCheck {

	public String noAnnoMethod(String s){return s;}
	
	@WebMethod()
	public String annoMethod(String s){return s;}
	
	public String annoMethod2(String s){return s;}
}
