//
// @(#) 1.1 autoFVT/src/mtom21/enablejaxb/wsfvt/client/EnableJAXBProviderSourceClient.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/15/08 14:29:37 [8/8/12 06:40:49]
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
import java.io.File;

import javax.imageio.ImageIO;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.soap.MTOMFeature;
import javax.xml.ws.soap.SOAPBinding;

import mtom21.disablejaxb.wsfvt.client.Common;

/*
 *   Message sent/received must use correct header/footer as defined in wsdl
 */
public class EnableJAXBProviderSourceClient {

    public static final String goodResult = "Message processed";
    public static final String badResult = "Problem in processing message";
    // the XML declaration added by Source transformation
    public static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

    public static final String str1 = "<ns1:inMessage xmlns:ns1=\"http://ws.apache.org/axis2\"><value>test output</value></ns1:inMessage>";
    public static final String expectedStr1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ns1:outMessage xmlns:ns1=\"http://ws.apache.org/axis2\"><value>test output</value></ns1:outMessage>";
    public static final String xmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

    public static final String axis2NSHeader = "<ns1:inMessage xmlns:ns1=\"http://ws.apache.org/axis2\">";
    public static final String axis2NSFooter = "</ns1:inMessage>";

    public static final String jpegFilename = "image1.jpeg";
    public static final String inHeader = "ns1:inMessage";
    public static final String outHeader = "ns1:outMessage";

    public static final QName serviceName = new QName("http://ws.apache.org/axis2", "EnableJAXBProviderSourceService");
    public static final QName portName = new QName("http://ws.apache.org/axis2", "AttachmentServicePort");
    public static final String endpointUrl = "http://@REPLACE_WITH_HOST_NAME@:"
            + "@REPLACE_WITH_PORT_NUM@/enablejaxb21/EnableJAXBProviderSourceService";

    public static final QName serviceNameVerify = new QName("http://ws.apache.org/axis2", "EnableJAXBProviderSourceServiceVerifyMTOM");
    public static final QName portNameVerify = new QName("http://ws.apache.org/axis2", "AttachmentServicePortVerifyMTOM");
    public static final String endpointUrlVerify = "http://@REPLACE_WITH_HOST_NAME@:"
            + "@REPLACE_WITH_PORT_NUM@/enablejaxb21/EnableJAXBProviderSourceServiceVerifyMTOM";

    public static final Service.Mode mode = Service.Mode.PAYLOAD;

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            EnableJAXBProviderSourceClient client = new EnableJAXBProviderSourceClient();
            client.SourceSOAP11Payload_01();
            client.SourceSequenceSOAP11Payload();
            client.SourceSOAP11Payload_02();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * This client method calls into a service that verfies that MTOM is NOT used over the wire on
     * the request. It corresponds to the SourceSOAP11Payload_01 method which verifies that the
     * image is sent and received correctly.
     * 
     * @return
     * @throws Exception
     */
    public String sourceSOAP11Payload_01_VerifyMTOM() throws Exception {

        // debug
        printMessageToConsole("***** In EnableJAXBProviderSourceClient.sourceSOAP11Payload_01_VerifyMTOM *****");

        String bindingID = SOAPBinding.SOAP11HTTP_BINDING;

        // create a service
        Service svc = Service.create(serviceNameVerify);
        svc.addPort(portNameVerify, bindingID, endpointUrlVerify);
        // debug
        printMessageToConsole("- add Port: portName=" + portNameVerify + ",bindingID=" + bindingID + ",endpointURL="
                + endpointUrlVerify);

        // set MTOM on dispatch
        MTOMFeature mtom21 = new MTOMFeature();

        // create dispatch<Source>
        javax.xml.ws.Dispatch<Source> dispatch = null;
        dispatch = svc.createDispatch(portNameVerify, Source.class, mode, mtom21);

        // force SOAPAction to match with wsdl action
        ((BindingProvider) dispatch).getRequestContext().put(BindingProvider.SOAPACTION_USE_PROPERTY, true);
        ((BindingProvider) dispatch).getRequestContext().put(BindingProvider.SOAPACTION_URI_PROPERTY, "echoString");

        Image image = ImageIO.read(new File(jpegFilename));
        byte[] ba = AttachmentHelper.getImageBytes(image, "image/jpeg");

        String orgSrc = axis2NSHeader + ba.toString() + axis2NSFooter;

        // call server's invoke
        Source retVal = dispatch.invoke(Common.toSource(orgSrc));

        // Common.toString - can only called once because it changes the Source
        String s = Common.toString(retVal);
        System.out.println("response: " + s);

        if (s != null && s.indexOf("MTOMOFF") != -1) {
            return goodResult;
        } else {
            return badResult;
        }
    }

    /**
     * This method will create a JAX-WS Dispatch object to call the remote method. - sets for MTOM
     * with SOAP11 using 2 steps: bindingID and setMTOMEnabled - mode = PAYLOAD
     *  - uses Source to wrap the byte[] format of an image, with correct header as defined in wsdl
     * 
     * @return returned from the remote method
     */
    public String SourceSOAP11Payload_01() {

        // debug
        printMessageToConsole("***** In EnableJAXBProviderSourceClient.SourceSOAP11Payload_01 *****");

        String sendBackStr = null;
        String bindingID = SOAPBinding.SOAP11HTTP_BINDING;

        try {

            // create a service
            Service svc = Service.create(serviceName);
            svc.addPort(portName, bindingID, endpointUrl);
            // debug
            printMessageToConsole("- add Port: portName=" + portName + ",bindingID=" + bindingID + ",endpointURL="
                    + endpointUrl);

            // set MTOM on dispatch
            MTOMFeature mtom21 = new MTOMFeature();

            // create dispatch<Source>
            javax.xml.ws.Dispatch<Source> dispatch = null;
            dispatch = svc.createDispatch(portName, Source.class, mode, mtom21);

            // force SOAPAction to match with wsdl action
            ((BindingProvider) dispatch).getRequestContext().put(BindingProvider.SOAPACTION_USE_PROPERTY, true);
            ((BindingProvider) dispatch).getRequestContext().put(BindingProvider.SOAPACTION_URI_PROPERTY, "echoString");

            Image image = ImageIO.read(new File(jpegFilename));
            byte[] ba = AttachmentHelper.getImageBytes(image, "image/jpeg");

            String orgSrc = axis2NSHeader + ba.toString() + axis2NSFooter;

            // call server's invoke
            Source retVal = dispatch.invoke(Common.toSource(orgSrc));

            // Common.toString - can only called once because it changes the Source
            String s = Common.toString(retVal);

            if (s != null) {
                String s2 = s.replaceAll(outHeader, inHeader);
                String newOrg = xmlHeader + orgSrc;

                if (s2.equals(newOrg)) { //
                    sendBackStr = goodResult;
                } else {

                    System.out.println("Received str is NOT correct.");
                    sendBackStr = badResult;
                }
            }
        } catch (Exception ex) {
            System.out.println("Client failed with exception.");
            ex.printStackTrace();
        }

        return sendBackStr;
    }
    
    /**
     * This client method calls into a service that verfies that MTOM is NOT used over the wire on
     * the request. It corresponds to the SourceSOAP11Payload_02 method which verifies that the
     * image is sent and received correctly.
     * 
     * @return
     * @throws Exception
     */
    public String sourceSOAP11Payload_02_VerifyMTOM() {

        // debug
        printMessageToConsole("***** In EnableJAXBProviderSourceClient.sourceSOAP11Payload_02_VerifyMTOM *****");

        String bindingID = SOAPBinding.SOAP11HTTP_BINDING;

        // create a service
        Service svc = Service.create(serviceNameVerify);
        svc.addPort(portNameVerify, bindingID, endpointUrlVerify);
        // debug
        printMessageToConsole("- add Port: portName=" + portNameVerify + ",bindingID=" + bindingID + ",endpointURL="
                + endpointUrlVerify);

        // set MTOM on dispatch
        MTOMFeature mtom21 = new MTOMFeature(true);

        // create dispatch<Source>
        javax.xml.ws.Dispatch<Source> dispatch = null;
        dispatch = svc.createDispatch(portNameVerify, Source.class, mode, mtom21);

        // force SOAPAction to match with wsdl action
        ((BindingProvider) dispatch).getRequestContext().put(BindingProvider.SOAPACTION_USE_PROPERTY, true);
        ((BindingProvider) dispatch).getRequestContext().put(BindingProvider.SOAPACTION_URI_PROPERTY, "echoString");

        String orgSrc = str1;
        // call server's invoke
        Source retVal = dispatch.invoke(Common.toSource(orgSrc));

        // Common.toString - can only called once because it changes the Source:
        String s = Common.toString(retVal);
        System.out.println("response: " + s);

        if (s != null && s.indexOf("MTOMOFF") != -1) {
            return goodResult;
        } else {
            return badResult;
        }
    }

    /**
     * This method will create a JAX-WS Dispatch object to call the remote method. - sets for MTOM
     * with SOAP11 using 2 steps: bindingID and setMTOMEnabled - mode = PAYLOAD - uses Source to
     * wrap a string, with correct header as defined in wsdl
     * 
     * @return returned from the remote method
     */

    public String SourceSOAP11Payload_02() {

        // debug
        printMessageToConsole("***** In EnableJAXBProviderSourceClient.SourceSOAP11Payload_02 *****");

        String sendBackStr = null;
        String bindingID = SOAPBinding.SOAP11HTTP_BINDING;

        try {

            // create a service
            Service svc = Service.create(serviceName);
            svc.addPort(portName, bindingID, endpointUrl);
            // debug
            printMessageToConsole("- add Port: portName=" + portName + ",bindingID=" + bindingID + ",endpointURL="
                    + endpointUrl);

            // set MTOM on dispatch
            MTOMFeature mtom21 = new MTOMFeature(true);

            // create dispatch<Source>
            javax.xml.ws.Dispatch<Source> dispatch = null;
            dispatch = svc.createDispatch(portName, Source.class, mode, mtom21);

            // force SOAPAction to match with wsdl action
            ((BindingProvider) dispatch).getRequestContext().put(BindingProvider.SOAPACTION_USE_PROPERTY, true);
            ((BindingProvider) dispatch).getRequestContext().put(BindingProvider.SOAPACTION_URI_PROPERTY, "echoString");

            String orgSrc = str1;
            // call server's invoke
            Source retVal = dispatch.invoke(Common.toSource(orgSrc));

            // Common.toString - can only called once because it changes the Source:
            String s = Common.toString(retVal);
            if (s != null) {

                if (s.equals(expectedStr1)) {
                    sendBackStr = goodResult;
                } else {
                    // debug
                    System.out.println("Received str is NOT correct.");
                    sendBackStr = badResult;
                }
            }
        } catch (Exception ex) {
            System.out.println("Client failed with exception.");
            ex.printStackTrace();
        }

        return sendBackStr;
    }
    
    /**
     * This client method calls into a service that verfies that MTOM is NOT used over the wire on
     * the request. It corresponds to the SourceSequenceSOAP11Payload method which verifies that the
     * image is sent and received correctly.
     * 
     * @return
     * @throws Exception
     */
    public String sourceSequenceSOAP11Payload_VerifyMTOM() {

        // debug
        printMessageToConsole("***** EnableJAXBProviderSourceClient.sourceSequenceSOAP11Payload_VerifyMTOM *****");

        String bindingID = SOAPBinding.SOAP11HTTP_BINDING;
        String orgSrc = str1;
        boolean mtomSetting;
        
        // create a service
        Service svc = Service.create(serviceNameVerify);
        svc.addPort(portNameVerify, bindingID, endpointUrlVerify);
        // debug
        printMessageToConsole("- add Port: portName=" + portNameVerify + ",bindingID=" + bindingID + ",endpointURL="
                + endpointUrlVerify);

        // set MTOM on dispatch
        MTOMFeature mtom21 = new MTOMFeature(true);

        // create dispatch<Source>
        javax.xml.ws.Dispatch<Source> dispatch = null;
        dispatch = svc.createDispatch(portNameVerify, Source.class, mode, mtom21);

        // force SOAPAction to match with wsdl action
        ((BindingProvider) dispatch).getRequestContext().put(BindingProvider.SOAPACTION_USE_PROPERTY, true);
        ((BindingProvider) dispatch).getRequestContext().put(BindingProvider.SOAPACTION_URI_PROPERTY, "echoString");

        // ---------Client MTOM ON ----------

        // test isMTOMEnalbed:
        mtomSetting = mtom21.isEnabled();

        if (mtomSetting == true) {
            System.out.println("mtomSetting (1) = True");
        } else {
            System.out.println("mtomSetting (1) = False");
            return badResult;
        }

        // call server's invoke
        Source retVal = dispatch.invoke(Common.toSource(orgSrc));

        // / Common.toString - can only called once because it changes the Source
        String s = Common.toString(retVal);
        System.out.println("response: " + s);

        if (s == null || s.indexOf("MTOMOFF") == -1) {
            return badResult;
        }

        // ---------- Client MTOM OFF ----------

        // set MTOM on dispatch
        mtom21 = new MTOMFeature(false);
        dispatch = svc.createDispatch(portNameVerify, Source.class, mode, mtom21);

        // test isMTOMEnalbed:
        mtomSetting = mtom21.isEnabled();

        if (mtomSetting == true) {
            System.out.println("mtomSetting (2) = True");
            return badResult;
        } else {
            System.out.println("mtomSetting (2) = False");
        }

        // call server's invoke
        retVal = dispatch.invoke(Common.toSource(orgSrc));

        // Common.toString - can only called once because it changes the Source:
        s = Common.toString(retVal);
        System.out.println("response: " + s);

        if (s == null || s.indexOf("MTOMOFF") == -1) {
            return badResult;
        }

        // ---------- Client MTOM ON ----------

        // set MTOM on dispatch
        mtom21 = new MTOMFeature(true);
        dispatch = svc.createDispatch(portNameVerify, Source.class, mode, mtom21);

        // test isMTOMEnalbed:
        mtomSetting = mtom21.isEnabled();

        if (mtomSetting == true) {
            System.out.println("mtomSetting (3) = True");
        } else {
            System.out.println("mtomSetting (3) = False");
            return badResult;
        }

        // call server's invoke
        retVal = dispatch.invoke(Common.toSource(orgSrc));

        // Common.toString - can only called once because it changes the Source:
        s = Common.toString(retVal);
        System.out.println("response: " + s);

        if (s == null || s.indexOf("MTOMOFF") == -1) {
            return badResult;
        }
        
        return goodResult;
    }

    public String SourceSequenceSOAP11Payload() {

        // currently, we only compare string.
        // to improve, add extract and compare images from the message

        // debug
        printMessageToConsole("***** EnableJAXBProviderSourceClient.SourceSequenceSOAP11Payload *****");

        String sendBackStr = null;
        String bindingID = SOAPBinding.SOAP11HTTP_BINDING;
        String orgSrc = str1;
        boolean mtomSetting;
        try {
            boolean test1 = true, test2 = true, test3 = true;

            // create a service
            Service svc = Service.create(serviceName);
            svc.addPort(portName, bindingID, endpointUrl);
            // debug
            printMessageToConsole("- add Port: portName=" + portName + ",bindingID=" + bindingID + ",endpointURL="
                    + endpointUrl);

            // set MTOM on dispatch
            MTOMFeature mtom21 = new MTOMFeature(true);

            // create dispatch<Source>
            javax.xml.ws.Dispatch<Source> dispatch = null;
            dispatch = svc.createDispatch(portName, Source.class, mode, mtom21);

            // force SOAPAction to match with wsdl action
            ((BindingProvider) dispatch).getRequestContext().put(BindingProvider.SOAPACTION_USE_PROPERTY, true);
            ((BindingProvider) dispatch).getRequestContext().put(BindingProvider.SOAPACTION_URI_PROPERTY, "echoString");

            // ---------Client MTOM ON ----------

            // test isMTOMEnalbed:
            mtomSetting = mtom21.isEnabled();

            if (mtomSetting == true) {
                System.out.println("mtomSetting (1) = True");
            } else {
                System.out.println("mtomSetting (1) = False");
                test1 = false;
            }

            // call server's invoke
            Source retVal = dispatch.invoke(Common.toSource(orgSrc));

            // Common.toString - can only called once because it changes the Source:
            String s = Common.toString(retVal);

            if (s != null) {
                if (s.equals(expectedStr1)) {
                    printMessageToConsole("Client MTOM ON - correct.");

                } else {
                    // debug
                    System.out.println("Client MTOM ON - Received str is NOT correct.");
                    test1 = false;
                }
            } else {
                test1 = false;
            }

            // ---------- Client MTOM OFF ----------

            // set MTOM on dispatch
            mtom21 = new MTOMFeature(false);
            dispatch = svc.createDispatch(portName, Source.class, mode, mtom21);

            // test isMTOMEnalbed:
            mtomSetting = mtom21.isEnabled();

            if (mtomSetting == true) {
                System.out.println("mtomSetting (2) = True");
                test2 = false;
            } else {
                System.out.println("mtomSetting (2) = False");
            }

            // call server's invoke
            retVal = dispatch.invoke(Common.toSource(orgSrc));

            // Common.toString - can only called once because it changes the Source:
            s = Common.toString(retVal);

            if (s != null) {
                if (s.equals(expectedStr1)) {
                    printMessageToConsole("Client MTOM OFF - correct.");

                } else {
                    // debug
                    System.out.println("Client MTOM OFF - Received str is NOT correct.");
                    test2 = false;
                }
            } else {
                test2 = false;
            }

            // ---------- Client MTOM ON ----------

            // set MTOM on dispatch
            mtom21 = new MTOMFeature(true);
            dispatch = svc.createDispatch(portName, Source.class, mode, mtom21);

            // test isMTOMEnalbed:
            mtomSetting = mtom21.isEnabled();

            if (mtomSetting == true) {
                System.out.println("mtomSetting (3) = True");
            } else {
                System.out.println("mtomSetting (3) = False");
                test3 = false;
            }

            // call server's invoke
            retVal = dispatch.invoke(Common.toSource(orgSrc));

            // Common.toString - can only called once because it changes the Source:
            s = Common.toString(retVal);

            if (s != null) {
                if (s.equals(expectedStr1)) {
                    printMessageToConsole("Client MTOM ON - correct.");
                } else {
                    // debug
                    System.out.println("Client MTOM ON - Received str is NOT correct.");
                    test3 = false;

                }
            } else {
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

    // ============================
    public static void printMessageToConsole(String msg) {
        System.out.println("At client side:\n" + msg);

    }

}
