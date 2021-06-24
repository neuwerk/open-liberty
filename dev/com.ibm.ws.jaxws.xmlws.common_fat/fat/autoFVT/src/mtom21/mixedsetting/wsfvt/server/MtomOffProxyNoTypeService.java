//
// @(#) 1.1 autoFVT/src/mtom21/mixedsetting/wsfvt/server/MtomOffProxyNoTypeService.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/15/08 14:38:04 [8/8/12 06:40:57]
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
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.soap.MTOM;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.BindingType;

import org.test.mtom21.mtomoffproxynotype.*;

@WebService(
		targetNamespace = "http://org/test/mtom21/MtomOffProxyNoType",
		serviceName = "MtomOffProxyNoTypeService",
		portName = "AttachmentServicePort",		
		wsdlLocation = "WEB-INF/wsdl/MtomOffProxyNoType.wsdl",
		endpointInterface = "org.test.mtom21.mtomoffproxynotype.ImageServiceInterface")

@BindingType (SOAPBinding.SOAP11HTTP_BINDING)
//@BindingType (SOAPBinding.SOAP11HTTP_MTOM_BINDING)
@MTOM(enabled=false, threshold=0)

     /**
     * Required impl method from JAXB interface
     * A JAXB implementation.  
     * - Using MTOM setting via @BindingType
     * - Using PAYLOAD mode
     * - Sending back the same obj it received
     * - The JAXB objects are for no MIME type in WSDL
     *
     * @param ImageDepot obj
     * @return ImageDepot obj
     */	

public class MtomOffProxyNoTypeService implements ImageServiceInterface {
	
	/* 
	 */
	public ImageDepot sendImage(ImageDepot input) {
        System.out.println("---------------------------------------");
		System.out.println("MtomOffProxyNoTypeService - Request received.");
		return input;
	}



}