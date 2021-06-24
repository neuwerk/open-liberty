//
// @(#) 1.2 autoFVT/src/saaj13dev/devtest/wsfvt/saaj13/TestBase.java, WAS.websvcs.fvt, WASX.FVT 8/11/06 15:44:48 [7/11/07 13:20:54]
//
// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5655-N01, 5733-W60, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date      UserId      Defect          Description
// ----------------------------------------------------------------------------
// 05/18/06  scheu       LIDB4238        Create
// 08/11/06  ulbricht    383776          Modify to work with downlevel junit 

package saaj13dev.devtest.wsfvt.saaj13;

import com.ibm.ws.webservices.engine.Constants;

import junit.framework.TestCase;

/**
 * Base class for the SAAJ tests.
 */
public abstract class TestBase extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	protected final String URI_SOAP11_ENV;
    protected final String URI_SCHEMA_XSI;
    protected final String URI_SCHEMA_XSD;
    protected final String URI_XMLNS;
    
	public TestBase() {
		super("");
		URI_SOAP11_ENV = Constants.URI_SOAP11_ENV;
        URI_SCHEMA_XSI = Constants.URI_DEFAULT_SCHEMA_XSI;
        URI_SCHEMA_XSD = Constants.URI_DEFAULT_SCHEMA_XSD;
        URI_XMLNS = Constants.NS_URI_XMLNS;
	}

	public TestBase(String arg0) {
		super(arg0);
		URI_SOAP11_ENV = Constants.URI_SOAP11_ENV;
        URI_SCHEMA_XSI = Constants.URI_DEFAULT_SCHEMA_XSI;
        URI_SCHEMA_XSD = Constants.URI_DEFAULT_SCHEMA_XSD;
        URI_XMLNS = Constants.NS_URI_XMLNS;
	}

}
