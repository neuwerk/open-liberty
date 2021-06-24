//
// @(#) 1.1 WautoFVT/src/jaxws/proxy/wsfvt/server/ProxyDocLitPortImpl.java, WAS.websvcs.fvt, WSFPB.WFVT 9/12/06 14:50:56 [9/12/06 14:56:54]
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
// 09/15/06 sedov       390173          removed non-Beta tests
// 11/13/06 sedov       404343          Added anyType parameter

package jaxws.proxy.doclit.wsfvt.server;

import javax.jws.WebService;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceException;

import jaxws.proxy.common.Constants;
import jaxws.proxy.doclit.wsfvt.doclit.*;

/**
 * Endpoint for Document literal invocations. Will roundtrip messages
 * when appropriate. Will throw an exception if request to by the client. 
 * @wsdl: proxy_doclit.wsdl  
 */
@WebService(
		serviceName="ProxyDocLitService",
		portName="ProxyDocLitPort",
		targetNamespace = "http://doclit.wsfvt.doclit.proxy.jaxws",
		wsdlLocation="WEB-INF/wsdl/proxy_doclit.wsdl",	
		endpointInterface = "jaxws.proxy.doclit.wsfvt.doclit.DocLitProxy")
public class ProxyDocLitPortImpl implements DocLitProxy {

	public void oneWayEmpty() {
		// do nothing
	}

	public void oneWay(String allByMyself) {
		
		// do nothing, unless requested to send an exception
		if (allByMyself.equals(Constants.THE_WSE_STRING)){
			throw new WebServiceException(Constants.THE_WSE_STRING);
		}
	}

	public String twoWaySimple(int allByMyself) {
		if (allByMyself == Constants.THE_INT)
			return Constants.THE_STRING;
		else
			return Constants.THE_FAULT_STRING;
	}

    public HeaderResponse header(
            Header payload,
            Holder<HeaderPart0> header0,
            HeaderPart1 header1){
    	HeaderResponse response = new HeaderResponse();
    	response.setInout(payload.getInout());
    	response.setOut(payload.getOut());
    	
    	return response;
    }
	
    
    public Object anyType(Object in){    
    	return in;
    }
    
	public void twoWayHolder(Holder<Composite> allByMyself) throws FaultBeanWithWrapper, SimpleFault {
		
		String value = allByMyself.value.getMyElement();
		// throw an exception if requested, else just roundtrip the message
		if (value.equals(Constants.THE_FAULT_STRING)){
			// client requested to throw a simple fault, which maps to a string
			throw new SimpleFault("message", Constants.THE_FAULT_STRING);
		} else if (value.equals(Constants.THE_WSE_STRING)){
			// client requested to throw a WSE
			throw new WebServiceException(Constants.THE_WSE_STRING);
		} else if (value.equals(Constants.THE_BASE_FAULT)){
			// polymorphic test: throw a base fault
			BaseFault faultInfo = new BaseFault();
			faultInfo.setErrorCode(Constants.THE_INT);
			faultInfo.setMessage(Constants.THE_BASE_FAULT);
			throw new FaultBeanWithWrapper(Constants.THE_BASE_FAULT, faultInfo);
		} else if (value.equals(Constants.THE_POLYMORPHIC_FAULT)){
			// polymorphic test: throw an extending fault
			ExtendedFault faultInfo = new ExtendedFault();
			faultInfo.setErrorCode(Constants.THE_INT);
			faultInfo.setMessage(Constants.THE_BASE_FAULT);
			faultInfo.setAnotherErrorCode(Constants.THE_INT + 1);
			faultInfo.setAnotherMessage(Constants.THE_POLYMORPHIC_FAULT);
			throw new FaultBeanWithWrapper(Constants.THE_POLYMORPHIC_FAULT, faultInfo);
		} else if (value.equals(Constants.THE_NULL_FAULTINFO_FAULT)){
			// send back a null fault info...should go back as a marker exception
			throw new FaultBeanWithWrapper(Constants.THE_POLYMORPHIC_FAULT, null);
		} else if (value.equals(Constants.THE_WSE_WITH_SPECIFIC_FAULT)) {
			// web service exception with wsdl:fautl as cause
			BaseFault faultInfo = new BaseFault();
			faultInfo.setErrorCode(Constants.THE_INT);
			faultInfo.setMessage(Constants.THE_BASE_FAULT);
			
			try {
				// throw it so we can send back a stack trace
				throw new FaultBeanWithWrapper(Constants.THE_BASE_FAULT, faultInfo);
			} catch (FaultBeanWithWrapper fbwr){	
				throw new WebServiceException(Constants.THE_WSE_WITH_SPECIFIC_FAULT, fbwr);
			}
		}
	}

	public void soapAction(Holder<Ping> msg) {
		// do nothing, just roundtrip the message
	}

}
