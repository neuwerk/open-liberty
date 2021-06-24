//
// @(#) 1.15 autoFVT/src/jaxws/proxy/wsdltypes/wsfvt/server/ProxyWsdlTypesPortImpl.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 9/23/09 14:16:34 [8/8/12 06:55:12]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date       UserId      Defect          Description
// ----------------------------------------------------------------------------
// 09/11/06   sedov       LIDB3296.42-01     New File
// 11/09/06   sedov       404343             Added anyType, QName, char, and extension types tests
// 01/10/07   sedov       413290             Updated polymorphic test
// 02/11/09   jramos      574405             Update method typesJava1DArray method header
// 09/23/09   jramos      615311             Provide implementation for HP version of typesJava1DArray 

package jaxws.proxy.wsdltypes.wsfvt.server;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.ws.Holder;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

import jaxws.proxy.wsdltypes.wsfvt.wsdltypes.WsdlTypesPort;
import jaxws.proxy.wsdltypes.wsfvt.wsdltypes1.*;
import jaxws.proxy.wsdltypes.wsfvt.wsdltypes2.*;
import jaxws.proxy.wsdltypes.wsfvt.wsdltypes3.*;

/**
 * DOC/LIT Wrapped endpoint used for testing of WSDL types support
 */
@WebService(serviceName = "ProxyWsdlTypesService",
		portName = "WsdlTypesPort",
		targetNamespace = "http://wsdltypes.wsfvt.wsdltypes.proxy.jaxws",
		endpointInterface = "jaxws.proxy.wsdltypes.wsfvt.wsdltypes.WsdlTypesPort",
		wsdlLocation = "WEB-INF/wsdl/proxy_wsdltypes.wsdl")
public class ProxyWsdlTypesPortImpl implements WsdlTypesPort {

    public List<Long> typesLongList(List<Long> in){
    	return in;
    }

    public byte[] typesBase64Binary(byte[] in){
    	return in;
    }

    public byte[] typesHexBinary(byte[] in){
    	return in;
    }
	
	public String[] typesJava1DArray(String[] inArray) {
		return inArray;
	}
	
	public List<String> typesJava1DArray(List<String> inArray) {
	    return inArray;
	}

	public MyStringArray types1DArray(MyStringArray myArray) {

		// print the contents of the array
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < myArray.getArray().size(); i++) {
			if (i > 0) buff.append(", ");
			buff.append(myArray.getArray().get(i));
		}
		System.out.println("types1DArray: {" + buff.toString() + "} size="
				+ myArray.getArray().size());

		return myArray;
	}

	public List<MyStringArray> types2DArray(List<MyStringArray> myArray) {

		// print the contents of the array
		for (int x = 0; x < myArray.size(); x++) {
			MyStringArray list = myArray.get(x);
			StringBuffer buff = new StringBuffer();

			for (int i = 0; i < list.getArray().size(); i++) {
				if (i > 0) buff.append(", ");
				buff.append(list.getArray().get(i));
			}
			System.out.println("types2DArray: {" + buff.toString() + "} size="
					+ myArray.size());
		}

		return myArray;
	}

	public List<MyAbstractBaseType> typesAbstractPolymorphicArray(
			List<MyAbstractBaseType> polyAbstractArray) {

		// print the contents of the array
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < polyAbstractArray.size(); i++) {
			if (i > 0) buff.append(", ");
			buff.append(polyAbstractArray.get(i).getClass().getName());
		}
		System.out.println("typesAbstractPolymorphicArray: {" + buff.toString()
				+ "} size=" + polyAbstractArray.size());

		return polyAbstractArray;
	}

	public MyNillableStringArray typesArrayWithNulls(
			MyNillableStringArray myArray) {

		// print the contents of the array
		StringBuffer buff = new StringBuffer();
		int i = 0;
		for (String key : myArray.getArray()) {
			if (i++ > 0) buff.append(", ");

			buff.append(key);
		}
		System.out.println("typesArrayWithNulls: {" + buff.toString()
				+ "} size=" + myArray.getArray().size());

		return myArray;
	}

	public List<MyBaseType> typesPolymorphicArray(List<MyBaseType> polyArray) {

		// print the contents of the array
		StringBuffer buff = new StringBuffer();
		int i = 0;
		for (MyBaseType obj : polyArray) {
			if (i++ > 0) buff.append(", ");
			buff.append((obj == null) ? null : obj.getClass().getName());
		}
		System.out.println("typesPolymorphicArray: {" + buff.toString()
				+ "} size=" + polyArray.size());

		return polyArray;
	}

	public ArrayOfString typesWrappedArray(ArrayOfString strArray) {
		return strArray;
	}

	public void typesDate(XMLGregorianCalendar myDate,
			XMLGregorianCalendar myTime,
			Holder<XMLGregorianCalendar> retMyDate,
			Holder<XMLGregorianCalendar> retMyTime) {
		retMyDate.value = myDate;
		retMyTime.value = myTime;
	}

	public XMLGregorianCalendar typesDateTime(XMLGregorianCalendar myDateTime) {
		return myDateTime;
	}

	public void typesGDate(XMLGregorianCalendar myGDay,
			XMLGregorianCalendar myGMonth, XMLGregorianCalendar myGYear,
			Holder<XMLGregorianCalendar> retMyGDay,
			Holder<XMLGregorianCalendar> retMyGMonth,
			Holder<XMLGregorianCalendar> retMyGYear) {
		retMyGDay.value = myGDay;
		retMyGMonth.value = myGMonth;
		retMyGYear.value = myGYear;
	}

	public BinaryTreeElement typesGraph(BinaryTreeElement binaryTree) {
		System.out.println("typesGraph: Data received");

		return binaryTree;
	}

	public int typesInt(int myInt) {
		return myInt;
	}

	public BigInteger typesInteger(BigInteger myInteger) {
		return myInteger;
	}

	public NamedComplexType1 typesElementIsNamedComplexType(String xsdString,
			long xsdLong) {
		jaxws.proxy.wsdltypes.wsfvt.wsdltypes1.ObjectFactory of
			= new jaxws.proxy.wsdltypes.wsfvt.wsdltypes1.ObjectFactory();
		NamedComplexType1 ret = of.createNamedComplexType1();

		ret.setXsdLong(xsdLong);
		ret.setXsdString(xsdString);

		return ret;
	}

	public QName typesQName(QName inQName) {
		return inQName;
	}

	public void typesAny(Holder<Object> any, Holder<String> type) {

		if (any.value == null)
			type.value = null;
		else
			type.value = any.value.getClass().getName();
	}

	public int typesChar(int inChar) {
		return inChar;
	}

	public void typesExtension(BasePolyType request, TypesMode mode, Holder<BasePolyType> response, Holder<String> receivedType) {
		
		BasePolyType reply = null;
		
		switch (mode){
			case ROUNDTRIP:
				reply = request;
				break;
			case BASE:
				reply = new BasePolyType();
				reply.setAmount(10);
				break;
				
			case DERRIVED_SAME_PACKAGE:

				if (request.getAmount() < 0){
					NegativePolyType npt = new NegativePolyType();
					npt.setAmount(request.getAmount());
					npt.setStrNegative("negative");
					
					reply = npt;
				} else {
					PositivePolyType ppt = new PositivePolyType();
					ppt.setAmount(request.getAmount());
					ppt.setIsPositive(true);
					
					reply = ppt;
				}
				break;
				
			case DERRIVED_DIFF_PACKAGE:
				if (request.getAmount() < 0){
					LargeNegativePolyType lnpt = new LargeNegativePolyType();
					lnpt.setAmount(request.getAmount());
					lnpt.setStrNegative("negative");
					lnpt.setIsVeryNegative(true);
					
					reply = lnpt;
				} else {
					LargePositivePolyType lppt = new LargePositivePolyType();
					lppt.setAmount(request.getAmount());
					lppt.setIsPositive(true);
					lppt.setIsVeryPositive(true);
					reply = lppt;
				}				
				break;
		}
		response.value = reply;
		receivedType.value = request.getClass().getName();

		//System.out.println("typesExtension: received " + request.getClass().getDgetName());
		//System.out.println("typesExtension: mode " + mode);
		//System.out.println("typesExtension: reply " + reply.getClass().getName());
	
	}

	public TypesAttributeType typesAttribute(TypesAttributeType request) {
		return request;
	}

	public Byte[] typesByteList(Byte[] request) {
		return request;
	}

	public BigDecimal[] typesDecimalList(BigDecimal[] request) {
		return request;
	}

	public Integer[] typesIntList(Integer[] request) {
		return request;
	}

	public BigInteger[] typesIntegerList(BigInteger[] request) {
		return request;
	}

	public int typesRestrictionEnum(int request) {
		return request;
	}

	public int typesRestrictionMinMax(int request) {
		return request;
	}

	public String typesRestrictionPattern(String request) {
		return request;
	}

	public String[] typesStringList(String[] request) {
		return request;
	}

	public void typesElementRef(Holder<String> in, Holder<TypesQName> typesQName, Holder<Types1DArray> types1DArray) {
		// do nothing
	}
}
