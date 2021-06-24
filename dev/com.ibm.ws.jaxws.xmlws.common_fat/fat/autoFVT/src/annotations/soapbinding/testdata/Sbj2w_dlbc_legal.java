package annotations.soapbinding.testdata;

import javax.xml.ws.*;
import javax.jws.*;
import javax.jws.soap.SOAPBinding;

@WebService
// doc lit bare on a class, should work
@SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL, 
		  parameterStyle=SOAPBinding.ParameterStyle.BARE)
public class Sbj2w_dlbc_legal {


	public String echo(String parm){
		return ("you passed me a string: "+parm);
	}	


}
