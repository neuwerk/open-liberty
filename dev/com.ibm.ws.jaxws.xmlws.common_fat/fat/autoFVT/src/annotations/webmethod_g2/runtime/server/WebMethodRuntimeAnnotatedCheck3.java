package annotations.webmethod_g2.runtime.server;
import javax.jws.*;

// method made non-public should not be exposed
@WebService(wsdlLocation="WEB-INF/wsdl/WebMethodRuntimeAnnotatedCheckService.wsdl")
public class WebMethodRuntimeAnnotatedCheck {

	public String noAnnoMethod(String s){return s;}	
	
	public String annoMethod(String s){return s;}
	
	String annoMethod2(String s){return s;}
}
