//
// @(#) 1.1 autoFVT/src/mtom21/enablejaxb/wsfvt/server/ProviderSOAPMessageSOAP12Service.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/15/08 14:30:37 [8/8/12 06:40:50]
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

package mtom21.enablejaxb.wsfvt.server;

import javax.xml.soap.SOAPMessage;
import javax.xml.ws.BindingType;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.soap.SOAPBinding;

/**
* - This class provides the server side implementation for JAX-WS Provider<MESSAGE>
* - for SOAP12 Binding
* - with Mode = MESSAGE.
* - MTOM setting = default = disabled, via @BindingType
* 
* The receiving message and the sending back message
* must have the headers defined in wsdl.
*/

@ServiceMode(value = Service.Mode.MESSAGE)
@WebServiceProvider(wsdlLocation = "WEB-INF/wsdl/ProviderSOAPMessageSOAP12.wsdl",
                    serviceName="ProviderSOAPMessageSOAP12Service",
                    targetNamespace="http://ws.apache.org/axis2",
                    portName="AttachmentServicePort")
	
@BindingType (SOAPBinding.SOAP12HTTP_BINDING)

public class ProviderSOAPMessageSOAP12Service implements Provider<SOAPMessage> {

        public static final String expectedStr1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><soapenv:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"><soapenv:Body><ns1:inMessage xmlns:ns1=\"http://ws.apache.org/axis2\"><value>test output</value></ns1:inMessage></soapenv:Body></soapenv:Envelope>";

	public static final String head = "<soapenv:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"><soapenv:Body><ns1:outMessage xmlns:ns1=\"http://ws.apache.org/axis2\">";
	public static final String tail = "</ns1:outMessage></soapenv:Body></soapenv:Envelope>";
	public static final String inHeader = "ns1:inMessage";
	public static final String outHeader = "ns1:outMessage";
	
public ProviderSOAPMessageSOAP12Service() {
}

  public SOAPMessage invoke(SOAPMessage obj) {
      System.out.println("--------------------------------------");
      System.out.println("enableJAXB - ProviderSOAPMessageSOAP12Service: Request received");

      SOAPMessage retVal = null;
      
      // check if MTOM is used
      if(obj.getAttachments().hasNext()) {
          return Common.toSOAPMessage(head + "***ERROR MTOM was used for SOAPMessage" + tail);
      }
		
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
		   System.out.println("ProviderSOAPMessageSOAP12Service: Failed at service endpoint.");	
		   ex.printStackTrace();     
		
      }
      return retVal;

  }
  		 
}