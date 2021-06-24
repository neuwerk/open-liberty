//
// @(#) 1.1 autoFVT/src/mtom21/enablejaxb/wsfvt/client/EnableJAXBImageJpegHEXClient.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/15/08 14:29:34 [8/8/12 06:40:49]
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
import javax.imageio.ImageIO;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.MTOMFeature;
import javax.xml.ws.soap.SOAPBinding;

import org.test.mtom21.enablejaxb_imagejpeghex.*;

/**
 * This testcase uses a JAXB generated objects for binary type = hexBinary. - covers the
 * "image/jpeg" MIME type using Dispatch client. - service has MTOM = default (no setting) -
 * exclusively enables MTOM using binding.setMTOMEnabled(true);
 */

public class EnableJAXBImageJpegHEXClient {

    public static final String goodResult = "Message processed";
    public static final String badResult = "Problem in processing message";

    String jpegFilename1 = "image1.jpeg";
    String jpegFilename2 = "image2.jpeg";

    String jpegFilename3 = "image3.jpeg";
    String jpegExpectedFilename2 = "image2Expected.jpeg";

    String jpegFromByteFilename1 = "image1Byte.jpeg";
    String jpegFromByteFilename2 = "image2Byte.jpeg";

    String sendBackStr = null;

    public static final QName serviceName = new QName("http://ws.apache.org/axis2", "EnableJAXBImageJpegHEXService");
    public static final QName portName = new QName("http://ws.apache.org/axis2", "AttachmentServicePort");
    public static final String endpointUrl = "http://@REPLACE_WITH_HOST_NAME@:"
            + "@REPLACE_WITH_PORT_NUM@/enablejaxb21/EnableJAXBImageJpegHEXService";

    public static final QName serviceNameVerify = new QName("http://ws.apache.org/axis2", "EnableJAXBImageJpegHEXServiceVerifyMTOM");
    public static final QName portNameVerify = new QName("http://ws.apache.org/axis2", "AttachmentServicePortVerifyMTOM");
    public static final String endpointUrlVerify = "http://@REPLACE_WITH_HOST_NAME@:"
            + "@REPLACE_WITH_PORT_NUM@/enablejaxb21/EnableJAXBImageJpegHEXServiceVerifyMTOM";

    public static final Service.Mode mode = Service.Mode.PAYLOAD;

    public static void main(String[] args) {
        try {
            EnableJAXBImageJpegHEXClient client = new EnableJAXBImageJpegHEXClient();
            client.JAXB_AttachmentLargeJpeg();
            client.JAXB_AttachmentSmallJpeg();
            client.JAXB_Sequence_SmallJpeg();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * This client method calls into a service that verfies that MTOM is NOT used over the wire on
     * the request. It corresponds to the JAXB_AttachmentSmallJpeg method which verifies that the
     * image is sent and received correctly.
     * 
     * @return
     * @throws Exception
     */
    public String jaxb_AttachmentSmallJpeg_VerifyMTOM() throws Exception {

        // debug
        System.out.println("\n*** In EnableJAXBImageJpegHEXClient.jaxb_AttachmentSmallJpeg_VerifyMTOM ***\n");

        String bindingID = SOAPBinding.SOAP11HTTP_BINDING;

        // create a Dynamic service & port
        Service svc = Service.create(serviceNameVerify);
        svc.addPort(portNameVerify, bindingID, endpointUrlVerify);
        // debug
        System.out
                .println("portname = " + portNameVerify + ",bindingID = " + bindingID + ",endpointUrl = " + endpointUrlVerify);

        // jaxb artifacts package
        JAXBContext jbc = JAXBContext.newInstance("org.test.mtom21.enablejaxb_imagejpeghex");

        // set MTOM on dispatch
        MTOMFeature mtom21 = new MTOMFeature(true);

        Dispatch<Object> dispatch = svc.createDispatch(portNameVerify, jbc, mode, mtom21);

        Image image = ImageIO.read(new File(jpegFilename1));

        byte[] byteImage = AttachmentHelper.getImageBytes(image, "image/jpeg");
        ImageDepot imageDepot = new ObjectFactory().createImageDepot();
        imageDepot.setImageData(byteImage);

        // Create a request bean with imagedepot bean as value
        ObjectFactory factory = new ObjectFactory();
        Invoke request = factory.createInvoke();
        request.setInput(imageDepot);

        SendImageResponseVerify response = (SendImageResponseVerify) dispatch.invoke(request);
        String responseVal = response.getOutput();
        if(responseVal == null || responseVal.indexOf("MTOMOFF") == -1) {
            return badResult;
        } else {
            return goodResult;
        }
    }

    /**
     * This testcase uses a JAXWS Dispatch invocation with JAXB generated request object as
     * parameter. The endpoint for these testcase is a JAXWS Source Provider. - sends small image
     * (12K) - sets for MTOM with SOAP11 using 2 steps: bindingID and setMTOMEnabled - mode =
     * PAYLOAD
     * 
     * @return returned from the remote method
     */
    public String JAXB_AttachmentSmallJpeg() throws Exception {

        // debug
        System.out.println("\n*** In EnableJAXBImageJpegHEXClient.JAXB_AttachmentSmallJpeg ***\n");

        String bindingID = SOAPBinding.SOAP11HTTP_BINDING;

        try {
            // create a Dynamic service & port
            Service svc = Service.create(serviceName);
            svc.addPort(portName, bindingID, endpointUrl);
            // debug
            System.out
                    .println("portname = " + portName + ",bindingID = " + bindingID + ",endpointUrl = " + endpointUrl);

            // jaxb artifacts package
            JAXBContext jbc = JAXBContext.newInstance("org.test.mtom21.enablejaxb_imagejpeghex");

            // set MTOM on dispatch
            MTOMFeature mtom21 = new MTOMFeature(true);

            Dispatch<Object> dispatch = svc.createDispatch(portName, jbc, mode, mtom21);

            Image image = ImageIO.read(new File(jpegFilename1));

            byte[] byteImage = AttachmentHelper.getImageBytes(image, "image/jpeg");
            ImageDepot imageDepot = new ObjectFactory().createImageDepot();
            imageDepot.setImageData(byteImage);

            // Create a request bean with imagedepot bean as value
            ObjectFactory factory = new ObjectFactory();
            Invoke request = factory.createInvoke();
            request.setInput(imageDepot);

            SendImageResponse response = (SendImageResponse) dispatch.invoke(request);

            byte[] retByte = response.getOutput().getImageData();
            // save result to file to compare.....
            // write to a file
            File outFile = new File("outFile.jpeg");
            DataOutputStream dO = new DataOutputStream(new FileOutputStream(outFile));
            // write received image to file
            dO.write(retByte);
            dO.flush();
            dO.close();
            // read back to image type for comparison
            Image retVal = ImageIO.read(new File("outFile.jpeg"));

            if (AttachmentHelper.compareImages(retVal, image) == true) {
                // debug
                printMessageToConsole("Image received equals to image sent");
                sendBackStr = goodResult;
            } else {
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
     * This client method calls into a service that verfies that MTOM is NOT used over the wire on
     * the request. It corresponds to the JAXB_AttachmentLargeJpeg method which verifies that the
     * image is sent and received correctly.
     * 
     * @return
     * @throws Exception
     */
    public String jaxb_AttachmentLargeJpeg_VerifyMTOM() throws Exception {

        // debug
        System.out.println("\n*** In EnableJAXBImageJpegHEXClient.jaxb_AttachmentLargeJpeg_VerifyMTOM ***\n");

        String bindingID = SOAPBinding.SOAP11HTTP_BINDING;

        // create a Dynamic service & port
        Service svc = Service.create(serviceNameVerify);
        svc.addPort(portNameVerify, bindingID, endpointUrlVerify);
        // debug
        System.out
                .println("portname = " + portNameVerify + ",bindingID = " + bindingID + ",endpointUrl = " + endpointUrlVerify);

        // jaxb artifacts package
        JAXBContext jbc = JAXBContext.newInstance("org.test.mtom21.enablejaxb_imagejpeghex");

        // set MTOM on dispatch
        MTOMFeature mtom21 = new MTOMFeature(true);

        Dispatch<Object> dispatch = svc.createDispatch(portNameVerify, jbc, mode, mtom21);

        Image image = ImageIO.read(new File(jpegFilename2));

        byte[] byteImage = AttachmentHelper.getImageBytes(image, "image/jpeg");
        ImageDepot imageDepot = new ObjectFactory().createImageDepot();
        imageDepot.setImageData(byteImage);

        // Create a request bean with imagedepot bean as value
        ObjectFactory factory = new ObjectFactory();
        Invoke request = factory.createInvoke();
        request.setInput(imageDepot);

        SendImageResponseVerify response = (SendImageResponseVerify) dispatch.invoke(request);
        String responseVal = response.getOutput();
        if(responseVal == null || responseVal.indexOf("MTOMOFF") == -1) {
            return badResult;
        } else {
            return goodResult;
        }
    }

    /**
     * This testcase uses a JAXWS Dispatch invocation with JAXB generated request object as
     * parameter. The endpoint for these testcase is a JAXWS Source Provider. - sends large image
     * (68K) - sets for MTOM with SOAP11 using 2 steps: bindingID and setMTOMEnabled - mode =
     * PAYLOAD
     * 
     * @return returned from the remote method
     */
    public String JAXB_AttachmentLargeJpeg() throws Exception {

        // debug
        System.out.println("\n*** In EnableJAXBImageJpegHEXClient.JAXB_AttachmentLargeJpeg ***\n");

        String bindingID = SOAPBinding.SOAP11HTTP_BINDING;

        try {
            // create a Dynamic service & port
            Service svc = Service.create(serviceName);
            svc.addPort(portName, bindingID, endpointUrl);
            // debug
            System.out
                    .println("portname = " + portName + ",bindingID = " + bindingID + ",endpointUrl = " + endpointUrl);

            // jaxb artifacts package
            JAXBContext jbc = JAXBContext.newInstance("org.test.mtom21.enablejaxb_imagejpeghex");

            // set MTOM on dispatch
            MTOMFeature mtom21 = new MTOMFeature(true);

            Dispatch<Object> dispatch = svc.createDispatch(portName, jbc, mode, mtom21);

            Image image = ImageIO.read(new File(jpegFilename2));

            byte[] byteImage = AttachmentHelper.getImageBytes(image, "image/jpeg");
            ImageDepot imageDepot = new ObjectFactory().createImageDepot();
            imageDepot.setImageData(byteImage);

            // Create a request bean with imagedepot bean as value
            ObjectFactory factory = new ObjectFactory();
            Invoke request = factory.createInvoke();
            request.setInput(imageDepot);

            SendImageResponse response = (SendImageResponse) dispatch.invoke(request);

            byte[] retByte = response.getOutput().getImageData();
            // save result to file to compare.....
            // write to a file
            File outFile = new File("outFile.jpeg");
            DataOutputStream dO = new DataOutputStream(new FileOutputStream(outFile));
            // write received image to file
            dO.write(retByte);
            dO.flush();
            dO.close();
            // read back to image type for comparison
            Image retVal = ImageIO.read(new File("outFile.jpeg"));

            // only used with large image?
            /*
             * Do not delete. This code is used to create the expected image to run only once. The
             * image was optimized by MTOM process. It is now saved back to client directory under
             * the name image1Expected.jpeg to be compared to in the test.
             * 
             * byte[] recB = AttachmentHelper.getImageBytes(retVal,"image/jpeg"); File outFile = new
             * File("c:/temp/outFile.jpeg"); DataOutputStream dO = new DataOutputStream(new
             * FileOutputStream(outFile)); // write received image to file dO.write(recB);
             * dO.flush(); dO.close();
             */

            // image expected - this image was optimized
            Image imageExpected = ImageIO.read(new File(jpegFromByteFilename2));

            if (AttachmentHelper.compareImages(retVal, image) == true) {
                // debug
                printMessageToConsole("Image received equals to image sent");
                sendBackStr = goodResult;
            } else if (AttachmentHelper.compareImages(retVal, imageExpected) == true) {
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
     * This client method calls into a service that verfies that MTOM is NOT used over the wire on
     * the request. It corresponds to the JAXB_Sequence_SmallJpeg method which verifies that the
     * image is sent and received correctly.
     * 
     * @return
     * @throws Exception
     */
    public String jaxb_Sequence_SmallJpeg_VerifyMTOM() throws Exception {

        // debug
        System.out.println("\n*** In EnableJAXB_imagejpegHEX.jaxb_Sequence_SmallJpeg_VerifyMTOM ***\n");

        byte[] byteImage = null;
        Image image = null;
        byte[] retByte = null;
        File outFile = null;
        DataOutputStream dO = null;
        
        String bindingID = SOAPBinding.SOAP11HTTP_BINDING;

        // create a Dynamic service & port
        Service svc = Service.create(serviceNameVerify);
        svc.addPort(portNameVerify, bindingID, endpointUrlVerify);
        // debug
        System.out
                .println("portname = " + portNameVerify + ",bindingID = " + bindingID + ",endpointUrl = " + endpointUrlVerify);

        // jaxb artifacts package
        JAXBContext jbc = JAXBContext.newInstance("org.test.mtom21.enablejaxb_imagejpeghex");

        // set MTOM on dispatch
        MTOMFeature mtom21 = new MTOMFeature();

        Dispatch<Object> dispatch = svc.createDispatch(portNameVerify, jbc, mode, mtom21);

        image = ImageIO.read(new File(jpegFilename1));

        byteImage = AttachmentHelper.getImageBytes(image, "image/jpeg");

        ImageDepot imageDepot = new ObjectFactory().createImageDepot();
        imageDepot.setImageData(byteImage);

        // Create a request bean with imagedepot bean as value
        ObjectFactory factory = new ObjectFactory();
        Invoke request = factory.createInvoke();
        request.setInput(imageDepot);

        SendImageResponseVerify response = (SendImageResponseVerify) dispatch.invoke(request);
        String responseVal = response.getOutput();
        if(responseVal == null || responseVal.indexOf("MTOMOFF") == -1) {
            return badResult;
        }

        // --------------- Step 2 - MTOM off --------

        // set MTOM on dispatch
        mtom21 = new MTOMFeature(false);
        dispatch = svc.createDispatch(portNameVerify, jbc, mode, mtom21);

        imageDepot = new ObjectFactory().createImageDepot();
        imageDepot.setImageData(byteImage);

        response = (SendImageResponseVerify) dispatch.invoke(request);
        responseVal = response.getOutput();
        if(responseVal == null || responseVal.indexOf("MTOMOFF") == -1) {
            return badResult;
        }

        // --------------- Step 3 - MTOM on again --------

        // set MTOM on dispatch
        mtom21 = new MTOMFeature(true);
        dispatch = svc.createDispatch(portNameVerify, jbc, mode, mtom21);

        imageDepot = new ObjectFactory().createImageDepot();
        imageDepot.setImageData(byteImage);

        response = (SendImageResponseVerify) dispatch.invoke(request);
        responseVal = response.getOutput();
        if(responseVal == null || responseVal.indexOf("MTOMOFF") == -1) {
            return badResult;
        }
        
        return goodResult;
    }

    public String JAXB_Sequence_SmallJpeg() throws Exception {

        // debug
        System.out.println("\n*** In EnableJAXB_imagejpegHEX.JAXB_Sequence_SmallJpeg ***\n");

        byte[] byteImage = null;
        Image image = null;
        byte[] retByte = null;
        File outFile = null;
        DataOutputStream dO = null;
        try {
            boolean test1 = true, test2 = true, test3 = true;
            String bindingID = SOAPBinding.SOAP11HTTP_BINDING;

            // create a Dynamic service & port
            Service svc = Service.create(serviceName);
            svc.addPort(portName, bindingID, endpointUrl);
            // debug
            System.out
                    .println("portname = " + portName + ",bindingID = " + bindingID + ",endpointUrl = " + endpointUrl);

            // jaxb artifacts package
            JAXBContext jbc = JAXBContext.newInstance("org.test.mtom21.enablejaxb_imagejpeghex");

            // set MTOM on dispatch
            MTOMFeature mtom21 = new MTOMFeature();

            Dispatch<Object> dispatch = svc.createDispatch(portName, jbc, mode, mtom21);

            image = ImageIO.read(new File(jpegFilename1));

            byteImage = AttachmentHelper.getImageBytes(image, "image/jpeg");

            ImageDepot imageDepot = new ObjectFactory().createImageDepot();
            imageDepot.setImageData(byteImage);

            // Create a request bean with imagedepot bean as value
            ObjectFactory factory = new ObjectFactory();
            Invoke request = factory.createInvoke();
            request.setInput(imageDepot);

            SendImageResponse response = (SendImageResponse) dispatch.invoke(request);

            retByte = response.getOutput().getImageData();
            // save result to file to compare.....
            // write to a file
            outFile = new File("outFile.jpeg");
            dO = new DataOutputStream(new FileOutputStream(outFile));
            // write received image to file
            dO.write(retByte);
            dO.flush();
            dO.close();
            // read back to image type for comparison
            Image retVal = ImageIO.read(new File("outFile.jpeg"));

            if (AttachmentHelper.compareImages(retVal, image) == true) {
                // debug
                printMessageToConsole("Client MTOM ON - Image received equals to image sent");
            } else {
                // debug
                printMessageToConsole("Client MTOM ON - Images not equal:returned image and image sent");
                test1 = false;
            }

            // --------------- Step 2 - MTOM off --------

            // set MTOM on dispatch
            mtom21 = new MTOMFeature(false);
            dispatch = svc.createDispatch(portName, jbc, mode, mtom21);

            imageDepot = new ObjectFactory().createImageDepot();
            imageDepot.setImageData(byteImage);

            response = (SendImageResponse) dispatch.invoke(request);

            retByte = response.getOutput().getImageData();
            // save result to file to compare.....
            // write to a file
            outFile = new File("outFile.jpeg");
            dO = new DataOutputStream(new FileOutputStream(outFile));
            // write received image to file
            dO.write(retByte);
            dO.flush();
            dO.close();
            // read back to image type for comparison
            retVal = ImageIO.read(new File("outFile.jpeg"));

            if (AttachmentHelper.compareImages(retVal, image) == true) {
                // debug
                printMessageToConsole("Client MTOM OFF - Image received equals to image sent");

            } else {
                // debug
                printMessageToConsole("Client MTOM OFF - Images not equal:returned image and image sent");
                test2 = false;
            }

            // --------------- Step 3 - MTOM on again --------

            // set MTOM on dispatch
            mtom21 = new MTOMFeature(true);
            dispatch = svc.createDispatch(portName, jbc, mode, mtom21);

            imageDepot = new ObjectFactory().createImageDepot();
            imageDepot.setImageData(byteImage);

            response = (SendImageResponse) dispatch.invoke(request);

            retByte = response.getOutput().getImageData();
            // save result to file to compare.....
            // write to a file
            outFile = new File("outFile.jpeg");
            dO = new DataOutputStream(new FileOutputStream(outFile));
            // write received image to file
            dO.write(retByte);
            dO.flush();
            dO.close();
            // read back to image type for comparison
            retVal = ImageIO.read(new File("outFile.jpeg"));

            if (AttachmentHelper.compareImages(retVal, image) == true) {
                // debug
                printMessageToConsole("Client MTOM ON - Image received equals to image sent");
            } else {
                // debug
                printMessageToConsole("Client MTOM ON - Images not equal:returned image and image sent");
                test3 = false;
            }

            if (test1 && test2 && test3) {
                sendBackStr = goodResult;
            } else {
                sendBackStr = badResult;
            }
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
