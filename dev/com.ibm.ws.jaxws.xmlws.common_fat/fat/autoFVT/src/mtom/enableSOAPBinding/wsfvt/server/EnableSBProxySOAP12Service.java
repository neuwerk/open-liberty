//
// @(#) 1.3 autoFVT/src/mtom/enableSOAPBinding/wsfvt/server/EnableSBProxySOAP12Service.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 1/31/07 16:57:28 [8/8/12 06:58:16]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// ----------------------------------------------------------------------------
// 09/25/06 jtnguyen    LIDB3402-07.01  New File
// 01/22/07 jtnguyen    416083          Changed package name to lower case
// 01/31/07 jtnguyen    417109          Removed @WebServiceMode annotation


package mtom.enableSOAPBinding.wsfvt.server;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.soap.SOAPBinding;

import org.apache.axiom.attachments.ByteArrayDataSource;
import org.test.mtom.enablesb_proxysoap12.*;

@WebService(
		targetNamespace = "http://org/test/mtom/EnableSB_proxySOAP12",
		serviceName = "EnableSBProxySOAP12Service",
		portName = "AttachmentServicePort",		
		wsdlLocation = "WEB-INF/wsdl/EnableSBProxySOAP12.wsdl",
		endpointInterface = "org.test.mtom.enablesb_proxysoap12.EnableSBProxySOAP12Interface")
@BindingType (SOAPBinding.SOAP12HTTP_MTOM_BINDING)
//@BindingType (SOAPBinding.SOAP12HTTP_BINDING)

     /**
     * Required impl method from JAXB interface
     * A JAXB implementation.  
     * - MTOM setting = True via @BindingType
     * - Using SOAP1.2
     * - Using PAYLOAD mode
     * - Sending back the same obj it received
     * - The JAXB objects are for multipart/* MIME type
     *
     * @param ImageDepot obj
     * @return ImageDepot obj
     */	

public class EnableSBProxySOAP12Service implements EnableSBProxySOAP12Interface {
	
	/* 
	 */
	public ImageDepot sendImage(ImageDepot input) {
        System.out.println("---------------------------------------");
		System.out.println("EnableSBProxySOAP12Service - sendImage method: Request received");
		return input;
	}

	
	public ImageDepot sendText(byte[] input) {
        System.out.println("---------------------------------------");
		System.out.println("EnableSBProxySOAP12Service - sendText method: Request received");

		DataSource ds = new ByteArrayDataSource(input);
		DataHandler dh = new DataHandler(ds);
		
		ImageDepot id = new ObjectFactory().createImageDepot();
		id.setImageData(dh);
		
		return id;
	}

}