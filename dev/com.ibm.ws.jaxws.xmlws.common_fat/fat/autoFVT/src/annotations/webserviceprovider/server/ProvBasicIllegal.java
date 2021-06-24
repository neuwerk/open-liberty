/*
 * @(#) 1.2 autoFVT/src/annotations/webserviceprovider/server/ProvBasicIllegal.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 1/5/07 14:53:57 [8/8/12 06:55:34]
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
 *  09/19/06   btiffany    LIDB3296.31.01     new file
 *
 */

/**
 * a basic provider service.  This will receive the soap message
 * from any client, write it to systemout, and return it. 
 * This one contains an illegal webservice anno and should not deploy
 */
package annotations.webserviceprovider.server;

import javax.jws.*;
import javax.xml.ws.*;
import javax.xml.soap.*;

// illegal because has both @WebService and @WebServiceProvider on it.
@WebService(wsdlLocation="WEB-INF/wsdl/ProvService.wsdl",
                    serviceName="ProvBasicService",
                    portName="ProvBasicPort")
@WebServiceProvider(wsdlLocation="WEB-INF/wsdl/ProvService.wsdl",
                    serviceName="ProvBasicService",
                    portName="ProvBasicPort")
@ServiceMode(value=Service.Mode.MESSAGE)
public class ProvBasicIllegal implements Provider<SOAPMessage>{

      @WebMethod
      public String echo(String s){return "this never should have deployed";}

      public SOAPMessage invoke(SOAPMessage request){
          try{             
             request.writeTo(System.out);
          }
          catch (Exception e){
              e.printStackTrace();
          }
          return request;
      }
}

