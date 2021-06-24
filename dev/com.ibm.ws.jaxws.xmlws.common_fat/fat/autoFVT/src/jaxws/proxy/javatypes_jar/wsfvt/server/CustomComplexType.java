/** 
 * @(#) 1.1 autoFVT/src/jaxws/proxy/javatypes_jar/wsfvt/server/CustomComplexType.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 1/19/07 11:28:32 [8/8/12 06:56:16]
 *
 * IBM Confidential OCO Source Material
 * (C) COPYRIGHT International Business Machines Corp. 2006
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        Author       Feature/Defect          Description
 * 01/19/07    sedov        D415799                 New File
 **/
package jaxws.proxy.javatypes_jar.wsfvt.server;

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
