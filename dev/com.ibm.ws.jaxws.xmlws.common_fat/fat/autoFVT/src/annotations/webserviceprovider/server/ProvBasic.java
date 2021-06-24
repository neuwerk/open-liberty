/*
 * @(#) 1.2 autoFVT/src/annotations/webserviceprovider/server/ProvBasic.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 1/5/07 14:53:53 [8/8/12 06:55:34]
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

// other imp. can't deploy without wsdlloc defined.
@WebServiceProvider(wsdlLocation="WEB-INF/wsdl/ProvService.wsdl")
@ServiceMode(value=Service.Mode.MESSAGE)
public class ProvBasic implements Provider<SOAPMessage>{

      public ProvBasic(){}

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

