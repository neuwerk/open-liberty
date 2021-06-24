package mtom21.disablejaxb.wsfvt.client;

import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.MTOMFeature;
import javax.xml.ws.soap.SOAPBinding;

import java.io.*;

import org.test.mtom21.disablejaxb_textplain.*;

/**
 * This testcase uses a JAXWS Dispatch invocation with JAXB generated request object as parameter.
 * The endpoint for these testcase is a JAXWS Source Provider.
 * 
 * This method covers the "text/plain" mime types.
 */

public class DisableJAXBTextPlainClient {

    public static final String goodResult = "Message processed";
    public static final String badResult = "Problem in processing message";

    String plainStr = "test input";

    // long string of 10KB
    String s2 = "<Text>A. What is WebSphere?In todays business environment, it is increasingly important for all enterprises to develop and implement intelligent on demand strategies. In recent years, IBM has established itself as the industry leader in providing state-of-the-art on demand business solutions. In order to meet the rapidly emerging real-time requirements of businesses everywhere, IBM has launched the WebSphere software suite. WebSphere is an IBM software brand used to distinguish a suite of more than 300 software products. The magnitude of WebSphere has led to the misperception that WebSphere is too complex and expensive for SMBs. In truth, WebSphere is tremendously flexible, and WebSphere products are ideally suited to the on demand needs of both SMBs and large enterprises.IBM WebSphere software provides businesses with an IT infrastructure that maximizes both flexibility and responsiveness. By integrating people, processes, and information, WebSphere enables businesses to respond to changing business conditions with improved flexibility and speed. By optimizing the application infrastructure, WebSphere creates a reliable, high-performance environment for deploying and running applicatironment that fosters operational excellence. And by extending the reach of IT, WebSphere enables companies to maximize the use of their IT infrastructure to reach users in new ways and support new business models.Figure 1: Business AlignmentIBM WebSphere software, with its integration and infrastructure capabilities, is a key part of IBM s overall middleware platform. IBM s middleware platform extends beyond WebSphere to include infrastructure management capabilities including security, provisioning, and infrastructure orchestration. The middleware also extends to business-driven development capabilities for improving the software life cycle management process. Furthermore, IBM s middleware platform provides business performance management capabilities, which extend the process integration and management capability of WebSphere to include the monitoring and optimization of the IT resources, which support your business processes. The IBM middleware platform is delivered as a modular product portfolio built on open standards. While functionally rich, the platform can be adopted incrementally, and you can start by adopting only those capabilities that you need to solve your most pressing business challenges. It provides integrated, role-based tools for application development and administration, utilizing a common installation, administration, security, and programming model. In short, the IBM middleware platform makes your technology infrastructure simple to develop, deploy, and manage.The WebSphere software suite enables on demand flexibility through a set of integration and infrastructure capabilities. Consider the following illustration; it shows WebSpheres capabilities, from the top down, in six distinct categories: People Integration, Process Integration, Information Integration, Application Integration, Application Infrastructure, and Accelerators. This abstract description of the WebSphere suite is probably less exciting than descriptions of the actual products; nevertheless, the capability classification is essential to defining WebSphere, because it provides the clearest orientation to the different areas that the WebSphere suite addresses.Modeling, simulating, and optimizing business processes Enabling you to efficiently manage, integrate, and access information that is scattered throughout the enterprise, and even beyond the enterprise in other companies with which you do businessApplication IntegrationWebSphere middleware ensures reliable and flexible information flow among diverse applications and organizations. The middleware enables reliability and seamless exchange of data among multiple applications. WebSphere software helps you manage the differences between multiple applications and business partners, and it adopts an enterprise-wide, flexible, service-oriented approach to integration.Application InfrastructureWebSphere middleware allows you to build, deploy, integrate, and enhance new and existing applications. You can quickly Web-enable green-screen applications, and adapt legacy applications for use in new Java environments. WebSphere delivers operational efficiency and enterprise quality of service for a mixed-workload infrastructure.AcceleratorsWebSphere Business Integration Accelerators are prebuilt accelerators that reduce deployment costs, provide increased cost savings, speed time to market, and lower business risk when using the IBM Business Integration infrastructure to solve customers business problems. IBM provides a set of accelerators for specific vertical industry processes. We also provide accelerators for processes that are common to many industries, such as multi-channel commerce. WebSphere Commerce provides prebuilt processes for business-to-consumer selling, as well as business-to-business selling. IBM also provides a set of prebuilt, industry-specific middleware designed to accelerate business transformation initiatives. As an example, IBM provides a prebuilt accelerator for improving supply chain integration in the electronics industry. This solution features WebSphere Business Integration Connect with a Rosettanet Accelerator. The middleware enables companies to send and receive electronic documents over the Internet, using the Rosettanet industry standard.In terms of increased cost savings:WebSphere Business Integration customers can reduce their integration costs by 50 percent when using prebuilt accelerators, when compared to traditional methods. Customers using prebuilt accelerators can achieve a high degree of reuse, reducing integration project costs and lowering total cost of ownership (TCO). Accelerators can speed the time to market:Prepackaged, out-of-the-box accelerators mean that customers are not bogged down in customer coding, allowing you to get to market more quickly. Customers are freed to focus on customization, not building and testing. Customers can focus on achieving business results, not on development.</Text>"
            + "<Text2>A. What is WebSphere?In todays business environment, it is increasingly important for all enterprises to develop and implement intelligent on demand strategies. In recent years, IBM has established itself as the industry leader in providing state-of-the-art on demand business solutions. In order to meet the rapidly emerging real-time requirements of businesses everywhere, IBM has launched the WebSphere software suite. WebSphere is an IBM software brand used to distinguish a suite of more than 300 software products. The magnitude of WebSphere has led to the misperception that WebSphere is too complex and expensive for SMBs. In truth, WebSphere is tremendously flexible, and WebSphere products are ideally suited to the on demand needs of both SMBs and large enterprises.IBM WebSphere software provides businesses with an IT infrastructure that maximizes both flexibility and responsiveness. By integrating people, processes, and information, WebSphere enables businesses to respond to changing business conditions with improved flexibility and speed. By optimizing the application infrastructure, WebSphere creates a reliable, high-performance environment for deploying and running applications-an environment that fosters operational excellence. And by extending the reach of IT, WebSphere enables companies to maximize the use of their IT infrastructure to reach users in new ways and support new business models.Figure 1: Business AlignmentIBM WebSphere software, with its integration and infrastructure capabilities, is a key part of IBM s overall middleware platform. IBM s middleware platform extends beyond WebSphere to include infrastructure management capabilities including security, provisioning, and infrastructure orchestration. The middleware also extends to business-driven development capabilities for improving the software life cycle management process. Furthermore, IBM s middleware platform provides business performance management capabilities, which extend the process integration and management capability of WebSphere to include the monitoring and optimization of the IT resources, which support your business processes. The IBM middleware platform is delivered as a modular product portfolio built on open standards. While functionally rich, the platform can be adopted incrementally, and you can start by adopting only those capabilities that you need to solve your most pressing business challenges. It provides integrated, role-based tools for application development and administration, utilizing a common installation, administration, security, and programming model. In short, the IBM middleware platform makes your technology infrastructure simple to develop, deploy, and manage.The WebSphere software suite enables on demand flexibility through a set of integration and infrastructure capabilities. Consider the following illustration; it shows WebSphere s capabilities, from the top down, in six distinct categories: People Integration, Process Integration, Information Integration, Application Integration, Application Infrastructure, and Accelerators. This abstract description of the WebSphere suite is probably less exciting than descriptions of the actual products; nevertheless, the capability classification is essential to defining WebSphere, because it provides the clearest orientation to the different areas that the WebSphere suite addresses.Modeling, simulating, and optimizing business processes Enabling you to efficiently manage, integrate, and access information that is scattered throughout the enterprise, and even beyond the enterprise in other companies with which you do businessApplication IntegrationWebSphere middleware ensures reliable and flexible information flow among diverse applications and organizations. The middleware enables reliability and seamless exchange of data among multiple applications. WebSphere software helps you manage the differences between multiple applications and business partners, and it adopts an enterprise-wide, flexible, service-oriented approach to integration.Application InfrastructureWebSphere middleware allows you to build, deploy, integrate, and enhance new and existing applications. You can quickly Web-enable green-screen applications, and adapt legacy applications for use in new Java environments. WebSphere delivers operational efficiency and enterprise quality of service for a mixed-workload infrastructure.AcceleratorsWebSphere Business Integration Accelerators are prebuilt accelerators that reduce deployment costs, provide increased cost savings, speed time to market, and lower business risk when using the IBM Business Integration infrastructure to solve customers  business problems. IBM provides a set of accelerators for specific vertical industry processes. We also provide accelerators for processes that are common to many industries, such as multi-channel commerce. WebSphere Commerce provides prebuilt processes for business-to-consumer selling, as well as business-to-business selling. IBM also provides a set of prebuilt, industry-specific middleware designed to accelerate business transformation initiatives. As an example, IBM provides a prebuilt accelerator for improving supply chain integration in the electronics industry. This solution features WebSphere Business Integration Connect with a Rosettanet Accelerator. The middleware enables companies to send and receive electronic documents over the Internet, using the Rosettanet industry standard.In terms of increased cost savings:WebSphere Business Integration customers can reduce their integration costs by 50 percent when using prebuilt accelerators, when compared to traditional methods. Customers using prebuilt accelerators can achieve a high degree of reuse, reducing integration project costs and lowering total cost of ownership (TCO). Accelerators can speed the time to market:Prepackaged, out-of-the-box accelerators mean that customers are not bogged down in customer coding, allowing you to get to market more quickly. Customers are freed to focus on customization, not building and testing. Customers can focus on achieving business results, not on development.</Text2>";

    String xmlFilename = "catalog.xml"; // about 31KB in size, has NO xml header, SOAP 11
    String jpegFilename = "image1.jpg";

    String sendBackStr = null;

    public static final QName serviceName = new QName("http://ws.apache.org/axis2", "MTOMDefaultTextPlainService");
    private static final QName portName = new QName("http://ws.apache.org/axis2", "AttachmentServicePort");
    private static final String endpointUrl = "http://@REPLACE_WITH_HOST_NAME@:"
            + "@REPLACE_WITH_PORT_NUM@/disablejaxbanno/MTOMDefaultTextPlainService";

    public static final QName serviceNameVerify = new QName("http://ws.apache.org/axis2", "MTOMDefaultTextPlainServiceVerifyMTOM");
    private static final QName portNameVerify = new QName("http://ws.apache.org/axis2", "AttachmentServicePortVerifyMTOM");
    private static final String endpointUrlVerify = "http://@REPLACE_WITH_HOST_NAME@:"
            + "@REPLACE_WITH_PORT_NUM@/disablejaxbanno/MTOMDefaultTextPlainServiceVerifyMTOM";
    
    private static final Service.Mode mode = Service.Mode.PAYLOAD;

    public static void main(String[] args) {
        try {
            DisableJAXBTextPlainClient client = new DisableJAXBTextPlainClient();
//            client.JAXB_AttachmentFromFile();
            client.JAXB_AttachmentLongString();
//            client.JAXB_AttachmentShortString();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * This client method calls into a service that verfies that MTOM is not used over the wire on
     * the request. It corresponds to the JAXB_AttachmentShortString method which verifies that the
     * image is sent and received correctly.
     * 
     * @return
     * @throws Exception
     */
    public String jaxb_AttachmentShortString_VerifyMTOM() throws Exception {

        // debug
        System.out.println("\n*** In DisableJAXBTextPlainClient.jaxb_AttachmentShortString_VerifyMTOM ***\n");
        String bindingID = SOAPBinding.SOAP11HTTP_BINDING;

            // create a Dynamic service & port
            Service svc = Service.create(serviceNameVerify);
            svc.addPort(portNameVerify, bindingID, endpointUrlVerify);
            // debug
            System.out
                    .println("portname = " + portNameVerify + ",bindingID = " + bindingID + ",endpointUrl = " + endpointUrlVerify);

            JAXBContext jbc = JAXBContext.newInstance("org.test.mtom21.disablejaxb_textplain");

            // set MTOM on dispatch to false
            MTOMFeature mtom21 = new MTOMFeature(false);

            Dispatch<Object> dispatch = svc.createDispatch(portNameVerify, jbc, mode, mtom21);

            String string = plainStr;

            ImageDepot imageDepot = new ObjectFactory().createImageDepot();
            imageDepot.setImageData(string);

            // Create a request bean with imagedepot bean as value
            ObjectFactory factory = new ObjectFactory();
            Invoke request = factory.createInvoke();
            request.setInput(imageDepot);

            SendImageResponseVerify response = (SendImageResponseVerify) dispatch.invoke(request);
            String responseVal = response.getOutput();
            System.out.println("response: " + responseVal);
            if(response == null || responseVal.indexOf("MTOMOFF") == -1) {
                return badResult;
            } else {
                return goodResult;
            }
    }

    /**
     * This testcase uses a JAXWS Dispatch invocation with JAXB generated request object as
     * parameter. The endpoint for these testcase is a JAXWS Source Provider.
     *  - covers the "text/plain" mime type. - sets for MTOM with SOAP11 using 2 steps: bindingID
     * and setMTOMEnabled - mode = PAYLOAD - SOAP version 1.1
     * 
     * @return returned from the remote method
     */

    public String JAXB_AttachmentShortString() throws Exception {

        // debug
        System.out.println("\n*** In DisableJAXBTextPlainClient.JAXB_AttachmentShortString ***\n");
        String bindingID = SOAPBinding.SOAP11HTTP_BINDING;

        try {
            // create a Dynamic service & port
            Service svc = Service.create(serviceName);
            svc.addPort(portName, bindingID, endpointUrl);
            // debug
            System.out
                    .println("portname = " + portName + ",bindingID = " + bindingID + ",endpointUrl = " + endpointUrl);

            JAXBContext jbc = JAXBContext.newInstance("org.test.mtom21.disablejaxb_textplain");

            // set MTOM on dispatch to false
            MTOMFeature mtom21 = new MTOMFeature(false);

            Dispatch<Object> dispatch = svc.createDispatch(portName, jbc, mode, mtom21);

            String string = plainStr;

            ImageDepot imageDepot = new ObjectFactory().createImageDepot();
            imageDepot.setImageData(string);

            // Create a request bean with imagedepot bean as value
            ObjectFactory factory = new ObjectFactory();
            Invoke request = factory.createInvoke();
            request.setInput(imageDepot);

            SendImageResponse response = (SendImageResponse) dispatch.invoke(request);

            String retVal = response.getOutput().getImageData().trim();

            if (retVal.equals(string)) {

                // debug
                printMessageToConsole("String received matched with string sent.");
                sendBackStr = goodResult;
            } else {
                // debug
                printMessageToConsole(badResult);

                sendBackStr = badResult;
            }

        } catch (Exception ex) {
            System.out.println("Client failed with exception.");
            ex.printStackTrace();
        }

        return sendBackStr;
    }
    
    /**
     * This client method calls into a service that verfies that MTOM is not used over the wire on
     * the request. It corresponds to the JAXB_AttachmentLongString method which verifies that the
     * image is sent and received correctly.
     * 
     * @return
     * @throws Exception
     */
    public String jaxb_AttachmentLongString_VerifyMTOM() throws Exception {

        // debug
        System.out.println("\n*** In DisableJAXBTextPlainClient.jaxb_AttachmentLongString_VerifyMTOM ***\n");
        String bindingID = SOAPBinding.SOAP11HTTP_BINDING;

        // create a Dynamic service & port
        Service svc = Service.create(serviceNameVerify);
        svc.addPort(portNameVerify, bindingID, endpointUrlVerify);
        // debug
        System.out
                .println("portname = " + portNameVerify + ",bindingID = " + bindingID + ",endpointUrl = " + endpointUrlVerify);

        JAXBContext jbc = JAXBContext.newInstance("org.test.mtom21.disablejaxb_textplain");

        // set MTOM to false on dispatch
        MTOMFeature mtom21 = new MTOMFeature(false);

        Dispatch<Object> dispatch = svc.createDispatch(portNameVerify, jbc, mode, mtom21);

        String string = s2;

        ImageDepot imageDepot = new ObjectFactory().createImageDepot();
        imageDepot.setImageData(string);
        System.out.println("request string: " + string);

        // Create a request bean with imagedepot bean as value
        ObjectFactory factory = new ObjectFactory();
        Invoke request = factory.createInvoke();
        request.setInput(imageDepot);

        SendImageResponseVerify response = (SendImageResponseVerify) dispatch.invoke(request);
        String responseVal = response.getOutput();
        System.out.println("response: " + responseVal);
        if(response == null || responseVal.indexOf("MTOMOFF") == -1) {
            return badResult;
        } else {
            return goodResult;
        }
    }

    /**
     * This testcase uses a JAXWS Dispatch invocation with JAXB generated request object as
     * parameter. The endpoint for these testcase is a JAXWS Source Provider.
     *  - uses a long string - covers the "text/plain" mime type. - sets for MTOM with SOAP11 using
     * 2 steps: bindingID and setMTOMEnabled - mode = PAYLOAD
     * 
     * @return returned from the remote method
     */

    public String JAXB_AttachmentLongString() throws Exception {

        // debug
        System.out.println("\n*** In DisableJAXBTextPlainClient.JAXB_AttachmentLongString ***\n");
        String bindingID = SOAPBinding.SOAP11HTTP_BINDING;

        try {
            // create a Dynamic service & port
            Service svc = Service.create(serviceName);
            svc.addPort(portName, bindingID, endpointUrl);
            // debug
            System.out
                    .println("portname = " + portName + ",bindingID = " + bindingID + ",endpointUrl = " + endpointUrl);

            JAXBContext jbc = JAXBContext.newInstance("org.test.mtom21.disablejaxb_textplain");

            // set MTOM to false on dispatch
            MTOMFeature mtom21 = new MTOMFeature(false);

            Dispatch<Object> dispatch = svc.createDispatch(portName, jbc, mode, mtom21);

            String string = s2;

            ImageDepot imageDepot = new ObjectFactory().createImageDepot();
            imageDepot.setImageData(string);
            System.out.println("request string: " + string);

            // Create a request bean with imagedepot bean as value
            ObjectFactory factory = new ObjectFactory();
            Invoke request = factory.createInvoke();
            request.setInput(imageDepot);

            // SendImageResponse response = (SendImageResponse) dispatch.invoke(request);
            SendImageResponse response = (SendImageResponse) dispatch.invoke(request);

            String retVal = response.getOutput().getImageData().trim();

            if (retVal.indexOf(string) != -1) {

                printMessageToConsole("String received matched with string sent.");
                sendBackStr = goodResult;
            } else {
                // debug
                printMessageToConsole(badResult);

                sendBackStr = badResult;
            }

        } catch (Exception ex) {
            System.out.println("Client failed with exception.");
            ex.printStackTrace();
        }

        return sendBackStr;
    }
    
    /**
     * This client method calls into a service that verfies that MTOM is not used over the wire on
     * the request. It corresponds to the JAXB_AttachmentFromFile method which verifies that the
     * image is sent and received correctly.
     * 
     * @return
     * @throws Exception
     */
    public String jaxb_AttachmentFromFile_VerifyMTOM() throws Exception {

        System.out.println("\n*** In DisableJAXBTextPlainClient.jaxb_AttachmentFromFile_VerifyMTOM  ***\n");

        String fileName = xmlFilename;
        BufferedReader inputStream = null;
        // this should be trumped by MTOMFeature
        String bindingID = SOAPBinding.SOAP11HTTP_MTOM_BINDING;

            // create a Dynamic service & port
            Service svc = Service.create(serviceNameVerify);
            svc.addPort(portNameVerify, bindingID, endpointUrlVerify);
            // debug
            System.out
                    .println("portname = " + portNameVerify + ",bindingID = " + bindingID + ",endpointUrl = " + endpointUrlVerify);

            JAXBContext jbc = JAXBContext.newInstance("org.test.mtom21.disablejaxb_textplain");

            // set MTOM to false on dispatch
            MTOMFeature mtom21 = new MTOMFeature(false);

            Dispatch<Object> dispatch = svc.createDispatch(portNameVerify, jbc, mode, mtom21);

            inputStream = new BufferedReader(new FileReader(fileName));

            String line = null;

            // Get the line
            line = inputStream.readLine();
            String string = line;

            while (line != null) {
                string += line;
                line = inputStream.readLine();
            }

            inputStream.close();

            ImageDepot imageDepot = new ObjectFactory().createImageDepot();
            imageDepot.setImageData(string);

            // Create a request bean with imagedepot bean as value
            ObjectFactory factory = new ObjectFactory();
            Invoke request = factory.createInvoke();
            request.setInput(imageDepot);

            SendImageResponseVerify response = (SendImageResponseVerify) dispatch.invoke(request);
            String responseVal = response.getOutput();
            System.out.println("response: " + responseVal);
            if(response == null || responseVal.indexOf("MTOMOFF") == -1) {
                return badResult;
            } else {
                return goodResult;
            }
    }

    /**
     * This testcase uses a JAXWS Dispatch invocation with JAXB generated request object as
     * parameter. The endpoint for these testcase is a JAXWS Source Provider.
     *  - reads from an xml file - covers the "text/plain" mime type. - sets for MTOM with SOAP11
     * using 2 steps: bindingID and setMTOMEnabled - mode = PAYLOAD
     * 
     * @return returned from the remote method
     */

    public String JAXB_AttachmentFromFile() throws Exception {

        System.out.println("\n*** In DisableJAXBTextPlainClient.JAXB_AttachmentFromFile  ***\n");

        String fileName = xmlFilename;
        BufferedReader inputStream = null;
        // this should be trumped by MTOMFeature
        String bindingID = SOAPBinding.SOAP11HTTP_MTOM_BINDING;

        try {
            // create a Dynamic service & port
            Service svc = Service.create(serviceName);
            svc.addPort(portName, bindingID, endpointUrl);
            // debug
            System.out
                    .println("portname = " + portName + ",bindingID = " + bindingID + ",endpointUrl = " + endpointUrl);

            JAXBContext jbc = JAXBContext.newInstance("org.test.mtom21.disablejaxb_textplain");

            // set MTOM to false on dispatch
            MTOMFeature mtom21 = new MTOMFeature(false);

            Dispatch<Object> dispatch = svc.createDispatch(portName, jbc, mode, mtom21);

            inputStream = new BufferedReader(new FileReader(fileName));

            String line = null;

            // Get the line
            line = inputStream.readLine();
            String string = line;

            while (line != null) {
                string += line;
                line = inputStream.readLine();
            }

            inputStream.close();

            ImageDepot imageDepot = new ObjectFactory().createImageDepot();
            imageDepot.setImageData(string);

            // Create a request bean with imagedepot bean as value
            ObjectFactory factory = new ObjectFactory();
            Invoke request = factory.createInvoke();
            request.setInput(imageDepot);

            SendImageResponse response = (SendImageResponse) dispatch.invoke(request);

            String retVal = response.getOutput().getImageData().trim();

            if (retVal.equals(string)) {

                printMessageToConsole("String received matched with string sent.");
                sendBackStr = goodResult;
            } else {
                // debug
                printMessageToConsole(badResult);

                sendBackStr = badResult;
            }

        } catch (FileNotFoundException e) {
            System.out.println("Error opening the file " + fileName);
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Error reading from the file " + fileName);

        } catch (Exception ex) {
            System.out.println("Client failed with exception.");
            ex.printStackTrace();
        }

        return sendBackStr;
    }

    // ============================================================
    // Utilites
    // ============================================================
    public static void printMessageToConsole(String msg) {
        System.out.println("At client side:\n" + msg);

    }

}
