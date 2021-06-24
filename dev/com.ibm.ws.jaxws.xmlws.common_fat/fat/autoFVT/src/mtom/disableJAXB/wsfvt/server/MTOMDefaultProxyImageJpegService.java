//
// @(#) 1.3 autoFVT/src/mtom/disableJAXB/wsfvt/server/MTOMDefaultProxyImageJpegService.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 1/31/07 16:56:52 [8/8/12 06:58:04]
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

import javax.jws.WebService;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;

import org.test.mtom.disablejaxbproxy_imagejpeg.*;

@WebService(
		targetNamespace = "http://org/test/mtom/DisableJAXBProxy_imagejpeg",
		serviceName = "MTOMDefaultProxyImageJpegService",
		portName = "AttachmentServicePort",		
		wsdlLocation = "WEB-INF/wsdl/MTOMDefaultProxyImageJpeg.wsdl",
		endpointInterface = "org.test.mtom.disablejaxbproxy_imagejpeg.ImageServiceInterface")

//@BindingType (SOAPBinding.SOAP11HTTP_BINDING)
//@BindingType (SOAPBinding.SOAP11HTTP_MTOM_BINDING)

     /**
     * Required impl method from JAXB interface
     * A JAXB implementation.  
     * - MTOM setting = default, i.e. no annotation @BindingType
     * - Using PAYLOAD mode
     * - Sending back the same obj it received
     * - The JAXB objects are for image/jpeg MIME type
     *
     * @param ImageDepot obj
     * @return ImageDepot obj
     */	

public class MTOMDefaultProxyImageJpegService implements ImageServiceInterface {
	
	/* 
	 */
	public ImageDepot sendImage(ImageDepot input) {
        System.out.println("---------------------------------------");
		System.out.println("MTOMDefaultImageJpegService - Request received.");
		return input;
	}

}