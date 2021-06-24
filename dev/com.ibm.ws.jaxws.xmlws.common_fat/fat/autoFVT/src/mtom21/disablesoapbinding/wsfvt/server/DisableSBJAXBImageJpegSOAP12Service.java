//
// @(#) 1.1 autoFVT/src/mtom21/disablesoapbinding/wsfvt/server/DisableSBJAXBImageJpegSOAP12Service.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/15/08 14:28:41 [8/8/12 06:40:47]
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
import javax.xml.ws.soap.MTOM;
import javax.xml.ws.soap.SOAPBinding;
import java.rmi.RemoteException;
import javax.xml.ws.WebServiceException;


import org.test.mtom21.disablesb_imagejpegsoap12.*;
/**
 * A JAXB implementation
 *
 */

@WebService(
		targetNamespace = "urn://DisableSB_imagejpegSOAP12.mtom21.test.org",
		serviceName = "DisableSBJAXBImageJpegSOAP12Service",
		portName = "AttachmentServicePort",		
		wsdlLocation = "WEB-INF/wsdl/JAXBImageJpegSOAP12.wsdl",
		endpointInterface = "org.test.mtom21.disablesb_imagejpegsoap12.ImageServiceInterface")

@BindingType(value = SOAPBinding.SOAP12HTTP_BINDING)
//@BindingType(value = SOAPBinding.SOAP12HTTP_MTOM_BINDING)
@MTOM(enabled=false, threshold=0)
     /**
     * Required impl method from JAXB interface
     * A JAXB implementation.  
     * - MTOM setting = Disable via @BindingType with SOAP1.2
     * - Using PAYLOAD mode
     * - Sending back the same obj it received
     * - The JAXB object is for image/jpeg MIME type
     *
     * @param ImageDepot obj
     * @return ImageDepot obj
     */	
		
public class DisableSBJAXBImageJpegSOAP12Service implements ImageServiceInterface {
 
	
    /**
     * Required impl method from javax.xml.ws.Provider interface
     * @param obj
     * @return same obj
     */
    public ImageDepot invoke(ImageDepot obj) {
        System.out.println("---------------------------------------");
        System.out.println("DisableSBJAXBImageJpegSOAP12Service: Request received.");

        try {
    	  if (obj != null) {
    		
            System.out.println("Sending the received JAXB object.");
   
          } else {
              throw new RemoteException("DisableSBJAXBImageJpegSOAP12Service:  Null input received.");
          }
        } catch (Exception e) {
        	System.out.println("ERROR - Exception from DisableSBJAXBImageJpegSOAP12Service.");
            e.printStackTrace();
        }

        return obj;

    }
}
