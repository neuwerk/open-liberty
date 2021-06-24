//
// @(#) 1.3 autoFVT/src/mtom/security/wsfvt/server/SecurityMtomOnProxySOAP12Service.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 1/31/07 16:57:58 [8/8/12 06:58:18]
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
// 10/30/06 jtnguyen    LIDB3402-07.04  New File
// 01/22/07 jtnguyen    416083          Changed package name to lower case
// 01/31/07 jtnguyen    417109          Removed @WebServiceMode annotation


package mtom.security.wsfvt.server;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.jws.WebService;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import org.apache.axiom.attachments.ByteArrayDataSource;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.BindingType;

import org.test.mtom.securitymtomonproxysoap12.*;

@WebService(
		targetNamespace = "http://org/test/mtom/SecurityMtomOnProxySOAP12",
		serviceName = "SecurityMtomOnProxySOAP12Service",
		portName = "AttachmentServicePort",		
		wsdlLocation = "WEB-INF/wsdl/SecurityMtomOnProxySOAP12.wsdl",
		endpointInterface = "org.test.mtom.securitymtomonproxysoap12.ImageServiceInterface")

//@BindingType (SOAPBinding.SOAP12HTTP_BINDING)
@BindingType (SOAPBinding.SOAP12HTTP_MTOM_BINDING)

     /**
     * Required impl method from JAXB interface
     * A JAXB implementation.  
     * - Using MTOM setting via @BindingType
     * - Using PAYLOAD mode
     * - Sending back the same obj it received
     * - The JAXB objects are for multipart/* MIME type
     *
     * @param ImageDepot obj
     * @return ImageDepot obj
     */	

public class SecurityMtomOnProxySOAP12Service implements ImageServiceInterface {
	
	/* 
	 */
	public ImageDepot sendImage(ImageDepot input) {
        System.out.println("---------------------------------------");
		System.out.println("SecurityMtomOnProxySOAP12Service - Request received.");
		return input;
	}

	
	public ImageDepot sendText(byte[] input) {
        System.out.println("---------------------------------------");
		System.out.println("SecurityMtomOnProxySOAP12Service - sendText method: Request received");

		DataSource ds = new ByteArrayDataSource(input);
		DataHandler dh = new DataHandler(ds);
		
		ImageDepot id = new ObjectFactory().createImageDepot();
		id.setImageData(dh);
		
		return id;
	}

}