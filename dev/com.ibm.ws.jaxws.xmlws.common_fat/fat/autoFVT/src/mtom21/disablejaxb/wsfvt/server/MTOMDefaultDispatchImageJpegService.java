//
// @(#) 1.1 autoFVT/src/mtom21/disablejaxb/wsfvt/server/MTOMDefaultDispatchImageJpegService.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/15/08 14:27:17 [8/8/12 06:40:44]
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

import java.rmi.RemoteException;

import javax.jws.WebService;
import javax.xml.ws.soap.MTOM;

import org.test.mtom21.disablejaxbdispatch_imagejpeg.ImageDepot;
import org.test.mtom21.disablejaxbdispatch_imagejpeg.ImageServiceInterface;

/**
 * A JAXB implementation
 *
 */
@WebService(
		targetNamespace = "urn://DisableJAXBDispatch_imagejpeg.mtom21.test.org",
		serviceName = "MTOMDefaultDispatchImageJpegService",
		portName = "AttachmentServicePort",		
		wsdlLocation = "WEB-INF/wsdl/MTOMDefaultDispatchImageJpeg.wsdl",
		endpointInterface = "org.test.mtom21.disablejaxbdispatch_imagejpeg.ImageServiceInterface")
@MTOM(enabled=false)
		
public class MTOMDefaultDispatchImageJpegService implements ImageServiceInterface {
 
	
    /**
     * Required impl method from JAXB interface
     * 
     * - No MTOM setting
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
