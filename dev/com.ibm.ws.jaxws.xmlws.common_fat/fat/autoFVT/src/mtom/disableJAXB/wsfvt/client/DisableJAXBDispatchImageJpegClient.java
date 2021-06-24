//
// @(#) 1.3 autoFVT/src/mtom/disableJAXB/wsfvt/client/DisableJAXBDispatchImageJpegClient.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 2/25/07 20:38:29 [8/8/12 06:58:03]
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
// 02/25/07 jtnguyen    421977          Removed soap12 methods (can't use with soap 11 service endpoint)

package mtom.disableJAXB.wsfvt.client;

import java.awt.Image;
import java.io.*;
import javax.imageio.ImageIO;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

import org.test.mtom.disablejaxbdispatch_imagejpeg.*;

/**
 * This testcase uses a JAXB generated objects.
 * - covers the "image/jpeg" MIME type using Dispatch client.
 * - service has MTOM = default (no setting) 
 * - exclusively disables MTOM using binding.setMTOMEnabled(false);
 */

public class DisableJAXBDispatchImageJpegClient {

	public static final String goodResult = "Message processed";
	public static final String badResult = "Problem in processing message";
       
	String jpegFilename1 = "image1.jpeg";
	String jpegFilename2 = "image2.jpeg";
	String jpegFilename3 = "image3.jpeg";
    String jpegExpectedFilename2 = "image2Expected.jpeg";

    String sendBackStr = null;

    public static final QName serviceName = new QName("http://ws.apache.org/axis2", "MTOMDefaultDispatchImageJpegService");
    public static final QName portName    = new QName("http://ws.apache.org/axis2", "AttachmentServicePort");
    public static final String endpointUrl = "http://@REPLACE_WITH_HOST_NAME@:"
                          + "@REPLACE_WITH_PORT_NUM@/disablejaxb/MTOMDefaultDispatchImageJpegService";
       
    public static final Service.Mode mode = Service.Mode.PAYLOAD;

	public static void main(String[] args) {
		
    }

	/**
	 * This testcase uses a JAXWS Dispatch invocation with JAXB generated request object
	 * as parameter. The endpoint for these testcase is a JAXWS Source Provider.
	 * 
	 * - sends small image (12K)
     * - sets for MTOM with SOAP11 using 2 steps:  bindingID and setMTOMEnabled
     * - mode = PAYLOAD
      * @return returned from the remote method
     */

    public String JAXB_AttachmentSmallJpeg() throws Exception {
    	
      	
   	   // debug
   	   System.out.println("\n*** In DisableJAXBDispatchImageJpegClient.JAXB_AttachmentSmallJpeg ***\n");  	   
     	
       String bindingID = SOAPBinding.SOAP11HTTP_BINDING;

     	try {
           // create a Dynamic service & port
     	       Service svc = Service.create(serviceName);
  	       svc.addPort(portName,bindingID,endpointUrl); 	       
  	       // debug
  	       System.out.println("portname = " + portName 
  	    		 + ",bindingID = " + bindingID + ",endpointUrl = " + endpointUrl);
     		
         
         // jaxb artifacts package
         JAXBContext jbc = JAXBContext.newInstance("org.test.mtom.disablejaxbdispatch_imagejpeg");
               
         Dispatch<Object> dispatch = svc
                 .createDispatch(portName, jbc, mode);
         
         // set MTOM on dispatch
         SOAPBinding binding = (SOAPBinding)dispatch.getBinding();
         binding.setMTOMEnabled(false);
      
        Image image = ImageIO.read (new File (jpegFilename1));
      	
    	
      	ImageDepot imageDepot = new ObjectFactory().createImageDepot();
      	imageDepot.setImageData(image);
        
         //Create a request bean with imagedepot bean as value
         ObjectFactory factory = new ObjectFactory();
         Invoke request = factory.createInvoke();
         request.setInput(imageDepot);
        
         SendImageResponse response = (SendImageResponse) dispatch.invoke(request);               
         Image retVal = response.getOutput().getImageData();

        
  	 if (AttachmentHelper.compareImages(retVal,image)== true){       	
          		// debug
      			printMessageToConsole("Image received equals to image sent");
  				sendBackStr = goodResult;
  			}
  			else {
                // debug
  				printMessageToConsole("Images not equal:returned image and image sent"); 	
  				
  				sendBackStr = badResult;
  			}
          
 	   } catch (Exception ex) {
 		   System.out.println("Client failed with exception.");	
 		   ex.printStackTrace();     
 		}
 		
 		
 		return sendBackStr;
     }
    

	/**
	 * This testcase uses a JAXWS Dispatch invocation with JAXB generated request object
	 * as parameter. The endpoint for these testcase is a JAXWS Source Provider.
	 * 
	 * - sends large image (68K)
         * - sets for MTOM with SOAP11 using 2 steps:  
             - first set MTOM=enabled using bindingID=SOAPBinding.SOAP11HTTP_MTOM_BINDING 
             - then set MTOM=disabled using setMTOMEnabled(false)
         * - mode = PAYLOAD
         * @return returned from the remote method
         */

    public String JAXB_doubleSets_AttachmentLargeJpeg() throws Exception {
    	
     	
    	// debug
    	System.out.println("\n*** In DisableJAXBDispatchImageJpegClient.JAXB_AttachmentLargeJpeg ***\n");  	   
    	String bindingID = SOAPBinding.SOAP11HTTP_MTOM_BINDING;

      	try {
           // create a Dynamic service & port
     	       Service svc = Service.create(serviceName);
  	       svc.addPort(portName,bindingID,endpointUrl); 	       
   	       // debug
   	       System.out.println("portname = " + portName 
   	    		 + ",bindingID = " + bindingID + ",endpointUrl = " + endpointUrl);
      		
          
          // jaxb artifacts package
          JAXBContext jbc = JAXBContext.newInstance("org.test.mtom.disablejaxbdispatch_imagejpeg");
                
          Dispatch<Object> dispatch = svc
                  .createDispatch(portName, jbc, mode);
                  
          // set MTOM on dispatch
          SOAPBinding binding = (SOAPBinding)dispatch.getBinding();
          binding.setMTOMEnabled(false);
        
         Image image = ImageIO.read (new File (jpegFilename2));
       	
     	
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
           *    This code is used to create the expected image to run only once.
           *    The image was optimized by MTOM process.  It is now saved back to client directory under the name 
           *    image1Expected.jpeg
           *    to be compared to in the test. 
          
          byte[] recB = AttachmentHelper.getImageBytes(retVal,"image/jpeg");
          File outFile = new File("c:/temp/outFile.jpeg");
          DataOutputStream dO = new DataOutputStream(new FileOutputStream(outFile));
          // write received image to file
          dO.write(recB);
          dO.flush();
          dO.close();
*/
           
          
          // image expected - this image was optimized
          Image imageExpected = ImageIO.read (new File (jpegExpectedFilename2));
          
          

         
   	 if (AttachmentHelper.compareImages(retVal,image)== true){       	
           		// debug
       			printMessageToConsole("Image received equals to image sent");
   				sendBackStr = goodResult;
   			}
   	 else if (AttachmentHelper.compareImages(retVal,imageExpected)== true){       	
    		// debug
			printMessageToConsole("Image received equals to optimized image");
			sendBackStr = goodResult;
		}

   			else {
                 // debug
   				printMessageToConsole("Images not equal:returned image and image sent"); 	
   				
   				sendBackStr = badResult;
   			}
           
  	   } catch (Exception ex) {
  		   System.out.println("Client failed with exception.");	
  		   ex.printStackTrace();     
  		}
  		
  		
  		return sendBackStr;
     }

    	/**
	 * This testcase uses a JAXWS Dispatch invocation with JAXB generated request object
	 * as parameter. The endpoint for these testcase is a JAXWS Source Provider.
	 * 
	 * - sends large image (68K)
     * - sets for MTOM with SOAP11 using 2 steps:  bindingID and setMTOMEnabled
     * - mode = PAYLOAD
     * @return returned from the remote method
     */

    public String JAXB_AttachmentLargeJpeg() throws Exception {
    	
     	
    	// debug
    	System.out.println("\n*** In DisableJAXBDispatchImageJpegClient.JAXB_AttachmentLargeJpeg ***\n");  	   
    	String bindingID = SOAPBinding.SOAP11HTTP_BINDING;

      	try {
           // create a Dynamic service & port
     	       Service svc = Service.create(serviceName);
  	       svc.addPort(portName,bindingID,endpointUrl); 	       
   	       // debug
   	       System.out.println("portname = " + portName 
   	    		 + ",bindingID = " + bindingID + ",endpointUrl = " + endpointUrl);
      		
          
          // jaxb artifacts package
          JAXBContext jbc = JAXBContext.newInstance("org.test.mtom.disablejaxbdispatch_imagejpeg");
                
          Dispatch<Object> dispatch = svc
                  .createDispatch(portName, jbc, mode);
                  
          // set MTOM on dispatch
          SOAPBinding binding = (SOAPBinding)dispatch.getBinding();
          binding.setMTOMEnabled(false);
        
         Image image = ImageIO.read (new File (jpegFilename2));
       	
     	
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
           *    This code is used to create the expected image to run only once.
           *    The image was optimized by MTOM process.  It is now saved back to client directory under the name 
           *    image1Expected.jpeg
           *    to be compared to in the test. 
          
          byte[] recB = AttachmentHelper.getImageBytes(retVal,"image/jpeg");
          File outFile = new File("c:/temp/outFile.jpeg");
          DataOutputStream dO = new DataOutputStream(new FileOutputStream(outFile));
          // write received image to file
          dO.write(recB);
          dO.flush();
          dO.close();
*/
           
          
          // image expected - this image was optimized
          Image imageExpected = ImageIO.read (new File (jpegExpectedFilename2));
          
          

         
   	 if (AttachmentHelper.compareImages(retVal,image)== true){       	
           		// debug
       			printMessageToConsole("Image received equals to image sent");
   				sendBackStr = goodResult;
   			}
   	 else if (AttachmentHelper.compareImages(retVal,imageExpected)== true){       	
    		// debug
			printMessageToConsole("Image received equals to optimized image");
			sendBackStr = goodResult;
		}

   			else {
                 // debug
   				printMessageToConsole("Images not equal:returned image and image sent"); 	
   				
   				sendBackStr = badResult;
   			}
           
  	   } catch (Exception ex) {
  		   System.out.println("Client failed with exception.");	
  		   ex.printStackTrace();     
  		}
  		
  		
  		return sendBackStr;
     }


    public String JAXB_Sequence_SmallJpeg() throws Exception {
    	
      	
   	   // debug
   	   System.out.println("\n*** In DisableJAXBDispatchImageJpegClient.JAXB_Sequence_SmallJpeg ***\n");  	   
     	


     	try {
   	      String bindingID = SOAPBinding.SOAP11HTTP_BINDING;

           // create a Dynamic service & port
     	       Service svc = Service.create(serviceName);
  	       svc.addPort(portName,bindingID,endpointUrl); 	       
  	       // debug
  	       System.out.println("portname = " + portName 
  	    		 + ",bindingID = " + bindingID + ",endpointUrl = " + endpointUrl);
     		
         
         // jaxb artifacts package
         JAXBContext jbc = JAXBContext.newInstance("org.test.mtom.disablejaxbdispatch_imagejpeg");
               
         Dispatch<Object> dispatch = svc
                 .createDispatch(portName, jbc, mode);
         
         // set MTOM on dispatch
         SOAPBinding binding = (SOAPBinding)dispatch.getBinding();
         binding.setMTOMEnabled(true);
      
        Image image = ImageIO.read (new File (jpegFilename1));
      	
    	
      	ImageDepot imageDepot = new ObjectFactory().createImageDepot();
      	imageDepot.setImageData(image);
        
         //Create a request bean with imagedepot bean as value
         ObjectFactory factory = new ObjectFactory();
         Invoke request = factory.createInvoke();
         request.setInput(imageDepot);
        
         SendImageResponse response = (SendImageResponse) dispatch.invoke(request);               
         Image retVal = response.getOutput().getImageData();

        
  	 if (AttachmentHelper.compareImages(retVal,image)== true){       	
          		// debug
      			printMessageToConsole("Client MTOM ON - Image received equals to image sent");
  				//sendBackStr = goodResult;
  			}
  			else {
                // debug
  				printMessageToConsole("Client MTOM ON - Images not equal:returned image and image sent"); 	
  				
  				//sendBackStr = badResult;
  			}
  	 
  	 //--------------- Step 2 - MTOM off --------
         
     binding.setMTOMEnabled(false);

  	imageDepot = new ObjectFactory().createImageDepot();
  	imageDepot.setImageData(image);
    
     //Create a request bean with imagedepot bean as value
    response = (SendImageResponse) dispatch.invoke(request);               
    retVal = response.getOutput().getImageData();

    
	 if (AttachmentHelper.compareImages(retVal,image)== true){       	
      		// debug
  			printMessageToConsole("Client MTOM OFF - Image received equals to image sent");
				//sendBackStr = goodResult;
			}
			else {
            // debug
				printMessageToConsole("Client MTOM OFF - Images not equal:returned image and image sent"); 	
				
				//sendBackStr = badResult;
			}

  	 //--------------- Step 3 - MTOM on again --------
     
     binding.setMTOMEnabled(true);

  	imageDepot = new ObjectFactory().createImageDepot();
  	imageDepot.setImageData(image);
    
     //Create a request bean with imagedepot bean as value
    response = (SendImageResponse) dispatch.invoke(request);               
    retVal = response.getOutput().getImageData();

    
	 if (AttachmentHelper.compareImages(retVal,image)== true){       	
      		// debug
  			printMessageToConsole("Client MTOM ON - Image received equals to image sent");
		        sendBackStr = goodResult;
			}
			else {
            // debug
				printMessageToConsole("Client MTOM ON - Images not equal:returned image and image sent"); 	
				
				sendBackStr = badResult;
			}

 	   } catch (Exception ex) {
 		   System.out.println("Client failed with exception.");	
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
