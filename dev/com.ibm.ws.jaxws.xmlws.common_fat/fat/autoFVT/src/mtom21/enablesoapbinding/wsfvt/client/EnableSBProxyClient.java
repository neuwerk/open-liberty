//
// @(#) 1.1 autoFVT/src/mtom21/enablesoapbinding/wsfvt/client/EnableSBProxyClient.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/15/08 14:33:28 [8/8/12 06:40:53]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006, 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// ----------------------------------------------------------------------------
// 03/10/08 jramos      LIDB4418-12.01  New File

package mtom21.enablesoapbinding.wsfvt.client;

import java.awt.Image;
import java.io.*;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.imageio.ImageIO;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.soap.SOAPBinding;

import org.apache.axiom.om.util.Base64;
import org.test.mtom21.enablesb_proxy.*;

/**
 * This testcase uses a JAXB generated objects
 * This testcase covers the "multipart/*" mime types, 
 * using Proxy to send a jpeg image.
 * Service has MTOM = ON with SOAP11
 * Client has no setting of MTOM (uses default)
 */


public class EnableSBProxyClient {

	public static final String goodResult = "Message processed";
    public static final String badResult = "Problem in processing message";

    String xmlFilename = "catalog.xml"; // about 31KB in size, has NO xml header
    String plainTextFilename = "plainText.txt";

    String jpegFilename1 = "image1.jpeg"; // about 10KB
    String jpegOptimizedFilename1 = "image1Expected.jpeg";

    String jpegFilename2 = "image2.jpeg"; // about 60KB
    String jpegOptimizedFilename2 = "image2Expected.jpeg";

    String sendBackStr = null;

    QName serviceName = new QName("http://ws.apache.org/axis2", "EnableSBProxyService");
    QName portName = new QName("http://ws.apache.org/axis2", "AttachmentServicePort");

    String endpointUrl = "http://@REPLACE_WITH_HOST_NAME@:"
            + "@REPLACE_WITH_PORT_NUM@/enablesoapbinding21/EnableSBProxyService";
    Service.Mode mode = Service.Mode.PAYLOAD;



	public static void main(String[] args) {
		try {
            EnableSBProxyClient client = new EnableSBProxyClient();
            client.Proxy_AttachmentFromXMLFile();
            client.Proxy_AttachmentLargeJpeg();
            client.Proxy_AttachmentSmallJpeg();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * This client method calls into the service and verfies that MTOM is used over the wire on
     * the response. It corresponds to the Proxy_AttachmentSmallJpeg method which verifies that the
     * image is sent and received correctly.
     * 
     * @return
     * @throws Exception
     */
    public String proxy_AttachmentSmallJpeg_VerifyMTOM() throws Exception {

        // read in the data
        File jpegFile = new File(jpegFilename1);
        Long fileSize = jpegFile.length();
        byte[] data = new byte[fileSize.intValue()];
        FileInputStream fis = null;
        fis = new FileInputStream(jpegFile);
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
        SOAPBodyElement payload = body.addBodyElement(new QName("http://org/test/mtom21/EnableSB_proxy", "sendImage"));
        SOAPElement output = payload.addChildElement("input");
        SOAPElement imageData = output.addChildElement("imageData");
        String dataString = Base64.encode(data);
        imageData.addTextNode(dataString);

        // Send the image and process the response
        SOAPMessage response = dispatch.invoke(request);

        // check if there is an attachment present
        if(response.getAttachments().hasNext()) {
            return goodResult;
        } else {
            System.out.println("MTOM not used on response.");
            return badResult;
        }
    }

	/**
	 * This testcase uses a JAXWS Proxy with JAXB generated request object
	 * as parameter. 
	 * 
	 * - Service has MTOM enabled with SOAP11
	 * - sends small image (12K)
     * - uses no setting for MTOM from client
     * - mode = PAYLOAD
 	 * - no setting for SOAP version
     * @return returned from the remote method
     */

    public String Proxy_AttachmentSmallJpeg() throws Exception {

        // debug
        System.out.println("\n*** In EnableSBProxyClient.Proxy_AttachmentSmallJpeg ***\n");

        try {

            File file = new File(jpegFilename1);
            System.out.println(">> Loading data from " + jpegFilename1);
            FileDataSource fds = new FileDataSource(file);
            DataHandler content = new DataHandler(fds);

            // Set the data inside of the appropriate object
            ImageDepot imageDepot = new ObjectFactory().createImageDepot();
            imageDepot.setImageData(content);

            if (imageDepot != null) {
                System.out.println("Data loaded.");

            } else {
                System.out.println("[ERROR] Could not load data");
                System.exit(-1);
            }

            // Setup the necessary JAX-WS artifacts
            Service svc = Service.create(serviceName);
            EnableSBProxyInterface proxy = svc.getPort(portName, EnableSBProxyInterface.class);

            // Set the target URL
            BindingProvider bp = (BindingProvider) proxy;
            Map<String, Object> requestCtx = bp.getRequestContext();
            requestCtx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl);

            // Enable MTOM
            // SOAPBinding binding = (SOAPBinding) bp.getBinding();
            // binding.setMTOMEnabled(false);

            // Send the image and process the response image
            ImageDepot response = proxy.sendImage(imageDepot);
            if (response != null) {
                System.out.println("-- Response received");
                System.out.println("-- Writing returned image to file.");

                DataHandler dh = response.getImageData();

                File f = null;
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
                Image imageReceived = ImageIO.read(new File("ReceivedFile.jpeg"));
                Image imageSent = ImageIO.read(new File(jpegFilename1));
                Image optImage = ImageIO.read(new File(jpegOptimizedFilename1));

                if (AttachmentHelper.compareImages(imageReceived, imageSent) == true) {
                    // debug
                    printMessageToConsole("Image received equals to image sent");
                    sendBackStr = goodResult;
                } else if (AttachmentHelper.compareImages(imageReceived, optImage) == true) {
                    // debug
                    printMessageToConsole("Image received equals to optimized image");
                    sendBackStr = goodResult;
                } else {
                    // debug
                    printMessageToConsole("Images not equal:returned image and image sent");

                    sendBackStr = badResult;
                }

            } else {
                System.out.println(">> [ERROR] - Response from the server was NULL");
            }

        } catch (Exception ex) {
            System.out.println("Proxy_AttachmentSmallJpeg Client failed with exception.");
            ex.printStackTrace();
        }

        return sendBackStr;
    }
    
    /**
     * This client method calls into the service and verfies that MTOM is used over the wire on
     * the response. It corresponds to the Proxy_AttachmentLargeJpeg method which verifies that the
     * image is sent and received correctly.
     * 
     * @return
     * @throws Exception
     */
    public String proxy_AttachmentLargeJpeg_VerifyMTOM() throws Exception {

        // read in the data
        File jpegFile = new File(jpegFilename2);
        Long fileSize = jpegFile.length();
        byte[] data = new byte[fileSize.intValue()];
        FileInputStream fis = null;
        fis = new FileInputStream(jpegFile);
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
        SOAPBodyElement payload = body.addBodyElement(new QName("http://org/test/mtom21/EnableSB_proxy", "sendImage"));
        SOAPElement output = payload.addChildElement("input");
        SOAPElement imageData = output.addChildElement("imageData");
        String dataString = Base64.encode(data);
        imageData.addTextNode(dataString);

        // Send the image and process the response
        SOAPMessage response = dispatch.invoke(request);

        // check if there is an attachment present
        if(response.getAttachments().hasNext()) {
            return goodResult;
        } else {
            System.out.println("MTOM not used on response.");
            return badResult;
        }
    }
    
	/**
     * This testcase uses a JAXWS Proxy with JAXB generated request object as parameter.
     *  - Service has MTOM enabled with SOAP11 - sends small image (12K) - uses no setting for MTOM
     * from client - mode = PAYLOAD - no setting for SOAP version
     * 
     * @return returned from the remote method
     */

    public String Proxy_AttachmentLargeJpeg() throws Exception {

        // debug
        System.out.println("\n*** In EnableSBProxyClient.Proxy_AttachmentLargeJpeg ***\n");

        try {

            File file = new File(jpegFilename2);
            System.out.println(">> Loading data from " + jpegFilename2);
            FileDataSource fds = new FileDataSource(file);
            DataHandler content = new DataHandler(fds);

            // Set the data inside of the appropriate object
            ImageDepot imageDepot = new ObjectFactory().createImageDepot();
            imageDepot.setImageData(content);

            if (imageDepot != null) {
                System.out.println("Data loaded.");

            } else {
                System.out.println("[ERROR] Could not load data");
                System.exit(-1);
            }

            // Setup the necessary JAX-WS artifacts
            Service svc = Service.create(serviceName);
            EnableSBProxyInterface proxy = svc.getPort(portName, EnableSBProxyInterface.class);

            // Set the target URL
            BindingProvider bp = (BindingProvider) proxy;
            Map<String, Object> requestCtx = bp.getRequestContext();
            requestCtx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl);

            // Enable MTOM
            // SOAPBinding binding = (SOAPBinding) bp.getBinding();
            // binding.setMTOMEnabled(false);

            // Send the image and process the response image
            ImageDepot response = proxy.sendImage(imageDepot);
            if (response != null) {
                System.out.println("-- Response received");

                DataHandler dh = response.getImageData();

                File f = null;
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
                Image imageReceived = ImageIO.read(new File("ReceivedFile.jpeg"));
                Image imageSent = ImageIO.read(new File(jpegFilename2));
                Image optImage = ImageIO.read(new File(jpegOptimizedFilename2));

                if (AttachmentHelper.compareImages(imageReceived, imageSent) == true) {
                    // debug
                    printMessageToConsole("Image received equals to image sent");
                    sendBackStr = goodResult;
                } else if (AttachmentHelper.compareImages(imageReceived, optImage) == true) {
                    // debug
                    printMessageToConsole("Image received equals to optimized image");
                    sendBackStr = goodResult;
                } else {
                    // debug
                    printMessageToConsole("Images not equal:returned image and image sent");

                    sendBackStr = badResult;
                }

            } else {
                System.out.println(">> [ERROR] - Response from the server was NULL");
            }

        } catch (Exception ex) {
            System.out.println("Proxy_AttachmentSmallJpeg Client failed with exception.");
            ex.printStackTrace();
        }

        return sendBackStr;
    }
    
    /**
     * This client method calls into the service and verfies that MTOM is used over the wire on
     * the response. It corresponds to the Proxy_AttachmentFromXMLFile method which verifies that the
     * image is sent and received correctly.
     * 
     * @return
     * @throws Exception
     */
    public String proxy_AttachmentFromXMLFile_VerifyMTOM() throws Exception {

        // read in the data
        File jpegFile = new File(xmlFilename);
        Long fileSize = jpegFile.length();
        byte[] data = new byte[fileSize.intValue()];
        FileInputStream fis = null;
        fis = new FileInputStream(jpegFile);
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
        SOAPBodyElement payload = body.addBodyElement(new QName("http://org/test/mtom21/EnableSB_proxy", "sendImage"));
        SOAPElement output = payload.addChildElement("input");
        SOAPElement imageData = output.addChildElement("imageData");
        String dataString = Base64.encode(data);
        imageData.addTextNode(dataString);

        // Send the image and process the response
        SOAPMessage response = dispatch.invoke(request);

        // check if there is an attachment present
        if(response.getAttachments().hasNext()) {
            return goodResult;
        } else {
            System.out.println("MTOM not used on response.");
            return badResult;
        }
    }

	/**
     * This testcase uses a JAXWS Proxy with JAXB generated request object as parameter. - Sends a
     * text read from a text file - Service has MTOM enabled with SOAP11 - sends small image (12K) -
     * uses no setting for MTOM from client - mode = PAYLOAD - no setting for SOAP version
     * 
     * @return returned from the remote method
     */

    public String Proxy_AttachmentFromXMLFile() throws Exception {

        // debug
        System.out.println("\n*** In EnableSBProxyClient.Proxy_AttachmentFromXMLFile ***\n");

        try {

            File file = new File(xmlFilename);
            System.out.println(">> Loading data from " + xmlFilename);
            FileDataSource fds = new FileDataSource(file);
            DataHandler content = new DataHandler(fds);

            //Set the data inside of the appropriate object
            ImageDepot imageDepot = new ObjectFactory().createImageDepot();
            imageDepot.setImageData(content);

            if (imageDepot != null) {
                System.out.println("Data loaded.");

            } else {
                System.out.println("[ERROR] Could not load data");
                System.exit(-1);
            }

            //Setup the necessary JAX-WS artifacts
            Service svc = Service.create(serviceName);
            EnableSBProxyInterface proxy = svc.getPort(portName, EnableSBProxyInterface.class);

            //Set the target URL
            BindingProvider bp = (BindingProvider) proxy;
            Map<String, Object> requestCtx = bp.getRequestContext();
            requestCtx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl);

            //Enable MTOM
            //SOAPBinding binding = (SOAPBinding) bp.getBinding();
            //binding.setMTOMEnabled(false);

            //Send the message and process the response message
            ImageDepot response = proxy.sendImage(imageDepot);
            String recFile = "ReceivedFile.txt";

            if (response != null) {
                System.out.println("-- Response received");

                DataHandler dh = response.getImageData();

                File f = null;

                if (dh != null) {
                    // write to current directory, whatever it is
                    f = new File(recFile);
                    if (f.exists()) {
                        f.delete();
                    }
                }

                FileOutputStream fos = new FileOutputStream(f);
                dh.writeTo(fos);
                fos.close();

                if (AttachmentHelper.compareTextFiles(recFile, xmlFilename)) {
                    sendBackStr = goodResult;
                    printMessageToConsole("File received is the same as file sent");

                } else {
                    sendBackStr = badResult;
                    printMessageToConsole("Files not equal.");

                }

            } else {
                System.out.println(">> [ERROR] - Response from the server was NULL");
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
