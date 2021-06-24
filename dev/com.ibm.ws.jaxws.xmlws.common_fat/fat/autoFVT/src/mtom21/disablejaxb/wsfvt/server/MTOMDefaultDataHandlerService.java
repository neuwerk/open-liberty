//
// @(#) 1.1 autoFVT/src/mtom21/disablejaxb/wsfvt/server/MTOMDefaultDataHandlerService.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/15/08 14:27:10 [8/8/12 06:40:43]
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

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.jws.WebService;

import org.apache.axiom.attachments.ByteArrayDataSource;
import org.test.mtom21.disablejaxbproxy_datahandler.ImageDepot;
import org.test.mtom21.disablejaxbproxy_datahandler.ImageServiceInterface;
import org.test.mtom21.disablejaxbproxy_datahandler.ObjectFactory;

@WebService(
		targetNamespace = "http://org/test/mtom21/DisableJAXBProxy_datahandler",
		serviceName = "MTOMDefaultDataHandlerService",
		portName = "AttachmentServicePort",		
		wsdlLocation = "WEB-INF/wsdl/MTOMDefaultProxyDataHandler.wsdl",
		endpointInterface = "org.test.mtom21.disablejaxbproxy_datahandler.ImageServiceInterface")

     /**
     * Required impl method from JAXB interface
     * A JAXB implementation.  
     * - Using default setting for MTOM
     * - Using PAYLOAD mode
     * - Sending back the same obj it received
     * - The JAXB objects are for multipart/* MIME type
     *
     * @param ImageDepot obj
     * @return ImageDepot obj
     */	

public class MTOMDefaultDataHandlerService implements ImageServiceInterface {

    /* 
     */
    public ImageDepot sendImage(ImageDepot input) {
        System.out.println("---------------------------------------");
        System.out.println("MTOMDefaultDataHandlerService - Request received.");
        return input;
    }

    public ImageDepot sendText(byte[] input) {
        System.out.println("---------------------------------------");
        System.out.println("MTOMDefaultDataHandlerService - sendText method: Request received");

        DataSource ds = new ByteArrayDataSource(input);
        DataHandler dh = new DataHandler(ds);

        ImageDepot id = new ObjectFactory().createImageDepot();
        id.setImageData(dh);

        return id;
    }

}