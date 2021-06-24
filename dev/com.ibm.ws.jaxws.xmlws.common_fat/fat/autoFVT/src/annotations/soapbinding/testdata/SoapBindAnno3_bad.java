package annotations.soapbinding.testdata.server;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

// change bare to wrapped style, no other changes, should fail to deploy/invoke.

@WebService(name = "SoapBindAnno3", targetNamespace = "http://server.testdata.soapbinding.annotations/")
@SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL, 
		  parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)
public interface SoapBindAnno3 {
    
    // put this here so we can tell the class files apart if we have to.
    public static String description="Wrapped interface annotation";
    
    /**
     * 
     * @param arg0
     * @return
     *     returns java.lang.String
     */
	@WebResult(name="echodlwReturn") 
    public String echodlw(String arg0);

    /**
     * 
     * @param echodlb
     * @return
     *     returns java.lang.String
     */
	@WebResult(name="echodlbReturn")
    public String echodlb(String echodlb);

}
