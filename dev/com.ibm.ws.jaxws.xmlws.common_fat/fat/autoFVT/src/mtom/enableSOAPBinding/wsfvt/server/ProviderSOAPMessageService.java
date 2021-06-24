//
// @(#) 1.2 autoFVT/src/mtom/enableSOAPBinding/wsfvt/server/ProviderSOAPMessageService.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 5/16/07 14:57:07 [8/8/12 06:58:24]
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
// 05/02/07 jtnguyen    434549          New file
// 05/16/07 jtnguyen    439466          Removed comparison

package mtom.enableSOAPBinding.wsfvt.server;

import javax.xml.ws.Provider;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.Service;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.BindingType;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.soap.SOAPMessage;

import mtom.enableSOAPBinding.wsfvt.server.Common;

/**
* - This class provides the server side implementation for JAX-WS Provider<MESSAGE>
* - for SOAP11 Binding
* - with Mode = MESSAGE.
* - MTOM setting = enabled, via @BindingType
* 
* The receiving message and the sending back message
* must have the headers defined in wsdl.
*/

@ServiceMode(value = Service.Mode.MESSAGE)
@WebServiceProvider(wsdlLocation = "WEB-INF/wsdl/ProviderSOAPMessage.wsdl",
                    serviceName="ProviderSOAPMessageService",
                    targetNamespace="http://ws.apache.org/axis2",
                    portName="AttachmentServicePort")
	
@BindingType (SOAPBinding.SOAP11HTTP_MTOM_BINDING)

public class ProviderSOAPMessageService implements Provider<SOAPMessage> {

	public static final String head = "<soapenv:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body><ns1:outMessage xmlns:ns1=\"http://ws.apache.org/axis2\">";
	public static final String tail = "</ns1:outMessage></soapenv:Body></soapenv:Envelope>";
	public static final String inHeader = "ns1:inMessage";
	public static final String outHeader = "ns1:outMessage";
	
public ProviderSOAPMessageService() {
}

/**
* - This class provides the server side implementation for JAX-WS Provider<MESSAGE>
* - for SOAP11 Binding
* - with Mode = MESSAGE.
* - MTOM setting = enabled, via @BindingType
* 
* The receiving message and the sending back message
* must have the headers defined in wsdl.
*/


  public SOAPMessage invoke(SOAPMessage obj) {
      System.out.println("--------------------------------------");
      System.out.println("enableSOAPBinding - ProviderSOAPMessageService: Request received");

      SOAPMessage retVal = null;
		
     try {
         String s = Common.toString(obj);
         System.out.println("---Received message=" + s); 

         if (s != null) {
            if (s.trim().indexOf(inHeader) != -1) {  // received str contains inMessage header

                   String s2 = s.replaceAll(inHeader,outHeader);			  
                   retVal = Common.toSOAPMessage(s2);

            }
            else {
                   String badResult = "***ERROR at Service Endpoint: Received message does not have inHeader.  See WAS systemOut.log for details.";
                   System.out.println(badResult);
                   retVal = Common.toSOAPMessage(head + badResult + tail);
            }
         } 
         else {   
             String badResult = "***ERROR at Service Endpoint: Received message is NULL.";
             System.out.println(badResult);
             retVal = Common.toSOAPMessage(head + badResult + tail);
         } 

      }catch (Exception ex) {
		   System.out.println("ProviderSOAPMessageService: Failed at service endpoint.");	
		   ex.printStackTrace();     
		
      }
      return retVal;

  }
  		 
}