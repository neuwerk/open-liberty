// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// ----------------------------------------------------------------------------
// Refer to WautoFVT/src/wasintegration/jaxwsrpc/

package saaj.server.provider;

// import org.apache.axis2.AxisFault;

import javax.xml.ws.Provider;


import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.BindingType;
import javax.xml.ws.soap.SOAPBinding;
import javax.jws.WebMethod;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.transform.dom.DOMSource;

import com.ibm.ws.saaj.XMLUtils;

@BindingType(value = SOAPBinding.SOAP12HTTP_BINDING)
@ServiceMode(value = Service.Mode.PAYLOAD)
@WebServiceProvider(targetNamespace="http://ws.apache.org/saaj",
                    serviceName="SAAJService1", portName="SAAJService1Port",
                    wsdlLocation="WEB-INF/wsdl/SAAJService1.wsdl")
public class String12Provider implements Provider<DOMSource>
{

    public String12Provider(){
    }

    private static String responseGood = "<s12:provider xmlns:s12=\"http://ws.apache.org/saaj\"><message>request 12 processed SAAJ </message></s12:provider>";
    private static String responseBad = "<s12:provider xmlns:s12=\"http://ws.apache.org/saaj\"><message>ERROR:null 12 request received SAAJ</message></s12:provider>";
    private static String responseGood1 = "<s12:provider xmlns:s12=\"http://ws.apache.org/saaj\"><message>request 12 anyinvoke SAAJ </message></s12:provider>";
    private static String responseBad1 = "<s12:provider xmlns:s12=\"http://ws.apache.org/saaj\"><message>ERROR:null request received 12 anyinvoke SAAJ</message></s12:provider>";

    //public void receive(MessageContext reqMsgContext) throws AxisFault{
    //    System.out.println( "*** in SAAJ String12Provider receive()" );
    //}
    @WebMethod
    public DOMSource invoke(DOMSource s) 
    {
        if(s != null)
        {
            String s1 = XMLUtils.getContentFromDOMSource(s);
            System.out.println(">> String12Provider invoke() received a new request");
            System.out.println((new StringBuilder()).append(">> request [").append(s1).append("]").toString());
            if( s1.indexOf( "NoPrefix" ) >= 0 ){
                // this is testing defect 412984
                String re1 =  "<provider><message>responded with the BODYElement has NoPrefix</message></provider>";            
                System.out.println( "respond string '" + re1 + "'" );
                return XMLUtils.createDOMSourceFromString(re1);
            } else {
                return XMLUtils.createDOMSourceFromString(responseGood);
            }
        } else
        {
            System.out.println(">> ERROR:null request received");
            return XMLUtils.createDOMSourceFromString(responseBad);
        }
    }

    //public void receive(MessageContext reqMsgContext) throws AxisFault{
    //    System.out.println( "*** in SAAJ String12Provider receive()" );
    //}
    @WebMethod
    public DOMSource anyinvoke(DOMSource s) 
    {
        if(s != null)
        {
            return XMLUtils.createDOMSourceFromString(responseGood1);
        } else
        {
            System.out.println(">> ERROR:null anyinvoke() request received");
            return XMLUtils.createDOMSourceFromString(responseBad1);
        }
    }


}
