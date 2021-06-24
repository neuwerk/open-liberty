package annotations.webmethod.testdata;
import javax.jws.*;
// the @WebMethod should be illegal since we are using an interface
@WebService(endpointInterface="annotations.webmethod.testdata.WebMethodOnIfIf")
public class WebMethodOnIfImpl {
	@WebMethod
	public String echo(String s){return s;}
	
}
