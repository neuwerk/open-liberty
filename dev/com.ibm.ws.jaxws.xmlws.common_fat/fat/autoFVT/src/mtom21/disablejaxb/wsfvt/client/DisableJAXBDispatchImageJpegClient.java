//
// @(#) 1.1 autoFVT/src/mtom21/disablejaxb/wsfvt/client/DisableJAXBDispatchImageJpegClient.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/15/08 14:24:16 [8/8/12 06:40:42]
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

package mtom21.disablejaxb.wsfvt.client;

import java.awt.Image;
import java.io.*;

import javax.imageio.ImageIO;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.MTOMFeature;
import javax.xml.ws.soap.SOAPBinding;

import org.apache.axiom.om.util.Base64;
import org.test.mtom21.disablejaxbdispatch_imagejpeg.*;

/**
 * This testcase uses a JAXB generated objects. - covers the "image/jpeg" MIME type using Dispatch
 * client. - service has MTOM = default (no setting) - exclusively disables MTOM using
 * binding.setMTOMEnabled(false);
 */

public class DisableJAXBDispatchImageJpegClient {

    public static final String goodResult = "Message processed";
    public static final String badResult = "Problem in processing message";

    String jpegFilename1 = "image1.jpeg";
    String jpegFilename2 = "image2.jpeg";
    String jpegFilename3 = "image3.jpeg";
    String jpegExpectedFilename2 = "image2Expected.jpeg";

    String sendBackStr = null;

    public static final QName serviceName = new QName("http://ws.apache.org/axis2",
            "MTOMDefaultDispatchImageJpegService");
    public static final QName portName = new QName("http://ws.apache.org/axis2", "AttachmentServicePort");
    public static final String endpointUrl = "http://@REPLACE_WITH_HOST_NAME@:"
        + "@REPLACE_WITH_PORT_NUM@" + "/disablejaxbanno/MTOMDefaultDispatchImageJpegService";

    public static final QName serviceNameVerify = new QName("http://ws.apache.org/axis2",
            "MTOMDefaultDispatchImageJpegServiceVerification");
    public static final QName portNameVerify = new QName("http://ws.apache.org/axis2", "AttachmentServicePortVerify");
    public static final String endpointUrlVerify = "http://@REPLACE_WITH_HOST_NAME@:"
        + "@REPLACE_WITH_PORT_NUM@" + "/disablejaxbanno/MTOMDefaultDispatchImageJpegServiceVerification";

    public static final Service.Mode mode = Service.Mode.PAYLOAD;
    
    public static void main(String[] args) {
        try {
            DisableJAXBDispatchImageJpegClient client = new DisableJAXBDispatchImageJpegClient();
            client.JAXB_AttachmentSmallJpeg();
            client.JAXB_AttachmentSmallJpeg();
            client.JAXB_doubleSets_AttachmentLargeJpeg();
            client.JAXB_Sequence_SmallJpeg();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * This client method calls into a service that verfies that MTOM is not used over the wire on
     * the request. It corresponds to the JAXB_AttachmentSmallJpeg method which verifies that the
     * image is sent and received correctly.
     * 
     * @return
     * @throws Exception
     */
    public String jaxb_AttachmentSmallJpeg_verifyMTOMOFF() throws Exception {

        // debug
        System.out.println("\n*** In DisableJAXBDispatchImageJpegClient.jaxb_AttachmentSmallJpeg_verifyMTOMOFF ***\n");

        String bindingID = SOAPBinding.SOAP11HTTP_BINDING;

        // create a Dynamic service & port
        Service svc = Service.create(serviceNameVerify);
        svc.addPort(portNameVerify, bindingID, endpointUrlVerify);
        // debug
        System.out
                .println("portname = " + portNameVerify + ",bindingID = " + bindingID + ",endpointUrl = " + endpointUrlVerify);

        // jaxb artifacts package
        JAXBContext jbc = JAXBContext.newInstance("org.test.mtom21.disablejaxbdispatch_imagejpeg");

        // set MTOM to false
        MTOMFeature mtom21 = new MTOMFeature(false);

        Dispatch<Object> dispatch = svc.createDispatch(portNameVerify, jbc, mode, mtom21);

        Image image = ImageIO.read(new File(jpegFilename1));

        ImageDepot imageDepot = new ObjectFactory().createImageDepot();
        imageDepot.setImageData(image);

        // Create a request bean with imagedepot bean as value
        ObjectFactory factory = new ObjectFactory();
        Invoke request = factory.createInvoke();
        request.setInput(imageDepot);

        // verify that MTOM was not used
        SendImageResponseVerification response = (SendImageResponseVerification) dispatch.invoke(request);
        String responseVal = response.getOutput();
        if (responseVal.indexOf("MTOMOFF") != -1) {
            return goodResult;
        } else {
            return badResult;
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
        System.out.println("\n*** In DisableJAXBDispatchImageJpegClient.JAXB_AttachmentSmallJpeg ***\n");

        String bindingID = SOAPBinding.SOAP11HTTP_BINDING;

        try {
            // create a Dynamic service & port
            Service svc = Service.create(serviceName);
            svc.addPort(portName, bindingID, endpointUrl);
            // debug
            System.out
                    .println("portname = " + portName + ",bindingID = " + bindingID + ",endpointUrl = " + endpointUrl);

            // jaxb artifacts package
            JAXBContext jbc = JAXBContext.newInstance("org.test.mtom21.disablejaxbdispatch_imagejpeg");

            // set MTOM to false
            MTOMFeature mtom21 = new MTOMFeature(false);

            Dispatch<Object> dispatch = svc.createDispatch(portName, jbc, mode, mtom21);

            Image image = ImageIO.read(new File(jpegFilename1));

            ImageDepot imageDepot = new ObjectFactory().createImageDepot();
            imageDepot.setImageData(image);

            // Create a request bean with imagedepot bean as value
            ObjectFactory factory = new ObjectFactory();
            Invoke request = factory.createInvoke();
            request.setInput(imageDepot);

            SendImageResponse response = (SendImageResponse) dispatch.invoke(request);
            Image retVal = response.getOutput().getImageData();

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
     * This client method calls into a service that verfies that MTOM is not used over the wire on
     * the request. It corresponds to the JAXB_doubleSets_AttachmentLargeJpeg method which verifies that the
     * image is sent and received correctly.
     * 
     * @return
     * @throws Exception
     */
    public String jaxb_doubleSets_AttachmentLargeJpeg_VerifyMTOMOFF() throws Exception {

        // debug
        System.out.println("\n*** In DisableJAXBDispatchImageJpegClient.jaxb_doubleSets_AttachmentLargeJpeg_VerifyMTOMOFF ***\n");
        String bindingID = SOAPBinding.SOAP11HTTP_MTOM_BINDING;

            // create a Dynamic service & port
            Service svc = Service.create(serviceNameVerify);
            svc.addPort(portNameVerify, bindingID, endpointUrlVerify);
            // debug
            System.out
                    .println("portname = " + portNameVerify + ",bindingID = " + bindingID + ",endpointUrl = " + endpointUrlVerify);

            // jaxb artifacts package
            JAXBContext jbc = JAXBContext.newInstance("org.test.mtom21.disablejaxbdispatch_imagejpeg");

            // set MTOM to false
            MTOMFeature mtom21 = new MTOMFeature(false);

            Dispatch<Object> dispatch = svc.createDispatch(portNameVerify, jbc, mode, mtom21);

            Image image = ImageIO.read(new File(jpegFilename2));

            ImageDepot imageDepot = new ObjectFactory().createImageDepot();
            imageDepot.setImageData(image);

            // Create a request bean with imagedepot bean as value
            ObjectFactory factory = new ObjectFactory();
            Invoke request = factory.createInvoke();
            request.setInput(imageDepot);

            SendImageResponseVerification response = (SendImageResponseVerification) dispatch.invoke(request);
            String responseVal = response.getOutput();
            if (responseVal.indexOf("MTOMOFF") != -1) {
                return goodResult;
            } else {
                return badResult;
            }
    }

    /**
     * This testcase uses a JAXWS Dispatch invocation with JAXB generated request object as
     * parameter. The endpoint for these testcase is a JAXWS Source Provider. - sends large image
     * (68K) - sets for MTOM with SOAP11 using 2 steps: - first set MTOM=enabled using
     * bindingID=SOAPBinding.SOAP11HTTP_MTOM_BINDING - then set MTOM=disabled using
     * setMTOMEnabled(false) - mode = PAYLOAD
     * 
     * @return returned from the remote method
     */

    public String JAXB_doubleSets_AttachmentLargeJpeg() throws Exception {
       
        // debug
        System.out.println("\n*** In DisableJAXBDispatchImageJpegClient.JAXB_AttachmentLargeJpeg ***\n");
        String bindingID = SOAPBinding.SOAP11HTTP_MTOM_BINDING;

        try {
            // create a Dynamic service & port
            Service svc = Service.create(serviceName);
            svc.addPort(portName, bindingID, endpointUrl);
            // debug
            System.out
                    .println("portname = " + portName + ",bindingID = " + bindingID + ",endpointUrl = " + endpointUrl);

            // jaxb artifacts package
            JAXBContext jbc = JAXBContext.newInstance("org.test.mtom21.disablejaxbdispatch_imagejpeg");

            // set MTOM to false
            MTOMFeature mtom21 = new MTOMFeature(false);

            Dispatch<Object> dispatch = svc.createDispatch(portName, jbc, mode, mtom21);

            Image image = ImageIO.read(new File(jpegFilename2));

            ImageDepot imageDepot = new ObjectFactory().createImageDepot();
            imageDepot.setImageData(image);

            // Create a request bean with imagedepot bean as value
            ObjectFactory factory = new ObjectFactory();
            Invoke request = factory.createInvoke();
            request.setInput(imageDepot);

            SendImageResponse response = (SendImageResponse) dispatch.invoke(request);
            Image retVal = response.getOutput().getImageData();

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
            Image imageExpected = ImageIO.read(new File(jpegExpectedFilename2));

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
     * This client method calls into a service that verfies that MTOM is not used over the wire on
     * the request. It corresponds to the JAXB_AttachmentLargeJpeg method which verifies that the
     * image is sent and received correctly.
     * 
     * @return
     * @throws Exception
     */
    public String jaxb_AttachmentLargeJpeg_verifyMTOMOFF() throws Exception {

        // debug
        System.out.println("\n*** In DisableJAXBDispatchImageJpegClient.jaxb_AttachmentLargeJpeg_verifyMTOMOFF ***\n");
        // this should get trumped by MTOMFeature
        String bindingID = SOAPBinding.SOAP11HTTP_MTOM_BINDING;

        // create a Dynamic service & port
        Service svc = Service.create(serviceNameVerify);
        svc.addPort(portName, bindingID, endpointUrlVerify);
        // debug
        System.out.println("portname = " + portName + ",bindingID = " + bindingID + ",endpointUrl = "
                + endpointUrlVerify);

        // jaxb artifacts package
        JAXBContext jbc = JAXBContext.newInstance("org.test.mtom21.disablejaxbdispatch_imagejpeg");

        // set MTOM to false
        MTOMFeature mtom21 = new MTOMFeature(false);

        Dispatch<Object> dispatch = svc.createDispatch(portName, jbc, mode, mtom21);

        Image image = ImageIO.read(new File(jpegFilename2));

        ImageDepot imageDepot = new ObjectFactory().createImageDepot();
        imageDepot.setImageData(image);

        // Create a request bean with imagedepot bean as value
        ObjectFactory factory = new ObjectFactory();
        Invoke request = factory.createInvoke();
        request.setInput(imageDepot);

        // verify that MTOM was not used
        SendImageResponseVerification response = (SendImageResponseVerification) dispatch.invoke(request);
        String responseVal = response.getOutput();
        if (responseVal.indexOf("MTOMOFF") != -1) {
            return goodResult;
        } else {
            return badResult;
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
        System.out.println("\n*** In DisableJAXBDispatchImageJpegClient.JAXB_AttachmentLargeJpeg ***\n");
        // this should get trumped by MTOMFeature
        String bindingID = SOAPBinding.SOAP11HTTP_MTOM_BINDING;

        try {
            // create a Dynamic service & port
            Service svc = Service.create(serviceName);
            svc.addPort(portName, bindingID, endpointUrl);
            // debug
            System.out
                    .println("portname = " + portName + ",bindingID = " + bindingID + ",endpointUrl = " + endpointUrl);

            // jaxb artifacts package
            JAXBContext jbc = JAXBContext.newInstance("org.test.mtom21.disablejaxbdispatch_imagejpeg");

            // set MTOM to false
            MTOMFeature mtom21 = new MTOMFeature(false);

            Dispatch<Object> dispatch = svc.createDispatch(portName, jbc, mode, mtom21);

            Image image = ImageIO.read(new File(jpegFilename2));

            ImageDepot imageDepot = new ObjectFactory().createImageDepot();
            imageDepot.setImageData(image);

            // Create a request bean with imagedepot bean as value
            ObjectFactory factory = new ObjectFactory();
            Invoke request = factory.createInvoke();
            request.setInput(imageDepot);

            // first check that MTOM is used
            SendImageResponse response = (SendImageResponse)dispatch.invoke(request);
            Image retVal = response.getOutput().getImageData();

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
            Image imageExpected = ImageIO.read(new File(jpegExpectedFilename2));

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
     * This client method calls into a service that verfies that MTOM is not used over the wire on
     * the requests. It corresponds to the JAXB_Sequence_SmallJpeg method which verifies that the
     * image is sent and received correctly.
     * 
     * @return
     * @throws Exception
     */
    public String jaxb_Sequence_SmallJpeg_VerifyMTOMOFF() throws Exception {
       
        // debug
        System.out.println("\n*** In DisableJAXBDispatchImageJpegClient.jaxb_Sequence_SmallJpeg_VerifyMTOMOFF ***\n");

        String bindingID = SOAPBinding.SOAP11HTTP_BINDING;

        // create a Dynamic service & port
        Service svc = Service.create(serviceNameVerify);
        svc.addPort(portNameVerify, bindingID, endpointUrlVerify);
        // debug
        System.out
                .println("portname = " + portNameVerify + ",bindingID = " + bindingID + ",endpointUrl = " + endpointUrlVerify);

        // jaxb artifacts package
        JAXBContext jbc = JAXBContext.newInstance("org.test.mtom21.disablejaxbdispatch_imagejpeg");

        // set MTOM true
        MTOMFeature mtom21 = new MTOMFeature(true);

        Dispatch<Object> dispatch = svc.createDispatch(portNameVerify, jbc, mode, mtom21);

        Image image = ImageIO.read(new File(jpegFilename1));

        ImageDepot imageDepot = new ObjectFactory().createImageDepot();
        imageDepot.setImageData(image);

        // Create a request bean with imagedepot bean as value
        ObjectFactory factory = new ObjectFactory();
        Invoke request = factory.createInvoke();
        request.setInput(imageDepot);

        // verify that MTOM was not used
        SendImageResponseVerification response = (SendImageResponseVerification) dispatch.invoke(request);
        String responseVal = response.getOutput();
        if (responseVal.indexOf("MTOMON") == -1) {
            return badResult;
        }

        // --------------- Step 2 - MTOM off --------
        mtom21 = new MTOMFeature(false);
        dispatch = svc.createDispatch(portNameVerify, jbc, mode, mtom21);

        imageDepot = new ObjectFactory().createImageDepot();
        imageDepot.setImageData(image);

        // Create a request bean with imagedepot bean as value
        response = (SendImageResponseVerification) dispatch.invoke(request);
        responseVal = response.getOutput();
        if (responseVal.indexOf("MTOMOFF") == -1) {
            return badResult;
        }

        // --------------- Step 3 - MTOM on again --------
        mtom21 = new MTOMFeature(true);
        dispatch = svc.createDispatch(portNameVerify, jbc, mode, mtom21);

        imageDepot = new ObjectFactory().createImageDepot();
        imageDepot.setImageData(image);

        // Create a request bean with imagedepot bean as value
        response = (SendImageResponseVerification) dispatch.invoke(request);
        responseVal = response.getOutput();
        if (responseVal.indexOf("MTOMON") == -1) {
            return badResult;
        }
        
        return goodResult;
    }

    public String JAXB_Sequence_SmallJpeg() throws Exception {
       
        // debug
        System.out.println("\n*** In DisableJAXBDispatchImageJpegClient.JAXB_Sequence_SmallJpeg ***\n");

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
            JAXBContext jbc = JAXBContext.newInstance("org.test.mtom21.disablejaxbdispatch_imagejpeg");

            // set MTOM true
            MTOMFeature mtom21 = new MTOMFeature(true);

            Dispatch<Object> dispatch = svc.createDispatch(portName, jbc, mode, mtom21);

            Image image = ImageIO.read(new File(jpegFilename1));

            ImageDepot imageDepot = new ObjectFactory().createImageDepot();
            imageDepot.setImageData(image);

            // Create a request bean with imagedepot bean as value
            ObjectFactory factory = new ObjectFactory();
            Invoke request = factory.createInvoke();
            request.setInput(imageDepot);

            SendImageResponse response = (SendImageResponse) dispatch.invoke(request);
            Image retVal = response.getOutput().getImageData();

            if (AttachmentHelper.compareImages(retVal, image) == true) {
                // debug
                printMessageToConsole("Client MTOM ON - Image received equals to image sent");
            } else {
                // debug
                printMessageToConsole("Client MTOM ON - Images not equal:returned image and image sent");
                test1 = false;
            }

            // --------------- Step 2 - MTOM off --------
            mtom21 = new MTOMFeature(false);
            dispatch = svc.createDispatch(portName, jbc, mode, mtom21);

            imageDepot = new ObjectFactory().createImageDepot();
            imageDepot.setImageData(image);

            // Create a request bean with imagedepot bean as value
            response = (SendImageResponse) dispatch.invoke(request);
            retVal = response.getOutput().getImageData();

            if (AttachmentHelper.compareImages(retVal, image) == true) {
                // debug
                printMessageToConsole("Client MTOM OFF - Image received equals to image sent");
            } else {
                // debug
                printMessageToConsole("Client MTOM OFF - Images not equal:returned image and image sent");
                test2 = false;
            }

            // --------------- Step 3 - MTOM on again --------
            mtom21 = new MTOMFeature(true);
            dispatch = svc.createDispatch(portName, jbc, mode, mtom21);

            imageDepot = new ObjectFactory().createImageDepot();
            imageDepot.setImageData(image);

            // Create a request bean with imagedepot bean as value
            response = (SendImageResponse) dispatch.invoke(request);
            retVal = response.getOutput().getImageData();

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
