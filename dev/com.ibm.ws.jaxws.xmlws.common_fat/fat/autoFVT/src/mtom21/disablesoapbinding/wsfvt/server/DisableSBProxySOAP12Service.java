//
// @(#) 1.1 autoFVT/src/mtom21/disablesoapbinding/wsfvt/server/DisableSBProxySOAP12Service.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/15/08 14:28:48 [8/8/12 06:40:47]
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

package mtom21.disablesoapbinding.wsfvt.server;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.soap.MTOM;
import javax.xml.ws.soap.SOAPBinding;

import org.apache.axiom.attachments.ByteArrayDataSource;
import org.test.mtom21.disablesb_proxysoap12.*;

@WebService(
		targetNamespace = "http://org/test/mtom21/DisableSB_proxySOAP12",
		serviceName = "DisableSBProxySOAP12Service",
		portName = "AttachmentServicePort",		
		wsdlLocation = "WEB-INF/wsdl/DisableSBProxySOAP12.wsdl",
		endpointInterface = "org.test.mtom21.disablesb_proxysoap12.ImageServiceInterface")
//@BindingType (SOAPBinding.SOAP12HTTP_MTOM_BINDING)
@BindingType (SOAPBinding.SOAP12HTTP_BINDING)
@MTOM(enabled=false)
     /**
     * Required impl method from JAXB interface
     * A JAXB implementation.  
     * - MTOM setting = False via @BindingType
     * - Using SOAP1.2
     * - Using PAYLOAD mode
     * - Sending back the same obj it received
     * - The JAXB objects are for multipart/* MIME type
     *
     * @param ImageDepot obj
     * @return ImageDepot obj
     */	

public class DisableSBProxySOAP12Service implements ImageServiceInterface {
	
	/* 
	 */
	public ImageDepot sendImage(ImageDepot input) {
        System.out.println("---------------------------------------");
		System.out.println("DisableSBProxySOAP12Service - sendImage method: Request received");
		return input;
	}

	
	public ImageDepot sendText(byte[] input) {
        System.out.println("---------------------------------------");
		System.out.println("DisableSBProxySOAP12Service - sendText method: Request received");

		DataSource ds = new ByteArrayDataSource(input);
		DataHandler dh = new DataHandler(ds);
		
		ImageDepot id = new ObjectFactory().createImageDepot();
		id.setImageData(dh);
		
		return id;
	}

}