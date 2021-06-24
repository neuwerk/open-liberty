//
// @(#) 1.2 autoFVT/src/jaxws22/xmlelement/wsfvt/test/XmlElementTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 10/14/10 14:24:19 [8/8/12 06:58:57]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2010
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date       UserId      Defect          Description
// ----------------------------------------------------------------------------
// 10/05/2010 jtnguyen    673723          New File
//

package jaxws22.xmlelement.wsfvt.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.Server;
import com.ibm.websphere.simplicity.PortType;

//import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;
import com.ibm.ws.wsfvt.test.framework.FVTTestCase;

import jaxws22.xmlelement.wsfvt.client.*;

/**
 * RTC Story: 13264 , task 34005:  JAX-WS 2.2 specification compliance, section 3.6.2.1, in conjunction with JAXB 2.1 specification.
 * This is the runtime test to confirm that we handle the @XmlElement annotation properly: 
 *  - for null value when @XmlElement annotation is used.  The conditions for Null values are defined in JAXB 2.1 specification section B.4.2.5.
 *  - the tooling generated artifact using @XmlElement is used correctly with @WebParam name. 
 *
 * Strategy:
 * - start from java, use wsgen to create wsdl and run wsimport to get client's artifact.  We don't package any wsdl or artifact with the EAR.
 * - To automate this test, collect the requests from tcpmonitor when method testRuntime() is run, and post the requests to the client tool PostMessageSender, then verify the response messages on the wire from PostMessageSender.
 * - We test both float and string types to make sure primitive types and object types are handled correctly.
 **/
 
public class XmlElementTest extends FVTTestCase {

    private static String hostandport = null;

    private static Server server = null;
    private static String hostname = null;
    private static Integer port = 0;
    private static String url = null;
        
    private static String postSoapaction = "";
    private int postTimeoutSec = 30;
    private boolean postIgnoreContents = false;
 
    private String response;  //return value
    
    private static final String inputStr = "any string";
    private String strRet;   
	
	static {
		try {
            server = TopologyDefaults.getDefaultAppServer();
			hostname = server.getNode().getHostname();
			port = server.getPortNumber(PortType.WC_defaulthost);
			hostandport = "http://" + hostname+ ":" + port;
			url = hostandport + "/jaxws22/xmlelement/EchoService";
		} catch (Exception e) {
		    // do nothing
		}
    }
    
    public static void main(String [] args) throws Exception {
        TestRunner.run(suite());
    }
        
    
    /** 
     * This method controls which test methods to run.
     * @return A suite of tests to run
     */
    public static Test suite() {    
        return new TestSuite(XmlElementTest.class);        
    }

    
    
    public void testNoAnnotation() throws Exception {    
        EchoService_Service service = new EchoService_Service();
        EchoService port = service.getEchoServicePort();

        strRet = port.noAnno(inputStr);        
        assertEquals(strRet,inputStr);
    }
    
    /*
     * test with input type = String
     */

    // expect response message to have nil="true" - null should be marshalled as nil="true" in the case of @XmlElement(nillable=true, required=true)
    public void testNull_stringNillTrueRequiredTrue(){  
        
        String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body><a:stringNillTrueRequiredTrue xmlns:a=\"http:/xmlelement/jaxws22\"></a:stringNillTrueRequiredTrue></soapenv:Body></soapenv:Envelope>";  
                   
        System.out.println("-- url = " + url);
        try {
            strRet = PostMsgSender.postToURL(url, request, postSoapaction, postTimeoutSec, postIgnoreContents);
        } catch(Exception e){
            e.printStackTrace();    
        }
        String expected = "nil=\"true\"";
        System.out.println("-- strRet = " + strRet);
        assertTrue("did not find substring " + expected, strRet.indexOf(expected)!= -1);
    }   

    // expect response message to have nil="true" - null should be marshalled as nil="true" in the case of @XmlElement(nillable=true, required=false)
    public void testNull_stringNillTrueRequiredFalse(){  
        
        String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body><a:stringNillTrueRequiredFalse xmlns:a=\"http:/xmlelement/jaxws22\"></a:stringNillTrueRequiredFalse></soapenv:Body></soapenv:Envelope>";               
        System.out.println("-- url = " + url);
        try {
            strRet = PostMsgSender.postToURL(url, request, postSoapaction, postTimeoutSec, postIgnoreContents);
            System.out.println("-- strRet = " + strRet);
        } catch(Exception e){
            e.printStackTrace();    
        }
        String expected = "nil=\"true\"";       
        assertTrue("did not find substring " + expected, strRet.indexOf(expected)!= -1);
    }   
    
    // input cannot be null.  Expect empty
    public void testNull_stringNillFalseRequiredTrue(){  
        
        String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body><a:stringNillFalseRequiredTrue xmlns:a=\"http:/xmlelement/jaxws22\"></a:stringNillFalseRequiredTrue></soapenv:Body></soapenv:Envelope>";             
        System.out.println("-- url = " + url);
        try {
            strRet = PostMsgSender.postToURL(url, request, postSoapaction, postTimeoutSec, postIgnoreContents);
            System.out.println("-- strRet = " + strRet);
        } catch(Exception e){
            e.printStackTrace(); 
        }
        String expected1 = "<.*:stringNillFalseRequiredTrueResponse xmlns:.*=\"http:/xmlelement/jaxws22\"/>";      
        String expected2 = "<.*:stringNillFalseRequiredTrueResponse xmlns:.*=\"http:/xmlelement/jaxws22\"></.*:stringNillFalseRequiredTrueResponse>";      
        assertTrue("did not find pattern " + expected1 + " or " + expected2, regMatch(strRet,expected1)||regMatch(strRet,expected2));
    }   

    // expect <return>inputStr</return>
    public void testStringNillFalseRequiredTrue(){  
        
        String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body><a:stringNillFalseRequiredTrue xmlns:a=\"http:/xmlelement/jaxws22\"><arg0>" 
                       + inputStr + "</arg0></a:stringNillFalseRequiredTrue></soapenv:Body></soapenv:Envelope>";               
        System.out.println("-- url = " + url);
        try {
            strRet = PostMsgSender.postToURL(url, request, postSoapaction, postTimeoutSec, postIgnoreContents);
            System.out.println("-- strRet = " + strRet);            
        } catch(Exception e){
            e.printStackTrace();             
        }
        String expected = "<return>" + inputStr + "</return>";       
        assertTrue("did not find substring " + expected, strRet.indexOf(expected)!= -1);
    }   
    
    // expect no marshalling on response
    public void testNull_stringNillFalseRequiredFalse(){  
        
        String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body><a:stringNillFalseRequiredFalse xmlns:a=\"http:/xmlelement/jaxws22\"></a:stringNillFalseRequiredFalse></soapenv:Body></soapenv:Envelope>";             
        System.out.println("-- url = " + url);
        try {
            strRet = PostMsgSender.postToURL(url, request, postSoapaction, postTimeoutSec, postIgnoreContents);
            System.out.println("-- strRet = " + strRet);            
        } catch(Exception e){
            e.printStackTrace();             
        }
        String expected1 = "<.*:stringNillFalseRequiredFalseResponse xmlns:.*=\"http:/xmlelement/jaxws22\"/>";      
        String expected2 = "<.*:stringNillFalseRequiredFalseResponse xmlns:.*=\"http:/xmlelement/jaxws22\"></.*:stringNillFalseRequiredFalseResponse>";      
        assertTrue("did not find pattern " + expected1 + " or " + expected2, regMatch(strRet,expected1)||regMatch(strRet,expected2));
    }   

    /*
     * test with input type = float 
     */
    
    // expect nil="true"
    public void testNull_floatNillTrueRequiredTrue(){  
        
        String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body><a:floatNillTrueRequiredTrue xmlns:a=\"http:/xmlelement/jaxws22\"></a:floatNillTrueRequiredTrue></soapenv:Body></soapenv:Envelope>";
         
        System.out.println("-- url = " + url);
        try {
            response = PostMsgSender.postToURL(url, request, postSoapaction, postTimeoutSec, postIgnoreContents);
        } catch(Exception e){
            e.printStackTrace();    
        }
        String expected = "nil=\"true\"";
        System.out.println("-- response = " + response);
        assertTrue("did not find substring " + expected, response.indexOf(expected)!= -1);
    }   

    // expect nil="true"
    public void testNull_floatNillTrueRequiredFalse(){  
        
        String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body><a:floatNillTrueRequiredFalse xmlns:a=\"http:/xmlelement/jaxws22\"></a:floatNillTrueRequiredFalse></soapenv:Body></soapenv:Envelope>";             
        System.out.println("-- url = " + url);
        try {
            response = PostMsgSender.postToURL(url, request, postSoapaction, postTimeoutSec, postIgnoreContents);
        } catch(Exception e){
            e.printStackTrace();    
        }
        String expected = "nil=\"true\"";
        System.out.println("-- response = " + response);
        assertTrue("did not find substring " + expected, response.indexOf(expected)!= -1);
    }   
    
    
    // input cannot be null.  Expect an empty body, not exception
    public void testNull_floatNillFalseRequiredTrue(){  
        
        String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body><a:floatNillFalseRequiredTrue xmlns:a=\"http:/xmlelement/jaxws22\"></a:floatNillFalseRequiredTrue></soapenv:Body></soapenv:Envelope>";             
        System.out.println("-- url = " + url);
        try {
            response = PostMsgSender.postToURL(url, request, postSoapaction, postTimeoutSec, postIgnoreContents);
            System.out.println("-- response = " + response);
        } catch(Exception e){
            e.printStackTrace(); 
            assertTrue(true);
        }
        String expected1 = "<.*:floatNillFalseRequiredTrueResponse xmlns:.*=\"http:/xmlelement/jaxws22\"/>";      
        String expected2 = "<.*:floatNillFalseRequiredTrueResponse xmlns:.*=\"http:/xmlelement/jaxws22\"></.*:floatNillFalseRequiredTrueResponse>";      
        assertTrue("did not find pattern " + expected1 + " or " + expected2, regMatch(response,expected1)||regMatch(response,expected2));

    }   
    
    // expect no marshalling on response
    public void testNull_floatNillFalseRequiredFalse(){  
        
        String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body><a:floatNillFalseRequiredFalse xmlns:a=\"http:/xmlelement/jaxws22\"></a:floatNillFalseRequiredFalse></soapenv:Body></soapenv:Envelope>";             
        System.out.println("-- url = " + url);
        try {
            response = PostMsgSender.postToURL(url, request, postSoapaction, postTimeoutSec, postIgnoreContents);
            System.out.println("-- response = " + response);            
        } catch(Exception e){
            e.printStackTrace();             
        }
        String expected1 = "<.*:floatNillFalseRequiredFalseResponse xmlns:.*=\"http:/xmlelement/jaxws22\"/>";  
        String expected2 = "<.*:floatNillFalseRequiredFalseResponse xmlns:.*=\"http:/xmlelement/jaxws22\"></.*:floatNillFalseRequiredFalseResponse>";
        assertTrue("did not find pattern " + expected1 + " or " + expected2, regMatch(response,expected1)||regMatch(response,expected2));
    }   

    /*
     * spot check on annotation on method or on field only
     */

    // expect nil="true"
    public void testAnnoOnMethodOnly_floatNillTrueRequiredTrue(){  
        
        String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body><a:annoOnMethodOnly xmlns:a=\"http:/xmlelement/jaxws22\"></a:annoOnMethodOnly></soapenv:Body></soapenv:Envelope>";
         
        System.out.println("-- url = " + url);
        try {
            response = PostMsgSender.postToURL(url, request, postSoapaction, postTimeoutSec, postIgnoreContents);
        } catch(Exception e){
            e.printStackTrace();    
        }
        String expected = "nil=\"true\"";
        System.out.println("-- response = " + response);
        assertTrue("did not find substring " + expected, response.indexOf(expected)!= -1);
    }   

    // expect 
    public void testAnnoOnFieldOnly_floatNillTrueRequiredTrue(){  
        
        String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body><a:annoOnFieldOnly xmlns:a=\"http:/xmlelement/jaxws22\"><arg0 xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/></a:annoOnFieldOnly></soapenv:Body></soapenv:Envelope>";
         
        System.out.println("-- url = " + url);
        try {
            response = PostMsgSender.postToURL(url, request, postSoapaction, postTimeoutSec, postIgnoreContents);
        } catch(Exception e){
            e.printStackTrace();    
        }
        String expected1 = "<.*:annoOnFieldOnlyResponse xmlns:.*=\"http:/xmlelement/jaxws22\"/>";
        String expected2 = "<.*:annoOnFieldOnlyResponse xmlns:.*=\"http:/xmlelement/jaxws22\"></.*:annoOnFieldOnlyResponse>";
        System.out.println("-- response = " + response);
        assertTrue("did not find pattern " + expected1 + " or " + expected2, regMatch(response,expected1)||regMatch(response,expected2));
    }   


    /*
     *  Test for change name:  verify the name used is mapped correctly to the @WebParam name value (e.g. "floatNewName" instead of "arg0")
     */
    
    // expect new name in response
    public void testChangeName(){  
        
        String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body><a:changeName xmlns:a=\"http:/xmlelement/jaxws22\"><floatNewName>1.11</floatNewName></a:changeName></soapenv:Body></soapenv:Envelope>";             
        System.out.println("-- url = " + url);
        try {
            response = PostMsgSender.postToURL(url, request, postSoapaction, postTimeoutSec, postIgnoreContents);
            System.out.println("-- response = " + response);            
        } catch(Exception e){
            e.printStackTrace();             
        }
        String expected = "<.*:changeNameResponse xmlns:.*=\"http:/xmlelement/jaxws22\"><return>1\\.11</return></.*:changeNameResponse>";
        assertTrue("did not find pattern " + expected, regMatch(response,expected));
    }  
    
    // expect new name in response
    public void testNull_ChangeName(){       
            
            String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body><a:changeName xmlns:a=\"http:/xmlelement/jaxws22\"></a:changeName></soapenv:Body></soapenv:Envelope>";             
            System.out.println("-- url = " + url);
            try {
                response = PostMsgSender.postToURL(url, request, postSoapaction, postTimeoutSec, postIgnoreContents);
                System.out.println("-- response = " + response);            
            } catch(Exception e){
                e.printStackTrace();             
            }
            String expected1 = "<.*:changeNameResponse xmlns:.*=\"http:/xmlelement/jaxws22\"/>";
            String expected2 = "<.*:changeNameResponse xmlns:.*=\"http:/xmlelement/jaxws22\"></.*:changeNameResponse>";
            assertTrue("did not find pattern " + expected1 + " or " + expected2, regMatch(response,expected1)||regMatch(response,expected2));
        } 
    
    private boolean regMatch(String content, String patternStr){
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(content);
        return matcher.find();
    }
    
    // collect requests & responses from  tcpmonitor  
    public void _testRuntime(){ 
        
        float num =  1.11f;
        Float nullValue = null;        
        float ret;
        
        String nullStr = null;
        String result;
        

        try {            
            
            String url = hostandport + "/jaxws22/xmlelement/EchoService";
            System.out.println("sending message using client artifact, url =" + url);
            
            EchoService_Service service = new EchoService_Service();
            EchoService port = service.getEchoServicePort();
            
          try{
            // Test for change name:  verify the name used is mapped correctly to the @WebParam name value (e.g. "floatNewName" instead of "arg0")
            // test change name
            ret = port.changeName(num);        
            System.out.println("============= floatChangeName is done");
          }catch (Exception e){      
            e.printStackTrace();          
          }
          
           try{
            // test change name with null;
            ret = port.changeName(nullValue);        
            System.out.println("============= floatChangeName is done");
          }catch (Exception e){      
            e.printStackTrace();          
          }
                 
            // test with type = float
          try{            
            //  test @XmlElement(nillable=true, required=false)
            ret = port.floatNillTrueRequiredFalse(nullValue);        
            System.out.println("============= floatNillTrueRequiredFalse is done");
           }catch (Exception e){      
            e.printStackTrace();          
          }
          try{                   
            // test @XmlElement(nillable=true, required=true)
            ret = port.floatNillTrueRequiredTrue(nullValue);        
            System.out.println("============= floatNillTrueRequiredTrue is done");         
          }catch (Exception e){      
            e.printStackTrace();          
          }
          try{            
            // test @XmlElement(nillable=false, required=true)
            ret = port.floatNillFalseRequiredTrue(nullValue);        
            System.out.println("============= floatNillFalseRequiredTrue is done");
           }catch (Exception e){      
            e.printStackTrace();          
          }
          try{           
            //  test @XmlElement(nillable=false, required=false)
            ret = port.floatNillFalseRequiredFalse(nullValue);        
            System.out.println("============= floatNillFalseRequiredFalse is done");
           }catch (Exception e){      
            e.printStackTrace();          
          }
          
    
          // test with type = String


          // test @XmlElement(nillable=true, required=true)
          try{            
            result =  port.stringNillTrueRequiredTrue(nullStr);       
            System.out.println("============= stringNillTrueRequiredTrue is done");
          }catch (Exception e){      
            e.printStackTrace();          
          }

          //  test @XmlElement(nillable=false, required=false)
          try{           
            result =  port.stringNillFalseRequiredFalse(nullStr);       
            System.out.println("============= stringNillFalseRequiredFalse is done");
          }catch (Exception e){      
            e.printStackTrace();          
          }

          //  test @XmlElement(nillable=true, required=false)
          try{    
            result =  port.stringNillTrueRequiredFalse(nullStr);       
            System.out.println("============= stringNillTrueRequiredFalse is done");
          }catch (Exception e){      
            e.printStackTrace();          
          }

          // test @XmlElement(nillable=false, required=true)
          try{    
            result =  port.stringNillFalseRequiredTrue(nullStr);       
            System.out.println("============= stringNillTrueRequiredFalse is done");
          }catch (Exception e){      
            e.printStackTrace();          
          }

          // test @XmlElement(nillable=true, required=true)
          try{    
            ret =  port.annoOnMethodOnly(nullValue);       
            System.out.println("============= annoOnMethodOnly is done");
          }catch (Exception e){      
            e.printStackTrace();          
          }

          // test @XmlElement(nillable=true, required=true)
          try{    
            ret =  port.annoOnFieldOnly(nullValue);       
            System.out.println("============= annoOnFieldOnly is done");
          }catch (Exception e){      
            e.printStackTrace();          
          }
      
        } catch (Exception e){      
            e.printStackTrace();          
        }
        
     }
   
 
}
