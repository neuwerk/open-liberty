//
// @(#) 1.1 autoFVT/src/mtom21/disablejaxb/wsfvt/server/MTOMDefaultProxyImageJpegService.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/15/08 14:27:27 [8/8/12 06:40:44]
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

package mtom21.disablejaxb.wsfvt.server;

import javax.jws.WebService;

import org.test.mtom21.disablejaxbproxy_imagejpeg.ImageDepot;
import org.test.mtom21.disablejaxbproxy_imagejpeg.ImageServiceInterface;

@WebService(
		targetNamespace = "http://org/test/mtom21/DisableJAXBProxy_imagejpeg",
		serviceName = "MTOMDefaultProxyImageJpegService",
		portName = "AttachmentServicePort",		
		wsdlLocation = "WEB-INF/wsdl/MTOMDefaultProxyImageJpeg.wsdl",
		endpointInterface = "org.test.mtom21.disablejaxbproxy_imagejpeg.ImageServiceInterface")

     /**
     * Required impl method from JAXB interface
     * A JAXB implementation.  
     * - MTOM setting = default
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