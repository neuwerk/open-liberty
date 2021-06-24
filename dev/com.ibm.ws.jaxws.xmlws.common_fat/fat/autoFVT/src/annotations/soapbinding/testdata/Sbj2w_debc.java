
package annotations.soapbinding.testdata;

import javax.xml.ws.*;
import javax.jws.*;
import javax.jws.soap.SOAPBinding;

@WebService
// doc encoded bare on a class, should not produce wsdl, as enc. style is not supported.
@SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.ENCODED, 
		  parameterStyle=SOAPBinding.ParameterStyle.BARE)
public class Sbj2w_debc {



	public String echo(String parm){
		return ("you passed me a string: "+parm);
	}	


}
