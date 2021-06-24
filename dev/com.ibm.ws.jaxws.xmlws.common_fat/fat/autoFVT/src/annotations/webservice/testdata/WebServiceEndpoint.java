package annotations.webservice.testdata;
import javax.jws.*;

/**
 * tests that when interface and class are used together, j2w correctly puts it 
 * together to gen the wsdl.  ref jsr181 3.1
 * @author btiffany
 *
 */
@WebService(name="WebServiceEndpoint")
public interface WebServiceEndpoint {
	@WebMethod()
	public String echo(String inp);	
}
