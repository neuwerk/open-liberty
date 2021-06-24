//
// @(#) 1.1 autoFVT/src/mtom21/enablejaxb/wsfvt/client/EnableJAXBProxyImageJpegClient.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/15/08 14:29:40 [8/8/12 06:40:49]
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

import javax.imageio.ImageIO;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.soap.MTOMFeature;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.BindingProvider;

import org.test.mtom21.enablejaxbproxy_imagejpeg.*;

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

    String xmlFilename = "catalog.xml"; // about 31KB in size, has NO xml header

    String jpegFilename1 = "image1.jpeg"; // about 10KB
    String jpegOptimizedFilename1 = "image1Expected.jpeg";
    String jpegFilename2 = "image2.jpeg"; // about 60KB
    String jpegOptimizedFilename2 = "image2Expected.jpeg";
    String jpegFilename3 = "image3.jpeg";
    String jpegOptimizedFilename3 = "image3Expected.jpeg";

    String sendBackStr = null;

    public static final QName serviceName = new QName("http://ws.apache.org/axis2", "EnableJAXBProxyImageJpegService");
    public static final QName portName = new QName("http://ws.apache.org/axis2", "AttachmentServicePort");
    public static final String endpointUrl = "http://@REPLACE_WITH_HOST_NAME@:"
            + "@REPLACE_WITH_PORT_NUM@/enablejaxb21/EnableJAXBProxyImageJpegService";

    public static final QName serviceNameVerify = new QName("http://ws.apache.org/axis2", "EnableJAXBProxyImageJpegServiceVerifyMTOM");
    public static final QName portNameVerify = new QName("http://ws.apache.org/axis2", "AttachmentServicePortVerifyMTOM");
    public static final String endpointUrlVerify = "http://@REPLACE_WITH_HOST_NAME@:"
            + "@REPLACE_WITH_PORT_NUM@/enablejaxb21/EnableJAXBProxyImageJpegServiceVerifyMTOM";

    String bindingID = SOAPBinding.SOAP11HTTP_BINDING;

    Service.Mode mode = Service.Mode.PAYLOAD;

    public static void main(String[] args) {
        try {
            EnableJAXBProxyImageJpegClient client = new EnableJAXBProxyImageJpegClient();
            client.Proxy_AttachmentSmallJpeg();
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
        System.out.println("\n*** In EnableJAXBProxyImageJpegClient.proxy_AttachmentSmallJpeg_VerifyMTOM ***\n");

        System.out.println(">> Loading data from " + jpegFilename1);

        Image image = ImageIO.read(new File(jpegFilename1));
        Image optImage = ImageIO.read(new File(jpegOptimizedFilename1));

        ImageDepot imageDepot = new ObjectFactory().createImageDepot();
        imageDepot.setImageData(image);

        // Create a request bean with imagedepot bean as value
        ObjectFactory factory = new ObjectFactory();
        SendImage request = factory.createSendImage();
        request.setInput(imageDepot);

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

            Image image = ImageIO.read(new File(jpegFilename1));
            Image optImage = ImageIO.read(new File(jpegOptimizedFilename1));

            ImageDepot imageDepot = new ObjectFactory().createImageDepot();
            imageDepot.setImageData(image);

            // Create a request bean with imagedepot bean as value
            ObjectFactory factory = new ObjectFactory();
            SendImage request = factory.createSendImage();
            request.setInput(imageDepot);

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
            Image imageReceived = response.getImageData();

            if (AttachmentHelper.compareImages(imageReceived, image) == true) {
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
