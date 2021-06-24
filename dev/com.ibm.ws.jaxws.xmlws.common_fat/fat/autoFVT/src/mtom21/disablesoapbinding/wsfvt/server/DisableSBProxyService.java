//
// @(#) 1.1 autoFVT/src/mtom21/disablesoapbinding/wsfvt/server/DisableSBProxyService.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/15/08 14:28:46 [8/8/12 06:40:47]
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
import javax.xml.ws.soap.SOAPBinding;

import org.apache.axiom.attachments.ByteArrayDataSource;
import org.test.mtom21.disablesb_proxy.*;


@WebService(
		targetNamespace = "http://org/test/mtom21/DisableSB_proxy",
		serviceName = "DisableSBProxyService",
		portName = "AttachmentServicePort",		
		wsdlLocation = "WEB-INF/wsdl/DisableSBProxy.wsdl",
		endpointInterface = "org.test.mtom21.disablesb_proxy.ImageServiceInterface")

@BindingType (SOAPBinding.SOAP11HTTP_BINDING)
//@BindingType (SOAPBinding.SOAP11HTTP_MTOM_BINDING)

     /**
     * Required impl method from JAXB interface
     * A JAXB implementation.  
     * - MTOM setting = disabled via @BindingType
     * - Using PAYLOAD mode
     * - Sending back the same obj it received
     * - The JAXB objects are for multipart/* MIME type
     *
     * @param ImageDepot obj
     * @return ImageDepot obj
     */	

public class DisableSBProxyService implements ImageServiceInterface {
	
	/* 
	 */
	public ImageDepot sendImage(ImageDepot input) {
        System.out.println("---------------------------------------");
		System.out.println("DisableSBProxyService - sendImage method: Request received");
		return input;
	}

	
	public ImageDepot sendText(byte[] input) {
        System.out.println("---------------------------------------");
		System.out.println("DisableSBProxyService - sendText method: Request received");

		DataSource ds = new ByteArrayDataSource(input);
		DataHandler dh = new DataHandler(ds);
		
		ImageDepot id = new ObjectFactory().createImageDepot();
		id.setImageData(dh);
		
		return id;
	}

}