//
// @(#) 1.3 autoFVT/src/mtom/defaultsetting/wsfvt/client/JAXBImageJpegClient.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/19/07 13:01:26 [8/8/12 06:58:06]
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
// 02/13/07 jtnguyen    420519          Change port # to @REPLACE_WITH_PORT_NUM@
// 04/18/07 jtnguyen    433294          Improved error logging


package mtom.defaultsetting.wsfvt.client;

import java.awt.Image;
import java.io.*;
import javax.imageio.ImageIO;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;

import org.test.mtom.imagejpeg.*;


/**
 * This testcase uses a JAXWS Dispatch invocation with JAXB generated request object via Mode = payload.
 * The service uses default setting for MTOM (no setting for MTOM).
 * The client also uses default setting for MTOM via bindingID=null.
 * This testcase covers the "image/jpeg" mime type.
 */
public class JAXBImageJpegClient {

    public static final String goodResult = "Message processed";
   
    public static final String jpegFilename1 = "image1.jpeg";
    public static final String jpegFilename2 = "image2.jpeg";
    public static final String jpegOptFilename2 = "image2Expected.jpeg";

    public static final QName serviceName = new QName("http://ws.apache.org/axis2", "JAXBImageJpegService");
    public static final QName portName    = new QName("http://ws.apache.org/axis2", "AttachmentServicePort");
    public static final String endpointUrl = "http://@REPLACE_WITH_HOST_NAME@:"
                          + "@REPLACE_WITH_PORT_NUM@/mtomdefaultsetting/JAXBImageJpegService";
    
    public static final String bindingID = null;    
    public static final Service.Mode mode = Service.Mode.PAYLOAD;

    public static void main(String[] args) throws Exception {
	
        JAXBImageJpegClient client = new JAXBImageJpegClient();
        System.out.println("Result 1:\n" + client.JAXB_AttachmentSmallJpeg());
        System.out.println("Result 2:\n" + client.JAXB_AttachmentLargeJpeg());

    }

    /**
      * sends a small image (12K)
      * @return returned from the remote method
      */

    public String JAXB_AttachmentSmallJpeg() {
    	
   	   System.out.println("\n*** In JAXBImageJpegClient.JAXB_AttachmentSmallJpeg ***\n");  	   
           String sendBackStr = null;

           // create a dynamic service & port
           Service svc = Service.create(serviceName);
           svc.addPort(portName,bindingID,endpointUrl); 	       
           System.out.println("portname = " + portName 
                     + ",bindingID = " + bindingID + ",endpointUrl = " + endpointUrl);
     
           JAXBContext jbc = null;
           try {		
                 // jaxb artifacts package
                 jbc = JAXBContext.newInstance("org.test.mtom.imagejpeg");
                       
           } catch (JAXBException jaxbe){
           }

         
           Dispatch<Object> dispatch = svc.createDispatch(portName, jbc, mode);
        
          Image image = null;
          try {  
          
            image = ImageIO.read (new File (jpegFilename1));
          	
          } catch (IOException ioe){
          }
    	
      	 ImageDepot imageDepot = new ObjectFactory().createImageDepot();
      	 imageDepot.setImageData(image);
        
         //Create a request bean with imagedepot bean as value
         ObjectFactory factory = new ObjectFactory();
         Invoke request = factory.createInvoke();
         request.setInput(imageDepot);
        
         SendImageResponse response = (SendImageResponse) dispatch.invoke(request);               
         Image retVal = response.getOutput().getImageData();

         try {  
  	 
             if (AttachmentHelper.compareImages(retVal,image)== true){       	          		
      			System.out.println("Image received equals to image sent");
  		        sendBackStr = goodResult;  			
             }
  	     else {                            
                 System.out.println("**ERROR - Received image does not match the image sent.");
                 sendBackStr = "**ERROR - Received image does not match expected image.";
             }          
       
         } catch (IOException ioe2) {       
         }
 		
         return sendBackStr;
     }
	
    /**
     * - sends large image (68K)
     * @return returned from the remote method
     */

    public String JAXB_AttachmentLargeJpeg(){    	
    	
       System.out.println("\n*** In JAXBImageJpegClient.JAXB_AttachmentLargeJpeg ***\n");  	         	
       String sendBackStr = null;
           
       // create a Dynamic service & port
       Service svc = Service.create(serviceName);
       svc.addPort(portName,bindingID,endpointUrl); 	       
       System.out.println("portname = " + portName 
                 + ",bindingID = " + bindingID + ",endpointUrl = " + endpointUrl);
      		          
       JAXBContext jbc = null;
       try {		
              // jaxb artifacts package
              jbc = JAXBContext.newInstance("org.test.mtom.imagejpeg");

       } catch (JAXBException jaxbe){
       }
    
       Dispatch<Object> dispatch = svc.createDispatch(portName, jbc, mode);

       Image image = null;
       try {  

         image = ImageIO.read (new File (jpegFilename2));

       } catch (IOException ioe){
       }
     	
       	ImageDepot imageDepot = new ObjectFactory().createImageDepot();
       	imageDepot.setImageData(image);
         
          //Create a request bean with imagedepot bean as value
          ObjectFactory factory = new ObjectFactory();
          Invoke request = factory.createInvoke();
          request.setInput(imageDepot);
         
          SendImageResponse response = (SendImageResponse) dispatch.invoke(request);               
          Image retVal = response.getOutput().getImageData();
          
          // only used with large image?
          /* Do not delete.
           * This code is used to create the expected image.  We need to run only once, then save
           * the temp image to client directory under the name image1Expected.jpeg
           * to be used in comparison for subsequent tests. 
          
          byte[] recB = AttachmentHelper.getImageBytes(retVal,"image/jpeg");
          File outFile = new File("c:/temp/outFile.jpeg");
          DataOutputStream dO = new DataOutputStream(new FileOutputStream(outFile));
          // write received image to file
          dO.write(recB);
          dO.flush();
          dO.close();
          */
           
          // image expected         
          Image imageOpt = null;
          try {  
          
            
              imageOpt = ImageIO.read (new File (jpegOptFilename2));
              if (AttachmentHelper.compareImages(retVal,image)== true){       	          		
                  System.out.println("Image received equals to image sent");
                  sendBackStr = goodResult;  			
              } else if (AttachmentHelper.compareImages(retVal,imageOpt)== true){                             
                  System.out.println("Image received equals to optimized image");
                  sendBackStr = goodResult;
              } else {
                  System.out.println("**ERROR - Received image does not match the image sent.");
                  sendBackStr = "**ERROR - Received image does not match expected image.";
              }          

          } catch (IOException ioe) {       
          }
    	
          return sendBackStr;

     }
    
}
