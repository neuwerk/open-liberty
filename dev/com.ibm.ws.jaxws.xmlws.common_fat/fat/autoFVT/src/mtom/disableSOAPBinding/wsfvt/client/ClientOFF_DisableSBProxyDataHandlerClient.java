//
// @(#) 1.2 autoFVT/src/mtom/disableSOAPBinding/wsfvt/client/ClientOFF_DisableSBProxyDataHandlerClient.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 1/23/07 10:43:56 [8/8/12 06:58:07]
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

package mtom.disableSOAPBinding.wsfvt.client;

import java.awt.Image;
import java.io.*;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.imageio.ImageIO;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.BindingProvider;

import org.test.mtom.disablesbproxy_datahandler.*;

/**
 * This testcase uses a JAXB generated objects
 * This testcase covers the "multipart/*" mime types, using Proxy.
 * Sevice has MTOM=OFF
 * Client has MTOM=OFF
 */


public class ClientOFF_DisableSBProxyDataHandlerClient {

	public static final String goodResult = "Message processed";
	public static final String badResult = "Problem in processing message";
       
 	String xmlFilename = "catalog.xml";  //about 31KB in size, has NO xml header
 
	String jpegFilename1 = "image1.jpeg";  //about 10KB
	String jpegOptimizedFilename1 = "image1Expected.jpeg";
 	String jpegFilename2 = "image2.jpeg";  // about 60KB
	String jpegOptimizedFilename2 = "image2Expected.jpeg";

	String jpegFilename3 = "image3.jpeg";
	String jpegOptimizedFilename3 = "image3Expected.jpeg";
	
	
    String sendBackStr = null;

    public static final QName serviceName = new QName("http://ws.apache.org/axis2", "DisableSBProxyDataHandlerService");
    public static final QName portName    = new QName("http://ws.apache.org/axis2", "AttachmentServicePort");
    public static final String endpointUrl = "http://@REPLACE_WITH_HOST_NAME@:"
                          + "@REPLACE_WITH_PORT_NUM@/disablesoapbinding/DisableSBProxyDataHandlerService";

       Service.Mode mode = Service.Mode.PAYLOAD;



	public static void main(String[] args) {
		
    }

	/**
	 * This testcase uses a JAXWS Proxy with JAXB generated request object
	 * as parameter. 
	 * 
	 * - client has no setting for MTOM
	 * - sends small image (12K)
     * - mode = PAYLOAD
     * @return returned from the remote method
     */

    public String Proxy_AttachmentSmallJpeg() throws Exception {
    	
      	
   	   // debug
   	   System.out.println("\n*** In ClientOFF_DisableSBProxyDataHandlerClient.Proxy_AttachmentSmallJpeg ***\n");  	   
     	
     	try {
     		        	
    	    File file = new File(jpegFilename1);
    	    System.out.println(">> Loading data from " + jpegFilename1);
    	    FileDataSource fds = new FileDataSource(file);
    	    DataHandler content = new DataHandler(fds);
           
            //Set the data inside of the appropriate object
            ImageDepot imageDepot = new ObjectFactory().createImageDepot();
            imageDepot.setImageData(content);
          	
            if (imageDepot != null) {
            	System.out.println("Data loaded.");

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
             
            //Enable MTOM
            SOAPBinding binding = (SOAPBinding) bp.getBinding();
            binding.setMTOMEnabled(false);
             
            //Send the image and process the response image
            ImageDepot response = proxy.sendImage(imageDepot);
            if (response != null) {
            	System.out.println("-- Response received");
            	System.out.println("-- Writing returned image to file.");
            	
            	DataHandler dh = response.getImageData();
            
                   File f= null;
                   if (dh != null) {
                       // write to current directory, whatever it is
                         f = new File("ReceivedFile.jpeg");
                         if (f.exists()) {
                               f.delete();
                         }
                   }


                     FileOutputStream fos = new FileOutputStream(f);
                     dh.writeTo(fos);
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

    public String Proxy_AttachmentLargeJpeg() throws Exception {
    	
      	
    	   // debug
    	   System.out.println("\n*** In ClientOFF_DisableSBProxyDataHandlerClient.Proxy_AttachmentLargeJpeg ***\n");  	   
      	
      	try {
      		        	
     	    File file = new File(jpegFilename2);
     	    System.out.println(">> Loading data from " + jpegFilename2);
     	    FileDataSource fds = new FileDataSource(file);
     	    DataHandler content = new DataHandler(fds);
            
             //Set the data inside of the appropriate object
             ImageDepot imageDepot = new ObjectFactory().createImageDepot();
             imageDepot.setImageData(content);
           	
             if (imageDepot != null) {
             	System.out.println("Data loaded.");

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
              
             //Enable MTOM
             SOAPBinding binding = (SOAPBinding) bp.getBinding();
             binding.setMTOMEnabled(false);
              
             //Send the image and process the response image
             ImageDepot response = proxy.sendImage(imageDepot);
             if (response != null) {
             	System.out.println("-- Response received");
             	System.out.println("-- Writing returned image to file.");
             	
             	DataHandler dh = response.getImageData();
             
                    File f= null;
                    if (dh != null) {
                        // write to current directory, whatever it is
                          f = new File("ReceivedFile.jpeg");
                          if (f.exists()) {
                                f.delete();
                          }
                    }


                      FileOutputStream fos = new FileOutputStream(f);
                      dh.writeTo(fos);
                      fos.close();
  
                    // open received file and compare to the file that was sent
                    Image imageReceived = ImageIO.read(new File ("ReceivedFile.jpeg"));
                    Image imageSent = ImageIO.read(new File (jpegFilename2));
                    Image optImage = ImageIO.read(new File (jpegOptimizedFilename2));
                    
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
