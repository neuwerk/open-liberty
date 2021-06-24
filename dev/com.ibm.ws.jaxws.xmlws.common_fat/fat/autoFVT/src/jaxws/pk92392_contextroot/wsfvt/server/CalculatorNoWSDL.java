//
// @(#) 1.1 autoFVT/src/jaxws/pk92392_contextroot/wsfvt/server/CalculatorNoWSDL.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 8/17/10 23:07:55 [8/8/12 06:58:51]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2010
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// ----------------------------------------------------------------------------
// 08/13/10 sudiptam    PK92392         New File
package jaxws.pk92392_contextroot.wsfvt.server;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService(serviceName = "CalculatorNoWSDLService")

public class CalculatorNoWSDL {
	
	
	@WebMethod
	public int calculate(int i, int j){
		int k = i+j;
		return k;
	}

}
