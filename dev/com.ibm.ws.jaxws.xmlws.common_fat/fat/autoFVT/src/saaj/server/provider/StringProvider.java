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

import javax.xml.ws.BindingType;
import javax.xml.ws.soap.SOAPBinding;

import javax.xml.ws.WebServiceProvider;
import javax.jws.WebMethod;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.transform.dom.DOMSource;

import com.ibm.ws.saaj.XMLUtils;

@BindingType(value=SOAPBinding.SOAP11HTTP_BINDING)
@ServiceMode(value=Service.Mode.PAYLOAD)
@WebServiceProvider(targetNamespace="http://ws.apache.org/saaj",
                    serviceName="SAAJService", portName="SAAJServicePort",
                    wsdlLocation="WEB-INF/wsdl/SAAJService.wsdl")
public class StringProvider implements Provider<DOMSource>
{

    public StringProvider(){
    }

    private static String responseGood = "<s11:provider xmlns:s11=\"http://ws.apache.org/saaj\"><message>request processed SAAJ </message></s11:provider>";
    private static String responseBad = "<s11:provider xmlns:s11=\"http://ws.apache.org/saaj\"><message>ERROR:null request received SAAJ</message></s11:provider>";
    private static String responseGood1 = "<s11:provider xmlns:s11=\"http://ws.apache.org/saaj\"><message>request anyinvoke SAAJ </message></s11:provider>";
    private static String responseBad1 = "<s11:provider xmlns:s11=\"http://ws.apache.org/saaj\"><message>ERROR:null request received anyinvoke SAAJ</message></s11:provider>";

    //public void receive(MessageContext reqMsgContext) throws AxisFault{
    //    System.out.println( "*** in SAAJ StringProvider receive()" );
    //}
    @WebMethod
    public DOMSource invoke(DOMSource s) 
    {
        if(s != null)
        {
            String s1 = XMLUtils.getContentFromDOMSource(s);
            System.out.println(">> StringProvider invoke() received a new request");
            System.out.println((new StringBuilder()).append(">> request [").append(s1).append("]").toString());
            return XMLUtils.createDOMSourceFromString(responseGood);
        } else
        {
            System.out.println(">> ERROR:null request received");
            return XMLUtils.createDOMSourceFromString(responseBad);
        }
    }

    //public void receive(MessageContext reqMsgContext) throws AxisFault{
    //    System.out.println( "*** in SAAJ StringProvider receive()" );
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
