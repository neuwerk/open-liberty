//
// @(#) 1.4 autoFVT/src/mtom/enableJAXB/wsfvt/server/EnableJAXBTextPlainService.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/9/07 22:24:52 [8/8/12 06:58:11]
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

package mtom.enableJAXB.wsfvt.server;

import javax.jws.WebService;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import java.rmi.RemoteException;
import javax.xml.ws.WebServiceException;

import org.test.mtom.enablejaxb_textplain.*;
/**
 * A JAXB implementation
 *
 */

@WebService(
		targetNamespace = "urn://EnableJAXB_textplain.mtom.test.org",
		serviceName = "EnableJAXBTextPlainService",
		portName = "AttachmentServicePort",		
		wsdlLocation = "WEB-INF/wsdl/EnableJAXBTextPlain.wsdl",
		endpointInterface = "org.test.mtom.enablejaxb_textplain.TextServiceInterface")

		
public class EnableJAXBTextPlainService implements TextServiceInterface {
    
	/**
     * Required impl method from JAXB interface
     *   
     * - No MTOM setting via @BindingType
     * - Using PAYLOAD mode
     * - Sending back the same obj it received
     * - The JAXB object is for plain/text MIME type
     * 
     * @param ImageDepot obj
     * @return ImageDepot obj
     */
	
 
    public ImageDepot invoke(ImageDepot obj) {
        System.out.println("--------------------------------------");
        System.out.println("EnableJAXBTextPlainService: Request received.");
         
        try {
        	
         	String outStr = obj.getImageData();
        	if (outStr != null) {
        		
              System.out.println("Sending back string wrapped in JAXB object.");
     
            } else {
                throw new RemoteException("EnableJAXBTextPlainService:  Null input received.");
            }

            
        } catch (Exception e) {
        	System.out.println("ERROR - Exception from EnableJAXBTextPlainService.");
            e.printStackTrace();
        }

        return obj;

    }
}
