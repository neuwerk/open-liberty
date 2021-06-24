//
// @(#) 1.3 autoFVT/src/mtom/enableJAXB/wsfvt/client/EnableJAXBProxyImageJpegClient.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 2/13/07 14:30:42 [8/8/12 06:58:10]
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
// 02/13/07 jtnguyen    420519          Change port # to @REPLACE_WITH_PORT_NUM@

package mtom.enableJAXB.wsfvt.client;

import java.awt.Image;
import java.io.*;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.BindingProvider;

import org.test.mtom.enablejaxbproxy_imagejpeg.*;

/**
 * This testcase uses a JAXB generated objects
 * and covers the "image/jpeg" mime types, using Proxy.
 * MTOM is enabled in this client.  Service has default setting for MTOM,
 * e.g, no setting via annotation.
 */



public class EnableJAXBProxyImageJpegClient {

	public static final String goodResult = "Message processed";
	public static final String badResult = "Problem in processing message";
       
	String plainStr = "test input";
	
	// short string in XML format
	String s1 = "<invoke>test input</invoke>";
	
	// long string of 10KB

	String xmlFilename = "catalog.xml";  //about 31KB in size, has NO xml header

	String jpegFilename1 = "image1.jpeg";  //about 10KB
	String jpegOptimizedFilename1 = "image1Expected.jpeg";
	String jpegFilename2 = "image2.jpeg";  // about 60KB
	String jpegOptimizedFilename2 = "image2Expected.jpeg";
	String jpegFilename3 = "image3.jpeg";
	String jpegOptimizedFilename3 = "image3Expected.jpeg";
	
	
    String sendBackStr = null;

    public static final QName serviceName = new QName("http://ws.apache.org/axis2", "EnableJAXBProxyImageJpegService");
    public static final QName portName    = new QName("http://ws.apache.org/axis2", "AttachmentServicePort");
    public static final String endpointUrl = "http://@REPLACE_WITH_HOST_NAME@:"
                          + "@REPLACE_WITH_PORT_NUM@/enablejaxb/EnableJAXBProxyImageJpegService";
    
    String bindingID = SOAPBinding.SOAP11HTTP_BINDING;

       Service.Mode mode = Service.Mode.PAYLOAD;



	public static void main(String[] args) {
		
    }

	/**
	 * This testcase uses a JAXWS Proxy with JAXB generated request object
	 * as parameter. 
	 * 
	 * - Service has no MTOM setting
	 * - sends small image (12K)
         * - mode = PAYLOAD
 	 * - setMTOMEnabled = true 
         * @return returned from the remote method
     */

    public String Proxy_AttachmentSmallJpeg() throws Exception {
    	
      	
   	   // debug
   	   System.out.println("\n*** In EnableJAXBProxyImageJpegClient.Proxy_AttachmentSmallJpeg ***\n");  	   
     	
     	try {
     		        	
    	    System.out.println(">> Loading data from " + jpegFilename1);

            Image image = ImageIO.read (new File (jpegFilename1));
            Image optImage = ImageIO.read (new File (jpegOptimizedFilename1));         	
        	
          	ImageDepot imageDepot = new ObjectFactory().createImageDepot();
          	imageDepot.setImageData(image);
            
             //Create a request bean with imagedepot bean as value
             ObjectFactory factory = new ObjectFactory();
             SendImage request = factory.createSendImage();
             request.setInput(imageDepot);
                       	
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
            binding.setMTOMEnabled(true);
             
            //Send the image and process the response image
           
            ImageDepot response = proxy.sendImage(imageDepot);
            Image imageReceived = response.getImageData();
               
             if (AttachmentHelper.compareImages(imageReceived,image)== true){       	
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
        		      
          
 	   } catch (Exception ex) {
 		   System.out.println("Proxy_AttachmentSmallJpeg Client failed with exception.");	
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
