//
// @(#) 1.10 autoFVT/src/mtom/security/wsfvt/client/ClientON_ServerON_Security_ProxySOAP12Client.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 10/23/07 15:14:17 [8/8/12 06:58:18]
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
// 10/30/06 jtnguyen    LIDB3402-07.04  New File
// 01/22/07 jtnguyen    416083          Changed package name to lower case
// 02/13/07 jtnguyen    420519          Change port # to @REPLACE_WITH_PORT_NUM_SECURE@
// 02/25/07 jtnguyen    421977          Changed for soap 12 service endpoint 
// 04/11/07 jtnguyen    431704          Used server object to set System Properties
// 04/23/07 ulbricht    434578          Allow running on dmgr
// 05/01/07 ulbricht    434046          z/OS SSL different
// 05/07/07 jtnguyen    435716          Added @REPLACE_WITH_PORT_NUM@ for ?wsdl
// 06/04/07 jramos      440922          Integrate ACUTE
// 10/23/07 jramos      476750          Use ACUTE 2.0 api and TopologyDefaults

package mtom.security.wsfvt.client;

import java.awt.Image;
import java.io.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.net.MalformedURLException;

import java.lang.Integer;
import java.net.URL;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.imageio.ImageIO;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.BindingProvider;

import org.test.mtom.securitymtomonproxysoap12.*;

import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

import common.utils.execution.ExecutionException;
import com.ibm.websphere.simplicity.OperatingSystem;
import common.utils.topology.Cell;
import common.utils.topology.IAppServer;
import common.utils.topology.IDmgrNodeContainer;
import common.utils.topology.IMachine;
import common.utils.topology.MachineFactory;
import common.utils.topology.TopologyActions;
import common.utils.topology.visitor.QueryDefaultNode;
 

/**
 * This test case requires client key and trust stores.  
 * Example on Base:  start the server and generate them using cmd:
 * WAS_HOME\bin\retrieveSigners.bat NodeDefaultTrustStore ClientDefaultTrustStore -conntype SOAP -autoAcceptBootstrapSigner
 */



public class ClientON_ServerON_Security_ProxySOAP12Client {

	public static final String goodResult = "Message processed";

	String jpegFilename2 = "source/image2.jpeg";  // about 60KB
	String jpegOptimizedFilename2 = "source/image2Expected.jpeg";	
	
    String sendBackStr = null;

    QName serviceName = new QName("http://org/test/mtom/SecurityMtomOnProxySOAP12", "SecurityMtomOnProxySOAP12Service");
    QName portName    = new QName("http://org/test/mtom/SecurityMtomOnProxySOAP12", "AttachmentServicePort");


    String endpointUrl = "https://@REPLACE_WITH_HOST_NAME@:"
                          + "@REPLACE_WITH_PORT_NUM_SECURE@/security/SecurityMtomOnProxySOAP12Service";

    String http_endpointUrl = "http://@REPLACE_WITH_HOST_NAME@:"
                          + "@REPLACE_WITH_PORT_NUM@/security/SecurityMtomOnProxySOAP12Service";

    Service.Mode mode = Service.Mode.PAYLOAD;


    public static void main(String[] args) throws Exception {

        ClientON_ServerON_Security_ProxySOAP12Client client = new ClientON_ServerON_Security_ProxySOAP12Client();
        System.out.println("Test result:\n" + client.Proxy_AttachmentLargeJpeg());

    }

    /**
     * This testcase uses a JAXB generated objects, covering the "image/jpeg" mime types, 
     * using Proxy to send a message as dataHandler object.
     * Tested with SSL, where
     *   Server has MTOM = ON with SOAP12
     *   Client has MTOM = ON
     *   Service Mode = payload
     */

    public String Proxy_AttachmentLargeJpeg() {
    	
   	   System.out.println("\n*** In ClientON_ServerON_Security_ProxySOAP12Client.Proxy_AttachmentLargeJpeg ***\n");  	   
     	   System.out.println("portname = " + portName  + ",endpointUrl = " + endpointUrl);

           // setting JSSE properties
           Cell cell = TopologyDefaults.defaultAppServerCell;
           IAppServer server = TopologyDefaults.defaultAppServer;
           String profileDir = null;

           Map sslProps = null;
           if (cell.isTopologyND()) {
               IDmgrNodeContainer dmgr = (IDmgrNodeContainer)cell.getRootNodeContainer();
               sslProps = dmgr.getJsseSslProps();
               profileDir = dmgr.getProfileDir();
           } else {
               sslProps = server.getNodeContainer().getJsseSslProps();
               profileDir = server.getNodeContainer().getProfileDir();
           }

           IMachine machine = null;
           try {
               machine = MachineFactory.getMachine(cell.getTopologyFile(), TopologyActions.FVT_HOSTNAME);
           } catch(Exception e) {
               e.printStackTrace();
           }
           if (machine.getOperatingSystem() == OperatingSystem.ZOS) {
               System.setProperty("com.ibm.SSL.ConfigURL", "file:"
                                  + profileDir.replace('\\', '/')
                                  + "/properties/ssl.client.props");
               //debug
               System.out.println("profileDir=" + profileDir);

           } else {
               for (Iterator i = sslProps.keySet().iterator(); i.hasNext(); ) {
                   String key = (String)i.next();
                   System.setProperty(key, (String)sslProps.get(key));
               }
           }
     		        	
            // set up wsdlUrl             	
            URL wsdlUrl = null;
            try {
            
                wsdlUrl = new URL(http_endpointUrl + "?wsdl"); 
            } catch (MalformedURLException Ex){
            }

    	    File file = new File(jpegFilename2);
    	    System.out.println(">> Loading data from " + jpegFilename2);
    	    FileDataSource fds = new FileDataSource(file);
    	    DataHandler content = new DataHandler(fds);
           
            //Set the data inside of the appropriate object
            ImageDepot imageDepot = new ObjectFactory().createImageDepot();
            imageDepot.setImageData(content);
          	
            if (imageDepot != null) {
            	System.out.println("Data loaded.");

            }
            else {
            	System.out.println("[ERROR] Could not load data");
            	System.exit(-1);
            }
 
            //Setup the necessary JAX-WS artifacts
            Service svc = Service.create(wsdlUrl, serviceName);
            ImageServiceInterface proxy = svc.getPort(portName, ImageServiceInterface.class);
 
            //Set the target URL
            BindingProvider bp = (BindingProvider) proxy;
            Map<String, Object> requestCtx = bp.getRequestContext();
            requestCtx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl);
             
            //Enable MTOM
            SOAPBinding binding = (SOAPBinding) bp.getBinding();
            binding.setMTOMEnabled(true);
             
            //Send the image and process the response image
            ImageDepot response = proxy.sendImage(imageDepot);
            if (response != null) {
            	System.out.println("-- Response received");
            	
            	DataHandler dh = response.getImageData();
            
                try {
                
                   File f= null;
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
                   Image imageReceived = ImageIO.read(new File ("source/ReceivedFile.jpeg"));
                   Image imageSent = ImageIO.read(new File (jpegFilename2));
                   Image optImage = ImageIO.read(new File (jpegOptimizedFilename2));
                   
                   if (AttachmentHelper.compareImages(imageReceived,imageSent)== true){       	
                       System.out.println("Image received equals to image sent.");
           	       sendBackStr = goodResult;
           	   }
                   else if (AttachmentHelper.compareImages(imageReceived,optImage)== true){       	
         	       System.out.println("Image received equals to optimized image.");
         	       sendBackStr = goodResult;
                   } else {
                       System.out.println("**ERROR - Received image does not match the image sent.");
                       sendBackStr = "**ERROR - Received image does not match expected image.";
           	  }
                } catch (IOException ex) {
                    ex.printStackTrace();     
                }

            }
            else {
                sendBackStr = "**ERROR - Received image is null";
                System.out.println("**ERROR - Received image is null");
            }
  	   return sendBackStr;
     }   

}
