package soapendpoint.wsfvt.server.errorsei;

import javax.jws.WebService;
import javax.xml.ws.BindingType;

import org.apache.axis2.jaxws.Constants;

/**
 * This service should not be able to be started. It is incorrect for
 * Constants.SOAP_HTTP_BINDING to be annotated to a Java Bean.
 */
@WebService(targetNamespace = "http://compare.scanning.annotations", serviceName = "AnnotationsScanningCompareService", portName = "AnnotationsScanningComparePort")
@BindingType(Constants.SOAP_HTTP_BINDING)
public class CompareServiceImpl {

	/**
	 * compare two strings
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	public boolean compareStrings(String str1, String str2) {
		if (str1 == null && str2 == null) {
			return true;
		}

		if (str1 != null && str1.equals(str2)) {
			return true;
		}

		return false;
	}
}
