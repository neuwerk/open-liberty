
package annotations.soapbinding.testdata;

import javax.xml.ws.*;
import javax.jws.*;
import javax.jws.soap.SOAPBinding;

@WebService
	  
public class Sbj2w_dewm {

	//	 doc enc wrap on method, should not produce wsdl, as enc style not supported.

	@SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.ENCODED, 
			  parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)

	public String echo(String parm){
		return ("you passed me a string: "+parm);
	}	


}
