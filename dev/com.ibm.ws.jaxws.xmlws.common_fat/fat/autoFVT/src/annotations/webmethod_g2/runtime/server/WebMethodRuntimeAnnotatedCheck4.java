package annotations.webmethod_g2.runtime.server;
import javax.jws.*;

// test redirection through annotation and exclusion through annotation
@WebService(wsdlLocation="WEB-INF/wsdl/WebMethodRuntimeAnnotatedCheckService.wsdl")
public class WebMethodRuntimeAnnotatedCheck {
    
    // so we can examine the .class binary and make sure we have right one. 
    private String Marker = "Case Two: WebMethodRuntimeAnnotatedCheck2";

	// these two methods should swap...
	@WebMethod(operationName="annoMethod")
	public String noAnnoMethod(String s){return "noAnnoMethod returns: "+ s;}	
	
	@WebMethod(operationName="noAnnoMethod")
	public String annoMethod(String s){return "annoMethod returns: "+ s;}
	

	@WebMethod(exclude = true)
	public String annoMethod2(String s){return "excluded annoMethod2 returns: " + s;}
}
