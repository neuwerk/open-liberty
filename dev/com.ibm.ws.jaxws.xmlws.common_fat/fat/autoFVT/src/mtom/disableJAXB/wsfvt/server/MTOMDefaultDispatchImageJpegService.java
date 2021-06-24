//
// @(#) 1.4 autoFVT/src/mtom/disableJAXB/wsfvt/server/MTOMDefaultDispatchImageJpegService.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/9/07 22:24:33 [8/8/12 06:58:04]
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
// 04/09/07 jtnguyen    431247          Removed throw statement to pass validation

package mtom.disableJAXB.wsfvt.server;

import javax.jws.WebService;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import java.rmi.RemoteException;
import javax.xml.ws.WebServiceException;


import org.test.mtom.disablejaxbdispatch_imagejpeg.*;

/**
 * A JAXB implementation
 *
 */
@WebService(
		targetNamespace = "urn://DisableJAXBDispatch_imagejpeg.mtom.test.org",
		serviceName = "MTOMDefaultDispatchImageJpegService",
		portName = "AttachmentServicePort",		
		wsdlLocation = "WEB-INF/wsdl/MTOMDefaultDispatchImageJpeg.wsdl",
		endpointInterface = "org.test.mtom.disablejaxbdispatch_imagejpeg.ImageServiceInterface")

		
public class MTOMDefaultDispatchImageJpegService implements ImageServiceInterface {
 
	
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
          System.out.println("MTOMDefaultDispatchImageJpegService: Request received.");
          
       }
       else {
           System.out.println("MTOMDefaultDispatchImageJpegService: Request received = null");
           throw new RemoteException("Null input received.");   	   
       }
     } catch (Exception e) {
    	System.out.println("ERROR - Exception from MTOMDefaultDispatchImageJpegService.");
        e.printStackTrace();
     }

     return obj;
    }
}
