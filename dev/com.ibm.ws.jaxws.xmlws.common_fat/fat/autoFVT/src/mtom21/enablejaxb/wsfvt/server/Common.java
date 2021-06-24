///
// @(#) 1.1 autoFVT/src/mtom21/enablejaxb/wsfvt/server/Common.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/15/08 14:30:05 [8/8/12 06:40:49]
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

package mtom21.enablejaxb.wsfvt.server;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPConstants;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class Common {

    public static final String SOAP11_NAMESPACE = "http://schemas.xmlsoap.org/soap/envelope";
    public static final String SOAP12_NAMESPACE = "http://www.w3.org/2003/05/soap-envelope";

    /**
     * Adapter method used to convert any type of Source to a String
     * 
     * @param input
     * @return
     */
    public static String toString(Source input) {

        if (input == null)
            return null;

        StringWriter writer = new StringWriter();
        Transformer trasformer;
        try {
            trasformer = TransformerFactory.newInstance().newTransformer();
            Result result = new StreamResult(writer);
            trasformer.transform(input, result);
        } catch (Exception e) {
            return null;
        }

        return writer.getBuffer().toString();
    }

    /**
     * Adapter method used to convert any type of SOAPMessage to a String
     * 
     * @param input
     * @return
     */
    public static String toString(SOAPMessage input) {

        if (input == null)
            return null;

        Source result = null;
        try {
            result = input.getSOAPPart().getContent();
        } catch (SOAPException e) {
            e.printStackTrace();
        }

        return toString(result);
    }

    /**
     * Build Stream Source object
     * 
     * @param src
     * @return
     */
    public static Source toSource(String src) {

        if (src == null)
            return null;

        StreamSource ret = null;
        InputStream stream = new ByteArrayInputStream(src.getBytes());
        ret = new StreamSource((InputStream) stream);

        return ret;
    }

    /**
     * Method used to convert Strings to SOAPMessages
     * 
     * @param msgString
     * @return
     */
    public static SOAPMessage toSOAPMessage(String msgString) {

        if (msgString == null)
            return null;

        SOAPMessage message = null;
        try {

            MessageFactory factory = null;

            // Force the usage of specific MesasgeFactories
            if (msgString.indexOf(SOAP11_NAMESPACE) >= 0) {
                factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
            } else {
                factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
            }

            message = factory.createMessage();
            message.getSOAPPart().setContent((Source) new StreamSource(new StringReader(msgString)));
            message.saveChanges();
        } catch (SOAPException e) {
            System.out.println("toSOAPMessage Exception encountered: " + e);
            e.printStackTrace();
        }

        return message;
    }

}