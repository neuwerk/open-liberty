//
// @(#) 1.1 autoFVT/src/mtom21/enablejaxb/wsfvt/client/EnableJAXBProxyDataHandlerClient.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/15/08 14:29:39 [8/8/12 06:40:49]
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

package mtom21.enablejaxb.wsfvt.client;

import java.awt.Image;
import java.io.*;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.imageio.ImageIO;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.soap.MTOMFeature;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.BindingProvider;

import org.test.mtom21.enablejaxbproxy_datahandler.*;

/**
 * This testcase uses a JAXB generated objects This testcase covers the "multipart/*" mime types,
 * using Proxy
 */

public class EnableJAXBProxyDataHandlerClient {

    public static final String goodResult = "Message processed";
    public static final String badResult = "Problem in processing message";

    String plainStr = "test input";

    // short string in XML format
    String s1 = "<invoke>test input</invoke>";

    // long string of 10KB

    String xmlFilename = "catalog.xml"; // about 31KB in size, has NO xml header
    String smallXMLFilename = "smallCatalog.xml";
    String gifFilename = "pic_01.gif";
    String plainTextFilename = "plainText.txt";

    String jpegFilename1 = "image1.jpeg"; // about 10KB
    String jpegOptimizedFilename1 = "image1Expected.jpeg";
    String jpegNoOptFilename1 = "image1NoOpt.jpeg";

    String jpegFilename2 = "image2.jpeg"; // about 60KB
    String jpegOptimizedFilename2 = "image2Expected.jpeg";

    String jpegFilename3 = "image3.jpeg";
    String jpegOptimizedFilename3 = "image3Expected.jpeg";

    String sendBackStr = null;

    public static final QName serviceName = new QName("http://ws.apache.org/axis2", "EnableJAXBProxyDataHandlerService");
    public static final QName portName = new QName("http://ws.apache.org/axis2", "AttachmentServicePort");
    public static final String endpointUrl = "http://@REPLACE_WITH_HOST_NAME@:"
            + "@REPLACE_WITH_PORT_NUM@/enablejaxb21/EnableJAXBProxyDataHandlerService";

    public static final QName serviceNameVerify = new QName("http://ws.apache.org/axis2", "EnableJAXBProxyDataHandlerServiceVerifyMTOM");
    public static final QName portNameVerify = new QName("http://ws.apache.org/axis2", "AttachmentServicePortVerifyMTOM");
    public static final String endpointUrlVerify = "http://@REPLACE_WITH_HOST_NAME@:"
            + "@REPLACE_WITH_PORT_NUM@/enablejaxb21/EnableJAXBProxyDataHandlerServiceVerifyMTOM";

    String bindingID = SOAPBinding.SOAP11HTTP_BINDING;

    Service.Mode mode = Service.Mode.PAYLOAD;

    public static void main(String[] args) {
        try {
            EnableJAXBProxyDataHandlerClient client = new EnableJAXBProxyDataHandlerClient();
            client.Proxy_AttachmentLargeJpeg();
            client.Proxy_AttachmentSmallJpeg();
            client.Proxy_SequenceLargeJpeg();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * This client method calls into a service that verfies that MTOM is used over the wire on
     * the request. It corresponds to the Proxy_AttachmentSmallJpeg method which verifies that the
     * image is sent and received correctly.
     * 
     * @return
     * @throws Exception
     */
    public String proxy_AttachmentSmallJpeg_VerifyMTOM() throws Exception {

        // debug
        System.out.println("\n*** In EnableJAXBProxyDataHandlerClient.proxy_AttachmentSmallJpeg_VerifyMTOM ***\n");

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

        // Enable MTOM
        MTOMFeature mtom21 = new MTOMFeature();

        // Setup the necessary JAX-WS artifacts
        Service svc = Service.create(serviceNameVerify);
        ImageServiceInterfaceVerifyMTOM proxy = svc.getPort(portNameVerify, ImageServiceInterfaceVerifyMTOM.class, mtom21);

        // Set the target URL
        BindingProvider bp = (BindingProvider) proxy;
        Map<String, Object> requestCtx = bp.getRequestContext();
        requestCtx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrlVerify);

        // Send the image and process the response image
        String response = proxy.sendImage(imageDepot);
        System.out.println("response: " + response);
        if(response != null && response.indexOf("MTOMON") != -1) {
            return goodResult;
        } else {
            return badResult;
        }
    }

    /**
     * This testcase uses a JAXWS Proxy with JAXB generated request object as parameter.
     *  - MTOM enabled via this client - sends small image (12K) - mode = PAYLOAD
     * @return returned from the remote method
     */

    public String Proxy_AttachmentSmallJpeg() throws Exception {

        // debug
        System.out.println("\n*** In EnableJAXBProxyDataHandlerClient.Proxy_AttachmentSmallJpeg ***\n");

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

            // Enable MTOM
            MTOMFeature mtom21 = new MTOMFeature();

            // Setup the necessary JAX-WS artifacts
            Service svc = Service.create(serviceName);
            ImageServiceInterface proxy = svc.getPort(portName, ImageServiceInterface.class, mtom21);

            // Set the target URL
            BindingProvider bp = (BindingProvider) proxy;
            Map<String, Object> requestCtx = bp.getRequestContext();
            requestCtx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl);

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
     * This client method calls into a service that verfies that MTOM is used over the wire on
     * the request. It corresponds to the Proxy_AttachmentLargeJpeg method which verifies that the
     * image is sent and received correctly.
     * 
     * @return
     * @throws Exception
     */
    public String proxy_AttachmentLargeJpeg_VerifyMTOM() throws Exception {

        // debug
        System.out.println("\n*** In EnableJAXBProxyDataHandlerClient.proxy_AttachmentLargeJpeg_VerifyMTOM ***\n");

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

        // Enable MTOM
        MTOMFeature mtom21 = new MTOMFeature(true);

        // Setup the necessary JAX-WS artifacts
        Service svc = Service.create(serviceNameVerify);
        ImageServiceInterfaceVerifyMTOM proxy = svc.getPort(portNameVerify, ImageServiceInterfaceVerifyMTOM.class, mtom21);

        // Set the target URL
        BindingProvider bp = (BindingProvider) proxy;
        Map<String, Object> requestCtx = bp.getRequestContext();
        requestCtx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrlVerify);

        // Send the image and process the response image
        String response = proxy.sendImage(imageDepot);
        System.out.println("response: " + response);
        if(response != null && response.indexOf("MTOMON") != -1) {
            return goodResult;
        } else {
            return badResult;
        }
    }

    public String Proxy_AttachmentLargeJpeg() throws Exception {

        // debug
        System.out.println("\n*** In EnableJAXBProxyDataHandlerClient.Proxy_AttachmentLargeJpeg ***\n");

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

            // Enable MTOM
            MTOMFeature mtom21 = new MTOMFeature(true);

            // Setup the necessary JAX-WS artifacts
            Service svc = Service.create(serviceName);
            ImageServiceInterface proxy = svc.getPort(portName, ImageServiceInterface.class, mtom21);

            // Set the target URL
            BindingProvider bp = (BindingProvider) proxy;
            Map<String, Object> requestCtx = bp.getRequestContext();
            requestCtx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl);

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
     * This client method calls into a service that verfies that MTOM is used over the wire on
     * the request. It corresponds to the Proxy_SequenceLargeJpeg method which verifies that the
     * image is sent and received correctly.
     * 
     * @return
     * @throws Exception
     */
    public String proxy_SequenceLargeJpeg_VerifyMTOM() throws Exception {

        // debug
        System.out.println("\n*** In EnableJAXBProxyDataHandlerClient.proxy_SequenceLargeJpeg_VerifyMTOM ***\n");

        File file = new File(jpegFilename2);
        System.out.println(">> Loading data from " + jpegFilename2);
        FileDataSource fds = new FileDataSource(file);
        DataHandler content = new DataHandler(fds);

        // Set the data inside of the appropriate object
        ImageDepot imageDepot = new ObjectFactory().createImageDepot();
        imageDepot.setImageData(content);

        if (imageDepot != null) {
            System.out.println("Data loaded = " + imageDepot.getImageData().getContent().toString());

        } else {
            System.out.println("[ERROR] Could not load data");
            System.exit(-1);
        }

        // Enable MTOM
        MTOMFeature mtom21 = new MTOMFeature();

        // Setup the necessary JAX-WS artifacts
        Service svc = Service.create(serviceNameVerify);
        ImageServiceInterfaceVerifyMTOM proxy = svc.getPort(portNameVerify, ImageServiceInterfaceVerifyMTOM.class, mtom21);

        // Set the target URL
        BindingProvider bp = (BindingProvider) proxy;
        Map<String, Object> requestCtx = bp.getRequestContext();
        requestCtx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrlVerify);

        // Send the image and process the response image
        String response = proxy.sendImage(imageDepot);
        System.out.println("response: " + response);
        if(response == null || response.indexOf("MTOMON") == -1) {
            return badResult;
        }

        // ---------------turn MTOM off --------
        mtom21 = new MTOMFeature(false);
        proxy = svc.getPort(portNameVerify, ImageServiceInterfaceVerifyMTOM.class, mtom21);

        // Set the target URL
        bp = (BindingProvider) proxy;
        requestCtx = bp.getRequestContext();
        requestCtx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrlVerify);

        // Send the image and process the response image
        response = proxy.sendImage(imageDepot);
        System.out.println("response: " + response);
        if(response == null || response.indexOf("MTOMOFF") == -1) {
            return badResult;
        }

        // --------------turn MTOM on again ---------
        mtom21 = new MTOMFeature(true);
        proxy = svc.getPort(portNameVerify, ImageServiceInterfaceVerifyMTOM.class, mtom21);

        // Set the target URL
        bp = (BindingProvider) proxy;
        requestCtx = bp.getRequestContext();
        requestCtx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrlVerify);

        // Send the image and process the response image
        response = proxy.sendImage(imageDepot);
        System.out.println("response: " + response);
        if(response == null || response.indexOf("MTOMON") == -1) {
            return badResult;
        }
        
        return goodResult;
    }
    
    public String Proxy_SequenceLargeJpeg() throws Exception {

        // debug
        System.out.println("\n*** In EnableJAXBProxyDataHandlerClient.Proxy_AttachmentLargeJpeg ***\n");

        try {
            boolean test1 = true, test2 = true, test3 = true;

            File file = new File(jpegFilename2);
            System.out.println(">> Loading data from " + jpegFilename2);
            FileDataSource fds = new FileDataSource(file);
            DataHandler content = new DataHandler(fds);

            // Set the data inside of the appropriate object
            ImageDepot imageDepot = new ObjectFactory().createImageDepot();
            imageDepot.setImageData(content);

            if (imageDepot != null) {
                System.out.println("Data loaded = " + imageDepot.getImageData().getContent().toString());

            } else {
                System.out.println("[ERROR] Could not load data");
                System.exit(-1);
            }

            // Enable MTOM
            MTOMFeature mtom21 = new MTOMFeature();

            // Setup the necessary JAX-WS artifacts
            Service svc = Service.create(serviceName);
            ImageServiceInterface proxy = svc.getPort(portName, ImageServiceInterface.class, mtom21);

            // Set the target URL
            BindingProvider bp = (BindingProvider) proxy;
            Map<String, Object> requestCtx = bp.getRequestContext();
            requestCtx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl);

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
                Image imageSent = ImageIO.read(new File(jpegFilename2));
                Image optImage = ImageIO.read(new File(jpegOptimizedFilename2));

                if (AttachmentHelper.compareImages(imageReceived, imageSent) == true) {
                    // debug
                    printMessageToConsole("Client MTOM ON - Image received equals to image sent");
                } else if (AttachmentHelper.compareImages(imageReceived, optImage) == true) {
                    // debug
                    printMessageToConsole("Client MTOM ON - Image received equals to optimized image");
                } else {
                    // debug
                    printMessageToConsole("Client MTOM ON - Images not equal:returned image and image sent");
                    test1 = false;
                }
            } else {
                System.out.println(">> [ERROR] - Response from the server was NULL");
                test1 = false;
            }

            // ---------------turn MTOM off --------
            mtom21 = new MTOMFeature(false);
            proxy = svc.getPort(portName, ImageServiceInterface.class, mtom21);

            // Set the target URL
            bp = (BindingProvider) proxy;
            requestCtx = bp.getRequestContext();
            requestCtx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl);

            // Send the image and process the response image
            response = proxy.sendImage(imageDepot);
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
                Image imageSent = ImageIO.read(new File(jpegFilename2));
                Image optImage = ImageIO.read(new File(jpegOptimizedFilename2));

                if (AttachmentHelper.compareImages(imageReceived, imageSent) == true) {
                    // debug
                    printMessageToConsole("Client MTOM OFF - Image received equals to image sent");
                } else if (AttachmentHelper.compareImages(imageReceived, optImage) == true) {
                    // debug
                    printMessageToConsole("Client MTOM OFF - Image received equals to optimized image");
                } else {
                    // debug
                    printMessageToConsole("Client MTOM OFF - Images not equal:returned image and image sent");
                    test2 = false;
                }
            } else {
                System.out.println(">> [ERROR] - Response from the server was NULL");
                test2 = false;
            }

            // --------------turn MTOM on again ---------
            mtom21 = new MTOMFeature(true);
            proxy = svc.getPort(portName, ImageServiceInterface.class, mtom21);

            // Set the target URL
            bp = (BindingProvider) proxy;
            requestCtx = bp.getRequestContext();
            requestCtx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl);

            // Send the image and process the response image
            response = proxy.sendImage(imageDepot);
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
                Image imageSent = ImageIO.read(new File(jpegFilename2));
                Image optImage = ImageIO.read(new File(jpegOptimizedFilename2));

                if (AttachmentHelper.compareImages(imageReceived, imageSent) == true) {
                    // debug
                    printMessageToConsole("Client MTOM ON - Image received equals to image sent");
                } else if (AttachmentHelper.compareImages(imageReceived, optImage) == true) {
                    // debug
                    printMessageToConsole("Client MTOM ON - Image received equals to optimized image");
                } else {
                    // debug
                    printMessageToConsole("Client MTOM ON - Images not equal:returned image and image sent");
                    test3 = false;
                }
            } else {
                System.out.println(">> [ERROR] - Response from the server was NULL");
                test3 = false;
            }

            if (test1 && test2 && test3) {
                sendBackStr = goodResult;
            } else {
                sendBackStr = badResult;
            }

        } catch (Exception ex) {
            System.out.println("Proxy_AttachmentSmallJpeg Client failed with exception.");
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
