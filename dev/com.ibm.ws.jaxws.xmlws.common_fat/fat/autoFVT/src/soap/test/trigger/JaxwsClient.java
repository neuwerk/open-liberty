//
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// ----------------------------------------------------------------------------
// 06/05/07 jramos      440922          Integrate ACUTE
//

package soap.test.trigger;

import java.net.URL;

import java.util.Map;

import javax.xml.namespace.QName;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;

import javax.xml.rpc.ServiceFactory;

import javax.xml.ws.http.HTTPBinding;
import javax.xml.ws.soap.SOAPBinding;

import common.utils.topology.IAppServer;
import common.utils.topology.Ports;
import common.utils.topology.visitor.QueryDefaultNode;

/**
 * This class will acts as a client to reach a JAX-WS and JAX-RPC based
 * server side implementations.
 */
public class JaxwsClient {

    static IAppServer server = QueryDefaultNode.defaultAppServer;
    private QName serviceName11 =
        new QName("http://ws.apache.org/saaj", "SAAJService");
    private QName serviceName12 =
        new QName("http://ws.apache.org/saaj", "SAAJService1");
    private QName portName =
        new QName("http://ws.apache.org/saaj", "SAAJServicePort");
    
    /** 
     * A no argument constructor to create a JaxwsClient.
     */
    public JaxwsClient() {
    }

    /** 
     * The main method.
     *
     * @param args The command line arguments
     */
    public static void main(String[] args) throws Exception {
        JaxwsClient client = new JaxwsClient();
        String value = client.callJaxwsStringProvider();
        System.out.println("JAX-WS  Response: " + value);
    }

    /** 
     * This method will create a JAX-WS Dispatch object
     * to call the remote method.
     *
     * @return The String returned from the remote method
     */
    public String callJaxwsStringProvider() {
        String endpointUrl = "http://" + server.getMachine().getHostname() +  
                             ":" + server.getPortMap().get(Ports.WC_defaulthost) +   
                             "/saajfvt/SAAJService";             
                             //"http://@REPLACE_WITH_HOST_NAME@:"
                             //+ "@REPLACE_WITH_PORT_NUM@"
                             //+ "/saajfvt/SAAJService";
        String xmlString = "<s11:invoke xmlns:s11=\"http://ws.apache.org/saaj\">test input</s11:invoke>";
        Service svc = Service.create(serviceName11);
        svc.addPort(portName, SOAPBinding.SOAP11HTTP_BINDING, "");
        Dispatch<String> dispatch = svc
                .createDispatch(portName, String.class, null);
        Map<String, Object> requestContext = dispatch.getRequestContext();
        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                endpointUrl);
        return dispatch.invoke(xmlString);
    }

    /** 
     * This method will create a JAX-WS Dispatch object
     * to call the remote method.
     *
     * @return The String returned from the remote method
     */
    public String callJaxwsString12Provider() {
        String endpointUrl = "http://" + server.getMachine().getHostname() +  
                             ":" + server.getPortMap().get(Ports.WC_defaulthost) +   
                             "/saajfvt/SAAJService1";             
                             //"http://@REPLACE_WITH_HOST_NAME@:"
                             //+ "@REPLACE_WITH_PORT_NUM@"
                             //+ "/saajfvt/SAAJService1";
        String xmlString = "<s12:invoke xmlns:s12=\"http://ws.apache.org/saaj\">test input</s12:invoke>";
        Service svc = Service.create(serviceName12);
        svc.addPort(portName, SOAPBinding.SOAP12HTTP_BINDING, "");
        Dispatch<String> dispatch = svc
                .createDispatch(portName, String.class, null);
        Map<String, Object> requestContext = dispatch.getRequestContext();
        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                endpointUrl);
        return dispatch.invoke(xmlString);
    }

}
