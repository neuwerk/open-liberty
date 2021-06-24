//
// @(#) 1.1 autoFVT/src/mtom21/security/wsfvt/client/ClientOFF_Security_ProxySOAP12Client.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/15/08 14:39:10 [8/8/12 06:40:59]
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

package mtom21.security.wsfvt.client;

import java.awt.Image;
import java.io.*;
import java.util.Map;
import java.net.URL;
import java.util.Iterator;

import java.net.MalformedURLException;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.imageio.ImageIO;
import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.MTOMFeature;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.BindingProvider;

import org.apache.axiom.om.util.Base64;

import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

import common.utils.execution.ExecutionException;
import common.utils.execution.ExecutionFactory;
import com.ibm.websphere.simplicity.OperatingSystem;
import common.utils.topology.Cell;
import common.utils.topology.IAppServer;
import common.utils.topology.IDmgrNodeContainer;
import common.utils.topology.IMachine;
import common.utils.topology.INodeContainer;
import common.utils.topology.MachineFactory;
import common.utils.topology.Topology;
import common.utils.topology.TopologyActions;
import common.utils.topology.visitor.QueryDefaultNode;

/**
 * This test case requires client key and trust stores.  
 * Example on Base:  start the server and generate them using cmd:
 * WAS_HOME\bin\retrieveSigners.bat NodeDefaultTrustStore ClientDefaultTrustStore -conntype SOAP -autoAcceptBootstrapSigner
 */

/**
 * This testcase uses a JAXB generated objects
 * This testcase covers the "image/jpeg" mime types, 
 * using Proxy to send a message as dataHandler object.
 * Server has MTOM = ON with SOAP12
 * Client has MTOM = OFF
 */


public class ClientOFF_Security_ProxySOAP12Client {

	public static final String goodResult = "Message processed";
    public static final String badResult = "Problem in processing message";

    String jpegFilename2 = "source/image2.jpeg"; // about 60KB
    String jpegOptimizedFilename2 = "source/image2Expected.jpeg";

    String sendBackStr = null;

    String endpointUrlProxy = "https://@REPLACE_WITH_HOST_NAME@:"
            + "@REPLACE_WITH_PORT_NUM_SECURE@/mtomsecurity21/SecurityMtomOnProxySOAP12Service";
    String http_endpointUrlProxy = "http://@REPLACE_WITH_HOST_NAME@:"
            + "@REPLACE_WITH_PORT_NUM@/mtomsecurity21/SecurityMtomOnProxySOAP12Service";

    String endpointUrlProvider = "https://@REPLACE_WITH_HOST_NAME@:"
            + "@REPLACE_WITH_PORT_NUM_SECURE@/mtomsecurity21/SecurityVerifyMTOMOff";
    String http_endpointUrlProvider = "http://@REPLACE_WITH_HOST_NAME@:"
            + "@REPLACE_WITH_PORT_NUM@/mtomsecurity21/SecurityVerifyMTOMOff";

    
    public static void main(String[] args) throws Exception {

         ClientOFF_Security_ProxySOAP12Client client = new ClientOFF_Security_ProxySOAP12Client();
         System.out.println("Test result:\n" + client.clientProxy_AttachmentLargeJpeg_ServerProvider());
         System.out.println("Test result:\n" + client.soapClient_AttachmentLargeJpeg_ProxyServer());
         System.out.println("Test result:\n" + client.proxyClient_AttachmentLargeJpeg_ProxyServer());

     }


    /**
     * This client sends a request with MTOM disabled to a SOAP Provider service with SSL
     * enabled. The client is a proxy client. The server verifies that MTOM is disabled on the
     * incoming request.
     */
    public String clientProxy_AttachmentLargeJpeg_ServerProvider() {

        QName serviceName = new QName("http://org/test/mtom21/SecurityVerifyMTOM",
                "SecurityVerifyMTOMOff");
        QName portName = new QName("http://org/test/mtom21/SecurityVerifyMTOM", "SecurityVerifyMTOMOffPort");

        System.out.println("\n*** In ClientOFF_Security_ProxySOAP12Client.clientProxy_AttachmentLargeJpeg_ServerProvider ***\n");
        System.out.println("portname = " + portName + ",endpointUrl = " + endpointUrlProvider);

        // setting JSSE properties
        Cell cell = TopologyDefaults.defaultAppServerCell;
        INodeContainer rootNode = cell.getRootNodeContainer();
        String profileDir = null;

        Map sslProps = null;
        sslProps = rootNode.getJsseSslProps();
        profileDir = rootNode.getProfileDir();

        IMachine machine = TopologyActions.FVT_MACHINE;
        if (machine.getOperatingSystem() == OperatingSystem.ZOS) {
            // copy the ssl props file locally
            String localSslProps = AppConst.FVT_BUILD_WORK_DIR + "/ssl.client.props";
            ExecutionFactory.getExecution().executeCopyFile(profileDir + "/properties/ssl.client.props",
                    rootNode.getMachine().getHostname(), localSslProps, TopologyActions.FVT_HOSTNAME);
            System.setProperty("com.ibm.SSL.ConfigURL", "file:" + localSslProps);
        } else {
            for (Iterator i = sslProps.keySet().iterator(); i.hasNext();) {
                String key = (String) i.next();
                System.setProperty(key, (String) sslProps.get(key));
            }
        }

        // set up wsdlUrl
        URL wsdlUrl = null;
        try {

            wsdlUrl = new URL(http_endpointUrlProvider + "?wsdl");
        } catch (MalformedURLException Ex) {
        }

        File file = new File(jpegFilename2);
        System.out.println(">> Loading data from " + jpegFilename2);
        FileDataSource fds = new FileDataSource(file);
        DataHandler content = new DataHandler(fds);

        // Set the data inside of the appropriate object
        org.test.mtom21.securityverifymtom.ImageDepot imageDepot = new org.test.mtom21.securityverifymtom.ObjectFactory().createImageDepot();
        imageDepot.setImageData(content);

        if (imageDepot != null) {
            System.out.println("Data loaded.");

        } else {
            System.out.println("[ERROR] Could not load data");
            System.exit(-1);
        }

        // Disable MTOM
        MTOMFeature mtom21 = new MTOMFeature(false);

        // Setup the necessary JAX-WS artifacts
        Service svc = Service.create(wsdlUrl, serviceName);
        org.test.mtom21.securityverifymtom.ImageServiceInterface proxy = svc.getPort(portName, org.test.mtom21.securityverifymtom.ImageServiceInterface.class, mtom21);

        // Set the target URL
        BindingProvider bp = (BindingProvider) proxy;
        Map<String, Object> requestCtx = bp.getRequestContext();
        requestCtx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrlProvider);

        // Send the image and process the response image
        org.test.mtom21.securityverifymtom.ImageDepot response = proxy.sendImage(imageDepot);
        if (response != null) {
            System.out.println("-- Response received");

            DataHandler dh = response.getImageData();

            File f = null;
            try {

                if (dh != null) {
                    // write to current directory, whatever it is
                    f = new File("source/ReceivedFile.jpeg");
                    if (f.exists()) {
                        f.delete();
                    }
                }

                FileOutputStream fos = new FileOutputStream(f);
                dh.writeTo(fos);
                fos.close();

                // open received file and compare to the file that was sent
                Image imageReceived = ImageIO.read(new File("source/ReceivedFile.jpeg"));
                Image imageSent = ImageIO.read(new File(jpegFilename2));
                Image optImage = ImageIO.read(new File(jpegOptimizedFilename2));

                if (AttachmentHelper.compareImages(imageReceived, imageSent) == true) {

                    System.out.println("Image received equals to image sent");
                    sendBackStr = goodResult;
                } else if (AttachmentHelper.compareImages(imageReceived, optImage) == true) {
                    // debug
                    System.out.println("Image received equals to optimized image");
                    sendBackStr = goodResult;
                } else {
                    System.out.println("**ERROR - Received image does not match the image sent.");
                    sendBackStr = "**ERROR - Received image does not match expected image.";
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        } else {
            sendBackStr = "**ERROR - Received image is null";
            System.out.println("**ERROR - Received image is null");
        }

        return sendBackStr;
    }


    /**
     * This client sends a request with MTOM disabled to a proxy service with SSL enabled. The
     * server sends the image back using MTOM and SOAP 1.2. The client examines the response message and verifies
     * that MTOM was used in the response.
     */
    public String soapClient_AttachmentLargeJpeg_ProxyServer() {

        QName serviceName = new QName("http://org/test/mtom21/SecurityMtomOnProxySOAP12", "SecurityMtomOnProxySOAP12Service");
        QName portName = new QName("http://org/test/mtom21/SecurityMtomOnProxySOAP12", "AttachmentServicePort");

        System.out.println("\n*** In ClientOFF_Security_ProxyClient.soapClient_AttachmentLargeJpeg_ProxyServer ***\n");
        System.out.println("portname = " + portName + ",endpointUrl = " + endpointUrlProxy);

        // setting JSSE properties
        Cell cell = TopologyDefaults.defaultAppServerCell;
        INodeContainer rootNode = cell.getRootNodeContainer();
        String profileDir = null;

        Map sslProps = null;
        sslProps = rootNode.getJsseSslProps();
        profileDir = rootNode.getProfileDir();

        IMachine machine = TopologyActions.FVT_MACHINE;
        if (machine.getOperatingSystem() == OperatingSystem.ZOS) {
            // copy the ssl props file locally
            String localSslProps = AppConst.FVT_BUILD_WORK_DIR + "/ssl.client.props";
            ExecutionFactory.getExecution().executeCopyFile(profileDir + "/properties/ssl.client.props",
                    rootNode.getMachine().getHostname(), localSslProps, TopologyActions.FVT_HOSTNAME);
            System.setProperty("com.ibm.SSL.ConfigURL", "file:" + localSslProps);
        } else {
            for (Iterator i = sslProps.keySet().iterator(); i.hasNext();) {
                String key = (String) i.next();
                System.setProperty(key, (String) sslProps.get(key));
            }
        }

        // read in the data
        File jpegFile = new File(jpegFilename2);
        Long fileSize = jpegFile.length();
        byte[] data = new byte[fileSize.intValue()];
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(jpegFile);
            fis.read(data);
        } catch(Exception e) {
            return sendBackStr;
        }
       
        //Setup the necessary JAX-WS artifacts
        Service svc = Service.create(serviceName);
        svc.addPort(portName, SOAPBinding.SOAP12HTTP_BINDING, endpointUrlProxy);
        
        // create Dispatch
        Dispatch<SOAPMessage> dispatch = svc.createDispatch(portName, SOAPMessage.class, Service.Mode.MESSAGE);

        // force SOAPAction to match with wsdl action
        ((BindingProvider) dispatch).getRequestContext().put(BindingProvider.SOAPACTION_USE_PROPERTY, true);
        ((BindingProvider) dispatch).getRequestContext().put(BindingProvider.SOAPACTION_URI_PROPERTY, "send");
       
        SOAPMessage request = null;
        try {
            // construct SOAPMessage
            request = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage();
            SOAPBody body = request.getSOAPBody();
            SOAPBodyElement payload = body.addBodyElement(new QName("http://org/test/mtom21/SecurityMtomOnProxySOAP12", "sendImage"));
            SOAPElement output = payload.addChildElement("input");
            SOAPElement imageData = output.addChildElement("imageData");
            String dataString = Base64.encode(data);
            imageData.addTextNode(dataString);
        } catch(Exception e) {
            return sendBackStr;
        }

        //Send the image and process the response image
        SOAPMessage response = dispatch.invoke(request);

        if(!response.getAttachments().hasNext()) {
            System.out.println("MTOM not used on server response");
            return badResult;
        }
        
        return goodResult;
    }

    /**
     * This client sends a request with MTOM disabled to a proxy server service with SSL
     * enabled. The client is a proxy client. It is verified that the data is passed back and forth
     * correctly with MTOM disabled on the client side and enabled on the server side.
     */
    public String proxyClient_AttachmentLargeJpeg_ProxyServer() {

        QName serviceName = new QName("http://org/test/mtom21/SecurityMtomOnProxySOAP12", "SecurityMtomOnProxySOAP12Service");
        QName portName = new QName("http://org/test/mtom21/SecurityMtomOnProxySOAP12", "AttachmentServicePort");

        System.out.println("\n*** In ClientOFF_Security_ProxyClient.proxyClient_AttachmentLargeJpeg_ProxyServer ***\n");
        System.out.println("portname = " + portName + ",endpointUrl = " + endpointUrlProxy);

        // setting JSSE properties
        Cell cell = TopologyDefaults.defaultAppServerCell;
        INodeContainer rootNode = cell.getRootNodeContainer();
        String profileDir = null;

        Map sslProps = null;
        sslProps = rootNode.getJsseSslProps();
        profileDir = rootNode.getProfileDir();

        IMachine machine = TopologyActions.FVT_MACHINE;
        if (machine.getOperatingSystem() == OperatingSystem.ZOS) {
            // copy the ssl props file locally
            String localSslProps = AppConst.FVT_BUILD_WORK_DIR + "/ssl.client.props";
            ExecutionFactory.getExecution().executeCopyFile(profileDir + "/properties/ssl.client.props",
                    rootNode.getMachine().getHostname(), localSslProps, TopologyActions.FVT_HOSTNAME);
            System.setProperty("com.ibm.SSL.ConfigURL", "file:" + localSslProps);
        } else {
            for (Iterator i = sslProps.keySet().iterator(); i.hasNext();) {
                String key = (String) i.next();
                System.setProperty(key, (String) sslProps.get(key));
            }
        }

        File file = new File(jpegFilename2);
        System.out.println(">> Loading data from " + jpegFilename2);
        FileDataSource fds = new FileDataSource(file);
        DataHandler content = new DataHandler(fds);

        //Set the data inside of the appropriate object
        org.test.mtom21.securitymtomonproxysoap12.ImageDepot imageDepot = new org.test.mtom21.securitymtomonproxysoap12.ObjectFactory().createImageDepot();
        imageDepot.setImageData(content);

        if (imageDepot != null) {
            System.out.println("Data loaded.");

        } else {
            System.out.println("[ERROR] Could not load data");
            System.exit(-1);
        }

        //Disable MTOM
        MTOMFeature mtom21 = new MTOMFeature(false);

        // set up wsdlUrl
        URL wsdlUrl = null;
        try {

            wsdlUrl = new URL(http_endpointUrlProxy + "?wsdl");
        } catch (MalformedURLException Ex) {
        }

        //Setup the necessary JAX-WS artifacts
        Service svc = Service.create(wsdlUrl, serviceName);
        org.test.mtom21.securitymtomonproxysoap12.ImageServiceInterface proxy = svc.getPort(portName, org.test.mtom21.securitymtomonproxysoap12.ImageServiceInterface.class, mtom21);

        //Set the target URL
        BindingProvider bp = (BindingProvider) proxy;
        Map<String, Object> requestCtx = bp.getRequestContext();
        requestCtx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrlProxy);

        //Send the image and process the response image
        org.test.mtom21.securitymtomonproxysoap12.ImageDepot response = proxy.sendImage(imageDepot);
        if (response != null) {
            System.out.println("-- Response received");

            DataHandler dh = response.getImageData();

            File f = null;
            try {

                if (dh != null) {
                    // write to current directory, whatever it is
                    f = new File("source/ReceivedFile.jpeg");
                    if (f.exists()) {
                        f.delete();
                    }
                }

                FileOutputStream fos = new FileOutputStream(f);
                dh.writeTo(fos);
                fos.close();

                // open received file and compare to the file that was sent
                Image imageReceived = ImageIO.read(new File("source/ReceivedFile.jpeg"));
                Image imageSent = ImageIO.read(new File(jpegFilename2));
                Image optImage = ImageIO.read(new File(jpegOptimizedFilename2));

                if (AttachmentHelper.compareImages(imageReceived, imageSent) == true) {
                    System.out.println("Image received equals to image sent");
                    sendBackStr = goodResult;
                } else if (AttachmentHelper.compareImages(imageReceived, optImage) == true) {
                    System.out.println("Image received equals to optimized image");
                    sendBackStr = goodResult;
                } else {
                    System.out.println("**ERROR - Received image does not match the image sent.");
                    sendBackStr = "**ERROR - Received image does not match expected image.";
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        } else {
            sendBackStr = "**ERROR - Received image is null";
            System.out.println("**ERROR - Received image is null");
        }

        return sendBackStr;
    }
}
