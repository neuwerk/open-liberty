//
// @(#) 1.1 autoFVT/src/mtom21/enablesoapbinding/wsfvt/server/EnableSBJAXBImageJpegService.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/15/08 14:34:21 [8/8/12 06:40:53]
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

package mtom21.enablesoapbinding.wsfvt.server;

import java.rmi.RemoteException;

import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.soap.MTOM;
import javax.xml.ws.soap.SOAPBinding;
import org.test.mtom21.enablesb_imagejpeg.*;
/**
 * A JAXB implementation
 *
 */
@WebService(
		targetNamespace = "urn://EnableSB_imagejpeg.mtom21.test.org",
		serviceName = "EnableSBJAXBImageJpegService",
		portName = "AttachmentServicePort",		
		wsdlLocation = "WEB-INF/wsdl/JAXBImageJpeg.wsdl",
		endpointInterface = "org.test.mtom21.enablesb_imagejpeg.ImageServiceInterface")

//@BindingType(value = SOAPBinding.SOAP11HTTP_BINDING)
//@BindingType(value = SOAPBinding.SOAP11HTTP_MTOM_BINDING)
@MTOM

     /**
     * Required impl method from JAXB interface
     * A JAXB implementation.  
     * - MTOM setting = True via @MTOM
     * - Using PAYLOAD mode
     * - Sending back the same obj it received
     * - The JAXB object is for image/jpeg MIME type
     *
     * @param ImageDepot obj
     * @return ImageDepot obj
     */	
		
public class EnableSBJAXBImageJpegService implements ImageServiceInterface {
 
	
    /**
     * Required impl method from javax.xml.ws.Provider interface
     * @param obj
     * @return same obj
     */
    public ImageDepot invoke(ImageDepot obj) {
        System.out.println("---------------------------------------");
        System.out.println("EnableSBJAXBImageJpegService: Request received.");

        try {
    	  if (obj != null) {
    		
            System.out.println("Sending the received JAXB object.");
   
          } else {
              throw new RemoteException("EnableSBJAXBImageJpegService:  Null input received.");
          }
        } catch (Exception e) {
        	System.out.println("ERROR - Exception from JAXBTextPlainService.");
            e.printStackTrace();
        }

        return obj;

    }
}
