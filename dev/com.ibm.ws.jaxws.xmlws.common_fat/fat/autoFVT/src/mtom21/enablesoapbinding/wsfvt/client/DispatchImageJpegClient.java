//
// @(#) 1.1 autoFVT/src/mtom21/enablesoapbinding/wsfvt/client/DispatchImageJpegClient.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/15/08 14:33:24 [8/8/12 06:40:52]
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
import javax.xml.ws.soap.SOAPBinding;

import org.apache.axiom.om.util.Base64;
import org.test.mtom21.enablesb_imagejpeg.*;

/**
 * This testcase uses a JAXB generated objects
 * This testcase covers the "image/jpeg" MIME type
 * using Dispatch.
 * The service has MTOM enabled with SOAP11, 
 * and client with MTOM setting as default (bindingID = null).
 */


public class DispatchImageJpegClient {

	public static final String goodResult = "Message processed";
    public static final String badResult = "Problem in processing message";

    String jpegFilename1 = "image1.jpeg";
    String jpegOptimizedFilename1 = "image1Expected.jpeg";
    String jpegFilename2 = "image2.jpeg";
    String jpegOptimizedFilename2 = "image2Expected.jpeg";
    String jpegFilename3 = "image3.jpeg";
    String jpegOptimizedFilename3 = "image3Expected.jpeg";

    String sendBackStr = null;

    QName serviceName = new QName("http://ws.apache.org/axis2", "EnableSBJAXBImageJpegService");
    QName portName = new QName("http://ws.apache.org/axis2", "AttachmentServicePort");

    String endpointUrl = "http://@REPLACE_WITH_HOST_NAME@:"
            + "@REPLACE_WITH_PORT_NUM@/enablesoapbinding21/EnableSBJAXBImageJpegService";
    String bindingID = null; // no setting of MTOM

    Service.Mode mode = Service.Mode.PAYLOAD;



    public static void main(String[] args) {
		try {
		    DispatchImageJpegClient client = new DispatchImageJpegClient();
            client.jaxb_AttachmentLargeJpeg_VerifyMTOM();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * This client method calls into the service and verfies that MTOM is used over the wire on
     * the response. It corresponds to the JAXB_AttachmentSmallJpeg method which verifies that the
     * image is sent and received correctly.
     * 
     * @return
     * @throws Exception
     */
    public String jaxb_AttachmentSmallJpeg_VerifyMTOM() throws Exception {

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
        SOAPBodyElement payload = body.addBodyElement(new QName("urn://EnableSB_imagejpeg.mtom21.test.org", "invoke"));
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
	 * This testcase uses a JAXWS Dispatch invocation with JAXB generated request object
	 * as parameter. The endpoint for these testcase is a JAXWS Source Provider.
	 * 
	 * - sends small image (12K)
         * - uses no setting for MTOM from client (bindingID = null)
         * - mode = PAYLOAD
 	 * - no setting for SOAP version
         * @return returned from the remote method
         */

    public String JAXB_AttachmentSmallJpeg() throws Exception {

        // debug
        System.out.println("\n*** In DispatchImageJpegClient.JAXB_AttachmentSmallJpeg() ***\n");

        try {

            // create a Dynamic service & port
            Service svc = Service.create(serviceName);
            svc.addPort(portName, bindingID, endpointUrl);

            // debug
            System.out
                    .println("portname = " + portName + ",bindingID = " + bindingID + ",endpointUrl = " + endpointUrl);

            // jaxb artifacts package
            JAXBContext jbc = JAXBContext.newInstance("org.test.mtom21.enablesb_imagejpeg");

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
     * This client method calls into the service and verfies that MTOM is used over the wire on
     * the response. It corresponds to the JAXB_AttachmentLargeJpeg method which verifies that the
     * image is sent and received correctly.
     * 
     * @return
     * @throws Exception
     */
    public String jaxb_AttachmentLargeJpeg_VerifyMTOM() throws Exception {

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
        SOAPBodyElement payload = body.addBodyElement(new QName("urn://EnableSB_imagejpeg.mtom21.test.org", "invoke"));
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
	 * This testcase uses a JAXWS Dispatch invocation with JAXB generated request object
	 * as parameter. The endpoint for these testcase is a JAXWS Source Provider.
	 * 
	 * - sends an image (68K)
     * - uses no setting for MTOM from client (bindingID = null)
     * - mode = PAYLOAD
 	 * - no setting for SOAP version
     * @return returned from the remote method
     */

    public String JAXB_AttachmentLargeJpeg() throws Exception {

        // debug
        System.out.println("\n*** In DispatchImageJpegClient.JAXB_AttachmentLargeJpeg() ***\n");

        try {

            // create a Dynamic service & port
            Service svc = Service.create(serviceName);
            svc.addPort(portName, bindingID, endpointUrl);

            // debug
            System.out
                    .println("portname = " + portName + ",bindingID = " + bindingID + ",endpointUrl = " + endpointUrl);

            // jaxb artifacts package
            JAXBContext jbc = JAXBContext.newInstance("org.test.mtom21.enablesb_imagejpeg");

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

            // image expected - this image was optimized
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
