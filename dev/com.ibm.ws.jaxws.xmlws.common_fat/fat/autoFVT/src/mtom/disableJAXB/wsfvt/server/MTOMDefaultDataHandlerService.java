//
// @(#) 1.3 autoFVT/src/mtom/disableJAXB/wsfvt/server/MTOMDefaultDataHandlerService.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 1/31/07 16:56:48 [8/8/12 06:58:04]
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


package mtom.disableJAXB.wsfvt.server;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.jws.WebService;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import org.apache.axiom.attachments.ByteArrayDataSource;

import org.test.mtom.disablejaxbproxy_datahandler.*;

@WebService(
		targetNamespace = "http://org/test/mtom/DisableJAXBProxy_datahandler",
		serviceName = "MTOMDefaultDataHandlerService",
		portName = "AttachmentServicePort",		
		wsdlLocation = "WEB-INF/wsdl/MTOMDefaultProxyDataHandler.wsdl",
		endpointInterface = "org.test.mtom.disablejaxbproxy_datahandler.ImageServiceInterface")

//@BindingType (SOAPBinding.SOAP11HTTP_BINDING)
//@BindingType (SOAPBinding.SOAP11HTTP_MTOM_BINDING)

     /**
     * Required impl method from JAXB interface
     * A JAXB implementation.  
     * - Using default setting for MTOM, i.g. no MTOM setting via @BindingType
     * - Using PAYLOAD mode
     * - Sending back the same obj it received
     * - The JAXB objects are for multipart/* MIME type
     *
     * @param ImageDepot obj
     * @return ImageDepot obj
     */	

public class MTOMDefaultDataHandlerService implements ImageServiceInterface {
	
	/* 
	 */
	public ImageDepot sendImage(ImageDepot input) {
        System.out.println("---------------------------------------");
		System.out.println("MTOMDefaultDataHandlerService - Request received.");
		return input;
	}

	
	public ImageDepot sendText(byte[] input) {
        System.out.println("---------------------------------------");
		System.out.println("MTOMDefaultDataHandlerService - sendText method: Request received");

		DataSource ds = new ByteArrayDataSource(input);
		DataHandler dh = new DataHandler(ds);
		
		ImageDepot id = new ObjectFactory().createImageDepot();
		id.setImageData(dh);
		
		return id;
	}

}