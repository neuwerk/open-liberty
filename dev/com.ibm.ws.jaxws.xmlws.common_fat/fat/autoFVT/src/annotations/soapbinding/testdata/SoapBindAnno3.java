package annotations.soapbinding.testdata.server;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


@WebService(name = "SoapBindAnno3", targetNamespace = "http://server.testdata.soapbinding.annotations/")
@SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL, 
		  parameterStyle=SOAPBinding.ParameterStyle.BARE)
public interface SoapBindAnno3 {

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
