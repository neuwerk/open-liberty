//
// %Z% %I% %W% %G% %U% [%H% %T%]
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
// 11/21/06 jtnguyen    407277          Fixed to build on Linux - removed a bad character
// 01/25/07 jtnguyen    410864          Removed invalid Message mode test
// 02/13/07 jtnguyen    420519          Change port # to @REPLACE_WITH_PORT_NUM@
// 04/18/07 jtnguyen    433294          Improved error logging

package mtom.defaultsetting.wsfvt.client;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import java.io.*;

import org.test.mtom.textplain.*;


/**
 * This testcase uses a JAXWS Dispatch invocation with JAXB generated request object.
 * The service uses default setting for MTOM (no setting for MTOM).
 * It covers the "text/plain" mime types.
 */

public class JAXBTextPlainClient {

    public static final String goodResult = "Message processed";       
    public static final String plainStr = "test input";
    
    // long string of 10KB
    public static final String s2 =
   "<Text>A. What is WebSphere?In todays business environment, it is increasingly important for all enterprises to develop and implement intelligent on demand strategies. In recent years, IBM has established itself as the industry leader in providing state-of-the-art on demand business solutions. In order to meet the rapidly emerging real-time requirements of businesses everywhere, IBM has launched the WebSphere software suite. WebSphere is an IBM software brand used to distinguish a suite of more than 300 software products. The magnitude of WebSphere has led to the misperception that WebSphere is too complex and expensive for SMBs. In truth, WebSphere is tremendously flexible, and WebSphere products are ideally suited to the on demand needs of both SMBs and large enterprises.IBM WebSphere software provides businesses with an IT infrastructure that maximizes both flexibility and responsiveness. By integrating people, processes, and information, WebSphere enables businesses to respond to changing business conditions with improved flexibility and speed. By optimizing the application infrastructure, WebSphere creates a reliable, high-performance environment for deploying and running application environment that fosters operational excellence. And by extending the reach of IT, WebSphere enables companies to maximize the use of their IT infrastructure to reach users in new ways and support new business models.Figure 1: Business AlignmentIBM WebSphere software, with its integration and infrastructure capabilities, is a key part of IBM s overall middleware platform. IBM s middleware platform extends beyond WebSphere to include infrastructure management capabilities including security, provisioning, and infrastructure orchestration. The middleware also extends to business-driven development capabilities for improving the software life cycle management process. Furthermore, IBM s middleware platform provides business performance management capabilities, which extend the process integration and management capability of WebSphere to include the monitoring and optimization of the IT resources, which support your business processes. The IBM middleware platform is delivered as a modular product portfolio built on open standards. While functionally rich, the platform can be adopted incrementally, and you can start by adopting only those capabilities that you need to solve your most pressing business challenges. It provides integrated, role-based tools for application development and administration, utilizing a common installation, administration, security, and programming model. In short, the IBM middleware platform makes your technology infrastructure simple to develop, deploy, and manage.The WebSphere software suite enables on demand flexibility through a set of integration and infrastructure capabilities. Consider the following illustration; it shows WebSpheres capabilities, from the top down, in six distinct categories: People Integration, Process Integration, Information Integration, Application Integration, Application Infrastructure, and Accelerators. This abstract description of the WebSphere suite is probably less exciting than descriptions of the actual products; nevertheless, the capability classification is essential to defining WebSphere, because it provides the clearest orientation to the different areas that the WebSphere suite addresses.Modeling, simulating, and optimizing business processes Enabling you to efficiently manage, integrate, and access information that is scattered throughout the enterprise, and even beyond the enterprise in other companies with which you do businessApplication IntegrationWebSphere middleware ensures reliable and flexible information flow among diverse applications and organizations. The middleware enables reliability and seamless exchange of data among multiple applications. WebSphere software helps you manage the differences between multiple applications and business partners, and it adopts an enterprise-wide, flexible, service-oriented approach to integration.Application InfrastructureWebSphere middleware allows you to build, deploy, integrate, and enhance new and existing applications. You can quickly Web-enable green-screen applications, and adapt legacy applications for use in new Java environments. WebSphere delivers operational efficiency and enterprise quality of service for a mixed-workload infrastructure.AcceleratorsWebSphere Business Integration Accelerators are prebuilt accelerators that reduce deployment costs, provide increased cost savings, speed time to market, and lower business risk when using the IBM Business Integration infrastructure to solve customers business problems. IBM provides a set of accelerators for specific vertical industry processes. We also provide accelerators for processes that are common to many industries, such as multi-channel commerce. WebSphere Commerce provides prebuilt processes for business-to-consumer selling, as well as business-to-business selling. IBM also provides a set of prebuilt, industry-specific middleware designed to accelerate business transformation initiatives. As an example, IBM provides a prebuilt accelerator for improving supply chain integration in the electronics industry. This solution features WebSphere Business Integration Connect with a Rosettanet Accelerator. The middleware enables companies to send and receive electronic documents over the Internet, using the Rosettanet industry standard.In terms of increased cost savings:WebSphere Business Integration customers can reduce their integration costs by 50 percent when using prebuilt accelerators, when compared to traditional methods. Customers using prebuilt accelerators can achieve a high degree of reuse, reducing integration project costs and lowering total cost of ownership (TCO). Accelerators can speed the time to market:Prepackaged, out-of-the-box accelerators mean that customers are not bogged down in customer coding, allowing you to get to market more quickly. Customers are freed to focus on customization, not building and testing. Customers can focus on achieving business results, not on development.</Text>" +
   "<Text2>A. What is WebSphere?In todays business environment, it is increasingly important for all enterprises to develop and implement intelligent on demand strategies. In recent years, IBM has established itself as the industry leader in providing state-of-the-art on demand business solutions. In order to meet the rapidly emerging real-time requirements of businesses everywhere, IBM has launched the WebSphere software suite. WebSphere is an IBM software brand used to distinguish a suite of more than 300 software products. The magnitude of WebSphere has led to the misperception that WebSphere is too complex and expensive for SMBs. In truth, WebSphere is tremendously flexible, and WebSphere products are ideally suited to the on demand needs of both SMBs and large enterprises.IBM WebSphere software provides businesses with an IT infrastructure that maximizes both flexibility and responsiveness. By integrating people, processes, and information, WebSphere enables businesses to respond to changing business conditions with improved flexibility and speed. By optimizing the application infrastructure, WebSphere creates a reliable, high-performance .... the reach of IT, WebSphere enables companies to maximize the use of their IT infrastructure to reach users in new ways and support new business models.Figure 1: Business AlignmentIBM WebSphere software, with its integration and infrastructure capabilities, is a key part of IBM s overall middleware platform. IBM s middleware platform extends beyond WebSphere to include infrastructure management capabilities including security, provisioning, and infrastructure orchestration. The middleware also extends to business-driven development capabilities for improving the software life cycle management process. Furthermore, IBM s middleware platform provides business performance management capabilities, which extend the process integration and management capability of WebSphere to include the monitoring and optimization of the IT resources, which support your business processes. The IBM middleware platform is delivered as a modular product portfolio built on open standards. While functionally rich, the platform can be adopted incrementally, and you can start by adopting only those capabilities that you need to solve your most pressing business challenges. It provides integrated, role-based tools for application development and administration, utilizing a common installation, administration, security, and programming model. In short, the IBM middleware platform makes your technology infrastructure simple to develop, deploy, and manage.The WebSphere software suite enables on demand flexibility through a set of integration and infrastructure capabilities. Consider the following illustration; it shows WebSphere s capabilities, from the top down, in six distinct categories: People Integration, Process Integration, Information Integration, Application Integration, Application Infrastructure, and Accelerators. This abstract description of the WebSphere suite is probably less exciting than descriptions of the actual products; nevertheless, the capability classification is essential to defining WebSphere, because it provides the clearest orientation to the different areas that the WebSphere suite addresses.Modeling, simulating, and optimizing business processes Enabling you to efficiently manage, integrate, and access information that is scattered throughout the enterprise, and even beyond the enterprise in other companies with which you do businessApplication IntegrationWebSphere middleware ensures reliable and flexible information flow among diverse applications and organizations. The middleware enables reliability and seamless exchange of data among multiple applications. WebSphere software helps you manage the differences between multiple applications and business partners, and it adopts an enterprise-wide, flexible, service-oriented approach to integration.Application InfrastructureWebSphere middleware allows you to build, deploy, integrate, and enhance new and existing applications. You can quickly Web-enable green-screen applications, and adapt legacy applications for use in new Java environments. WebSphere delivers operational efficiency and enterprise quality of service for a mixed-workload infrastructure.AcceleratorsWebSphere Business Integration Accelerators are prebuilt accelerators that reduce deployment costs, provide increased cost savings, speed time to market, and lower business risk when using the IBM Business Integration infrastructure to solve customers  business problems. IBM provides a set of accelerators for specific vertical industry processes. We also provide accelerators for processes that are common to many industries, such as multi-channel commerce. WebSphere Commerce provides prebuilt processes for business-to-consumer selling, as well as business-to-business selling. IBM also provides a set of prebuilt, industry-specific middleware designed to accelerate business transformation initiatives. As an example, IBM provides a prebuilt accelerator for improving supply chain integration in the electronics industry. This solution features WebSphere Business Integration Connect with a Rosettanet Accelerator. The middleware enables companies to send and receive electronic documents over the Internet, using the Rosettanet industry standard.In terms of increased cost savings:WebSphere Business Integration customers can reduce their integration costs by 50 percent when using prebuilt accelerators, when compared to traditional methods. Customers using prebuilt accelerators can achieve a high degree of reuse, reducing integration project costs and lowering total cost of ownership (TCO). Accelerators can speed the time to market:Prepackaged, out-of-the-box accelerators mean that customers are not bogged down in customer coding, allowing you to get to market more quickly. Customers are freed to focus on customization, not building and testing. Customers can focus on achieving business results, not on development.</Text2>";

    public static final String xmlFilename = "catalog.xml";  //about 31KB in size, has NO xml header, SOAP 11
    public static final String jpegFilename = "image1.jpg";
	
   public static final QName serviceName = new QName("http://ws.apache.org/axis2", "JAXBTextPlainService");
   private static final QName portName    = new QName("http://ws.apache.org/axis2", "AttachmentServicePort");
   private static final String endpointUrl = "http://@REPLACE_WITH_HOST_NAME@:"
                          + "@REPLACE_WITH_PORT_NUM@/mtomdefaultsetting/JAXBTextPlainService";
   private static final Service.Mode mode = Service.Mode.PAYLOAD;
   private static final String bindingID = null;
	

    public static void main(String[] args) throws Exception {
 
        JAXBTextPlainClient client = new JAXBTextPlainClient();
        System.out.println("Result 1:\n" + client.JAXB_AttachmentShortString());
        System.out.println("Result 2:\n" + client.JAXB_AttachmentLongString());
        System.out.println("Result 3:\n" + client.JAXB_AttachmentFromFile());

    }

    /**
     * - sends a string
     * @return returned from the remote method
     */


    public String JAXB_AttachmentShortString(){
    	
        String sendBackStr = null;
   	System.out.println("\n*** In JAXBTextPlainClient.JAXB_AttachmentShortString ***\n");
         
           // create a Dynamic service & port
     	       Service svc = Service.create(serviceName);
  	       svc.addPort(portName,bindingID,endpointUrl); 	       
  	       // debug
  	     System.out.println("portname = " + portName 
  	    		 + ",bindingID = " + bindingID + ",endpointUrl = " + endpointUrl);
     		       
         JAXBContext jbc = null;
         try {		
               // jaxb artifacts package
               jbc = JAXBContext.newInstance("org.test.mtom.textplain");

         } catch (JAXBException jaxbe){
         }

         Dispatch<Object> dispatch = svc
                 .createDispatch(portName, jbc, mode);

        String orgStr = plainStr;

      	ImageDepot imageDepot = new ObjectFactory().createImageDepot();
      	imageDepot.setImageData(orgStr);
      	        
         //Create a request bean with imagedepot bean as value
         ObjectFactory factory = new ObjectFactory();
         Invoke request = factory.createInvoke();
         request.setInput(imageDepot);
         
         SendImageResponse response = (SendImageResponse) dispatch.invoke(request);
         
         String retVal = response.getOutput().getImageData().trim();

         if (retVal.equals(orgStr)){
            System.out.println("String received equals to string sent");   				
            sendBackStr = goodResult;   			
        } else {                            
            System.out.println("**ERROR - Received message does not match expected message.\nExpected:\n"                                                
                               + orgStr + "\nReceived:\n" + retVal);                            
            sendBackStr = "**ERROR - Received message does not match expected message.";   			
        }


 		return sendBackStr;
     }
    
    /**
      * - uses a long string
      * @return returned from the remote method
      */

    public String JAXB_AttachmentLongString() {
    	
        String sendBackStr = null;
        System.out.println("\n*** In JAXBTextPlainClient.JAXB_AttachmentLongString ***\n");
    	   
           // create a Dynamic service & port
     	       Service svc = Service.create(serviceName);
  	       svc.addPort(portName,bindingID,endpointUrl); 	       
   	       // debug
   	     System.out.println("portname = " + portName 
   	    		 + ",bindingID = " + bindingID + ",endpointUrl = " + endpointUrl);
      		
          
             JAXBContext jbc = null;
             try {		
                   // jaxb artifacts package
                   jbc = JAXBContext.newInstance("org.test.mtom.textplain");

             } catch (JAXBException jaxbe){
             }

                
          Dispatch<Object> dispatch = svc
                  .createDispatch(portName, jbc, mode);

         String orgStr = s2;

       	ImageDepot imageDepot = new ObjectFactory().createImageDepot();
       	imageDepot.setImageData(orgStr);
 
       	//Create a request bean with imagedepot bean as value
          ObjectFactory factory = new ObjectFactory();
          Invoke request = factory.createInvoke();
          request.setInput(imageDepot);
          
          SendImageResponse response = (SendImageResponse) dispatch.invoke(request);
          
          String retVal = response.getOutput().getImageData().trim();
         
         if (retVal.equals(orgStr)){

             System.out.println("String received equals to string sent");   				
             sendBackStr = goodResult;   			
         } else {                            
             System.out.println("**ERROR - Received message does not match expected message.\nExpected:\n"                                                
                                + orgStr + "\nReceived:\n" + retVal);                            
             sendBackStr = "**ERROR - Received message does not match expected message.";   			
         }

         return sendBackStr;
                			           		       			              

      }

 
    /**
     * - sends a string that read from an xml file
     * @return returned from the remote method
     */

   public String JAXB_AttachmentFromFile() {
       
       String sendBackStr = null;

           System.out.println("\n*** In JAXBTextPlainClient.JAXB_AttachmentFromXMLFile  ***\n");
    	   
           String fileName = xmlFilename;
           BufferedReader inputStream = null;

           // create a Dynamic service & port
     	       Service svc = Service.create(serviceName);
  	       svc.addPort(portName,bindingID,endpointUrl); 	       
   	       // debug
   	     System.out.println("portname = " + portName 
   	    		 + ",bindingID = " + bindingID + ",endpointUrl = " + endpointUrl);
      		
             JAXBContext jbc = null;
             try {		
                   // jaxb artifacts package
                   jbc = JAXBContext.newInstance("org.test.mtom.textplain");

             } catch (JAXBException jaxbe){
             }

                
          Dispatch<Object> dispatch = svc.createDispatch(portName, jbc, mode);
          String orgStr = null; 

          try {
        
                inputStream = new BufferedReader(new FileReader(fileName));
                String line = inputStream.readLine();
                orgStr = line;
                
                while (line != null) {
                    orgStr += line;
                    line = inputStream.readLine();
                }
    
                inputStream.close();

          } catch (Exception ex) {
              System.out.println("Can't read file " + fileName);	
              ex.printStackTrace(); 
          }

       	ImageDepot imageDepot = new ObjectFactory().createImageDepot();
       	imageDepot.setImageData(orgStr);
          
          //Create a request bean with imagedepot bean as value
          ObjectFactory factory = new ObjectFactory();
          Invoke request = factory.createInvoke();
          request.setInput(imageDepot);
               
          SendImageResponse response = (SendImageResponse) dispatch.invoke(request);
          
          String retVal = response.getOutput().getImageData().trim();
         
          if (retVal.equals(orgStr)){   			           		       			              
              System.out.println("String received equals to string sent");   				
              sendBackStr = goodResult;   			
          } else {                            
              System.out.println("**ERROR - Received message does not match expected message.\nExpected:\n"                                                
                                 + orgStr + "\nReceived:\n" + retVal);                            
              sendBackStr = "**ERROR - Received message does not match expected message.";   			
          }
  		  		
          return sendBackStr;
      
   }   

}
