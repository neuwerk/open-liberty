//
// @(#) 1.1 autoFVT/src/mtom21/disablesoapbinding/wsfvt/client/DisableSBDispatchSOAP12Client.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/15/08 14:28:17 [8/8/12 06:40:46]
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

package mtom21.disablesoapbinding.wsfvt.client;

import java.awt.Image;
import java.io.*;

import javax.imageio.ImageIO;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

import org.test.mtom21.disablesb_imagejpegsoap12.*;

/**
 * This testcase uses a JAXB generated objects This testcase covers the "image/jpeg" MIME type,
 * using Dispatch. The service has MTOM disabled using SOAP12
 */

public class DisableSBDispatchSOAP12Client {

    public static final String goodResult = "Message processed";
    public static final String badResult = "Problem in processing message";

    String xmlFilename = "catalog.xml"; // about 31KB in size, has NO xml header

    String jpegFilename1 = "image1.jpeg";
    String jpegOptimizedFilename1 = "image1Expected.jpeg";
    String jpegFilename2 = "image2.jpeg";
    String jpegOptimizedFilename2 = "image2Expected.jpeg";
    String jpegFilename3 = "image3.jpeg";
    String jpegOptimizedFilename3 = "image3Expected.jpeg";

    String sendBackStr = null;

    QName serviceName = new QName("http://ws.apache.org/axis2", "DisableSBJAXBImageJpegSOAP12Service");
    QName portName = new QName("http://ws.apache.org/axis2", "AttachmentServicePort");

    String endpointUrl = "http://@REPLACE_WITH_HOST_NAME@:"
            + "@REPLACE_WITH_PORT_NUM@/disablesoapbinding21/DisableSBJAXBImageJpegSOAP12Service";
    String bindingID = SOAPBinding.SOAP12HTTP_BINDING;

    Service.Mode mode = Service.Mode.PAYLOAD;

    public static void main(String[] args) {
        try {
            DisableSBDispatchSOAP12Client client = new DisableSBDispatchSOAP12Client();
            client.JAXB_AttachmentLargeJpeg();
            client.JAXB_AttachmentSmallJpeg();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This testcase uses a JAXWS Dispatch invocation with JAXB generated request object as
     * parameter. The endpoint for these testcase is a JAXWS Source Provider.
     *  - sends small image (12K) - mode = PAYLOAD
     * @return returned from the remote method
     */

    public String JAXB_AttachmentSmallJpeg() throws Exception {

        // debug
        System.out.println("\n*** In DisableSBDispatchSOAP12Client.JAXB_AttachmentSmallJpeg() ***\n");

        try {

            // create a Dynamic service & port
            Service svc = Service.create(serviceName);
            svc.addPort(portName, bindingID, endpointUrl);

            // debug
            System.out
                    .println("portname = " + portName + ",bindingID = " + bindingID + ",endpointUrl = " + endpointUrl);

            // jaxb artifacts package
            JAXBContext jbc = JAXBContext.newInstance("org.test.mtom21.disablesb_imagejpegsoap12");

            Dispatch<Object> dispatch = svc.createDispatch(portName, jbc, mode);

            Image image = ImageIO.read(new File(jpegFilename1));
            ImageDepot imageDepot = new ObjectFactory().createImageDepot();
            imageDepot.setImageData(image);

            // Create a request bean
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
            Image imageExpected = ImageIO.read(new File(jpegOptimizedFilename1));

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
     * This testcase uses a JAXWS Dispatch invocation with JAXB generated request object as
     * parameter. The endpoint for these testcase is a JAXWS Source Provider.
     *  - sends an image (68K) - mode = PAYLOAD
     * @return returned from the remote method
     */

    public String JAXB_AttachmentLargeJpeg() throws Exception {

        // debug
        System.out.println("\n*** In DisableSBDispatchSOAP12Client.JAXB_AttachmentLargeJpeg() ***\n");

        try {

            // create a Dynamic service & port
            Service svc = Service.create(serviceName);
            svc.addPort(portName, bindingID, endpointUrl);

            // debug
            System.out
                    .println("portname = " + portName + ",bindingID = " + bindingID + ",endpointUrl = " + endpointUrl);

            // jaxb artifacts package
            JAXBContext jbc = JAXBContext.newInstance("org.test.mtom21.disablesb_imagejpegsoap12");

            Dispatch<Object> dispatch = svc.createDispatch(portName, jbc, mode);

            Image image = ImageIO.read(new File(jpegFilename2));
            ImageDepot imageDepot = new ObjectFactory().createImageDepot();
            imageDepot.setImageData(image);

            // Create a request bean
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

            // this image was optimized
            Image imageExpected = ImageIO.read(new File(jpegOptimizedFilename2));

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

    // ============================================================
    // Utilites
    // ============================================================
    public static void printMessageToConsole(String msg) {
        System.out.println("At client side:\n" + msg);

    }

}
