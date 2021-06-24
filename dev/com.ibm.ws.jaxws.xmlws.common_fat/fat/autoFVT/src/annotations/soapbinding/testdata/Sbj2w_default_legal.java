package annotations.soapbinding.testdata;

import javax.xml.ws.*;
import javax.jws.*;
import javax.jws.soap.SOAPBinding;

@WebService
// default annos on class and method, should work.
@SOAPBinding
public class Sbj2w_default_legal {

	@SOAPBinding
	public String echo(String parm){
		return ("you passed me a string: "+parm);
	}	


}
