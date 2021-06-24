
package annotations.soapbinding.testdata;

import javax.xml.ws.*;
import javax.jws.*;
import javax.jws.soap.SOAPBinding;

@WebService
//doc encoded bare on a method, should not produce wsdl, as enc. style is not supported.			  
public class Sbj2w_debm {


	@SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.ENCODED, 
			  parameterStyle=SOAPBinding.ParameterStyle.BARE)
	public String echo(String parm){
		return ("you passed me a string: "+parm);
	}	


}
