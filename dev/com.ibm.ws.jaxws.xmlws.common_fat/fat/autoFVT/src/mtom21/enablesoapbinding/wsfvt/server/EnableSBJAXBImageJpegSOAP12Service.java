//
// @(#) 1.1 autoFVT/src/mtom21/enablesoapbinding/wsfvt/server/EnableSBJAXBImageJpegSOAP12Service.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/15/08 14:34:22 [8/8/12 06:40:53]
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

import org.test.mtom21.enablesb_imagejpegsoap12.*;
/**
 * A JAXB implementation
 *
 */

@WebService(
		targetNamespace = "urn://EnableSB_imagejpegSOAP12.mtom21.test.org",
		serviceName = "EnableSBJAXBImageJpegSOAP12Service",
		portName = "AttachmentServicePort",		
		wsdlLocation = "WEB-INF/wsdl/JAXBImageJpegSOAP12.wsdl",
		endpointInterface = "org.test.mtom21.enablesb_imagejpegsoap12.ImageServiceSOAP12Interface")

@BindingType(value = SOAPBinding.SOAP12HTTP_BINDING)
//@BindingType(value = SOAPBinding.SOAP12HTTP_MTOM_BINDING)
@MTOM(enabled=true)

     /**
     * Required impl method from JAXB interface
     * A JAXB implementation.  
     * - MTOM setting = True via @BindingType with SOAP1.2
     * - Using PAYLOAD mode
     * - Sending back the same obj it received
     * - The JAXB object is for image/jpeg MIME type
     *
     * @param ImageDepot obj
     * @return ImageDepot obj
     */	
		
public class EnableSBJAXBImageJpegSOAP12Service implements ImageServiceSOAP12Interface {
 
	
    /**
     * Required impl method from javax.xml.ws.Provider interface
     * @param obj
     * @return same obj
     */
    public ImageDepot invoke(ImageDepot obj) {
        System.out.println("---------------------------------------");
        System.out.println("EnableSBJAXBImageJpegSOAP12Service: Request received.");

        try {
    	  if (obj != null) {
    		
            System.out.println("Sending the received JAXB object.");
   
          } else {
              throw new RemoteException("EnableSBJAXBImageJpegSOAP12Service:  Null input received.");
          }
        } catch (Exception e) {
        	System.out.println("ERROR - Exception from EnableSBJAXBImageJpegSOAP12Service.");
            e.printStackTrace();
        }

        return obj;

    }
}
