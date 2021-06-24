//
// @(#) 1.5 autoFVT/src/mtom/enableJAXB/wsfvt/server/EnableJAXBImageJpegHEXService.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/9/07 22:24:45 [8/8/12 06:58:19]
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
// 11/27/06 jtnguyen    LIDB3402-07.07  New File for binary type=hexBinary
// 01/22/07 jtnguyen    416083          Changed package name to lower case
// 01/31/07 jtnguyen    417109          Removed @WebServiceMode annotation
// 03/02/07 jtnguyen    424165          Changed wsdlLocation to correct case 
// 04/09/07 jtnguyen    431247          Removed throw statement to pass validation

package mtom.enableJAXB.wsfvt.server;

import javax.jws.WebService;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import java.rmi.RemoteException;
import javax.xml.ws.WebServiceException;


import org.test.mtom.enablejaxb_imagejpeghex.*;

/**
 * A JAXB implementation
 *
 */

@WebService(
		targetNamespace = "urn://EnableJAXB_imagejpegHEX.mtom.test.org",
		serviceName = "EnableJAXBImageJpegHEXService",
		portName = "AttachmentServicePort",		
		wsdlLocation = "WEB-INF/wsdl/EnableJAXBImageJpegHex.wsdl",
		endpointInterface = "org.test.mtom.enablejaxb_imagejpeghex.ImageServiceInterface")

		
public class EnableJAXBImageJpegHEXService implements ImageServiceInterface {
 
	
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
          System.out.println("EnableJAXBImageJpegHEXService: Request received.");
          
       }
       else {
           System.out.println("EnableJAXBImageJpegHEXService: Request received = null");
           throw new RemoteException("Null input received.");   	   
       }
     } catch (Exception e) {
    	System.out.println("ERROR - Exception from EnableJAXBImageJpegHEXService.");
        e.printStackTrace();
     }

     return obj;
    }
}
