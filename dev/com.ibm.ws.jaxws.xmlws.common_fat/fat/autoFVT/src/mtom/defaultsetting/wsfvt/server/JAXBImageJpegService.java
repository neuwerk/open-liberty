//
// @(#) 1.3 autoFVT/src/mtom/defaultsetting/wsfvt/server/JAXBImageJpegService.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/9/07 22:24:23 [8/8/12 06:58:06]
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
// 01/31/07 jtnguyen    417109          Removed @WebServiceMode annotation
// 04/09/07 jtnguyen    431247          Removed throw statement to pass validation

package mtom.defaultsetting.wsfvt.server;

import java.rmi.RemoteException;

import javax.jws.WebService;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceException;

import org.test.mtom.imagejpeg.*;

@WebService(
		targetNamespace = "urn://imagejpeg.mtom.test.org",
		serviceName = "JAXBImageJpegService",
		portName = "AttachmentServicePort",		
		wsdlLocation = "WEB-INF/wsdl/JAXBImageJpeg.wsdl",
		endpointInterface = "org.test.mtom.imagejpeg.ImageServiceInterface")

		
public class JAXBImageJpegService implements ImageServiceInterface {
 
	
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
          System.out.println("JAXBImageJpegService: Request received.");
          
       }
       else {
           System.out.println("JAXBImageJpegService: Request received = null");
           throw new RemoteException("Null input received.");   	   
       }
     } catch (Exception e) {
    	System.out.println("ERROR - Exception from JAXBImageJpegService.");
        e.printStackTrace();
     }

     return obj;
    }
}
