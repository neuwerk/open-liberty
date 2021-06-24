//
// @(#) 1.3 autoFVT/src/mtom/mixedsetting/wsfvt/server/MtomOffProxyNoTypeService.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 1/31/07 16:57:34 [8/8/12 06:58:17]
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


package mtom.mixedsetting.wsfvt.server;

import javax.jws.WebService;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.BindingType;

import org.test.mtom.mtomoffproxynotype.*;

@WebService(
		targetNamespace = "http://org/test/mtom/MtomOffProxyNoType",
		serviceName = "MtomOffProxyNoTypeService",
		portName = "AttachmentServicePort",		
		wsdlLocation = "WEB-INF/wsdl/MtomOffProxyNoType.wsdl",
		endpointInterface = "org.test.mtom.mtomoffproxynotype.ImageServiceInterface")

@BindingType (SOAPBinding.SOAP11HTTP_BINDING)
//@BindingType (SOAPBinding.SOAP11HTTP_MTOM_BINDING)

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