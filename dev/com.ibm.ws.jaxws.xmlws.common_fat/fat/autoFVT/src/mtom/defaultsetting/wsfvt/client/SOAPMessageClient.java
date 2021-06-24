//
// @(#) 1.3 autoFVT/src/mtom/defaultsetting/wsfvt/client/SOAPMessageClient.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 5/16/07 14:56:45 [8/8/12 06:58:20]
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
// 05/02/07 jtnguyen    434549          Used Provider<SOAPMessage> and changed error logging
// 05/16/07 jtnguyen    439466          Added a space to extra_xmls

package mtom.defaultsetting.wsfvt.client;

import javax.imageio.ImageIO;
import javax.xml.ws.Service;
import javax.xml.namespace.QName;
import java.awt.Image;
import java.io.*;

import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.soap.SOAPMessage;

import mtom.defaultsetting.wsfvt.client.Common;

/*
 * Message sent/received must use correct header/footer as defined in wsdl
 * - uses no setting for MTOM 
 * - mode = Message
 * - Service end point is a Provider<SOAPMessage>
*/

public class SOAPMessageClient {
	
	public static final String goodResult = "Message processed";
		
        public static final String str1 = "<value>test output</value>";
 	public static final String xmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

	public static final String axis2NSHeader = "<ns1:inMessage xmlns:ns1=\"http://ws.apache.org/axis2\">";
	public static final String axis2NSFooter = "</ns1:inMessage>";
 	
    //public static final String SOAP11_HEADER = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body>";
    //public static final String SOAP11_TRAILER = "</soapenv:Body></soapenv:Envelope>";
	// Liberty jaxws impl. return "soap" as the namespace prefix
	public static final String SOAP11_HEADER = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body>";
    public static final String SOAP11_TRAILER = "</soap:Body></soap:Envelope>";

	public static final String jpegFilename = "image1.jpeg";  
	public static final String inHeader = "ns1:inMessage";
	public static final String outHeader = "ns1:outMessage";
	
	public static final QName serviceName = new QName("http://ws.apache.org/axis2", "ProviderSOAPMessageService");
        public static final QName portName = new QName("http://ws.apache.org/axis2", "AttachmentServicePortTest");

	public static final String endpointUrl = "http://@REPLACE_WITH_HOST_NAME@:"
          + "@REPLACE_WITH_PORT_NUM@/mtomdefaultsetting/ProviderSOAPMessageService";
	   
	public static final String bindingID = SOAPBinding.SOAP11HTTP_BINDING;
	public static final Service.Mode mode = Service.Mode.MESSAGE;

        public static final String extra_xmlns = " xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\"";  // one space at beginning is intentional
 	
        /**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

            SOAPMessageClient client = new SOAPMessageClient();
            System.out.println("Result 1:\n" + client.DefaultSetting_SOAPMessage_ImageMessage());
            System.out.println("Result 2:\n" + client.DefaultSetting_SOAPMessage_TextMessage());
    }
		
        
    /** 
     * This method will create a JAX-WS Dispatch object to call the remote method.
     * It uses correct header as defined in wsdl for the sending string 
     * @return returned from the remote method
     */
    public String DefaultSetting_SOAPMessage_TextMessage() {


        System.out.println("\n***** In SOAPMessageClient.DefaultSetting_SOAPMessage_TextMessage *****\n"); 	      
      	
        // create a service
        Service svc = Service.create(serviceName);    	       
        svc.addPort(portName,bindingID,endpointUrl);     			
       
        System.out.println("- add Port: portName=" + portName + ",bindingID=" + bindingID + ",endpointURL=" + endpointUrl);

        javax.xml.ws.Dispatch<SOAPMessage> dispatch = null;
        dispatch = svc.createDispatch(portName,SOAPMessage.class, mode);
              
        // force SOAPAction to match with wsdl action                        
        ((BindingProvider)dispatch).getRequestContext().put(BindingProvider.SOAPACTION_USE_PROPERTY, true);                         
        ((BindingProvider)dispatch).getRequestContext().put(BindingProvider.SOAPACTION_URI_PROPERTY, "echoString");

     
         String orgSrc = SOAP11_HEADER + axis2NSHeader + str1 + axis2NSFooter + SOAP11_TRAILER;
         SOAPMessage message = Common.toSOAPMessage(orgSrc);         
         SOAPMessage retVal = dispatch.invoke(message);
                    
         String s = Common.toString(retVal);
         return compare(s,orgSrc);

    }

    /** 
    * This method will create a JAX-WS Dispatch object to call the remote method.
    * It uses Source to wrap the byte[] format of an image, with correct header as defined in wsdl 
    * @return returned from the remote method
    */

    public String DefaultSetting_SOAPMessage_ImageMessage() {


        System.out.println("\n***** In SOAPMessageClient.DefaultSetting_SOAPMessage_ImageMessage *****\n");

         // create a service
           Service svc = Service.create(serviceName);
           svc.addPort(portName,bindingID,endpointUrl);
           System.out.println("- add Port: portName=" + portName + ",bindingID=" + bindingID + ",endpointURL=" + endpointUrl);

           javax.xml.ws.Dispatch<SOAPMessage> dispatch = null;
           dispatch = svc.createDispatch(portName,SOAPMessage.class, mode);

           // force SOAPAction to match with wsdl action                        
           ((BindingProvider)dispatch).getRequestContext().put(BindingProvider.SOAPACTION_USE_PROPERTY, true);                         
           ((BindingProvider)dispatch).getRequestContext().put(BindingProvider.SOAPACTION_URI_PROPERTY, "echoString");
                       
           byte[] ba = null; 
           Image image = null;
           try {                      
               image = ImageIO.read (new File (jpegFilename));           
               ba = AttachmentHelper.getImageBytes(image,"image/jpeg");
           } catch (IOException ioe){
               System.out.println("**ERROR - IOException caught.");	
               ioe.printStackTrace();     
           }

           String orgSrc = SOAP11_HEADER + axis2NSHeader + ba.toString() + axis2NSFooter + SOAP11_TRAILER;

         SOAPMessage message = Common.toSOAPMessage(orgSrc);
         SOAPMessage retVal = dispatch.invoke(message);
         String s = Common.toString(retVal);

         return compare(s,orgSrc);
    }
        
    // Utility
    public String compare(String s, String orgSrc){
        String sendBackStr = null;
        System.out.println("Message received:\"" + s + "\"");

        if (s != null) {
            String s1 = s.replaceAll(outHeader,inHeader); 
            String s2 = s1;
            String s3 = xmlHeader + orgSrc;

            if (s1.indexOf(extra_xmlns)!= -1) {

                s2 = s1.replaceAll(extra_xmlns,"");
                System.out.println("INFO: Replacing extra_xmlns.");

            }
            else {
                System.out.println("INFO: Message does not have extra_xmlns.");
            }

            if (s2.equals(s3)){  
                System.out.println("Message received matched with message sent.");
                sendBackStr = goodResult;
            }
            else {   
                sendBackStr = "**ERROR - Received message does not match expected message.\n--Expected:\"" 
                 + s3 + "\"\n---Received:\"" + s2 + "\"";

                System.out.println(sendBackStr);
            }             
        } else {   
            System.out.println("**ERROR - Received message is null.");
            sendBackStr = "**ERROR - Received message is null.";
        } 

        return sendBackStr;
   }

    
}
	   
	