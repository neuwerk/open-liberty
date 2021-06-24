package annotations.webmethod.testdata;
import javax.jws.*;
// used to check that @WebMethod is illegal in impl. class since contract is defined
// in interface
@WebService
public interface WebMethodOnIfIf {
	public String echo(String s);
	
}
