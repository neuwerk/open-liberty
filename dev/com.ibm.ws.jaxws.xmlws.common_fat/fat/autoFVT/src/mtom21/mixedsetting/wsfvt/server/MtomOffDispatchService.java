//
// @(#) 1.1 autoFVT/src/mtom21/mixedsetting/wsfvt/server/MtomOffDispatchService.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/15/08 14:38:01 [8/8/12 06:40:57]
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

package mtom21.mixedsetting.wsfvt.server;

import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.soap.MTOM;
import javax.xml.ws.soap.SOAPBinding;
import java.rmi.RemoteException;
import javax.xml.ws.WebServiceException;


import org.test.mtom21.mtomoffdispatch.*;
/**
 * A JAXB implementation
 *
 */

@WebService(
		targetNamespace = "urn://MtomOffDispatch.mtom21.test.org",
		serviceName = "MtomOffDispatchService",
		portName = "AttachmentServicePort",		
		wsdlLocation = "WEB-INF/wsdl/MtomOffDispatch.wsdl",
		endpointInterface = "org.test.mtom21.mtomoffdispatch.ImageServiceInterface")

//@BindingType(value = SOAPBinding.SOAP11HTTP_BINDING)
@BindingType(value = SOAPBinding.SOAP11HTTP_MTOM_BINDING)
// this should trump @BindingType
@MTOM(enabled=false, threshold=0)

     /**
     * Required impl method from JAXB interface
     * A JAXB implementation.  
     * - MTOM setting = Disable via @BindingType
     * 
     * - Using PAYLOAD mode
     * - Sending back the same obj it received
     * - The JAXB object is for image/jpeg MIME type
     *
     * @param ImageDepot obj
     * @return ImageDepot obj
     */	
		
public class MtomOffDispatchService implements ImageServiceInterface {
 
	
    /**
     * Required impl method from javax.xml.ws.Provider interface
     * @param obj
     * @return same obj
     */
    public ImageDepot invoke(ImageDepot obj)  {
        System.out.println("---------------------------------------");
        System.out.println("MtomOffDispatchService: Request received.");

        try {
    	  if (obj != null) {
    		
            System.out.println("Sending the received JAXB object.");
   
          } else {
              throw new RemoteException("MtomOffDispatchService:  Null input received.");
          }
        } catch (Exception e) {
        	System.out.println("ERROR - Exception from MtomOffDispatchService.");
            e.printStackTrace();
        }

        return obj;

    }
}
