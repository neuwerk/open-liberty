//
// @(#) 1.6 autoFVT/src/jaxws/proxy/javatypes/wsfvt/server/ProxyJavaTypesPortImpl.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 1/25/07 14:40:21 [8/8/12 06:55:40]
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
// 01/05/07 sedov       413205          Modified list/set/array comparison routines
// 01/22/07 sedov       415477          Added additional types
//

package jaxws.proxy.javatypes.wsfvt.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.Holder;


/**
 * DOC/LIT Wrapped endpoint used for testing of Java types
 * support
 * Note: HashMap has to be untyped to be supported
 * jaxws.proxy.javatypes.wsfvt.server.ProxyJavaTypesPortImpl
 */
@WebService(
		serviceName="ProxyJavaTypesService",
		portName="JavaTypesPort",
		name="JavaTypesPort",
		targetNamespace="http://javatypes.wsfvt.javatypes.proxy.jaxws")
public class ProxyJavaTypesPortImpl {

	public enum COLOR_ENUM {RED, GREEN, BLUE}

	public String pingString(String str){
		return str;
	}	
	
	public int pingInt(int i){
		return i;
	}
	
	public Integer pingInteger(Integer i){
		return i;
	}	
	
	public char pingChar(char c){
		return c;
	}
	
	public long pingLong(long l){
		return l;
	}
	
	public COLOR_ENUM pingEnum(COLOR_ENUM en){
		return en;
	}
	
	public COLOR_ENUM[] pingEnumArray(COLOR_ENUM[] en){
		return en;
	}	
	
	
	public String[] pingArray(String[] arr){
		StringBuffer buff = new StringBuffer();
		for (int i=0; i < arr.length; i++){
			if (i > 0) buff.append(", ");
			buff.append(arr[i]);
		}
		System.out.println("pingArray: {" + buff.toString() + "}");
		
		return arr;
	}
	
	public String[][] ping2Darray(String[][] arr){
		
		StringBuffer buff = null;
		for (int x =0; x < arr.length; x++){
			buff = new StringBuffer();
			
			for (int i=0; i < arr[x].length; i++){
				if (i > 0) buff.append(", ");
				buff.append(arr[x][i]);
			}
			System.out.println("ping2Darray: {" + buff.toString() + "}");
		}
		
		return arr;
	}
	
	public List<String> pingList(List<String> list){
		StringBuffer buff = new StringBuffer();
		for (int i=0; i < list.size(); i++){
			if (i > 0) buff.append(", ");
			buff.append(list.get(i));
		}
		System.out.println("pingList: {" + buff.toString() + "}");		
		
		return list;
	}	
	
	public ArrayList<String> pingArrayList(ArrayList<String> list){
		StringBuffer buff = new StringBuffer();
		for (int i=0; i < list.size(); i++){
			if (i > 0) buff.append(", ");
			buff.append(list.get(i));
		}
		System.out.println("pingArrayList: {" + buff.toString() + "}");		
		
		return list;
	}
	
	public java.util.Set<Integer> pingSet(java.util.Set<Integer> set){
		StringBuffer buff = new StringBuffer();
		
		int i=0;
		for (Integer key: set){
			if (i > 0) buff.append(", ");
			buff.append(key);
			
			i++;
		}
		
		System.out.println("pingSet: {" + buff.toString() + "}");		
		return set;
	}	
	
	public Vector pingVectorGeneric(Vector v){
		System.out.println("pingVectorGeneric: " + v.toString());
		return v;
	}	
	
	public Vector<String> pingVector(Vector<String> v){
		System.out.println("pingVector: " + v.toString());
		return v;
	}
	
	public Vector<CustomComplexType> pingVectorOfPojo(Vector<CustomComplexType> v){
		System.out.println("pingVectorOfPojo: " + v.toString());
		return v;
	}	
	
	public java.util.Date pingDate(java.util.Date date){
		System.out.println("pingDate: " + date);
		return date;
	}
	
	public java.util.GregorianCalendar pingGregorianCalendar(java.util.GregorianCalendar gc){
		System.out.println("pingGregorianCalendar: " + gc);
		return gc;
	}	
	
	public CustomComplexType pingJavaBean(CustomComplexType pojo){
		System.out.println("pingJavaBean: " + pojo);
		return pojo;
	}
	
	public CustomDerrivedType pingJavaBeanPoly(CustomDerrivedType pojo){
		System.out.println("pingJavaBeanPoly: " + pojo);
		return pojo;
	}	
	
	public void pingOneWay(String in){
		System.out.println("pingOneWay: " + in);
	}
	
	public String pingWeirdoMethod(String in,
			@WebParam(mode=WebParam.Mode.INOUT)
			Holder<String> inout,
			@WebParam(mode=WebParam.Mode.OUT)
			Holder<Integer> out1){
		
		System.out.println("pingWeirdoMethod: " + in);
		
		return in;
	}
}
