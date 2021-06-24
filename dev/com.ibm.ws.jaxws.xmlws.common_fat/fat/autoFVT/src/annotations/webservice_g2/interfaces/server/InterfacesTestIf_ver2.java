package annotations.webservice_g2.interfaces.server;
import javax.jws.*;


@WebService(name="InterfaceTest",
			targetNamespace="iftest",
			wsdlLocation="WEB-INF/wsdl/if.wsdl"
			)

//a copy of InterfacesTestif with exclude set to true on a method
public interface InterfacesTestIf {	
	
	public String bareIfMethod(String s);
	
	@WebMethod()
	public String annoIfMethod(String s);
	
	// per jsr181 3.2, this method should not map to wsdl. 
	@WebMethod(exclude=true)
	public String annoIfExcluded(String s);	
	

}
