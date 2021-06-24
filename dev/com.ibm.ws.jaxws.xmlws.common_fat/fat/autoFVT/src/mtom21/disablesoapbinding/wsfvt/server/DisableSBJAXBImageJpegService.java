//
// @(#) 1.1 autoFVT/src/mtom21/disablesoapbinding/wsfvt/server/DisableSBJAXBImageJpegService.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/15/08 14:28:40 [8/8/12 06:40:47]
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

import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.soap.SOAPBinding;
import java.rmi.RemoteException;
import javax.xml.ws.WebServiceException;


import org.test.mtom21.disablesb_imagejpeg.*;
/**
 * A JAXB implementation
 *
 */

@WebService(
		targetNamespace = "urn://DisableSB_imagejpeg.mtom21.test.org",
		serviceName = "DisableSBJAXBImageJpegService",
		portName = "AttachmentServicePort",		
		wsdlLocation = "WEB-INF/wsdl/JAXBImageJpeg.wsdl",
		endpointInterface = "org.test.mtom21.disablesb_imagejpeg.ImageServiceInterface")

@BindingType(value = SOAPBinding.SOAP11HTTP_BINDING)
//@BindingType(value = SOAPBinding.SOAP11HTTP_MTOM_BINDING)  

     /**
     * Required impl method from JAXB interface
     * A JAXB implementation.  
     * - MTOM setting = Disable via @BindingType
     * - Using PAYLOAD mode
     * - Sending back the same obj it received
     * - The JAXB object is for image/jpeg MIME type
     *
     * @param ImageDepot obj
     * @return ImageDepot obj
     */	
		
public class DisableSBJAXBImageJpegService implements ImageServiceInterface {
 
	
    /**
     * Required impl method from javax.xml.ws.Provider interface
     * @param obj
     * @return same obj
     */
    public ImageDepot invoke(ImageDepot obj) {
        System.out.println("---------------------------------------");
        System.out.println("DisableSBJAXBImageJpegService: Request received.");

        try {
    	  if (obj != null) {
    		
            System.out.println("Sending the received JAXB object.");
   
          } else {
              throw new RemoteException("DisableSBJAXBImageJpegService:  Null input received.");
          }
        } catch (Exception e) {
        	System.out.println("ERROR - Exception from DisableSBJAXBImageJpegService.");
            e.printStackTrace();
        }

        return obj;

    }
}
