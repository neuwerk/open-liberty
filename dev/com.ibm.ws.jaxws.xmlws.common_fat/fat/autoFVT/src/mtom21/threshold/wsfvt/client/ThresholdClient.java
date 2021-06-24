package mtom21.threshold.wsfvt.client;

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.imageio.ImageIO;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.MTOMFeature;
import javax.xml.ws.soap.SOAPBinding;

import org.apache.axiom.om.util.Base64;
import org.apache.axis2.jaxws.message.attachments.JAXBAttachmentUnmarshaller;

import com.ibm.ws.wsfvt.build.tools.AppConst;

import mtom21.threshold.wsfvt.client.AttachmentHelper;

public class ThresholdClient {

    public static final String positiveResult = "Message processed as expected";
    public static final String negativeResult = "MTOM usage did not match expected MTOM usage.";
    // 68K
    public static final File jpegFile1 = new File(AppConst.FVT_HOME + "/src/mtom21/threshold/etc/images/image1.jpeg");
    // 11K
    public static final File jpegFile2 = new File(AppConst.FVT_HOME + "/src/mtom21/threshold/etc/images/image2.jpeg");
    
    public static void main(String[] args) {
        try {
            ThresholdClient client = new ThresholdClient();
            System.out.println(client.callMTOMProxyServerThreshold(true, jpegFile1));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Call a Dispatch client passing in an MTOMFeature with a threshold. This covers the image/jpeg
     * MIME type using Dispatch client. If expected MTOM usage matches actual, a good response is
     * returned. Otherwise a bad response is returned.
     * 
     * @param mtomon true if MTOM is expected to be used on the client side
     * @param booleanParm The boolean parameter to use for the MTOMFeature constructor
     * @param threshold Threshold value
     * @return
     */
    public String callMTOMDispatchClient(boolean mtomon, Boolean booleanParm, int threshold) throws Exception {
        System.out.println("threshold: " + threshold);
        
        QName serviceName = new QName("http://ws.apache.org/axis2", "VerifyMTOMDispatch");
        QName portName = new QName("http://ws.apache.org/axis2", "VerifyMTOMDispatchPort");
        String endpointUrl = "http://@REPLACE_WITH_HOST_NAME@:" + "@REPLACE_WITH_PORT_NUM@/threshold21/VerifyMTOMDispatch";
        
        // create a Dynamic service & port
        Service svc = Service.create(serviceName);
        svc.addPort(portName, SOAPBinding.SOAP11HTTP_BINDING, endpointUrl);

        // jaxb artifacts package
        JAXBContext jbc = JAXBContext.newInstance("mtom21.threshold.wsfvt.client.dispatch");

        // set MTOM on dispatch
        MTOMFeature mtom21 = null;
        if(booleanParm != null) {
            System.out.println("MTOMFeature(" + booleanParm + ", " + threshold + ")");
            mtom21 = new MTOMFeature(booleanParm, threshold);
        } else {
            mtom21 = new MTOMFeature(threshold);
            System.out.println("MTOMFeature(" + threshold + ")");
        }

        Dispatch<Object> dispatch = svc.createDispatch(portName, jbc, Service.Mode.PAYLOAD, mtom21);

        Image image = ImageIO.read(jpegFile1);

        mtom21.threshold.wsfvt.client.dispatch.ImageDepot imageDepot = new mtom21.threshold.wsfvt.client.dispatch.ObjectFactory().createImageDepot();
        imageDepot.setImageData(image);

        // Create a request bean with imagedepot bean as value
        mtom21.threshold.wsfvt.client.dispatch.ObjectFactory factory = new mtom21.threshold.wsfvt.client.dispatch.ObjectFactory();
        mtom21.threshold.wsfvt.client.dispatch.Invoke request = factory.createInvoke();
        request.setInput(imageDepot);
        
        mtom21.threshold.wsfvt.client.dispatch.SendImageResponse response = (mtom21.threshold.wsfvt.client.dispatch.SendImageResponse) dispatch.invoke(request);
        String responseVal = response.getOutput();
        
        return verifyResponse(responseVal, mtomon);
    }
    
    /**
     * Call a Dispatch Source client passing in an MTOMFeature with a threshold. If expected MTOM
     * usage matches actual, a good response is returned. Otherwise a bad response is returned.
     * 
     * @param mtomon true if MTOM is expected to be used on the client side
     * @param booleanParm The boolean parameter to use for the MTOMFeature constructor
     * @param threshold Threshold value
     * @return
     */
    public String callMTOMDispatchSourceClient(boolean mtomon, Boolean booleanParm, int threshold) throws Exception {
        System.out.println("threshold: " + threshold);
        
        QName serviceName = new QName("http://ws.apache.org/axis2", "VerifyMTOMSource");
        QName portName = new QName("http://ws.apache.org/axis2", "VerifyMTOMSourcePort");
        String endpointUrl = "http://@REPLACE_WITH_HOST_NAME@:"
                + "@REPLACE_WITH_PORT_NUM@/threshold21/VerifyMTOMSource";

        // create a service
        Service svc = Service.create(serviceName);
        svc.addPort(portName, SOAPBinding.SOAP11HTTP_BINDING, endpointUrl);

        // set MTOM on dispatch
        MTOMFeature mtom21 = null;
        if (booleanParm != null) {
            System.out.println("MTOMFeature(" + booleanParm + ", " + threshold + ")");
            mtom21 = new MTOMFeature(booleanParm, threshold);
        } else {
            mtom21 = new MTOMFeature(threshold);
            System.out.println("MTOMFeature(" + threshold + ")");
        }

        // create dispatch<Source>
        javax.xml.ws.Dispatch<Source> dispatch = null;
        dispatch = svc.createDispatch(portName, Source.class, Service.Mode.PAYLOAD, mtom21);

        // force SOAPAction to match with wsdl action
        ((BindingProvider) dispatch).getRequestContext().put(BindingProvider.SOAPACTION_USE_PROPERTY, true);
        ((BindingProvider) dispatch).getRequestContext().put(BindingProvider.SOAPACTION_URI_PROPERTY, "echoString");

        Image image = ImageIO.read(jpegFile1);
        byte[] ba = AttachmentHelper.getImageBytes(image, "image/jpeg");

        String axis2NSHeader = "<ns1:inMessage xmlns:ns1=\"http://ws.apache.org/axis2\">";
        String axis2NSFooter = "</ns1:inMessage>";
        String orgSrc = axis2NSHeader + Base64.encode(ba) + axis2NSFooter;

        // call server's invoke
        Source response = dispatch.invoke(Common.toSource(orgSrc));

        // Common.toString - can only called once because it changes the Source
        String responseVal = Common.toString(response);

        return verifyResponse(responseVal, mtomon);
    }
    
    /**
     * Call a Proxy DataHandler client passing in an MTOMFeature with a threshold. This covers the mulitpart/*
     * MIME type using Proxy DataHandler client. If expected MTOM usage matches actual, a good response is
     * returned. Otherwise a bad response is returned.
     * 
     * @param mtomon true if MTOM is expected to be used on the client side
     * @param booleanParm The boolean parameter to use for the MTOMFeature constructor
     * @param threshold Threshold value
     * @return
     */
    public String callMTOMProxyDataHandlerClient(boolean mtomon, Boolean booleanParm, int threshold) throws Exception {
        System.out.println("threshold: " + threshold);

        QName serviceName = new QName("http://org/test/mtom21/VerifyMTOMProxyDataHandler", "VerifyMTOMProxyDataHandler");
        QName portName = new QName("http://org/test/mtom21/VerifyMTOMProxyDataHandler", "VerifyMTOMProxyDataHandlerPort");
        String endpointUrl = "http://@REPLACE_WITH_HOST_NAME@:"
                + "@REPLACE_WITH_PORT_NUM@/threshold21/VerifyMTOMProxyDataHandler";

        FileDataSource fds = new FileDataSource(jpegFile1);
        DataHandler content = new DataHandler(fds);

        // Set the data inside of the appropriate object
        mtom21.threshold.wsfvt.client.proxydatahandler.ImageDepot imageDepot = new mtom21.threshold.wsfvt.client.proxydatahandler.ObjectFactory().createImageDepot();
        imageDepot.setImageData(content);

        // Enable MTOM
        MTOMFeature mtom21 = null;
        if (booleanParm != null) {
            System.out.println("MTOMFeature(" + booleanParm + ", " + threshold + ")");
            mtom21 = new MTOMFeature(booleanParm, threshold);
        } else {
            mtom21 = new MTOMFeature(threshold);
            System.out.println("MTOMFeature(" + threshold + ")");
        }

        // Setup the necessary JAX-WS artifacts
        Service svc = Service.create(serviceName);
        mtom21.threshold.wsfvt.client.proxydatahandler.ImageServiceInterface proxy = svc.getPort(portName, mtom21.threshold.wsfvt.client.proxydatahandler.ImageServiceInterface.class, mtom21);

        // Set the target URL
        BindingProvider bp = (BindingProvider) proxy;
        Map<String, Object> requestCtx = bp.getRequestContext();
        requestCtx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl);

        // Send the image and process the response image
        String response = proxy.sendImage(imageDepot);
        
        return verifyResponse(response, mtomon);
    }
    
    /**
     * Call a Proxy client passing in an MTOMFeature with a threshold. This covers the mulitpart/*
     * MIME type using Proxy client. If expected MTOM usage matches actual, a good response is
     * returned. Otherwise a bad response is returned.
     * 
     * @param mtomon true if MTOM is expected to be used on the client side
     * @param booleanParm The boolean parameter to use for the MTOMFeature constructor
     * @param threshold Threshold value
     * @return
     */
    public String callMTOMProxyClient(boolean mtomon, Boolean booleanParm, int threshold) throws Exception {
        System.out.println("threshold: " + threshold);

        QName serviceName = new QName("http://org/test/mtom21/VerifyMTOMProxy", "VerifyMTOMProxy");
        QName portName = new QName("http://org/test/mtom21/VerifyMTOMProxy", "VerifyMTOMProxyPort");
        String endpointUrl = "http://@REPLACE_WITH_HOST_NAME@:"
                + "@REPLACE_WITH_PORT_NUM@/threshold21/VerifyMTOMProxy";

        Image image = ImageIO.read(jpegFile1);

        mtom21.threshold.wsfvt.client.proxy.ImageDepot imageDepot = new mtom21.threshold.wsfvt.client.proxy.ObjectFactory().createImageDepot();
        imageDepot.setImageData(image);

        // Create a request bean with imagedepot bean as value
        mtom21.threshold.wsfvt.client.proxy.ObjectFactory factory = new mtom21.threshold.wsfvt.client.proxy.ObjectFactory();
        mtom21.threshold.wsfvt.client.proxy.SendImage request = factory.createSendImage();
        request.setInput(imageDepot);

        // Enable MTOM
        MTOMFeature mtom21 = null;
        if (booleanParm != null) {
            System.out.println("MTOMFeature(" + booleanParm + ", " + threshold + ")");
            mtom21 = new MTOMFeature(booleanParm, threshold);
        } else {
            mtom21 = new MTOMFeature(threshold);
            System.out.println("MTOMFeature(" + threshold + ")");
        }

        // Setup the necessary JAX-WS artifacts
        Service svc = Service.create(serviceName);
        mtom21.threshold.wsfvt.client.proxy.ImageServiceInterface proxy = svc.getPort(portName, mtom21.threshold.wsfvt.client.proxy.ImageServiceInterface.class, mtom21);

        // Set the target URL
        BindingProvider bp = (BindingProvider) proxy;
        Map<String, Object> requestCtx = bp.getRequestContext();
        requestCtx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl);

        // Send the image and process the response image
        String response = proxy.sendImage(imageDepot);
        
        return verifyResponse(response, mtomon);
    }
    
    /**
     * Call a Dispatch client passing in an MTOMFeature with a threshold. This covers the text/plain
     * MIME type using Dispatch client. If expected MTOM usage matches actual, a good response is
     * returned. Otherwise a bad response is returned.
     * 
     * @param mtomon true if MTOM is expected to be used on the client side
     * @param booleanParm The boolean parameter to use for the MTOMFeature constructor
     * @param threshold Threshold value
     * @return
     */
    public String callMTOMDipatchTextPlainClient(boolean mtomon, Boolean booleanParm, int threshold) throws Exception {
        System.out.println("threshold: " + threshold);

        QName serviceName = new QName("http://org/test/mtom21/VerifyMTOMTextPlain", "VerifyMTOMTextPlain");
        QName portName = new QName("http://org/test/mtom21/VerifyMTOMTextPlain", "VerifyMTOMTextPlainPort");
        String endpointUrl = "http://@REPLACE_WITH_HOST_NAME@:"
                + "@REPLACE_WITH_PORT_NUM@/threshold21/VerifyMTOMTextPlain";

        // create a Dynamic service & port
        Service svc = Service.create(serviceName);
        svc.addPort(portName, SOAPBinding.SOAP11HTTP_BINDING, endpointUrl);
        
        JAXBContext jbc = JAXBContext.newInstance("mtom21.threshold.wsfvt.client.textplain");

        // set MTOM
        MTOMFeature mtom21 = null;
        if(booleanParm != null) {
            System.out.println("MTOMFeature(" + booleanParm + ", " + threshold + ")");
            mtom21 = new MTOMFeature(booleanParm, threshold);
        } else {
            mtom21 = new MTOMFeature(threshold);
            System.out.println("MTOMFeature(" + threshold + ")");
        }

        Dispatch<Object> dispatch = svc.createDispatch(portName, jbc, Service.Mode.PAYLOAD, mtom21);

        // 10K
        String data = "<Text>A. What is WebSphere?In todays business environment, it is increasingly important for all enterprises to develop and implement intelligent on demand strategies. In recent years, IBM has established itself as the industry leader in providing state-of-the-art on demand business solutions. In order to meet the rapidly emerging real-time requirements of businesses everywhere, IBM has launched the WebSphere software suite. WebSphere is an IBM software brand used to distinguish a suite of more than 300 software products. The magnitude of WebSphere has led to the misperception that WebSphere is too complex and expensive for SMBs. In truth, WebSphere is tremendously flexible, and WebSphere products are ideally suited to the on demand needs of both SMBs and large enterprises.IBM WebSphere software provides businesses with an IT infrastructure that maximizes both flexibility and responsiveness. By integrating people, processes, and information, WebSphere enables businesses to respond to changing business conditions with improved flexibility and speed. By optimizing the application infrastructure, WebSphere creates a reliable, high-performance environment for deploying and running applicat fosters operational excellence. And by extending the reach of IT, WebSphere enables companies to maximize the use of their IT infrastructure to reach users in new ways and support new business models.Figure 1: Business AlignmentIBM WebSphere software, with its integration and infrastructure capabilities, is a key part of IBM s overall middleware platform. IBM s middleware platform extends beyond WebSphere to include infrastructure management capabilities including security, provisioning, and infrastructure orchestration. The middleware also extends to business-driven development capabilities for improving the software life cycle management process. Furthermore, IBM s middleware platform provides business performance management capabilities, which extend the process integration and management capability of WebSphere to include the monitoring and optimization of the IT resources, which support your business processes. The IBM middleware platform is delivered as a modular product portfolio built on open standards. While functionally rich, the platform can be adopted incrementally, and you can start by adopting only those capabilities that you need to solve your most pressing business challenges. It provides integrated, role-based tools for application development and administration, utilizing a common installation, administration, security, and programming model. In short, the IBM middleware platform makes your technology infrastructure simple to develop, deploy, and manage.The WebSphere software suite enables on demand flexibility through a set of integration and infrastructure capabilities. Consider the following illustration; it shows WebSpheres capabilities, from the top down, in six distinct categories: People Integration, Process Integration, Information Integration, Application Integration, Application Infrastructure, and Accelerators. This abstract description of the WebSphere suite is probably less exciting than descriptions of the actual products; nevertheless, the capability classification is essential to defining WebSphere, because it provides the clearest orientation to the different areas that the WebSphere suite addresses.Modeling, simulating, and optimizing business processes Enabling you to efficiently manage, integrate, and access information that is scattered throughout the enterprise, and even beyond the enterprise in other companies with which you do businessApplication IntegrationWebSphere middleware ensures reliable and flexible information flow among diverse applications and organizations. The middleware enables reliability and seamless exchange of data among multiple applications. WebSphere software helps you manage the differences between multiple applications and business partners, and it adopts an enterprise-wide, flexible, service-oriented approach to integration.Application InfrastructureWebSphere middleware allows you to build, deploy, integrate, and enhance new and existing applications. You can quickly Web-enable green-screen applications, and adapt legacy applications for use in new Java environments. WebSphere delivers operational efficiency and enterprise quality of service for a mixed-workload infrastructure.AcceleratorsWebSphere Business Integration Accelerators are prebuilt accelerators that reduce deployment costs, provide increased cost savings, speed time to market, and lower business risk when using the IBM Business Integration infrastructure to solve customers business problems. IBM provides a set of accelerators for specific vertical industry processes. We also provide accelerators for processes that are common to many industries, such as multi-channel commerce. WebSphere Commerce provides prebuilt processes for business-to-consumer selling, as well as business-to-business selling. IBM also provides a set of prebuilt, industry-specific middleware designed to accelerate business transformation initiatives. As an example, IBM provides a prebuilt accelerator for improving supply chain integration in the electronics industry. This solution features WebSphere Business Integration Connect with a Rosettanet Accelerator. The middleware enables companies to send and receive electronic documents over the Internet, using the Rosettanet industry standard.In terms of increased cost savings:WebSphere Business Integration customers can reduce their integration costs by 50 percent when using prebuilt accelerators, when compared to traditional methods. Customers using prebuilt accelerators can achieve a high degree of reuse, reducing integration project costs and lowering total cost of ownership (TCO). Accelerators can speed the time to market:Prepackaged, out-of-the-box accelerators mean that customers are not bogged down in customer coding, allowing you to get to market more quickly. Customers are freed to focus on customization, not building and testing. Customers can focus on achieving business results, not on development.</Text>"
            + "<Text2>A. What is WebSphere?In todays business environment, it is increasingly important for all enterprises to develop and implement intelligent on demand strategies. In recent years, IBM has established itself as the industry leader in providing state-of-the-art on demand business solutions. In order to meet the rapidly emerging real-time requirements of businesses everywhere, IBM has launched the WebSphere software suite. WebSphere is an IBM software brand used to distinguish a suite of more than 300 software products. The magnitude of WebSphere has led to the misperception that WebSphere is too complex and expensive for SMBs. In truth, WebSphere is tremendously flexible, and WebSphere products are ideally suited to the on demand needs of both SMBs and large enterprises.IBM WebSphere software provides businesses with an IT infrastructure that maximizes both flexibility and responsiveness. By integrating people, processes, and information, WebSphere enables businesses to respond to changing business conditions with improved flexibility and speed. By optimizing the application infrastructure, WebSphere creates a reliable, high-performance environment for deploying and running applications-an environment that fosters operational excellence. And by extending the reach of IT, WebSphere enables companies to maximize the use of their IT infrastructure to reach users in new ways and support new business models.Figure 1: Business AlignmentIBM WebSphere software, with its integration and infrastructure capabilities, is a key part of IBM s overall middleware platform. IBM s middleware platform extends beyond WebSphere to include infrastructure management capabilities including security, provisioning, and infrastructure orchestration. The middleware also extends to business-driven development capabilities for improving the software life cycle management process. Furthermore, IBM s middleware platform provides business performance management capabilities, which extend the process integration and management capability of WebSphere to include the monitoring and optimization of the IT resources, which support your business processes. The IBM middleware platform is delivered as a modular product portfolio built on open standards. While functionally rich, the platform can be adopted incrementally, and you can start by adopting only those capabilities that you need to solve your most pressing business challenges. It provides integrated, role-based tools for application development and administration, utilizing a common installation, administration, security, and programming model. In short, the IBM middleware platform makes your technology infrastructure simple to develop, deploy, and manage.The WebSphere software suite enables on demand flexibility through a set of integration and infrastructure capabilities. Consider the following illustration; it shows WebSphere s capabilities, from the top down, in six distinct categories: People Integration, Process Integration, Information Integration, Application Integration, Application Infrastructure, and Accelerators. This abstract description of the WebSphere suite is probably less exciting than descriptions of the actual products; nevertheless, the capability classification is essential to defining WebSphere, because it provides the clearest orientation to the different areas that the WebSphere suite addresses.Modeling, simulating, and optimizing business processes Enabling you to efficiently manage, integrate, and access information that is scattered throughout the enterprise, and even beyond the enterprise in other companies with which you do businessApplication IntegrationWebSphere middleware ensures reliable and flexible information flow among diverse applications and organizations. The middleware enables reliability and seamless exchange of data among multiple applications. WebSphere software helps you manage the differences between multiple applications and business partners, and it adopts an enterprise-wide, flexible, service-oriented approach to integration.Application InfrastructureWebSphere middleware allows you to build, deploy, integrate, and enhance new and existing applications. You can quickly Web-enable green-screen applications, and adapt legacy applications for use in new Java environments. WebSphere delivers operational efficiency and enterprise quality of service for a mixed-workload infrastructure.AcceleratorsWebSphere Business Integration Accelerators are prebuilt accelerators that reduce deployment costs, provide increased cost savings, speed time to market, and lower business risk when using the IBM Business Integration infrastructure to solve customers  business problems. IBM provides a set of accelerators for specific vertical industry processes. We also provide accelerators for processes that are common to many industries, such as multi-channel commerce. WebSphere Commerce provides prebuilt processes for business-to-consumer selling, as well as business-to-business selling. IBM also provides a set of prebuilt, industry-specific middleware designed to accelerate business transformation initiatives. As an example, IBM provides a prebuilt accelerator for improving supply chain integration in the electronics industry. This solution features WebSphere Business Integration Connect with a Rosettanet Accelerator. The middleware enables companies to send and receive electronic documents over the Internet, using the Rosettanet industry standard.In terms of increased cost savings:WebSphere Business Integration customers can reduce their integration costs by 50 percent when using prebuilt accelerators, when compared to traditional methods. Customers using prebuilt accelerators can achieve a high degree of reuse, reducing integration project costs and lowering total cost of ownership (TCO). Accelerators can speed the time to market:Prepackaged, out-of-the-box accelerators mean that customers are not bogged down in customer coding, allowing you to get to market more quickly. Customers are freed to focus on customization, not building and testing. Customers can focus on achieving business results, not on development.</Text2>";

        mtom21.threshold.wsfvt.client.textplain.ImageDepot imageDepot = new mtom21.threshold.wsfvt.client.textplain.ObjectFactory().createImageDepot();
        imageDepot.setImageData(data);

        // Create a request bean with imagedepot bean as value
        mtom21.threshold.wsfvt.client.textplain.ObjectFactory factory = new mtom21.threshold.wsfvt.client.textplain.ObjectFactory();
        mtom21.threshold.wsfvt.client.textplain.Invoke request = factory.createInvoke();
        request.setInput(imageDepot);

        // SendImageResponse response = (SendImageResponse) dispatch.invoke(request);
        mtom21.threshold.wsfvt.client.textplain.SendImageResponse response = (mtom21.threshold.wsfvt.client.textplain.SendImageResponse) dispatch.invoke(request);
        String responseVal = response.getOutput();
        
        return verifyResponse(responseVal, mtomon);
    }

    /**
     * Call a Dispatch SOAPMessage client passing in an MTOMFeature with a threshold. If expected
     * MTOM usage matches actual, a good response is returned. Otherwise a bad response is returned.
     * 
     * @param mtomon true if MTOM is expected to be used on the client side
     * @param booleanParm The boolean parameter to use for the MTOMFeature constructor
     * @param threshold Threshold value
     * @return
     */
    public String callMTOMDipatchSOAPMessageClient(boolean mtomon, Boolean booleanParm, int threshold) throws Exception {
        System.out.println("threshold: " + threshold);

        QName serviceName = new QName("http://org/test/mtom21/VerifyMTOMProxy", "VerifyMTOMProxy");
        QName portName = new QName("http://org/test/mtom21/VerifyMTOMProxy", "VerifyMTOMProxyPort");
        String endpointUrl = "http://@REPLACE_WITH_HOST_NAME@:"
                + "@REPLACE_WITH_PORT_NUM@/threshold21/VerifyMTOMProxy";

        // read in the data
        Long fileSize = jpegFile1.length();
        byte[] data = new byte[fileSize.intValue()];
        FileInputStream fis = null;
        fis = new FileInputStream(jpegFile1);
        fis.read(data);

        //Setup the necessary JAX-WS artifacts
        Service svc = Service.create(serviceName);
        svc.addPort(portName, SOAPBinding.SOAP11HTTP_BINDING, endpointUrl);

        // set MTOM
        MTOMFeature mtom21 = null;
        if(booleanParm != null) {
            System.out.println("MTOMFeature(" + booleanParm + ", " + threshold + ")");
            mtom21 = new MTOMFeature(booleanParm, threshold);
        } else {
            mtom21 = new MTOMFeature(threshold);
            System.out.println("MTOMFeature(" + threshold + ")");
        }

        // create Dispatch
        Dispatch<SOAPMessage> dispatch = svc.createDispatch(portName, SOAPMessage.class, Service.Mode.MESSAGE, mtom21);

        // force SOAPAction to match with wsdl action
        ((BindingProvider) dispatch).getRequestContext().put(BindingProvider.SOAPACTION_USE_PROPERTY, true);
        ((BindingProvider) dispatch).getRequestContext().put(BindingProvider.SOAPACTION_URI_PROPERTY, "send");
       
        SOAPMessage request = null;
        // construct SOAPMessage
        request = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL).createMessage();
        SOAPBody body = request.getSOAPBody();
        SOAPBodyElement payload = body.addBodyElement(new QName("http://org/test/mtom21/VerifyMTOMProxy", "sendImage"));
        SOAPElement output = payload.addChildElement("input");
        SOAPElement imageData = output.addChildElement("imageData");
        String dataString = Base64.encode(data);
        imageData.addTextNode(dataString);

        // Send the image and process the response image
        SOAPMessage response = dispatch.invoke(request);

        // get the response value
        System.out.println("repsonse: " + response);
        SOAPBody responseBody = response.getSOAPBody();
        String responseVal = responseBody.getChildElements().next().toString();
        
        return verifyResponse(responseVal, mtomon);
    }

    /**
     * Make a call to a Proxy service with a threshold set to 20K. Examine the response for MTOM and
     * return a good result if the expected MTOM usage matches the actual.
     * 
     * @param mtomon true if MTOM is expected to be used on the server side
     * @param image The image to use
     * @return
     */
    public String callMTOMProxyServerThreshold(boolean mtomon, File image) throws Exception {

        QName serviceName = new QName("urn://VerifyMTOMProxyServer.mtom21.test.org", "VerifyMTOMProxyServerThreshold");
        QName portName = new QName("urn://VerifyMTOMProxyServer.mtom21.test.org", "VerifyMTOMProxyServerThresholdPort");
        String endpointUrl = "http://@REPLACE_WITH_HOST_NAME@:"
                + "@REPLACE_WITH_PORT_NUM@/threshold21/VerifyMTOMProxyServerThreshold";
        
        return callMTOMProxyServer(mtomon, image, serviceName, portName, endpointUrl);
    }
    
    /**
     * Make a call to a Proxy service with a threshold set to 20K and MTOM enabled equal to true.
     * Examine the response for MTOM and return a good result if the expected MTOM usage matches the
     * actual.
     * 
     * @param mtomon true if MTOM is expected to be used on the server side
     * @param image The image to use
     * @return
     */
    public String callMTOMProxyServerThresholdEnabledTrue(boolean mtomon, File image) throws Exception {

        QName serviceName = new QName("urn://VerifyMTOMProxyServer.mtom21.test.org", "VerifyMTOMProxyServerThresholdMTOMEnabledTrue");
        QName portName = new QName("urn://VerifyMTOMProxyServer.mtom21.test.org", "VerifyMTOMProxyServerThresholdMTOMEnabledTruePort");
        String endpointUrl = "http://@REPLACE_WITH_HOST_NAME@:"
                + "@REPLACE_WITH_PORT_NUM@/threshold21/VerifyMTOMProxyServerThresholdMTOMEnabledTrue";
        
        return callMTOMProxyServer(mtomon, image, serviceName, portName, endpointUrl);
    }

    /**
     * Make a call to a Proxy service with a threshold set to 20K and MTOM enabled equal to false.
     * Examine the response for MTOM and return a good result if the expected MTOM usage matches the
     * actual.
     * 
     * @param mtomon true if MTOM is expected to be used on the server side
     * @param image The image to use
     * @return
     */
    public String callMTOMProxyServerThresholdEnabledFalse(boolean mtomon, File image) throws Exception {

        QName serviceName = new QName("urn://VerifyMTOMProxyServer.mtom21.test.org", "VerifyMTOMProxyServerThresholdMTOMEnabledFalse");
        QName portName = new QName("urn://VerifyMTOMProxyServer.mtom21.test.org", "VerifyMTOMProxyServerThresholdMTOMEnabledFalsePort");
        String endpointUrl = "http://@REPLACE_WITH_HOST_NAME@:"
                + "@REPLACE_WITH_PORT_NUM@/threshold21/VerifyMTOMProxyServerThresholdMTOMEnabledFalse";
        
        return callMTOMProxyServer(mtomon, image, serviceName, portName, endpointUrl);
    }
    
    /**
     * Make a call to a Proxy service with a threshold set to 20K. Examine the response for MTOM and
     * return a good result if the expected MTOM usage matches the actual.
     * 
     * @param mtomon true if MTOM is expected to be used on the server side
     * @param image The image to use
     * @param serviceName The name of the service to call
     * @param portName The name of the port to call
     * @param endpointUrl The endpoint to invoke
     * @return
     */
    private String callMTOMProxyServer(boolean mtomon, File image, QName serviceName, QName portName, String endpointUrl) throws Exception {
        System.out.println("serviceName: " + serviceName);
        System.out.println("portName: " + portName);
        System.out.println("endpointUrl: " + endpointUrl);
        
        // read in the data
        Long fileSize = image.length();
        byte[] data = new byte[fileSize.intValue()];
        FileInputStream fis = null;
        fis = new FileInputStream(image);
        fis.read(data);
        
        //Setup the necessary JAX-WS artifacts
        Service svc = Service.create(serviceName);
        svc.addPort(portName, SOAPBinding.SOAP11HTTP_BINDING, endpointUrl);
        
        // create Dispatch
        Dispatch<SOAPMessage> dispatch = svc.createDispatch(portName, SOAPMessage.class, Service.Mode.MESSAGE);

        // force SOAPAction to match with wsdl action
        ((BindingProvider) dispatch).getRequestContext().put(BindingProvider.SOAPACTION_USE_PROPERTY, true);
        ((BindingProvider) dispatch).getRequestContext().put(BindingProvider.SOAPACTION_URI_PROPERTY, "sendImage");
       
        SOAPMessage request = null;
        // construct SOAPMessage
        request = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL).createMessage();
        SOAPBody body = request.getSOAPBody();
        SOAPBodyElement payload = body.addBodyElement(new QName("urn://VerifyMTOMProxyServer.mtom21.test.org", "invoke"));
        SOAPElement output = payload.addChildElement("input");
        SOAPElement imageData = output.addChildElement("imageData");
        String dataString = Base64.encode(data);
        imageData.addTextNode(dataString);

        // Send the image and process the response
        SOAPMessage response = dispatch.invoke(request);

        // check if there is an attachment present
        System.out.println("motmon: " + mtomon);
        System.out.println("attachments: " + response.getAttachments().hasNext());
        if(mtomon && response.getAttachments().hasNext()) {
            return positiveResult;
        } else if(!mtomon && !response.getAttachments().hasNext()) {
            return positiveResult;
        }else {
            return negativeResult;
        }
    }
    
    private String verifyResponse(String responseVal, boolean mtomon) {
        System.out.println("response value: " + responseVal);
        System.out.println("expected MTOM usage: " + mtomon);
        if(responseVal != null) {
            if(mtomon && responseVal.trim().indexOf("MTOMON") != -1) {
                return positiveResult;
            } else if(!mtomon && responseVal.trim().indexOf("MTOMOFF") != -1) {
                return positiveResult;
            } else {
                return negativeResult;
            }
        } else {
            System.out.println("Received null response.");
            return negativeResult;
        }
    }
}
