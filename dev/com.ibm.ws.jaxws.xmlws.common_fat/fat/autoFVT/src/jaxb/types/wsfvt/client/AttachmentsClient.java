//
// @(#) 1.3 autoFVT/src/jaxb/types/wsfvt/client/AttachmentsClient.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/14/09 12:44:27 [8/8/12 06:56:55]
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

package jaxb.types.wsfvt.client;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.rmi.*;
import java.util.*;
import javax.xml.ws.*;
import javax.xml.soap.*;
import javax.activation.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.*;
import javax.xml.namespace.QName;

import swatest.client.SwaTestPortType;
import swatest.client.SwaTestService;
import swatest.client.InputRequest;
import swatest.client.InputRequestGet;
import swatest.client.InputRequestPut;
import swatest.client.InputRequestString;
import swatest.client.InputRequestSwaRef;
import swatest.client.InputRequestThrowAFault;
import swatest.client.InputRequestWithHeader;
import swatest.client.MyFault;
import swatest.client.MyFaultType;
import swatest.client.MyHeader;
import swatest.client.ObjectFactory;
import swatest.client.OutputResponse;
import swatest.client.OutputResponseString;
import swatest.client.OutputResponseSwaRef;

public class AttachmentsClient {

    private static String protocol = "http";
    private static String hostname = "@REPLACE_WITH_HOST_NAME@";
    private static int portnum = @REPLACE_WITH_PORT_NUM@;
    private static String ctxroot = "/wijaxbtypes2";
    private String endpointUrl = "/SwaTestService";
    private String wsdlUrl = "/SwaTestService?WSDL";
    private static SwaTestPortType port = null;
    private static URL url = null;
    private static int idx=0, numoftests=70;
    private static boolean tests[] = new boolean[numoftests];

    public AttachmentsClient() throws Exception {
        SwaTestService service = new SwaTestService();
        port = service.getSwaTestPort();
        System.out.println("WebService Port = "+port);
        BindingProvider bindingprovider = (BindingProvider)port;
        java.util.Map<String,Object> context = bindingprovider.getRequestContext();
        String targetEndpointAddr = (String)context.get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
        System.out.println("WebService TargetEndpointAddress = "+targetEndpointAddr);
        url = new URL(targetEndpointAddr); 
        hostname = url.getHost();
        portnum = url.getPort();
        protocol = url.getProtocol();
        String path = url.getPath();
        ctxroot = path.substring(0, path.indexOf("/", 2));
        System.out.println("WebService Ctxroot = "+ctxroot);
    }

    public static void main(String[] args ) {
	try {
            AttachmentsClient client = new AttachmentsClient();
            System.out.println("------------------------------------------");
            client.GetMultipleAttachmentsTest();
            System.out.println("------------------------------------------");
            client.PutMultipleAttachmentsTest();
            System.out.println("------------------------------------------");
            client.EchoMultipleAttachmentsTest();
            System.out.println("------------------------------------------");
            client.EchoAttachmentsAndThrowAFaultTest();
            System.out.println("------------------------------------------");
            client.EchoAttachmentsWithHeaderTest();
            System.out.println("------------------------------------------");
            client.EchoAttachmentsWithHeaderAndThrowAFaultTest();
            System.out.println("------------------------------------------");
            client.EchoMultipleAttachmentsSwaRefTest();
            System.out.println("*********************************************");
            System.out.println("* 	        Test Summary Results	        *");
            System.out.println("*********************************************");
            if(tests[0])
                System.out.println("GetMultipleAttachmentsTest  .......  PASSED");
            else
                System.out.println("GetMultipleAttachmentsTest  .......  FAILED");
            if(tests[1])
                System.out.println("PutMultipleAttachmentsTest  .......  PASSED");
            else
                System.out.println("PutMultipleAttachmentsTest  .......  FAILED");
            if(tests[2])
                System.out.println("EchoMultipleAttachmentsTest  .......  PASSED");
            else
                System.out.println("EchoMultipleAttachmentsTest  .......  FAILED");
            if(tests[3])
                System.out.println("EchoAttachmentsAndThrowAFault  .......  PASSED");
            else
                System.out.println("EchoAttachmentsAndThrowAFault  .......  FAILED");
            if(tests[4])
                System.out.println("EchoAttachmentsWithHeaderTest  .......  PASSED");
            else
                System.out.println("EchoAttachmentsWithHeaderTest  .......  FAILED");
            if(tests[5])
                System.out.println("EchoAttachmentsWithHeaderAndThrowAFaultTest  .......  PASSED");
            else
                System.out.println("EchoAttachmentsWithHeaderAndThrowAFaultTest  .......  FAILED");
            if(tests[6])
                System.out.println("EchoMultipleAttachmentsSwaRefTest  .......  PASSED");
            else
                System.out.println("EchoMultipleAttachmentsSwaRefTest  .......  FAILED");
	} catch(Exception e) {
	    System.err.println("Exception occured: "+e.getMessage());
	    e.printStackTrace(System.err);
	    System.exit(1);
	}
    }

    public boolean GetMultipleAttachmentsTest() {
	System.out.println("GetMultipleAttachmentsTest");
	boolean pass = true;

	try {
	    InputRequestGet request = new InputRequestGet();
	    URL url1 = new URL(protocol, hostname, portnum, ctxroot + "/attach.text");
	    URL url2 = new URL(protocol, hostname, portnum, ctxroot + "/attach.html");
	    System.out.println("url1="+url1);
	    System.out.println("url2="+url2);
	    request.setMimeType1("text/plain");
	    request.setMimeType2("text/html");
	    request.setUrl1(url1.toString());
	    request.setUrl2(url2.toString());
	    System.out.println("Get 2 attachments (text/plain) and (text/html)");
	    Holder<javax.activation.DataHandler> attach1 = new Holder<javax.activation.DataHandler>();
	    Holder<javax.activation.DataHandler> attach2 = new Holder<javax.activation.DataHandler>();
	    Holder<OutputResponse> response = new Holder<OutputResponse>();
	    port.getMultipleAttachments(request, response, attach1, attach2);
	    if(!ValidateRequestResponseAttachmentsGetTestCase(
				request, response.value, attach1, attach2))
		pass = false;
	} catch(Exception e) {
	    System.err.println("Caught exception: " + e.getMessage());
            e.printStackTrace(System.err);
	    pass = false;
 	}
	tests[idx++]=pass;

        return pass;
    }

    public boolean PutMultipleAttachmentsTest() {
	System.out.println("PutMultipleAttachmentsTest");
	boolean pass = true;

	try {
	    InputRequestPut request = new InputRequestPut();
	    URL url1 = new URL(protocol, hostname, portnum, ctxroot + "/attach.text");
	    URL url2 = new URL(protocol, hostname, portnum, ctxroot + "/attach.html");
	    System.out.println("url1="+url1);
	    System.out.println("url2="+url2);
	    request.setMimeType1("text/plain");
	    request.setMimeType2("text/html");
	    request.setHeader("notused");
	    DataHandler attach1 = new DataHandler(url1);
	    DataHandler attach2 = new DataHandler(url2);
	    System.out.println("Put 2 attachments (text/plain) and (text/html)");
	    OutputResponseString response = port.putMultipleAttachments(request, attach1, attach2);
	    if(!response.getMyString().equals("ok")) {
		System.err.println("Return status is " + 
				response.getMyString() + ", expected ok");
		pass = false;
	    }
	} catch(Exception e) {
	    System.err.println("Caught exception: " + e.getMessage());
            e.printStackTrace(System.err);
            pass = false;
 	}
	tests[idx++]=pass;

        return pass;
    }

    public boolean EchoMultipleAttachmentsTest() {
	System.out.println("EchoMultipleAttachmentsTest");
	boolean pass = true;

	try {
	    InputRequest request = new InputRequest();
	    URL url1 = new URL(protocol, hostname, portnum, ctxroot + "/attach.text");
	    URL url2 = new URL(protocol, hostname, portnum, ctxroot + "/attach.html");
	    System.out.println("url1="+url1);
	    System.out.println("url2="+url2);
	    request.setMimeType1("text/plain");
	    request.setMimeType2("text/html");
	    DataHandler dh1 = new DataHandler(url1);
	    DataHandler dh2 = new DataHandler(url2);
	    Holder<javax.activation.DataHandler> attach1 = new Holder<javax.activation.DataHandler>();
	    Holder<javax.activation.DataHandler> attach2 = new Holder<javax.activation.DataHandler>();
	    attach1.value = dh1;
	    attach2.value = dh2;
	    System.out.println("Echo 2 attachments (text/plain) and (text/html)");
	    OutputResponse response = port.echoMultipleAttachments(request, attach1, attach2);
	    if(!ValidateRequestResponseAttachmentsEchoTestCase(
				request, response, attach1, attach2))
		pass = false;
	} catch(Exception e) {
	    System.err.println("Caught exception: " + e.getMessage());
            e.printStackTrace(System.err);
            pass = false;
 	}
	tests[idx++]=pass;

        return pass;
    }

    public boolean EchoAttachmentsAndThrowAFaultTest() {
	System.out.println("EchoAttachmentsAndThrowAFaultTest");
	boolean pass = true;

	try {
	    InputRequestThrowAFault request = new InputRequestThrowAFault();
	    URL url1 = new URL(protocol, hostname, portnum, ctxroot + "/attach.text");
	    URL url2 = new URL(protocol, hostname, portnum, ctxroot + "/attach.html");
	    System.out.println("url1="+url1);
	    System.out.println("url2="+url2);
	    request.setMimeType1("text/plain");
	    request.setMimeType2("text/html");
	    DataHandler dh1 = new DataHandler(url1);
	    DataHandler dh2 = new DataHandler(url2);
	    Holder<javax.activation.DataHandler> attach1 = new Holder<javax.activation.DataHandler>();
	    Holder<javax.activation.DataHandler> attach2 = new Holder<javax.activation.DataHandler>();
	    attach1.value = dh1;
	    attach2.value = dh2;
	    System.out.println("Echo attachments and throw a fault");
	    OutputResponse response = port.echoAttachmentsAndThrowAFault(request, attach1, attach2);
	    pass = false;
	} catch(MyFault e) {
	    System.out.println("Caught expected MyFault exception: " + e.getMessage());
	} catch(Exception e) {
	    System.err.println("Caught exception: " + e.getMessage());
            e.printStackTrace(System.err);
            pass = false;
 	}
	tests[idx++]=pass;

        return pass;
    }

    public boolean EchoAttachmentsWithHeaderTest() {
	System.out.println("EchoAttachmentsWithHeaderTest");
	boolean pass = true;

	try {
	    InputRequestWithHeader request = new InputRequestWithHeader();
	    URL url1 = new URL(protocol, hostname, portnum, ctxroot + "/attach.text");
	    URL url2 = new URL(protocol, hostname, portnum, ctxroot + "/attach.html");
	    System.out.println("url1="+url1);
	    System.out.println("url2="+url2);
	    request.setMimeType1("text/plain");
	    request.setMimeType2("text/html");
	    DataHandler dh1 = new DataHandler(url1);
	    DataHandler dh2 = new DataHandler(url2);
	    Holder<javax.activation.DataHandler> attach1 = new Holder<javax.activation.DataHandler>();
	    Holder<javax.activation.DataHandler> attach2 = new Holder<javax.activation.DataHandler>();
	    attach1.value = dh1;
	    attach2.value = dh2;
	    MyHeader header = new MyHeader();
	    header.setMessage("do not throw my fault");
	    System.out.println("Echo attachments with a header");
	    OutputResponse response = port.echoAttachmentsWithHeader(
				request, header, attach1, attach2);
	    if(!ValidateRequestResponseAttachmentsEchoWithHeaderTestCase(
				request, response, attach1, attach2))
		pass = false;
	} catch(Exception e) {
	    System.err.println("Caught exception: " + e.getMessage());
            e.printStackTrace(System.err);
            pass = false;
 	}
	tests[idx++]=pass;

        return pass;
    }

    public boolean EchoAttachmentsWithHeaderAndThrowAFaultTest() {
	System.out.println("EchoAttachmentsWithHeaderAndThrowAFaultTest");
	boolean pass = true;

	try {
	    InputRequestWithHeader request = new InputRequestWithHeader();
	    URL url1 = new URL(protocol, hostname, portnum, ctxroot + "/attach.text");
	    URL url2 = new URL(protocol, hostname, portnum, ctxroot + "/attach.html");
	    System.out.println("url1="+url1);
	    System.out.println("url2="+url2);
	    request.setMimeType1("text/plain");
	    request.setMimeType2("text/html");
	    DataHandler dh1 = new DataHandler(url1);
	    DataHandler dh2 = new DataHandler(url2);
	    Holder<javax.activation.DataHandler> attach1 = new Holder<javax.activation.DataHandler>();
	    Holder<javax.activation.DataHandler> attach2 = new Holder<javax.activation.DataHandler>();
	    attach1.value = dh1;
	    attach2.value = dh2;
	    MyHeader header = new MyHeader();
	    header.setMessage("do throw a fault");
	    System.out.println("Echo attachments with a header and throw a fault");
	    OutputResponse response = port.echoAttachmentsWithHeader(request, header, attach1, attach2);
	    pass = false;
	} catch(MyFault e) {
	    System.out.println("Caught expected MyFault exception: " + e.getMessage());
	} catch(Exception e) {
	    System.err.println("Caught exception: " + e.getMessage());
            e.printStackTrace(System.err);
            pass = false;
 	}
	tests[idx++]=pass;

        return pass;
    }

    public boolean EchoMultipleAttachmentsSwaRefTest() {
	System.out.println("EchoMultipleAttachmentsSwaRefTest");
	boolean pass = true;

	try {
	    InputRequestSwaRef request = new InputRequestSwaRef();
	    URL url1 = new URL(protocol, hostname, portnum, ctxroot + "/attach.text");
	    URL url2 = new URL(protocol, hostname, portnum, ctxroot + "/attach.html");
	    System.out.println("url1="+url1);
	    System.out.println("url2="+url2);
	    request.setMimeType1("text/plain");
	    request.setMimeType2("text/html");
	    DataHandler attach1 = new DataHandler(url1);
            DataHandler attach2 = new DataHandler(url2);
            request.setAttachRef1(attach1);
            request.setAttachRef2(attach2);
	    System.out.println("Echo 2 attachments (text/plain) and (text/html) using SwaRef");
	    OutputResponseSwaRef response = port.echoMultipleAttachmentsSwaRef(request);
	    if(!ValidateRequestResponseAttachmentsEchoSwaRefTestCase(request, response))
		pass = false;
	} catch(Exception e) {
	    System.err.println("Caught exception: " + e.getMessage());
            e.printStackTrace(System.err);
            pass = false;
 	}
	tests[idx++]=pass;

        return pass;
    }

    /*******************************************************************************
     * Validate request, response and attachments (getMultipleAttachments)
     ******************************************************************************/
    private boolean ValidateRequestResponseAttachmentsGetTestCase(InputRequestGet request, 
	OutputResponse response, Holder<javax.activation.DataHandler> attach1, Holder<javax.activation.DataHandler> attach2)
    {
        boolean result=true;
	System.out.println("Validating the request, the response, and the attachments");
	System.out.println("Check if the mime types are correct");
	if(!response.getMimeType1().equals(request.getMimeType1())) {
	    System.err.println("MimeType1 is not equal in request and response");
	    System.err.println("Request MimeType1 = " + request.getMimeType1());
	    System.err.println("Response MimeType1 = " + response.getMimeType1());
	    result=false;
	}
	if(!response.getMimeType2().equals(request.getMimeType2())) {
	    System.err.println("MimeType2 is not equal in request and response");
	    System.err.println("Request MimeType2 = " + request.getMimeType2());
	    System.err.println("Response MimeType2 = " + response.getMimeType2());
	    result=false;
	} else {
	    System.out.println("The mime types are correct");
        }
	System.out.println("Check if the response result is correct");
	if(!response.getResult().equals("ok")) {
	    System.err.println("Return status is " + response + ", expected ok");
	    System.err.println("Return Reason is: " + response.getReason()); 
	    result=false;
	} else {
	    System.out.println("The response result is correct");
        }
	try {
	    System.out.println("Check if the attachment contents are correct");
	    DataHandler dh1 = new DataHandler(new URL(request.getUrl1()));
	    DataHandler dh2 = new DataHandler(new URL(request.getUrl2()));
	    byte data1[] = new byte[4096];
	    byte data2[] = new byte[4096];
	    int count1 = dh1.getInputStream().read(data1, 0, 4096);
	    int count2 = attach1.value.getInputStream().read(data2, 0, 4096);
	    if(!ValidateAttachmentData(count1, data1, count2, data2, "Attachment1"))
		result=false;
	    count1 = dh2.getInputStream().read(data1, 0, 4096);
	    data2 = new byte[4096];
	    count2 = attach2.value.getInputStream().read(data2, 0, 4096);
	    if(!ValidateAttachmentData(count1, data1, count2, data2, "Attachment2"))
	        result=false;
	    System.out.println("The attachment contents are equal");
	} catch(Exception e) {
         result=false;
	    System.err.println("Caught unexpected exception: " + e.getMessage());
	    e.printStackTrace(System.err);
	}
	return result;
    }

    /*******************************************************************************
     * Validate request, response and attachments (echoMultipleAttachments)
     ******************************************************************************/
    private boolean ValidateRequestResponseAttachmentsEchoTestCase(InputRequest request, 
	OutputResponse response, Holder<javax.activation.DataHandler> attach1, Holder<javax.activation.DataHandler> attach2)
    {
        boolean result=true;
	System.out.println("Validating the request, the response, and the attachments");
	System.out.println("Check if the mime types are correct");
	if(!response.getMimeType1().equals(request.getMimeType1())) {
	    System.err.println("MimeType1 is not equal in request and response");
	    System.err.println("Request MimeType1 = " + request.getMimeType1());
	    System.err.println("Response MimeType1 = " + response.getMimeType1());
	    result=false;
	}
	if(!response.getMimeType2().equals(request.getMimeType2())) {
	    System.err.println("MimeType2 is not equal in request and response");
	    System.err.println("Request MimeType2 = " + request.getMimeType2());
	    System.err.println("Response MimeType2 = " + response.getMimeType2());
	    result=false;
	} else {
	    System.out.println("The mime types are correct");
     }
	System.out.println("Check if the response result is correct");
	if(!response.getResult().equals("ok")) {
	    System.err.println("Return status is " + response + ", expected ok");
	    System.err.println("Return Reason is: " + response.getReason()); 
	    result=false;
	} else {
	    System.out.println("The response result is correct");
        }
	try {
	    System.out.println("Check if the attachment contents are correct");
	    URL url1 = new URL(protocol, hostname, portnum, ctxroot + "/attach.text");
	    URL url2 = new URL(protocol, hostname, portnum, ctxroot + "/attach.html");
	    DataHandler dh1 = new DataHandler(url1);
	    byte data1[] = new byte[4096];
	    byte data2[] = new byte[4096];
	    int count1 = dh1.getInputStream().read(data1, 0, 4096);
	    int count2 = attach1.value.getInputStream().read(data2, 0, 4096);
	    if(!ValidateAttachmentData(count1, data1, count2, data2, "Attachment1"))
		result=false;

	    dh1 = new DataHandler(url2);
	    count1 = dh1.getInputStream().read(data1, 0, 4096);
	    count2 = attach2.value.getInputStream().read(data2, 0, 4096);
	    if(!ValidateAttachmentData(count1, data1, count2, data2, "Attachment2"))
		result=false;
	} catch(Exception e) {
	    result=false;
	    System.err.println("Caught unexpected exception: " + e.getMessage());
	    e.printStackTrace(System.err);
	}
	return result;
    }

    /*******************************************************************************
     * Validate request, response and attachments (echoAttachmentsWithHeader)
     ******************************************************************************/
    private boolean ValidateRequestResponseAttachmentsEchoWithHeaderTestCase(InputRequestWithHeader
	request, OutputResponse response, Holder<javax.activation.DataHandler> attach1, Holder<javax.activation.DataHandler> attach2)
    {
        boolean result=true;
	System.out.println("Validating the request, the response, and the attachments");
	System.out.println("Check if the mime types are correct");
	if(!response.getMimeType1().equals(request.getMimeType1())) {
	    System.err.println("MimeType1 is not equal in request and response");
	    System.err.println("Request MimeType1 = " + request.getMimeType1());
	    System.err.println("Response MimeType1 = " + response.getMimeType1());
	    result=false;
	}
	if(!response.getMimeType2().equals(request.getMimeType2())) {
	    System.err.println("MimeType2 is not equal in request and response");
	    System.err.println("Request MimeType2 = " + request.getMimeType2());
	    System.err.println("Response MimeType2 = " + response.getMimeType2());
	    result=false;
	} else {
	    System.out.println("The mime types are correct");
        }
	System.out.println("Check if the response result is correct");
	if(!response.getResult().equals("ok")) {
	    System.err.println("Return status is " + response + ", expected ok");
	    System.err.println("Return Reason is: " + response.getReason()); 
	    result=false;
	} else {
	    System.out.println("The response result is correct");
        }
	try {
	    System.out.println("Check if the attachment contents are correct");
	    URL url1 = new URL(protocol, hostname, portnum, ctxroot + "/attach.text");
	    URL url2 = new URL(protocol, hostname, portnum, ctxroot + "/attach.html");
	    DataHandler dh1 = new DataHandler(url1);
	    DataHandler dh2 = new DataHandler(url2);
	    byte data1[] = new byte[4096];
	    byte data2[] = new byte[4096];
	    int count1 = dh1.getInputStream().read(data1, 0, 4096);
	    int count2 = attach1.value.getInputStream().read(data2, 0, 4096);
	    if(!ValidateAttachmentData(count1, data1, count2, data2, "Attachment1"))
		result=false;
	    count1 = dh2.getInputStream().read(data1, 0, 4096);
	    data2 = new byte[4096];
	    count2 = attach2.value.getInputStream().read(data2, 0, 4096);
	    if(!ValidateAttachmentData(count1, data1, count2, data2, "Attachment2"))
	        result=false;
	    System.out.println("The attachment contents are equal");
	} catch(Exception e) {
            result=false;
	    System.err.println("Caught unexpected exception: " + e.getMessage());
	    e.printStackTrace(System.err);
	}
	return result;
    }

    /*******************************************************************************
     * Validate request, response and attachments (echoMultipleAttachmentsSwaRef)
     ******************************************************************************/
    private boolean ValidateRequestResponseAttachmentsEchoSwaRefTestCase(InputRequestSwaRef request, 
	OutputResponseSwaRef response)
    {
	boolean result=true;
	System.out.println("Validating the request, the response, and the attachments");
	System.out.println("Check if the mime types are correct");
	if(!response.getMimeType1().equals(request.getMimeType1())) {
	    System.err.println("MimeType1 is not equal in request and response");
	    System.err.println("Request MimeType1 = " + request.getMimeType1());
	    System.err.println("Response MimeType1 = " + response.getMimeType1());
	    result=false;
	}
	if(!response.getMimeType2().equals(request.getMimeType2())) {
	    System.err.println("MimeType2 is not equal in request and response");
	    System.err.println("Request MimeType2 = " + request.getMimeType2());
	    System.err.println("Response MimeType2 = " + response.getMimeType2());
	    result=false;
	} else {
	    System.out.println("The mime types are correct");
	}
	System.out.println("Check if the response result is correct");
	if(!response.getResult().equals("ok")) {
	    System.err.println("Return status is " + response + ", expected ok");
	    System.err.println("Return Reason is: " + response.getReason()); 
	    result=false;
	} else {
	    System.out.println("The response result is correct");
	}
	if(response.getAttachRef1() == null) {
	    System.err.println("AttachRef1 is null (unexpected)");
	    result=false;
	} 
	if(response.getAttachRef2() == null) {
	    System.err.println("AttachRef2 is null (unexpected)");
	    result=false;
	} 
	if(response.getAttachRef1() == null || response.getAttachRef2() != null)
	    return result;
	try {
	    System.out.println("Check if the attachment contents are correct");
	    DataHandler dh1 = request.getAttachRef1();
	    DataHandler dh2 = request.getAttachRef2();
	    byte data1[] = new byte[4096];
	    byte data2[] = new byte[4096];
	    int count1 = dh1.getInputStream().read(data1, 0, 4096);
	    int count2 = response.getAttachRef1().getInputStream().read(data2, 0, 4096);
	    if(!ValidateAttachmentData(count1, data1, count2, data2, "SwaRefAttachment1"))
		result=false;
	    count1 = dh2.getInputStream().read(data1, 0, 4096);
	    data2 = new byte[4096];
	    count2 = response.getAttachRef2().getInputStream().read(data2, 0, 4096);
	    if(!ValidateAttachmentData(count1, data1, count2, data2, "SwaRefAttachment2"))
	        result=false;
	    if(result) System.out.println("The attachment contents are equal");
	} catch(Exception e) {
	    result=false;
	    System.err.println("Caught unexpected exception: " + e.getMessage());
	    e.printStackTrace(System.err);
	}
	return result;
    }

    private boolean ValidateAttachmentData(
		int count1, byte[] data1, int count2, byte[] data2, String attach) 
    {
	int max=0;
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	PrintStream ps = new PrintStream(baos);
	if(count2 > count1) {
	    System.out.println("Data counts are different so check for and remove any trailing CR's");
	    System.out.println("Data count1="+count1+", Data count2="+count2);
	    for(int i=count1; i<count2; i++) {
		if((char)data2[i] != '\r') break;
	    }
	    System.out.println("Removed "+(count2-count1)+" trailing CR's from data2");
	    count2 = count1;
	}
	if(count1 != count2) {
	    System.err.println(attach+" data count is not equal in request and response");
	    System.err.println("Request data count = " + count1);
	    System.err.println("Response data count = " + count2);
	    if(count2 > count1) max = count1; else max = count2;
	    ps.printf("data1[%d]=0x%x  data2[%d]=0x%x", 
	    	max-1, data1[max-1], max-1, data2[max-1]);
	    System.err.println(baos.toString());
	    baos.reset();
	    if(count2 > count1) {
	        for(int i=count1; i<count2; i++) {
	            ps.printf("Extra data was: data2[%d]=0x%x|0%o", i, data2[i], data2[i]);
	            System.err.println(baos.toString());
		    baos.reset();
		}
	    } else {
 	        for(int i=count2; i<count1; i++) {
	            ps.printf("Extra data was: data1[%d]=0x%x|0%o", i, data1[i], data1[i]);
	            System.err.println(baos.toString());
		    baos.reset();
		}
	    }
	    return false;
	}
	for(int i=0; i<count1; i++) {
	    if(data1[i] != data2[i]) {
	        System.err.println(attach+" data content is not equal in attachment");
		System.err.println("Failed at byte "+i+", data1["+i+"]="+data1[i]+
			", data2["+i+"]="+data2[i]);
	        return false;
	    }
	}
	System.out.println(attach+" data count ["+count1+"] and content is equal in attachment");
	return true;
    }

}
