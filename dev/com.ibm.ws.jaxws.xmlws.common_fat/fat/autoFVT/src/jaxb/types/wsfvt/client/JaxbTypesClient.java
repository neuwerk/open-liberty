//
// @(#) 1.1 autoFVT/src/jaxb/types/wsfvt/client/JaxbTypesClient.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 2/10/07 12:19:34 [8/8/12 06:56:21]
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
// 11/02/06 ulbricht    420151          New File
//

package jaxb.types.wsfvt.client;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import java.math.BigInteger;

import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.ws.Holder;

import javax.xml.namespace.QName;

import net.wachovia.ts.message.math.v1x0.MultiplyNumbersRequest;
import net.wachovia.ts.message.math.v1x0.MultiplyNumbersResponse;

import net.wachovia.ts.wbxml.requestheader.v1x1.WachoviaRequestHeader;
import net.wachovia.ts.wbxml.requestheader.v1x1.IntermediaryType;
import net.wachovia.ts.wbxml.requestheader.v1x1.AuditDataType;

import net.wachovia.ts.wbxml.responseheader.v1x1.WachoviaResponseHeader;

import net.wachovia.ts.service.math.v1x0.MathServiceV1X0;
import net.wachovia.ts.service.math.v1x0.MathPortV1X0;


/**
 * This class will acts as a client to reach a JAX-WS based
 * server side implementation.
 */
public class JaxbTypesClient {

    /** 
     * A no argument constructor to create a JaxbTypesClient.
     */
    public JaxbTypesClient() {
    }

    /** 
     * The main method.
     *
     * @param args The command line arguments
     */
    public static void main(String[] args) throws Exception {
        JaxbTypesClient client = new JaxbTypesClient();
        String test = null;
        if (args != null && args.length == 1) {
            test = args[0];
        }
        String value = client.callService(test);
        System.out.println("Response: " + value);
    }

    /** 
     * This method will call the service using a JAX-WS
     * proxy.
     *
     * @param num The scenario to run
     * @return The String returned from the remote method
     * @throws Exception Any kind of exception
     */
    public String callService(String type) throws Exception {
        MathServiceV1X0 service = new MathServiceV1X0();
        MathPortV1X0 port = service.getMathPortV1X0();

        // REQUEST HEADER
        net.wachovia.ts.wbxml.requestheader.v1x1.ObjectFactory objFactory1 =
            new net.wachovia.ts.wbxml.requestheader.v1x1.ObjectFactory();
        WachoviaRequestHeader wachoviaRequestHeader = objFactory1.createWachoviaRequestHeader();
        XMLGregorianCalendar calendar =
            DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
        wachoviaRequestHeader.setSendingTimeStamp(calendar);
        wachoviaRequestHeader.setDestinationSystem("ws-rhel4-3.austin.ibm.com");
        wachoviaRequestHeader.setOriginationSystem("ws-rhel4-2.austin.ibm.com");
        wachoviaRequestHeader.setMessageID("ID-20061012-20061101");
        wachoviaRequestHeader.setDestinationAction("multiply");
        List intList = wachoviaRequestHeader.getIntermediarySystem();
        for (int i = 0; i < 30; i++) {
            IntermediaryType intermediary = objFactory1.createIntermediaryType();
            intermediary.setSystem("ws-win-" + i + ".austin.ibm.com");
            intermediary.setTimeStamp(calendar);
            intList.add(intermediary);
        }
        AuditDataType audit = objFactory1.createAuditDataType();
        audit.setClientInteractionID("ID-20070210-2008-2007-1");
        audit.setTransactionId("ID-20070210-2008-2007-2");
        audit.setOtherAuditData("AUDIT-DATA:2309232374");
        wachoviaRequestHeader.setAuditData(audit);
        // REQUEST HEADER
        

        // RESPONSE HEADER
        net.wachovia.ts.wbxml.responseheader.v1x1.ObjectFactory objFactory2 =
            new net.wachovia.ts.wbxml.responseheader.v1x1.ObjectFactory();
        WachoviaResponseHeader wachoviaResponseHeader = objFactory2.createWachoviaResponseHeader();
        Holder hWachoviaResponseHeader = new Holder(wachoviaResponseHeader);
        // RESPONSE HEADER
        

        // REQUEST
        net.wachovia.ts.message.math.v1x0.ObjectFactory objFactory3 =
            new net.wachovia.ts.message.math.v1x0.ObjectFactory();
        MultiplyNumbersRequest multiplyNumbersRequest =
            objFactory3.createMultiplyNumbersRequest();
        BigInteger num = new BigInteger("5");
        multiplyNumbersRequest.setFirstNumber(num);
        num = new BigInteger("6");
        multiplyNumbersRequest.setSecondNumber(num);
        // REQUEST


        // RESPONSE
        MultiplyNumbersResponse multiplyNumbersResponse =
            objFactory3.createMultiplyNumbersResponse();
        Holder hMultiplyNumbersResponse = new Holder(multiplyNumbersResponse);
        // RESPONSE

        
        port.multiplyNumbers(wachoviaRequestHeader,
                             multiplyNumbersRequest,
                             hWachoviaResponseHeader,
                             hMultiplyNumbersResponse);

        multiplyNumbersResponse = (MultiplyNumbersResponse)hMultiplyNumbersResponse.value;
        BigInteger result = multiplyNumbersResponse.getResult();

        return result.toString();
    }

}
