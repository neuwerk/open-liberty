//
// @(#) 1.1 autoFVT/src/com/ibm/ws/wsfvt/build/tools/configRequirements/Requirement.java, WAS.websvcs.fvt, WASX.FVT, o0841.14 11/14/07 13:08:34 [10/16/08 22:12:36]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// ----------------------------------------------------------------------------
// 11/14/07 jramos      483501          New File
//

package com.ibm.ws.wsfvt.build.tools.configRequirements;

/**
 * A requirement that must be met for a test case to execute.
 * 
 * @author jramos
 */
public interface Requirement<T> {
    
    public boolean requirementMet() throws Exception;
    
    public T getReqVal();
    
    public String getRequirementDescr();
    
}
