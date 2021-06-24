//
// @(#) 1.1 autoFVT/src/jaxws/pk92392_contextroot/wsfvt/client/CalculatorClient.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 8/17/10 23:07:53 [8/8/12 06:58:51]
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
// 08/13/10 sudiptam    PK92392    New File

package jaxws.pk92392_contextroot.wsfvt.client;


import jaxws.pk92392_contextroot.wsfvt.CalculatorImportWSDLClient.*;
import jaxws.pk92392_contextroot.wsfvt.CalculatorNoWSDLClient.*;
import jaxws.pk92392_contextroot.wsfvt.CalculatorSingleWSDLClient.*;


public class CalculatorClient {
	
	//private String endpoint;
	private int a;
	private int b;
	
	
	public int invokeCalculatorImportWSDLService(int a, int b) throws Exception{
		
		CalculatorImportWSDLService service = new CalculatorImportWSDLService();
		CalculatorImportWSDL port = service.getCalculatorImportWSDLPort();	
		int output = port.calculate(a,b);
		System.out.println("-- output from invokeCalculatorImportWSDLService: " + output);
		
		return output;
	}
	public int invokeCalculatorSingleWSDLService(int a, int b) throws Exception{
		
		CalculatorSingleWSDLService service = new CalculatorSingleWSDLService();
		CalculatorSingleWSDL port = service.getCalculatorSingleWSDLPort();	
		int output = port.calculate(a,b);
		System.out.println("-- output from invokeCalculatorSingleWSDLService: " + output);
		
		return output;
	}
	public int invokeCalculatorNoWSDLService(int a, int b) throws Exception{
		
		CalculatorNoWSDLService service = new CalculatorNoWSDLService();
		CalculatorNoWSDL port = service.getCalculatorNoWSDLPort();	
		int output = port.calculate(a,b);
		System.out.println("-- output from invokeCalculatorNoWSDLService: " + output);
		
		return output;
	}
	
	public static void main(String[] args){
	}

}
