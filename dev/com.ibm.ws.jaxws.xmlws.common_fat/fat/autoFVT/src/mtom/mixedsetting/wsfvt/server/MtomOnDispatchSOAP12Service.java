//
// @(#) 1.4 autoFVT/src/mtom/mixedsetting/wsfvt/server/MtomOnDispatchSOAP12Service.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/9/07 22:25:15 [8/8/12 06:58:13]
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

package mtom.mixedsetting.wsfvt.server;

import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.soap.SOAPBinding;
import java.rmi.RemoteException;
import javax.xml.ws.WebServiceException;


import org.test.mtom.mtomondispatchsoap12.*;
/**
 * A JAXB implementation
 *
 */
@WebService(
		targetNamespace = "urn://MtomOnDispatchSOAP12.mtom.test.org",
		serviceName = "MtomOnDispatchSOAP12Service",
		portName = "AttachmentServicePort",		
		wsdlLocation = "WEB-INF/wsdl/MtomOnDispatchSOAP12.wsdl",
		endpointInterface = "org.test.mtom.mtomondispatchsoap12.ImageServiceInterface")

//@BindingType(value = SOAPBinding.SOAP12HTTP_BINDING)
@BindingType(value = SOAPBinding.SOAP12HTTP_MTOM_BINDING)  

     /**
     * Required impl method from JAXB interface
     * A JAXB implementation.  
     * - MTOM setting = True via @BindingType
     * - Using PAYLOAD mode
     * - Sending back the same obj it received
     * - The JAXB object is for image/jpeg MIME type
     *
     * @param ImageDepot obj
     * @return ImageDepot obj
     */	
		
public class MtomOnDispatchSOAP12Service implements ImageServiceInterface {
 
	
    /**
     * Required impl method from javax.xml.ws.Provider interface
     * @param obj
     * @return same obj
     */
    public ImageDepot invoke(ImageDepot obj) {
        System.out.println("---------------------------------------");
        System.out.println("MtomOnDispatchSOAP12Service: Request received.");

        try {
    	  if (obj != null) {
    		
            System.out.println("Sending the received JAXB object.");
   
          } else {
              throw new RemoteException("MtomOnDispatchSOAP12Service:  Null input received.");
          }
        } catch (Exception e) {
        	System.out.println("ERROR - Exception from MtomOnDispatchSOAP12Service.");
            e.printStackTrace();
        }

        return obj;

    }
}
