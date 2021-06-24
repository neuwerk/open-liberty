//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date        UserId         Defect            Description
// ----------------------------------------------------------------------------
// 09/18/2006  mzheng         LIDB3296-46.02    New File
// 03/22/2007  mzheng         428129            Not sent mustUnderstand="true"
//

package jaxws.soapfaults.wsfvt.server;

import java.util.Locale;

import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;

import javax.xml.ws.BindingType;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.soap.SOAPFaultException;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.WebServiceException;

import javax.jws.WebService;

@WebService (targetNamespace="http://soapfaults.jaxws",
             portName="SOAP12FaultsPort",
             serviceName="SOAP12FaultsService",
             wsdlLocation="WEB-INF/wsdl/SOAPFaults.wsdl",
             endpointInterface="jaxws.soapfaults.wsfvt.server.SOAP12FaultsPortType")

@BindingType (value=javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING)

public class SOAP12FaultsImpl implements SOAP12FaultsPortType {

    private final static String SENDER = "Sender";

    private final static String RECEIVER = "Receiver";

    private String namespace = null;

    private String prefix = null;

    public DoFaultTestResponseType doFaultTest(DoFaultTestType doFaultTest,
                                               CodeHeaderTypeSOAP12 codeHeader,
                                         NamespaceHeaderType namespaceHeader) 
        throws ProcessFault, ValidateFault {

        if (namespaceHeader != null) {
            namespace = namespaceHeader.getURI();
            prefix = namespaceHeader.getPrefix();
        }

        if (namespace == null || prefix == null) {
            // Client should be able to catch SOAPFaultException 
            throw createSOAPFaultException(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE, SOAPConstants.SOAP_ENV_PREFIX, RECEIVER, "Null namespace header field", null);
        }

        if (namespace.equals("null") || prefix.equals("null")) {
            // This will cause runtime SOAPException
            // Client should be able to catch SOAPFaultException 
            throw createSOAPFaultException(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE, SOAPConstants.SOAP_ENV_PREFIX, SENDER, "Unrecognized namespace header field", null);
        }

        if (doFaultTest.getReqString() == null) {
            ValidateFaultType faultInfo = new ValidateFaultType();
            faultInfo.setMsgID(400);
            faultInfo.setMsg("Client did not send request string");
            faultInfo.setField("doFaultTest");

            // Client should be able to catch ValidateFault
            throw createSOAPFaultException(namespace, prefix, SENDER, "Null parameter", faultInfo);
        }

        if (doFaultTest.getReqString().equals("null")) {
            ValidateFaultType faultInfo = new ValidateFaultType();
            faultInfo.setMsgID(400);
            faultInfo.setMsg("Client did not send request string");
            faultInfo.setField("doFaultTest");

            // Client should be able to catch ValidateFault
            throw new ValidateFault("Cannot process request", faultInfo);
        }

        if (codeHeader.getCode().equals("mustUnderstand")) {

            // Throw SOAPFaultException - soapfault code - MustUnderstand
            throw createSOAPFaultException(namespace, prefix, "MustUnderstand", "Incorrect code in codeHeader", null);
        } 
        
        if (doFaultTest.getReqString().equals("role")) {
            throw createSOAPFaultException(namespace, prefix, RECEIVER, "Test SOAP 1.2 Fault Role", null);
        }

        if (doFaultTest.getReqString().equals("test")) {
            ValidateFaultType faultInfo = new ValidateFaultType();
            faultInfo.setMsgID(500);
            faultInfo.setMsg("Unexpected request");
            faultInfo.setField("ReqString");

            // Client should be able to catch ProcessFault
            // SOAP faultstring should be ProcessFault.getMessage()
            throw new ProcessFault("Cannot process request", faultInfo);
        } 

        DoFaultTestResponseType resp = new DoFaultTestResponseType();
        resp.setResult(true);
        return resp;
    }


    /**
     * Create a SOAPFaultException with code as faultcode, str as 
     * faultstring, and if ValidateFaultType is not null, use it to 
     * add a detail that serializes the ValidateFault.
     */
    private SOAPFaultException createSOAPFaultException 
                               (String namespace, String prefix,
                                String code, String reason,
                                ValidateFaultType faultInfo) {
        try {
            SOAPFactory factory = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
            SOAPFault fault = factory.createFault();

            QName codeQName = new QName(namespace, code, prefix);

            fault.setFaultCode(codeQName);

            // Append a subcode
            QName subcodeQName = new QName(namespace, "DataEncodingUnknown", prefix + "-sub");
            fault.appendFaultSubcode(subcodeQName) ;

            fault.addFaultReasonText(reason, Locale.US);

            // Set fault role for testing purpose
            if (reason.indexOf("Role") != -1) {
               fault.setFaultRole(SOAPConstants.URI_SOAP_1_2_ROLE_NONE);
            }

            // add SOAP fault detail for ValidateFault
            if (faultInfo != null) {
                Detail detail = fault.addDetail();

                String schemaNS = this.getClass().getAnnotation(WebService.class).targetNamespace() + "/xsd";
                DetailEntry dEntry = detail.addDetailEntry(factory.createName("validateFault", "test-ns", schemaNS));
                SOAPElement dElement = dEntry.addChildElement(factory.createName("msgID"));
                dElement.addTextNode(Integer.toString(faultInfo.getMsgID()));

                dElement = dEntry.addChildElement(factory.createName("msg"));
                dElement.addTextNode(faultInfo.getMsg());

                dElement = dEntry.addChildElement(factory.createName("field"));
                dElement.addTextNode(faultInfo.getField());
            }

            return new SOAPFaultException(fault);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ProtocolException("Error in createSOAPFaultException(): "
                                        + e.getMessage());
        } 
    }
}

