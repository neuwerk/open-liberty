//
// @(#) 1.1 autoFVT/src/jaxws/proxy/javatypes/wsfvt/server/CustomDerrivedType.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 1/25/07 14:41:23 [8/8/12 06:56:17]
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
// 01/25/07 sedov       416630          New File
//

package jaxws.proxy.javatypes.wsfvt.server;

import javax.xml.bind.annotation.XmlType;

/**
 * test pojo for start from java scenario. Per defect #416355 the pojo should be
 * annotated to avoid unmarshalling errors
 */

@XmlType(name="customDerrivedType", namespace="http://javatypes.wsfvt.javatypes.proxy.jaxws")
public class CustomDerrivedType extends CustomComplexType{

	private String middleInitial = null;

	public String getMiddleInitial() {
		return middleInitial;
	}

	public void setMiddleInitial(String middleInitial) {
		this.middleInitial = middleInitial;
	}
}
