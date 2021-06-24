//
// @(#) 1.2 autoFVT/src/mtom/mixedsetting/wsfvt/client/ClientON_MixedSetting_ProxyNoTypeClient.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 1/23/07 10:47:43 [8/8/12 06:58:16]
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
// 09/25/06 jtnguyen    LIDB3402-07.02  New File
// 01/22/07 jtnguyen    416083          Changed package name to lower case

package mtom.mixedsetting.wsfvt.client;

import java.awt.Image;
import java.io.*;
import java.util.Map;


import javax.imageio.ImageIO;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.BindingProvider;

import org.test.mtom.mtomoffproxynotype.*;  

/**
 * This testcase uses a JAXB generated objects
 * This testcase covers the empty mime type in WSDL 
 * using Proxy
 * Server has MTOM set to OFF with SOAP11
 * Client has MTOM set to ON
 */


public class ClientON_MixedSetting_ProxyNoTypeClient {

	public static final String goodResult = "Message processed";
	public static final String badResult = "Problem in processing message";
       
	String xmlFilename = "catalog.xml";  //about 31KB in size, has NO xml header
	String jpegFilename1 = "image1.jpeg";  //about 10KB
	String jpegOptimizedFilename1 = "image1Expected.jpeg";
 
	String jpegFilename2 = "image2.jpeg";  // about 60KB
	String jpegOptimizedFilename2 = "image2Expected.jpeg";
	String jpegByteArrayImageFilename2 = "image2ByteArray.jpeg";
	String jpegByteArrayReceivedFilename2 = "ReceivedByteArrayFile.jpeg";
	
	
    String sendBackStr = null;

    QName serviceName = new QName("http://ws.apache.org/axis2", "MtomOffProxyNoTypeService");
    QName portName    = new QName("http://ws.apache.org/axis2", "AttachmentServicePort");

    String endpointUrl = "http://@REPLACE_WITH_HOST_NAME@:"
                          + "@REPLACE_WITH_PORT_NUM@/mtommixedsetting/MtomOffProxyNoTypeService";

    Service.Mode mode = Service.Mode.PAYLOAD;

	public static void main(String[] args) {
		
    }

	/**
	 * This testcase uses a JAXWS Proxy with JAXB generated request object
	 * as parameter. 
	 * 
	 * - Service has MTOM disabled
	 * - sends small image (12K)
     * - mode = PAYLOAD
 	 * - no setting for SOAP version
     * @return returned from the remote method
     */

    public String Proxy_AttachmentSmallJpeg() throws Exception {
    	
      	
   	   // debug
   	   System.out.println("\n*** In ClientON_MixedSetting_ProxyNoTypeClient.Proxy_AttachmentSmallJpeg ***\n");  	   
     	
     	try {
     		        	
    	    File file = new File(jpegFilename1);
    	    System.out.println(">> Loading data from " + jpegFilename1);

    	    Image image = ImageIO.read(file);
        	byte[] content = AttachmentHelper.getImageBytes(image, "image/jpeg");
           
            //Set the data inside of the appropriate object
            ImageDepot imageDepot = new ObjectFactory().createImageDepot();
            imageDepot.setImageData(content);
            
          	
            if (imageDepot != null) {
            	System.out.println("Data loaded = " + imageDepot.getImageData().toString());
            }
            else {
            	System.out.println("[ERROR] Could not load data");
            	System.exit(-1);
            }
 
            //Setup the necessary JAX-WS artifacts
            Service svc = Service.create(serviceName);
            ImageServiceInterface proxy = svc.getPort(portName, ImageServiceInterface.class);
 
            //Set the target URL
            BindingProvider bp = (BindingProvider) proxy;
            Map<String, Object> requestCtx = bp.getRequestContext();
            requestCtx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl);
             
            //Disable MTOM
            SOAPBinding binding = (SOAPBinding) bp.getBinding();
            binding.setMTOMEnabled(true);
             
            //Send the image and process the response image
            ImageDepot response = proxy.sendImage(imageDepot);
            if (response != null) {
            	System.out.println("-- Response received");
            	System.out.println("-- Writing returned image to file.");
            	
            	byte[] output = response.getImageData();
            
                   File f= null;
                   if (output != null) {
                       // write to current directory, whatever it is
                         f = new File("ReceivedFile.jpeg");
                         if (f.exists()) {
                               f.delete();
                         }
                   }


                     FileOutputStream fos = new FileOutputStream(f);
                     fos.write(output);
                     fos.close();
 
                   // open received file and compare to the file that was sent
                   Image imageReceived = ImageIO.read(new File ("ReceivedFile.jpeg"));
                   Image imageSent = ImageIO.read(new File (jpegFilename1));
                   Image optImage = ImageIO.read(new File (jpegOptimizedFilename1));
                   
             if (AttachmentHelper.compareImages(imageReceived,imageSent)== true){       	
                   		// debug
               			printMessageToConsole("Image received equals to image sent");
           				sendBackStr = goodResult;
           			}
           	 else if (AttachmentHelper.compareImages(imageReceived,optImage)== true){       	
            		// debug
         			printMessageToConsole("Image received equals to optimized image");
         			sendBackStr = goodResult;
         	 } else {
                     // debug
           			printMessageToConsole("Images not equal:returned image and image sent"); 	
           				
           				sendBackStr = badResult;
           	  }
        		
            }
            else {
            	System.out.println(">> [ERROR] - Response from the server was NULL");
            }
      
          
 	   } catch (Exception ex) {
 		   System.out.println("Proxy_AttachmentSmallJpeg Client failed with exception.");	
 		   ex.printStackTrace();     
 		}
 		
 	   return sendBackStr;
     }

    /**
     * This testcase uses a JAXB generated objects
     * This testcase covers the empty mime type in WSDL, 
     * using Proxy
     * Server has MTOM set to OFF with SOAP11
     * Client has MTOM set to ON
     * Sends a large jpeg image
     */

    public String Proxy_AttachmentLargeJpeg() throws Exception { 	
          	
        	   // debug
        	   System.out.println("\n*** In ClientON_MixedSetting_ProxyNoTypeClient.Proxy_AttachmentLargeJpeg ***\n");  	   
          	
          	try {
          		        	
         	    File file = new File(jpegFilename2);
         	    System.out.println(">> Loading data from " + jpegFilename2);

         	    Image image = ImageIO.read(file);
             	byte[] content = AttachmentHelper.getImageBytes(image, "image/jpeg");
                
                 //Set the data inside of the appropriate object
                 ImageDepot imageDepot = new ObjectFactory().createImageDepot();
                 imageDepot.setImageData(content);
                 
               	
                 if (imageDepot != null) {
                 	System.out.println("Data loaded = " + imageDepot.getImageData().toString());
                 }
                 else {
                 	System.out.println("[ERROR] Could not load data");
                 	System.exit(-1);
                 }
      
                 //Setup the necessary JAX-WS artifacts
                 Service svc = Service.create(serviceName);
                 ImageServiceInterface proxy = svc.getPort(portName, ImageServiceInterface.class);
      
                 //Set the target URL
                 BindingProvider bp = (BindingProvider) proxy;
                 Map<String, Object> requestCtx = bp.getRequestContext();
                 requestCtx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl);
                  
                 //Disable MTOM
                 SOAPBinding binding = (SOAPBinding) bp.getBinding();
                 binding.setMTOMEnabled(false);
                  
                 //Send the image and process the response image
                 ImageDepot response = proxy.sendImage(imageDepot);
                 if (response != null) {
                 	System.out.println("-- Response received.  Writing returned image to file.");
                 	
                 	byte[] output = response.getImageData();
                 
                        File f= null;
                        if (output != null) {
                            // write to current directory, whatever it is
                              f = new File(jpegByteArrayReceivedFilename2);
                              if (f.exists()) {
                                    f.delete();
                              }
                        }


                          FileOutputStream fos = new FileOutputStream(f);
                          fos.write(output);
                          fos.close();
      
                        // open received file and compare to the file that was sent
                        Image imageReceived = ImageIO.read(new File (jpegByteArrayReceivedFilename2));
                        Image imageSent = ImageIO.read(new File (jpegFilename2));
                         Image byteArrayImage = ImageIO.read(new File (jpegByteArrayImageFilename2));
                        
                     if (AttachmentHelper.compareImages(imageReceived,imageSent)== true){       	
                        		// debug
                    			printMessageToConsole("Image received equals to image sent");
                				sendBackStr = goodResult;
                	 }
                     else if (AttachmentHelper.compareImages(imageReceived,byteArrayImage)== true){       	
                  		// debug
               			printMessageToConsole("Image received equals to byteArray image");
               			sendBackStr = goodResult;

                	 } else {
                          // debug
                			printMessageToConsole("Images not equal:returned image and image sent"); 	
                				
                				sendBackStr = badResult;
                	  }
             		
                 }
                 else {
                 	System.out.println(">> [ERROR] - Response from the server was NULL");
                 }
           
               
      	   } catch (Exception ex) {
      		   System.out.println("Proxy_AttachmentLargeJpeg Client failed with exception.");	
      		   ex.printStackTrace();     
      		}
      		
      	   return sendBackStr;
          }


 
    //============================================================
    // Utilites
    //============================================================
    public static void printMessageToConsole(String msg) {
		 System.out.println("At client side:\n" + msg);
	
    }
   

}
