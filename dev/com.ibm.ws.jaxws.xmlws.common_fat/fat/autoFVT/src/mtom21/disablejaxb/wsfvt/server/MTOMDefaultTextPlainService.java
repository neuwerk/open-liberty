//
// @(#) 1.1 autoFVT/src/mtom21/disablejaxb/wsfvt/server/MTOMDefaultTextPlainService.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/15/08 14:27:30 [8/8/12 06:40:44]
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

import org.test.mtom21.disablejaxb_textplain.ImageDepot;
import org.test.mtom21.disablejaxb_textplain.TextServiceInterface;
/**
 * A JAXB implementation
 *
 */

@WebService(
		targetNamespace = "urn://DisableJAXB_textplain.mtom21.test.org",
		serviceName = "MTOMDefaultTextPlainService",
		portName = "AttachmentServicePort",		
		wsdlLocation = "WEB-INF/wsdl/MTOMDefaultTextPlain.wsdl",
		endpointInterface = "org.test.mtom21.disablejaxb_textplain.TextServiceInterface")
@MTOM(enabled=false)
		
public class MTOMDefaultTextPlainService implements TextServiceInterface {
    
	/**
     * Required impl method from JAXB interface
     *   
     * - No MTOM setting
     * - Using PAYLOAD mode
     * - Sending back the same obj it received
     * - The JAXB object is for plain/text MIME type
     * 
     * @param ImageDepot obj
     * @return ImageDepot obj
     */
	
 
    public ImageDepot invoke(ImageDepot obj) {
        System.out.println("--------------------------------------");
        System.out.println("MTOMDefaultTextPlainService: Request received.");
         
        try {
        	
         	String outStr = obj.getImageData();
        	if (outStr != null) {
        		
              System.out.println("Sending back string wrapped in JAXB object.");
     
            } else {
                throw new RemoteException("MTOMDefaultTextPlainService:  Null input received.");
            }

            
        } catch (Exception e) {
        	System.out.println("ERROR - Exception from MTOMDefaultTextPlainService.");
            e.printStackTrace();
        }

        return obj;

    }
}
