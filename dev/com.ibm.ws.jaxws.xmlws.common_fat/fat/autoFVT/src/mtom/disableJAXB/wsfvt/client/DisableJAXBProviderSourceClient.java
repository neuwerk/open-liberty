//
// @(#) 1.3 autoFVT/src/mtom/disableJAXB/wsfvt/client/DisableJAXBProviderSourceClient.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 2/12/07 11:34:12 [8/8/12 06:58:03]
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
// 11/21/06 jtnguyen    407277          Added isMTOMEnabled() and fixed remarks
// 02/12/07 jtnguyen    420303          Matched action in client to wsdl's

package mtom.disableJAXB.wsfvt.client;

import javax.imageio.ImageIO;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import java.awt.Image;
import java.io.File;

import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.BindingProvider;

import mtom.disableJAXB.wsfvt.client.Common;
/*
 *   Message sent/received must use correct header/footer as defined in wsdl
*/
public class DisableJAXBProviderSourceClient {
	
	public static final String goodResult = "Message processed";
	public static final String badResult = "Problem in processing message";
	// the XML declaration added by Source transformation
	public static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		
	public static final String str1 = "<ns1:inMessage xmlns:ns1=\"http://ws.apache.org/axis2\"><value>test output</value></ns1:inMessage>";
	public static final String expectedStr1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ns1:outMessage xmlns:ns1=\"http://ws.apache.org/axis2\"><value>test output</value></ns1:outMessage>";
	public static final String xmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

	public static final String axis2NSHeader = "<ns1:inMessage xmlns:ns1=\"http://ws.apache.org/axis2\">";
	public static final String axis2NSFooter = "</ns1:inMessage>";
   
	public static final String jpegFilename = "image1.jpeg";  
	public static final String inHeader = "ns1:inMessage";
	public static final String outHeader = "ns1:outMessage";
	
	public static final QName serviceName = new QName("http://ws.apache.org/axis2", "MTOMDefaultProviderSourceService");
	public static final QName portName = new QName("http://ws.apache.org/axis2", "AttachmentServicePort");
	   
	public static final String endpointUrl = "http://@REPLACE_WITH_HOST_NAME@:"
          + "@REPLACE_WITH_PORT_NUM@/disablejaxb/MTOMDefaultProviderSourceService";
	   
	public static final Service.Mode mode = Service.Mode.PAYLOAD;

 	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
    }
		
        
    /** 
     * This method will create a JAX-WS Dispatch object
     * to call the remote method.
     * - sets for MTOM=Diable with SOAP11 using 2 steps:  bindingID and setMTOMEnabled
     * - mode = PAYLOAD

     * - uses Source to wrap the byte[] format of an image, with correct
     * header as defined in wsdl 
 	 *
     * @return returned from the remote method
     */
    public String SourceSOAP11Payload_01() {

	   
	   // debug
	   printMessageToConsole("***** In DisableJAXBProviderSourceClient.SourceSOAP11Payload_01 *****");
 	   
       String sendBackStr = null;
       String bindingID = SOAPBinding.SOAP11HTTP_BINDING;
	
    	try {
   		
              // create a service
    	       Service svc = Service.create(serviceName);
    	       svc.addPort(portName,bindingID,endpointUrl);
    	       // debug
     			printMessageToConsole("- add Port: portName=" + portName + ",bindingID=" + bindingID + ",endpointURL=" + endpointUrl);
  	       
    	       // create dispatch<Source>
    			javax.xml.ws.Dispatch<Source> dispatch = null;
    			dispatch = svc.createDispatch(portName, Source.class, mode);
 
                        // force SOAPAction to match with wsdl action
                        ((BindingProvider)dispatch).getRequestContext().put(BindingProvider.SOAPACTION_USE_PROPERTY, true); 
                        ((BindingProvider)dispatch).getRequestContext().put(BindingProvider.SOAPACTION_URI_PROPERTY, "echoString");

    			// set MTOM on dispatch
    	        SOAPBinding binding = (SOAPBinding)dispatch.getBinding();
    	        binding.setMTOMEnabled(false);
   			
     		       Image image = ImageIO.read (new File (jpegFilename));
    		       byte[] ba = AttachmentHelper.getImageBytes(image,"image/jpeg");
    			   
    		   
    			   //String base64Str = Base64.encode(ba);
    			   String orgSrc = axis2NSHeader + ba.toString() + axis2NSFooter;
    	  		    	 	   
        		// call server's invoke
     		    Source retVal = dispatch.invoke(Common.toSource(orgSrc));
     		    
     		   
     		    
     			/// Common.toString - can only called once because it changes the Source
    			String s = Common.toString(retVal);
    			    			
    			
    			if (s != null) {
    			  String s2 = s.replaceAll(outHeader,inHeader);
     	    		  String newOrg = xmlHeader + orgSrc;

    	    		  if (s2.equals(newOrg)){  //
      			      sendBackStr = goodResult;
    			  }
    			  else {

                               System.out.println("Received str is NOT correct.");
 		               sendBackStr = badResult;
    			  }
    			}
    		} catch (Exception ex) {
    		   System.out.println("Client failed with exception.");	
    		   ex.printStackTrace();     
    		}
    		
    		
    		return sendBackStr;
    }

    /** 
     * This method will create a JAX-WS Dispatch object
     * to call the remote method.
     * - sets for MTOM=disable with SOAP11 using 2 steps:  bindingID and setMTOMEnabled
     * - mode = PAYLOAD 
     * - uses Source to wrap a string, with correct
     * header as defined in wsdl 
 	 *
     * @return returned from the remote method
     */
   
    public String SourceSOAP11Payload_02() {
	   
	   // debug
	   printMessageToConsole("***** In DisableJAXBProviderSourceClient.SourceSOAP11Payload_02 *****");
 	   
       String sendBackStr = null;
       String bindingID = SOAPBinding.SOAP11HTTP_BINDING;

    	try {
   		
              // create a service
    	       Service svc = Service.create(serviceName);
    	       svc.addPort(portName,bindingID,endpointUrl);
    	       // debug
     			printMessageToConsole("- add Port: portName=" + portName + ",bindingID=" + bindingID + ",endpointURL=" + endpointUrl);
  	       
    	       // create dispatch<Source>
    			javax.xml.ws.Dispatch<Source> dispatch = null;
    			dispatch = svc.createDispatch(portName, Source.class, mode);
    	          
                        // force SOAPAction to match with wsdl action
                        ((BindingProvider)dispatch).getRequestContext().put(BindingProvider.SOAPACTION_USE_PROPERTY, true); 
                        ((BindingProvider)dispatch).getRequestContext().put(BindingProvider.SOAPACTION_URI_PROPERTY, "echoString");

    			//set MTOM on dispatch
    	        SOAPBinding binding = (SOAPBinding)dispatch.getBinding();
    	        binding.setMTOMEnabled(false);
    			    			
    			   
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
                      //debug
    				  System.out.println("Received str is NOT correct.");
 		                  sendBackStr = badResult;
    			  }
    			}
    		} catch (Exception ex) {
    		   System.out.println("Client failed with exception.");	
    		   ex.printStackTrace();     
    		}
    		
    		
    		return sendBackStr;
    }
    
    public String SourceSequenceSOAP11Payload() {

        // currently, we only compare string.  
        // to improve, add extract and compare images from the message
 	   
 	   // debug
 	   printMessageToConsole("***** In DisableJAXBProviderSourceClient.SourceSequenceSOAP11Payload *****");
  	   
        String sendBackStr = null;
        String bindingID = SOAPBinding.SOAP11HTTP_BINDING;
		String orgSrc = str1;
        boolean mtomSetting;

     	try {
    		
               // create a service
     	       Service svc = Service.create(serviceName);
     	       svc.addPort(portName,bindingID,endpointUrl);
     	       // debug
      			printMessageToConsole("- add Port: portName=" + portName + ",bindingID=" + bindingID + ",endpointURL=" + endpointUrl);
   	       
     	       // create dispatch<Source>
     			javax.xml.ws.Dispatch<Source> dispatch = null;
     			dispatch = svc.createDispatch(portName, Source.class, mode);
     	          
                        // force SOAPAction to match with wsdl action
                        ((BindingProvider)dispatch).getRequestContext().put(BindingProvider.SOAPACTION_USE_PROPERTY, true); 
                        ((BindingProvider)dispatch).getRequestContext().put(BindingProvider.SOAPACTION_URI_PROPERTY, "echoString");

     			//---------Client MTOM ON ----------
     			
     			//set MTOM on dispatch
     	        SOAPBinding binding = (SOAPBinding)dispatch.getBinding();
     	        binding.setMTOMEnabled(true);  

                // test isMTOMEnalbed:
                mtomSetting = binding.isMTOMEnabled();

                if (mtomSetting == true) {
                    System.out.println("mtomSetting (1)= True");
                }
                else
                    System.out.println("mtomSetting (1)= False");
     	  		    	 	   
         		// call server's invoke
      		    Source retVal = dispatch.invoke(Common.toSource(orgSrc));
      		    
      			// Common.toString - can only called once because it changes the Source
     			String s = Common.toString(retVal);
     			
     			if (s != null) {          			    			 
          	      if (s.equals(expectedStr1)){      			     				  
           			printMessageToConsole("Client MTOM ON - correct.");
           		 
     			  }
     			  else {
                       //debug
     				  System.out.println("Client MTOM ON - Received str is NOT correct.");       				

     			  }
     			}
     			
     			//---------- Client MTOM OFF ----------
     			
     			//set MTOM on dispatch
     	        binding.setMTOMEnabled(false);     

                // test isMTOMEnalbed:
                mtomSetting = binding.isMTOMEnabled();

                if (mtomSetting == true) {
                    System.out.println("mtomSetting (2)= True");
                }
                else
                    System.out.println("mtomSetting (2)= False");
     	  		    	 	   
         		// call server's invoke
      		    retVal = dispatch.invoke(Common.toSource(orgSrc));
      		    
      			// Common.toString - can only called once because it changes the Source
     			s = Common.toString(retVal);
     			
     			
     			if (s != null) {          			    			 
          	      if (s.equals(expectedStr1)){      			     				  
           			printMessageToConsole("Client MTOM OFF - correct.");
           		 
     			  }
     			  else {
                       //debug
     				  System.out.println("Client MTOM OFF - Received str is NOT correct.");       				

     			  }
     			}
     			
     			//---------- Client MTOM ON ----------
     			
     			//set MTOM on dispatch     	        
     	        binding.setMTOMEnabled(true);     			    					   
     	  		    	 	   
                // test isMTOMEnalbed:
                mtomSetting = binding.isMTOMEnabled();

                if (mtomSetting == true) {
                    System.out.println("mtomSetting (3) = True");
                }
                else
                    System.out.println("mtomSetting (3) = False");

         		// call server's invoke
      		    retVal = dispatch.invoke(Common.toSource(orgSrc));
      		    
      			// Common.toString - can only called once because it changes the Source
     			s = Common.toString(retVal);
     			     			
     			if (s != null) {          			    			 
          	      if (s.equals(expectedStr1)){
           			printMessageToConsole("Client MTOM ON - correct.");           		 
      				sendBackStr = goodResult;
     			  }
     			  else {
                       //debug
     				System.out.println("Client MTOM ON - Received str is NOT correct.");
       				sendBackStr = badResult;

     			  }
     			}
    			
     		} catch (Exception ex) {
     		   System.out.println("Client failed with exception.");	
     		   ex.printStackTrace();     
     		}
     		     		
     		return sendBackStr;
     }
     
    //============================
    public static void printMessageToConsole(String msg) {
		 System.out.println("At client side:\n" + msg);
	
    }
    
}
	   
	