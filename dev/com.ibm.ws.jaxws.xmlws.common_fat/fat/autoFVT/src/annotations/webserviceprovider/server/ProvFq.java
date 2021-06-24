/*
 * @(#) 1.2 autoFVT/src/annotations/webserviceprovider/server/ProvFq.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 1/5/07 14:54:01 [8/8/12 06:55:34]
 *
 *
 * IBM Confidential OCO Source Material
 * 5724-J08 (C) COPYRIGHT International Business Machines Corp. 2003, 2005
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 *  09/01/06   btiffany    LIDB3296.31.01     new file
 *
 */

/**
 * a basic provider service.  This will receive the soap message
 * from any client, write it to systemout, and return it. 
 */
package annotations.webserviceprovider.server;

import javax.jws.*;
import javax.xml.ws.*;
import javax.xml.soap.*;
import javax.xml.transform.dom.DOMSource;

// provFqService.wsdl is based off this, keep them in sync.
@WebServiceProvider(wsdlLocation="WEB-INF/wsdl/ProvFqService.wsdl",
                    serviceName="ProvFqService",
                    portName = "FqPort",                    
                    targetNamespace = "annotations.webserviceprovider.fq"
                        )
@ServiceMode(value=Service.Mode.PAYLOAD)
public class ProvFq implements Provider<DOMSource>{  

      public ProvFq(){}
      
      // for debug
      public static void main (String [] args){
    	  DOMSource result = new ProvFq().invoke(XMLUtils.createDOMSourceFromString("<echo><arg0>calling Mr. echo</arg0></echo>"));
          System.out.println("result="+XMLUtils.getContentFromDOMSource(result));
          
      }

      public DOMSource invoke(DOMSource request){

       //inbound <ns2:echo xmlns:ns2="annotations.webserviceprovider.fq"><arg0>calling Mr. echo</arg0></ns2:echo>
       // outbound: <ns2:echoResponse xmlns:ns2="annotations.webserviceprovider.fq"><return>calling Mr. echo</return></ns2:echoResponse>
          
          // manually extract here...
    	  String requestStr = XMLUtils.getContentFromDOMSource(request);
          String body=requestStr.substring(requestStr.indexOf("0>")+2);
          body= body.substring(0, body.indexOf("</ar"));

          System.out.println("ProvFq received:"+requestStr);
          System.out.println("ProvFq received body:"+body);        
          // need to put a namespace in these. 
          String prologue = "<ns2:echoResponse xmlns:ns2=\"annotations.webserviceprovider.fq\"><return>";
          String epilogue = "</return></ns2:echoResponse>"; 
          String resp = prologue + "ProvFq received:"+body + epilogue;                        
          
          System.out.println("ProvFq response=" +resp);
                       
          return XMLUtils.createDOMSourceFromString(resp);
      }
}

