/**
 * @(#) 1.1 autoFVT/src/jaxws/xmlcatalog/wsfvt/client/WSClient.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 6/30/09 10:50:44 [8/8/12 06:09:26] 
 * 
 * IBM Confidential OCO Source Material
 * (C) COPYRIGHT International Business Machines Corp. 2006
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date       UserId        Feature/Defect  Description
 * ----------------------------------------------------------------------------
 * 06/29/2009 samerrel      552420          New File
 * 
 */
package jaxws.xmlcatalog.wsfvt.client;

import java.net.URL;

import javax.xml.namespace.QName;

import jaxws.xmlcatalog.wsfvt.service.HelloEjb;
import jaxws.xmlcatalog.wsfvt.service.HelloEjbService;
import jaxws.xmlcatalog.wsfvt.service.HelloWar;
import jaxws.xmlcatalog.wsfvt.service.HelloWarService;

/**
 *
 *
 */
public class WSClient {

    String hostandport = "http://localhost:9080";

    public void setHostandport(String newhostandport) {
        this.hostandport = "http://" + newhostandport;
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        WSClient wsc = new WSClient();
        if ( args.length == 1 ) {
            System.out.println("Setting hostandport from " + wsc.hostandport + " to: " + args[0]);
        }
        // try {
        // System.out.println("\n*** testHelloWar");
        // System.out.println(wsc.testHelloWar("testHelloWar"));
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        try {
            System.out.println("\n*** testHelloWarClientBadWsdlLoc");
            System.out.println(wsc.testHelloWarClientBadWsdlLoc("testHelloWarClientBadWsdlLoc"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            System.out.println("\n*** testHelloWarClientEjbRef");
            System.out.println(wsc.testHelloWarClientEjbRefBadWsdlLoc("testHelloWarClientEjbRefBadWsdlLoc"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // try {
        // System.out.println("\n*** testHelloEjb");
        // System.out.println(wsc.testHelloEjb("testHelloEjb"));
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        try {
            System.out.println("\n*** testHelloEjbClientBadWsdlLoc");
            System.out.println(wsc.testHelloEjbClientBadWsdlLoc("testHelloEjbClientBadWsdlLoc"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            System.out.println("\n*** testHelloEjbClientWarRefBadWsdlLoc");
            System.out.println(wsc.testHelloEjbClientWarRefBadWsdlLoc("testHelloEjbClientWarRefBadWsdlLoc"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String testHelloWar(String message) throws Exception {
        URL loc = new URL(hostandport + "/xmlcatalog/hellowar/HelloWarService?wsdl");
        QName qn = new QName("http://service.wsfvt.xmlcatalog.jaxws/", "HelloWarService");
        HelloWarService helloWarSvc = new HelloWarService(loc, qn);
        HelloWar helloWar = helloWarSvc.getHelloWarPort();
        String msg = helloWar.sayHello(message);
        return msg;
    }

    public String testHelloWarClientBadWsdlLoc(String message) throws Exception {
        URL loc = new URL(hostandport + "/xmlcatalog/hellowarclient/HelloWarClientService?wsdl");
        QName qn = new QName("http://client.wsfvt.xmlcatalog.jaxws/", "HelloWarClientService");
        HelloWarClientService helloWarClientSvc = new HelloWarClientService(loc, qn);
        HelloWarClient helloWarClient = helloWarClientSvc.getHelloWarClientPort();
        String msg = helloWarClient.callHelloWarClientBadWsdlAnnotation(message);
        return msg;
    }

    public String testHelloWarClientEjbRefBadWsdlLoc(String message) throws Exception {
        URL loc = new URL(hostandport + "/xmlcatalog/hellowarclient/HelloWarClientService?wsdl");
        QName qn = new QName("http://client.wsfvt.xmlcatalog.jaxws/", "HelloWarClientService");
        HelloWarClientService helloWarClientSvc = new HelloWarClientService(loc, qn);
        HelloWarClient helloWarClient = helloWarClientSvc.getHelloWarClientPort();
        String msg = helloWarClient.callHelloEjbClientBadWsdlAnnotation(message);
        return msg;
    }

    public String testHelloEjb(String message) throws Exception {
        URL loc = new URL(hostandport + "/xmlcatalog-helloejb/HelloEjbService?wsdl");
        QName qn = new QName("http://service.wsfvt.xmlcatalog.jaxws/", "HelloEjb");
        HelloEjbService helloEjbSvc = new HelloEjbService(loc, qn);
        HelloEjb helloEjb = helloEjbSvc.getHelloEjbPort();
        String msg = helloEjb.sayHello(message);
        return msg;
    }

    public String testHelloEjbClientBadWsdlLoc(String message) throws Exception {
        URL loc = new URL(hostandport + "/xmlcatalog-helloejbclient/HelloEjbClientService?wsdl");
        QName qn = new QName("http://client.wsfvt.xmlcatalog.jaxws/", "HelloEjbClientService");
        HelloEjbClientService helloEjbClientSvc = new HelloEjbClientService(loc, qn);
        HelloEjbClient helloEjbClient = helloEjbClientSvc.getHelloEjbClientPort();
        String msg = helloEjbClient.callHelloEjbClientBadWsdlAnnotation(message);
        return msg;
    }

    public String testHelloEjbClientWarRefBadWsdlLoc(String message) throws Exception {
        URL loc = new URL(hostandport + "/xmlcatalog-helloejbclient/HelloEjbClientService?wsdl");
        QName qn = new QName("http://client.wsfvt.xmlcatalog.jaxws/", "HelloEjbClientService");
        HelloEjbClientService helloEjbClientSvc = new HelloEjbClientService(loc, qn);
        HelloEjbClient helloEjbClient = helloEjbClientSvc.getHelloEjbClientPort();
        String msg = helloEjbClient.callHelloWarClientBadWsdlAnnotation(message);
        return msg;
    }
}
