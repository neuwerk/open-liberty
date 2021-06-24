//
// @(#) 1.1 autoFVT/src/mtom21/enablejaxb/wsfvt/server/EnableJAXBProxyImageJpegService.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/15/08 14:30:26 [8/8/12 06:40:50]
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

package mtom21.enablejaxb.wsfvt.server;

import javax.jws.WebService;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;

import org.test.mtom21.enablejaxbproxy_imagejpeg.*;

@WebService(
		targetNamespace = "http://org/test/mtom21/EnableJAXBProxy_imagejpeg",
		serviceName = "EnableJAXBProxyImageJpegService",
		portName = "AttachmentServicePort",		
		wsdlLocation = "WEB-INF/wsdl/EnableJAXBProxyImageJpeg.wsdl",
		endpointInterface = "org.test.mtom21.enablejaxbproxy_imagejpeg.ImageServiceInterface")

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

public class EnableJAXBProxyImageJpegService implements ImageServiceInterface {
	
	/* 
	 */
	public ImageDepot sendImage(ImageDepot input) {
        System.out.println("---------------------------------------");
		System.out.println("EnableJAXBProxyImageJpegService - Request received.");
		return input;
	}

}