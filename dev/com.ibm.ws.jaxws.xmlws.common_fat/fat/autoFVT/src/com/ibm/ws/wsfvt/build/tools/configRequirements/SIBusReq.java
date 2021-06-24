//
// @(#) 1.3 autoFVT/src/com/ibm/ws/wsfvt/build/tools/configRequirements/SIBusReq.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/7/08 11:07:52 [8/8/12 06:57:20]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
//  Date        Author       Feature/Defect       Description
//  10/31/08    jramos       559143               Incorporate Simplicity
//

package com.ibm.ws.wsfvt.build.tools.configRequirements;

import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.RemoteFile;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

/**
 * Requirement for a specific SIBus to be defined
 *
 */
public class SIBusReq implements Requirement<String> {

	private String siBusName = null;
	
	public SIBusReq(String siBusName){
		this.siBusName = siBusName;
	}
	
	public String getReqVal() {
		return null;
	}

	public String getRequirementDescr() {
		return "SIBus " + siBusName + " exists";
	}

	public boolean requirementMet() throws Exception {
		
		Cell dCell = TopologyDefaults.getDefaultAppServer().getCell();
		String sep = dCell.getManager().getNode().getMachine().getOperatingSystem().getFileSeparator();
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(dCell.getManager().getNode().getProfileDir());
		buffer.append(sep);
		buffer.append("config");
		buffer.append(sep);
		buffer.append("cells");
		buffer.append(sep);
		buffer.append(dCell.getName());
		buffer.append(sep);
		buffer.append("buses");
		buffer.append(sep);	
		buffer.append(siBusName);
		
		 
		System.out.println("SIBusReq checking" + buffer.toString());
		
		try {
            RemoteFile f = dCell.getManager().getNode().getMachine().getFile(buffer.toString());
            RemoteFile[] files = f.list(false);
			// iterate over all files in si-bus directory and
			// look for si-bus.xml config file
			for (RemoteFile s: files){
				//System.out.println("file: " + s);
				
				if ("sib-bus.xml".equals(s.getName())){
					return true;
				}
			}
		
		} catch (Exception e){
			System.out.println("SIBusReq.requirementMet() error: " + e);
		}
		
		return false;
	}

}
