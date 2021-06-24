//
// @(#) 1.1 autoFVT/src/mtom21/enablejaxb/wsfvt/server/EnableJAXBImageJpegService.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/15/08 14:30:12 [8/8/12 06:40:50]
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
import java.rmi.RemoteException;
import javax.xml.ws.WebServiceException;


import org.test.mtom21.enablejaxb_imagejpeg.*;

/**
 * A JAXB implementation
 *
 */

@WebService(
		targetNamespace = "urn://EnableJAXB_imagejpeg.mtom21.test.org",
		serviceName = "EnableJAXBImageJpegService",
		portName = "AttachmentServicePort",		
		wsdlLocation = "WEB-INF/wsdl/EnableJAXBImageJpeg.wsdl",
		endpointInterface = "org.test.mtom21.enablejaxb_imagejpeg.ImageServiceInterface")

		
public class EnableJAXBImageJpegService implements ImageServiceInterface {
 
	
    /**
     * Required impl method from JAXB interface
     * 
     * - No MTOM setting via @BindingType
     * - Using PAYLOAD mode
     * - Sending back the same obj it received
     * - The JAXB object is for image/jpeg MIME type
     *
     * @param ImageDepot obj
     * @return ImageDepot obj
     */
    public ImageDepot invoke(ImageDepot obj) {
       System.out.println("--------------------------------------");

       try {
       if (obj != null){
          System.out.println("EnableJAXBImageJpegService: Request received.");
          
       }
       else {
           System.out.println("EnableJAXBImageJpegService: Request received = null");
           throw new RemoteException("Null input received.");   	   
       }
     } catch (Exception e) {
    	System.out.println("ERROR - Exception from EnableJAXBImageJpegService.");
        e.printStackTrace();
     }

     return obj;
    }
}
