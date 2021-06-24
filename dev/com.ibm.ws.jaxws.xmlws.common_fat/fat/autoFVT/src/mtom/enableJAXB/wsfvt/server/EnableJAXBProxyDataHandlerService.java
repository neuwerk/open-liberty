//
// @(#) 1.4 autoFVT/src/mtom/enableJAXB/wsfvt/server/EnableJAXBProxyDataHandlerService.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 2/5/07 11:02:16 [8/8/12 06:58:10]
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
// 02/05/07 jtnguyen    418814          Changed output msg from SB to JAXB


package mtom.enableJAXB.wsfvt.server;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.jws.WebService;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import org.apache.axiom.attachments.ByteArrayDataSource;

import org.test.mtom.enablejaxbproxy_datahandler.*;

@WebService(
		targetNamespace = "http://org/test/mtom/EnableJAXBProxy_datahandler",
		serviceName = "EnableJAXBProxyDataHandlerService",
		portName = "AttachmentServicePort",		
		wsdlLocation = "WEB-INF/wsdl/EnableJAXBProxyDataHandler.wsdl",
		endpointInterface = "org.test.mtom.enablejaxbproxy_datahandler.ImageServiceInterface")

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

public class EnableJAXBProxyDataHandlerService implements ImageServiceInterface {
	
	/* 
	 */
	public ImageDepot sendImage(ImageDepot input) {
        System.out.println("---------------------------------------");
		System.out.println("EnableJAXBProxyService - Request received.");
		return input;
	}

	
	public ImageDepot sendText(byte[] input) {
        System.out.println("---------------------------------------");
		System.out.println("EnableJAXBProxyService - sendText method: Request received");

		DataSource ds = new ByteArrayDataSource(input);
		DataHandler dh = new DataHandler(ds);
		
		ImageDepot id = new ObjectFactory().createImageDepot();
		id.setImageData(dh);
		
		return id;
	}

}