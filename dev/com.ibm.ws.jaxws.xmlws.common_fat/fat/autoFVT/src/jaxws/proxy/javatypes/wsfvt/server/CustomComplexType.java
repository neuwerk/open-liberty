//
// @(#) 1.4 autoFVT/src/jaxws/proxy/javatypes/wsfvt/server/CustomComplexType.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 1/25/07 14:39:59 [8/8/12 06:55:40]
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

@XmlType(name="customComplexType", namespace="http://javatypes.wsfvt.javatypes.proxy.jaxws")
public class CustomComplexType {

	private String firstName;
	private String lastName;
	private int id;
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}
