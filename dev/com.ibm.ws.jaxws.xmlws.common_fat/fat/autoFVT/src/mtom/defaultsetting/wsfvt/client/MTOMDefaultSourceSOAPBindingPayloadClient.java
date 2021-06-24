//
// @(#) 1.4 autoFVT/src/mtom/defaultsetting/wsfvt/client/MTOMDefaultSourceSOAPBindingPayloadClient.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/19/07 13:01:30 [8/8/12 06:58:06]
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
// 02/15/06 jtnguyen    421071          Added action to match wsdl's action
// 04/17/07 jtnguyen    433059          Changed server.Common to client.Common in import
// 04/18/07 jtnguyen    433294          Improved error logging

package mtom.defaultsetting.wsfvt.client;

import java.io.*;
import javax.imageio.ImageIO;
import javax.xml.ws.Service;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import java.awt.Image;

import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.BindingProvider;

import mtom.defaultsetting.wsfvt.client.Common;

/*
 * Dispatch client with Source type.  
 * The message sent/received must use correct header/footer as defined in the wsdl 
 * - no setting for MTOM (bindingID = null)
 * - mode = PAYLOAD
 * - no setting for SOAP version
*/
public class MTOMDefaultSourceSOAPBindingPayloadClient {
	
	public static final String goodResult = "Message processed";
			
	public static final String str1 = "<ns1:inMessage xmlns:ns1=\"http://ws.apache.org/axis2\"><value>test output</value></ns1:inMessage>";
	public static final String expectedStr1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ns1:outMessage xmlns:ns1=\"http://ws.apache.org/axis2\"><value>test output</value></ns1:outMessage>";
	public static final String xmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

	public static final String axis2NSHeader = "<ns1:inMessage xmlns:ns1=\"http://ws.apache.org/axis2\">";
	public static final String axis2NSFooter = "</ns1:inMessage>";
   
	public static final String jpegFilename = "image1.jpeg";  
	public static final String inHeader = "ns1:inMessage";
	public static final String outHeader = "ns1:outMessage";
	
	public static final QName serviceName = new QName("http://ws.apache.org/axis2", "ProviderSourceSOAP11HTTPPayloadService");
	public static final QName portName = new QName("http://ws.apache.org/axis2", "AttachmentServicePort");
	public static final String endpointUrl = "http://@REPLACE_WITH_HOST_NAME@:"
          + "@REPLACE_WITH_PORT_NUM@/mtomdefaultsetting/ProviderSourceSOAP11HTTPPayloadService";
	   
	public static final String bindingID = null;
	public static final Service.Mode mode = Service.Mode.PAYLOAD;

 	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
	
            MTOMDefaultSourceSOAPBindingPayloadClient client = new MTOMDefaultSourceSOAPBindingPayloadClient();
            System.out.println("Result 1:\n" + client.DefaultSetting_DispatchSource_01());
            System.out.println("Result 2:\n" + client.DefaultSetting_DispatchSource_02());

    }
		
        
    /** 
     * This method will create a JAX-WS Dispatch object to call the remote method.
     * It uses Source to wrap the byte[] format of an image, with correct header as defined in wsdl 
     * @return returned from the remote method
     */
    public String DefaultSetting_DispatchSource_01() {
	   
        System.out.println("***** In MTOMDefaultSourceSOAPBindingPayloadClient.DefaultSetting_DispatchSource_01 *****"); 	   
        String sendBackStr = null;
   		
       // create a service
       Service svc = Service.create(serviceName);
       svc.addPort(portName,bindingID,endpointUrl);
       // debug
       System.out.println("- add Port: portName=" + portName + ",bindingID=" + bindingID + ",endpointURL=" + endpointUrl);
       
       // create dispatch<Source>
       javax.xml.ws.Dispatch<Source> dispatch = null;
       dispatch = svc.createDispatch(portName, Source.class, mode);
               
       // force SOAPAction to match with wsdl action
       ((BindingProvider)dispatch).getRequestContext().put(BindingProvider.SOAPACTION_USE_PROPERTY, true); 
       ((BindingProvider)dispatch).getRequestContext().put(BindingProvider.SOAPACTION_URI_PROPERTY, "echoString");

       Image image = null;
       byte[] ba = null;
       try{
           image = ImageIO.read (new File (jpegFilename));
           ba = AttachmentHelper.getImageBytes(image,"image/jpeg");
       } catch (IOException ioe){
       }

       String orgSrc = axis2NSHeader + ba.toString() + axis2NSFooter;
            
                           
       // call server's invoke
       Source retVal = dispatch.invoke(Common.toSource(orgSrc));
    
       // string after converting from Source result - can only called once because it changes the source:
       String s = Common.toString(retVal);
                                
       if (s != null) {
          String s2 = s.replaceAll(outHeader,inHeader);
          String newOrg = xmlHeader + orgSrc;
          
          if (s2.equals(newOrg)){  //
              System.out.println("Message received matched with message sent.");                                                                  
              sendBackStr = goodResult;
          }
          else {

              System.out.println("**ERROR - Received message does not match expected message.\nExpected:\n" 
                                 + newOrg + "\nReceived:\n" + s2);
              sendBackStr = "**ERROR - Received message does not match expected message.";
                                              
          }
       }

       return sendBackStr;
    }

    /** 
     * This method will create a JAX-WS Dispatch object to call the remote method.
     * It uses Source to wrap a string with correct header as defined in wsdl 
     * @return returned from the remote method
     */
   
    public String DefaultSetting_DispatchSource_02() {
	   
        System.out.println("***** In MTOMDefaultSourceSOAPBindingPayloadClient.DefaultSetting_DispatchSource_02 *****"); 	   
        String sendBackStr = null;
      	   		
        // create a service
        Service svc = Service.create(serviceName);
        svc.addPort(portName,bindingID,endpointUrl);
           
        System.out.println("- add Port: portName=" + portName + ",bindingID=" + bindingID + ",endpointURL=" + endpointUrl);
   
        // create dispatch<Source>
        javax.xml.ws.Dispatch<Source> dispatch = null;
        dispatch = svc.createDispatch(portName, Source.class, mode);
                                                               
        // force SOAPAction to match with wsdl action
        ((BindingProvider)dispatch).getRequestContext().put(BindingProvider.SOAPACTION_USE_PROPERTY, true); 
        ((BindingProvider)dispatch).getRequestContext().put(BindingProvider.SOAPACTION_URI_PROPERTY, "echoString");
           
        String orgSrc = str1;
                           
        // call server's invoke
        Source retVal = dispatch.invoke(Common.toSource(orgSrc));
    
        // string after converting from Source result - can only called once because it changes the source:
        String s = Common.toString(retVal);
        
        if (s != null) {
                  
         
      if (s.equals(expectedStr1)){      			     				  
                sendBackStr = goodResult;
          }
          else {
              System.out.println("**ERROR - Received message does not match expected message.\nExpected:\n" 
                                 + expectedStr1 + "\nReceived:\n" + s);
              sendBackStr = "**ERROR - Received message does not match expected message.";
          }
        }

        return sendBackStr;
    }
       
}
	   
	