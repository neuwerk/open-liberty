/*
 * @(#) 1.2 autoFVT/src/annotations/webserviceprovider/client/ProviderTestClient.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 12/19/06 15:23:14 [8/8/12 06:55:34]
 *
 *
 * IBM Confidential OCO Source Material
 * 5724-J08 (C) COPYRIGHT International Business Machines Corp. 2003, 2005
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 *  09/01/06   btiffany    LIDB3296.31.01     new file
 *
 */
package annotations.webserviceprovider.client;

import java.io.StringReader;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

public class ProviderTestClient {

    // multiple deps, do not change this message, clone this class if you need a new msg. 
    //private static String soapMsgReqString = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body><echo xmlns=\"http://annotations.webserviceprovider.fq\"><arg0>Hello Server</arg0></echo></soapenv:Body></soapenv:Envelope>";
    private static String soapMsgReqString = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body><echo xmlns=\"http://server.webserviceprovider.annotations/\"><arg0>Hello Server</arg0></echo></soapenv:Body></soapenv:Envelope>";
    private static Service service;

    public static void main(String[] args) {  
        if (args.length >0 ){
            runClient(args[0]);
        } else    {
            System.out.println("usage: ProviderTestClient (url)");
            runClient("");
        }        
    }
    
    public static String runClient(String url){

        ProviderTestClient client = new ProviderTestClient();
        String responseStr = null;

        String soapBindingURI = SOAPBinding.SOAP11HTTP_BINDING;
        
        /*
        QName qs = new QName("http://annotations.webserviceprovider.fq", "ProvFqService");
        QName qp = new QName("http://annotations.webserviceprovider.fq", "FqPort");
        */
        QName qs = new QName("server.webserviceprovider.annotations", "ProvBasicService");
        QName qp = new QName("server.webserviceprovider.annotations", "ProvBasicPort");
        URL   u = null;
        //String urlString = "file:/tmp/test.wsdl";
        String urlString = "http://localhost:19080/testprov/testprov";
        if (url.length() > 0) urlString = url;
        System.out.println("invoking service at url: "+ urlString);
        try{
            // this wsdl has to be for real. 
            u = new URL(urlString);
        } catch (Exception e){
            e.printStackTrace();
        }

        // invoke the basic Service creator directly, don't use anything generated.
        service = Service.create(qs );
        service.addPort(qp,SOAPBinding.SOAP11HTTP_BINDING, urlString);  // now WHY do I have to do this? 
        
        // now create a dispatch object from it 
        Dispatch<SOAPMessage> dispatch = service.createDispatch(qp, SOAPMessage.class, Service.Mode.MESSAGE);
        
        // now it would be too easy to just send the soapmsgrequest string down the wire.
        // We have to make a soapmessage out of it using SAAJ's rather convoluted approach here..
        SOAPMessage soapMsg = null;
        try {
            MessageFactory mf = MessageFactory.newInstance();
            soapMsg = mf.createMessage();
            soapMsg.getSOAPPart().setContent(
                    (Source) new StreamSource(new StringReader(soapMsgReqString)));
            soapMsg.saveChanges();
            // wasn't that simplifying...
        } catch (SOAPException e) {
            e.printStackTrace();
        } 
        

        try {
            System.out.println("\n Sending " + soapMsgReqString);
            
            if(dispatch== null || soapMsg == null){
                throw new RuntimeException("dispatch or soapMsg is null!");
            }

            SOAPMessage response = dispatch.invoke(soapMsg);  
            
            // imho, SOAPMessage = too cute for real world OOP
            responseStr = response.getSOAPPart().getEnvelope().getBody().getTextContent().toString();
            System.out.println("received body content:"+ responseStr );
            return responseStr;

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return responseStr;
    }


}
