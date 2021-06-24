//
// @(#) 1.3 autoFVT/src/jaxb/types/wsfvt/server/SwaTestImpl.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 8/5/07 20:25:02 [8/8/12 06:56:55]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// ----------------------------------------------------------------------------
// 11/02/06 ulbricht    450943          New File
// 08/05/07 ulbricht    454467          Change interface name
//

package swatest.server;

import javax.xml.ws.WebServiceException;
import javax.xml.soap.*;
import javax.activation.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.*;
import java.net.*;

import javax.jws.WebService;

@WebService(portName="SwaTestPort", serviceName="SwaTestService",
    targetNamespace="http://SwaTestService.org/wsdl",
    wsdlLocation="WEB-INF/wsdl/SwaTestService.wsdl",
    endpointInterface="swatest.server.SwaTestPortType")
public class SwaTestImpl implements SwaTestPortType {

    public void getMultipleAttachments(swatest.server.InputRequestGet request, javax.xml.ws.Holder<swatest.server.OutputResponse> response, javax.xml.ws.Holder<javax.activation.DataHandler> attach1, javax.xml.ws.Holder<javax.activation.DataHandler> attach2)  {
	try {
	    System.out.println("Enter getMultipleAttachments() ......");
	    OutputResponse theResponse = new OutputResponse();
	    theResponse.setMimeType1(request.getMimeType1());
	    theResponse.setMimeType2(request.getMimeType2());
	    theResponse.setResult("ok");
	    theResponse.setReason("ok");
	    response.value = theResponse;
	    DataHandler dh1 = new DataHandler(new URL(request.getUrl1()));
	    DataHandler dh2 = new DataHandler(new URL(request.getUrl2()));
	    attach1.value = dh1;
	    attach2.value = dh2;
	    System.out.println("Leave getMultipleAttachments() ......");
	} catch (Exception e) {
	    throw new WebServiceException(e.getMessage());
	}
    }

    public swatest.server.OutputResponseString putMultipleAttachments(swatest.server.InputRequestPut request, javax.activation.DataHandler attach1, javax.activation.DataHandler attach2)  {
	try {
	    OutputResponseString theResponse = new OutputResponseString();
 	    theResponse.setMyString("ok");
	    System.out.println("Enter putMultipleAttachments() ......");
	    if(attach1 == null) {
		System.err.println("attach1 is null (unexpected)");
		theResponse.setMyString("not ok");
	    }
	    if(attach2 == null) {
		System.err.println("attach2 is null (unexpected)");
		theResponse.setMyString("not ok");
	    }
	    System.out.println("Leave putMultipleAttachments() ......");
	    return theResponse;
	} catch (Exception e) {
	    throw new WebServiceException(e.getMessage());
	}
    }

    public swatest.server.OutputResponse echoMultipleAttachments(swatest.server.InputRequest request, javax.xml.ws.Holder<javax.activation.DataHandler> attach1, javax.xml.ws.Holder<javax.activation.DataHandler> attach2)  {
	try {
	    System.out.println("Enter echoMultipleAttachments() ......");
	    OutputResponse theResponse = new OutputResponse();
	    theResponse.setMimeType1(request.getMimeType1());
	    theResponse.setMimeType2(request.getMimeType2());
	    theResponse.setResult("ok");
	    theResponse.setReason("ok");
	    if(attach1 == null || attach1.value == null) {
		System.err.println("attach1.value is null (unexpected)");
		theResponse.setReason("attach1.value is null (unexpected)");
		theResponse.setResult("not ok");
	    }
	    if(attach2 == null || attach2.value == null) {
		System.err.println("attach2.value is null (unexpected)");
		if(theResponse.getReason().equals("ok"))
		    theResponse.setReason("attach2.value is null (unexpected)");
		else
		    theResponse.setReason(theResponse.getReason() + 
			"\nattach2.value is null (unexpected)");
		theResponse.setResult("not ok");
	    }
	    System.out.println("Leave echoMultipleAttachments() ......");
	    return theResponse;
	} catch (Exception e) {
	    throw new WebServiceException(e.getMessage());
	}
    }

    public swatest.server.OutputResponse echoAttachmentsAndThrowAFault(swatest.server.InputRequestThrowAFault request, javax.xml.ws.Holder<javax.activation.DataHandler> attach1, javax.xml.ws.Holder<javax.activation.DataHandler> attach2)  throws swatest.server.MyFault {
	System.out.println("Enter echoAttachmentsAndThrowAFault() ......");
	System.out.println("Throwing back a fault [MyFault] ......");
	throw new MyFault("This is my fault", new MyFaultType());
    }

    public swatest.server.OutputResponse echoAttachmentsWithHeader(swatest.server.InputRequestWithHeader request, swatest.server.MyHeader header, javax.xml.ws.Holder<javax.activation.DataHandler> attach1, javax.xml.ws.Holder<javax.activation.DataHandler> attach2) throws swatest.server.MyFault {
	System.out.println("Enter echoAttachmentsWithHeader() ......");
	if(header.getMessage().equals("do throw a fault")) {
	    System.out.println("Throwing back a fault [MyFault] ......");
	    throw new MyFault("This is my fault", new MyFaultType());
	}
	try {
	    OutputResponse theResponse = new OutputResponse();
	    theResponse.setMimeType1(request.getMimeType1());
	    theResponse.setMimeType2(request.getMimeType2());
	    theResponse.setResult("ok");
	    theResponse.setReason("ok");
	    System.out.println("Leave echoAttachmentsWithHeader() ......");
	    return theResponse;
	} catch (Exception e) {
	    throw new WebServiceException(e.getMessage());
	}
    }

    public swatest.server.OutputResponseSwaRef echoMultipleAttachmentsSwaRef(swatest.server.InputRequestSwaRef request) {
	System.out.println("Enter echoMultiplAttachmentsSwaRef() ......");
	try {
	    String response = "ok";
	    if(request.getAttachRef1() == null) {
                System.err.println("swaRef attach1 is null (unexpected)");
                response = "not ok";
            }
            if(request.getAttachRef2() == null) {
                System.err.println("swaRef attach2 is null (unexpected)");
                response = "not ok";
            }
	    OutputResponseSwaRef theResponse = new OutputResponseSwaRef();
	    theResponse.setMimeType1(request.getMimeType1());
	    theResponse.setMimeType2(request.getMimeType2());
	    theResponse.setAttachRef1(request.getAttachRef1());
            theResponse.setAttachRef2(request.getAttachRef2());
	    theResponse.setResult(response);
	    theResponse.setReason(response);
	    System.out.println("Leave echoMultiplAttachmentsSwaRef() ......");
	    return theResponse;
	} catch (Exception e) {
	    throw new WebServiceException(e.getMessage());
	}
    }

}
