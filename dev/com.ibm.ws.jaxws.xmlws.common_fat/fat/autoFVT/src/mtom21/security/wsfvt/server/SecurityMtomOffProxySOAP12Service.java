//
// @(#) 1.2 autoFVT/src/mtom21/security/wsfvt/server/SecurityMtomOffProxySOAP12Service.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 5/21/08 09:54:39 [8/8/12 06:41:00]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006, 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// ----------------------------------------------------------------------------
// 03/10/08 jramos      LIDB4418-12.01  New File
// 05/21/08 jtnguyen    522871          Removed javax.persistence.ManyToMany


package mtom21.security.wsfvt.server;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.jws.WebService;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import org.apache.axiom.attachments.ByteArrayDataSource;

import javax.xml.ws.soap.MTOM;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.BindingType;

import org.test.mtom21.securitymtomoffproxysoap12.*;

@WebService(
		targetNamespace = "http://org/test/mtom21/SecurityMtomOffProxySOAP12",
		serviceName = "SecurityMtomOffProxySOAP12Service",
		portName = "AttachmentServicePort",		
		wsdlLocation = "WEB-INF/wsdl/SecurityMtomOffProxySOAP12.wsdl",
		endpointInterface = "org.test.mtom21.securitymtomoffproxysoap12.ImageServiceInterface")

@BindingType (SOAPBinding.SOAP12HTTP_BINDING)
//@BindingType (SOAPBinding.SOAP12HTTP_MTOM_BINDING)
@MTOM(enabled=false)

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

public class SecurityMtomOffProxySOAP12Service implements ImageServiceInterface {
	
	/* 
	 */
	public ImageDepot sendImage(ImageDepot input) {
        System.out.println("---------------------------------------");
		System.out.println("SecurityMtomOffProxySOAP12Service - Request received.");
		return input;
	}

	
	public ImageDepot sendText(byte[] input) {
        System.out.println("---------------------------------------");
		System.out.println("SecurityMtomOffProxySOAP12Service - sendText method: Request received");

		DataSource ds = new ByteArrayDataSource(input);
		DataHandler dh = new DataHandler(ds);
		
		ImageDepot id = new ObjectFactory().createImageDepot();
		id.setImageData(dh);
		
		return id;
	}

}