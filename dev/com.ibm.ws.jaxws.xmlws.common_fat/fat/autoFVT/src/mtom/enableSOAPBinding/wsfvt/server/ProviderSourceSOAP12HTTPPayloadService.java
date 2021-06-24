//
// @(#) 1.2 autoFVT/src/mtom/enableSOAPBinding/wsfvt/server/ProviderSourceSOAP12HTTPPayloadService.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/9/07 22:25:04 [8/8/12 06:58:22]
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
// 03/14/07 jtnguyen    LIDB3402-07.08  New File
// 04/09/07 jtnguyen    431247          Removed throw statement to pass validation

package mtom.enableSOAPBinding.wsfvt.server;

import javax.xml.ws.Provider;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.Service;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.BindingType;
import javax.xml.ws.soap.SOAPBinding;

import mtom.enableSOAPBinding.wsfvt.server.Common;
/**
* - This class provides the server side implementation for JAX-WS Provider<Source>
* - for SOAP12 Binding
* - with Mode = PAYLOAD.
* 
* The receiving message and the sending back message
* must have the headers defined in wsdl.
*/

@ServiceMode(value = Service.Mode.PAYLOAD)
@WebServiceProvider(wsdlLocation = "WEB-INF/wsdl/ProviderSourceSOAP12HTTPPayload.wsdl",
                    serviceName="ProviderSourceSOAP12HTTPPayloadService",
                    targetNamespace="http://ws.apache.org/axis2",
                    portName="AttachmentServicePort")
	
@BindingType (SOAPBinding.SOAP12HTTP_MTOM_BINDING)


public class ProviderSourceSOAP12HTTPPayloadService implements Provider<Source> {

//	public static final String sendbackStr1 = "<ns1:outMessage xmlns:ns1=\"http://ws.apache.org/axis2\"><value>test output</value></ns1:outMessage>";
	public static final String expectedStr1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ns1:inMessage xmlns:ns1=\"http://ws.apache.org/axis2\"><value>test output</value></ns1:inMessage>";
	public static final String badResult = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ns1:outMessage xmlns:ns1=\"http://ws.apache.org/axis2\"><invoke>received not the same as expected</invoke></ns1:outMessage>";
	public static final String inHeader = "ns1:inMessage";
	public static final String outHeader = "ns1:outMessage";
 
	
public ProviderSourceSOAP12HTTPPayloadService() {
}

/**
* This service receives the Source source and return it back to client
*
* @param source The Source object sent by the client
* @return the same source
* 
*/


  public Source invoke(Source obj) {
      System.out.println("--------------------------------------");
      System.out.println("ProviderSourceSOAP12HTTPPayloadService: Request received");

	  Source retVal = obj;
	
		
     try {
		String s = Common.toString(obj);
		//debug
                System.out.println("---Request=" + s);		

		if (s != null) {
          
		  if (s.trim().equals(expectedStr1)){			 
			  
			  System.out.println("Received input is correct.");
			  String s2 = s.replaceAll(inHeader,outHeader);			  
			  
			  retVal = Common.toSource(s2);

		  }
		  else if (s.trim().indexOf(inHeader) != -1) {  // received str contains inMessage header
			 
			  System.out.println("Received str with correct inHeader. Sending it back with outHeader format.");
			  String s2 = s.replaceAll(inHeader,outHeader);			  
			 
			  retVal = Common.toSource(s2);
			  
		  }
		  else {
			  System.out.println("Received str is NOT correct.");
			  
		  	  retVal = Common.toSource(badResult);
		  }
		}
      }catch (Exception ex) {
		   System.out.println("ProviderSourceSOAP12HTTPPayloadService: Failed with exception.");	
		   ex.printStackTrace();     
		}
	  return retVal;

  }
  		 
}