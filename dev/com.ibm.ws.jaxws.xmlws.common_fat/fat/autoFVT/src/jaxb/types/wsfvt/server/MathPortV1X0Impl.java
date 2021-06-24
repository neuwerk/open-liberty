//
// @(#) 1.1 autoFVT/src/jaxb/types/wsfvt/server/MathPortV1X0Impl.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 2/10/07 12:19:36 [8/8/12 06:56:21]
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

package jaxb.types.wsfvt.server;

import java.math.BigInteger;

import java.util.GregorianCalendar;
import java.util.List;

import javax.jws.WebService;

import javax.xml.ws.Holder;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import net.wachovia.ts.wbxml.responseheader.v1x1.IntermediaryType;

import net.wachovia.ts.service.math.v1x0.MathPortV1X0;
import net.wachovia.ts.message.math.v1x0.MultiplyNumbersRequest;
import net.wachovia.ts.message.math.v1x0.MultiplyNumbersResponse;

import net.wachovia.ts.wbxml.requestheader.v1x1.WachoviaRequestHeader;

import net.wachovia.ts.wbxml.responseheader.v1x1.WachoviaResponseHeader;

/**
 * This is an implementation class for the MathPortV1X0 interface.
 */
@WebService(serviceName="MathService_V1X0",
	    portName="MathPort_V1X0",
            targetNamespace="http://v1x0.math.service.ts.wachovia.net/", 
	    endpointInterface = "net.wachovia.ts.service.math.v1x0.MathPortV1X0",
	    wsdlLocation="WEB-INF/wsdl/MathV1.0.wsdl")
public class MathPortV1X0Impl implements MathPortV1X0 {

    /**
     * This method will multiply the two input numbers and return the result.
     *
     * @param wachoviaRequestHeader The request header
     * @param multiplyNumbersRequest The numbers to multiply
     * @param hWachoviaResponseHeader The response header
     * @param hMultiplyNumbersResponse The result of the multiplication 
     */
    public void multiplyNumbers(WachoviaRequestHeader wachoviaRequestHeader,
                                MultiplyNumbersRequest multiplyNumbersRequest,
                                Holder<WachoviaResponseHeader> hWachoviaResponseHeader,
                                Holder<MultiplyNumbersResponse> hMultiplyNumbersResponse) {
        
        BigInteger firstNumber = multiplyNumbersRequest.getFirstNumber();
        BigInteger secondNumer = multiplyNumbersRequest.getSecondNumber();
        BigInteger result = firstNumber.multiply(secondNumer);
        
        net.wachovia.ts.message.math.v1x0.ObjectFactory objFactory3 =
            new net.wachovia.ts.message.math.v1x0.ObjectFactory();
        MultiplyNumbersResponse multiplyNumbersResponse =
            objFactory3.createMultiplyNumbersResponse();
        multiplyNumbersResponse.setResult(result);
        hMultiplyNumbersResponse.value = multiplyNumbersResponse;


        net.wachovia.ts.wbxml.responseheader.v1x1.ObjectFactory objFactory2 =
            new net.wachovia.ts.wbxml.responseheader.v1x1.ObjectFactory();
        WachoviaResponseHeader wachoviaResponseHeader = objFactory2.createWachoviaResponseHeader();

        XMLGregorianCalendar calendar = null;
        try {
            calendar =
                DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
        } catch (Exception e) {
            e.printStackTrace();
        }
        wachoviaResponseHeader.setSendingTimeStamp(calendar);
        wachoviaResponseHeader.setDestinationSystem("ws-rhel4-3.austin.ibm.com");
        wachoviaResponseHeader.setOriginationSystem("ws-rhel4-2.austin.ibm.com");
        wachoviaResponseHeader.setMessageID("ID-20061012-20061101");
        wachoviaResponseHeader.setCorrelationID("C-ID-20061012-20070229");
        List intList = wachoviaResponseHeader.getIntermediarySystem();
        for (int i = 0; i < 30; i++) {
            IntermediaryType intermediary = objFactory2.createIntermediaryType();
            intermediary.setSystem("ws-win-" + i + ".austin.ibm.com");
            intermediary.setTimeStamp(calendar);
            intList.add(intermediary);
        }
        hWachoviaResponseHeader.value = wachoviaResponseHeader;

    }

}
