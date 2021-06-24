//
// @(#) 1.1 autoFVT/src/jaxws/defaultpackage/wsfvt/server/EchoString.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 6/29/10 16:14:26 [8/8/12 06:58:49]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date       UserId      Defect          Description
// ----------------------------------------------------------------------------
// 06/28/2010 jtnguyen    658405          New File

package jaxws.defaultpackage.wsfvt.server;

//import javax.xml.ws.*;
import javax.jws.*;
import javax.jws.soap.SOAPBinding;

/**
 * Use Doc/Lit BARE service to test jaxws 2.2 (spec 3.6 Conformance "Overriding JAXB types empty namespace: JAX-WS tools and runtimes MUST override
 *  the default empty namespace for JAXB types and elements to SEI's targetNamespace.")
 *
 */
@WebService
public class EchoString {

        @WebResult(name = "echoResponse", targetNamespace = "http://server.wsfvt.defaultpackage.jaxws/", partName = "echoResponse")

        @SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL,
                          parameterStyle=SOAPBinding.ParameterStyle.BARE)
        public String echo(String parm){
                return (parm);
        }

}
