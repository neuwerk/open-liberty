/*
 * @(#) 1.5 autoFVT/src/annotations/webfault/checkexception/server/AddNegativeNumbersException.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 2/5/07 10:20:32 [8/8/12 06:55:13]
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
 * 06/26/2006  euzunca     LIDB3296.31.01     new file
 * 07/01/2007  btiffany                       get rid of inheritance
 */
package annotations.webfault.checkexception.server;

import javax.xml.ws.WebFault;

//@WebFault()
public class AddNegativeNumbersException  extends Exception {
    public AddNegativeNumbersException( String s){
        super(s);
    }

}
