//
// @(#) 1.1 autoFVT/src/jaxws/badprovider/wsfvt/server/NoAnnotationBadPortImpl.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 9/20/06 16:59:30 [8/8/12 06:55:21]
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
// 07/31/06 sedov       LIDB3296.42     New File
//

package jaxws.badprovider.wsfvt.server;

import javax.xml.transform.Source;
import javax.xml.ws.Provider;


/**
 * Bad provider test - no WebServiceProvider annotation
 *
 */
public class NoAnnotationBadPortImpl implements Provider<Source>{

	
	public Source invoke(Source arg0) {
		return PingProvider.invoke(arg0);
	}

}
