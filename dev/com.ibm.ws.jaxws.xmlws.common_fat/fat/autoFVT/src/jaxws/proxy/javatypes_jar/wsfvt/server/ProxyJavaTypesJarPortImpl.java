/** 
 * @(#) 1.1 autoFVT/src/jaxws/proxy/javatypes_jar/wsfvt/server/ProxyJavaTypesJarPortImpl.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 1/19/07 11:28:32 [8/8/12 06:56:16]
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

import javax.jws.WebService;

/**
 * DOC/LIT Wrapped endpoint used for testing of Java types
 * support
 * This is specifically to test the case when wsgen beans are
 * packed in a jar
 */
@WebService(
		serviceName="ProxyJavaTypesJarService",
		portName="JavaTypesJarPort",
		name="JavaTypesJarPort",
		targetNamespace="http://javatypesjar.wsfvt.javatypesjar.proxy.jaxws")
public class ProxyJavaTypesJarPortImpl {

	public String pingString(String str){
		return str;
	}	
	
	public int pingInt(int i){
		return i;
	}
	
	public CustomComplexType pingJavaBean(CustomComplexType request){
		return request;
	}
}
