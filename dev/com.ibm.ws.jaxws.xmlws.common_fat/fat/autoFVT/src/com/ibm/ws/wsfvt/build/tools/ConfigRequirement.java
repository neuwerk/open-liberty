//
// @(#) 1.9 autoFVT/src/com/ibm/ws/wsfvt/build/tools/ConfigRequirement.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/7/08 11:07:14 [8/8/12 06:40:28]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
//  Date        Author       Feature/Defect       Description
//  03/23/07    jramos       428286               New File
//  04/16/07    jramos       432761               Add requireNDProduct and requireBaseTopology
//  04/16/07    sedov        432313               Added new requirements
//  04/19/07    sedov        433386               Fixed NPE in checking cluster requirement
//  05/24/07    jramos       440922               Changes for Pyxis
//  10/22/2007  jramos       476750               Use TopologyDefaults tool and ACUTE 2.0 api
//  10/26/2007  sedov        476785               Added AdminAgent, JobManager and Non-Base toology requirements
//  11/14/2007  jramos       483501               Use Requirement Objects

package com.ibm.ws.wsfvt.build.tools;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ibm.websphere.simplicity.Cell;
import com.ibm.ws.wsfvt.build.tools.configRequirements.AdminAgentTopologyReq;
import com.ibm.ws.wsfvt.build.tools.configRequirements.AppInstalledReq;
import com.ibm.ws.wsfvt.build.tools.configRequirements.BaseTopologyReq;
import com.ibm.ws.wsfvt.build.tools.configRequirements.ClusterReq;
import com.ibm.ws.wsfvt.build.tools.configRequirements.DistributedReq;
import com.ibm.ws.wsfvt.build.tools.configRequirements.JobManagerTopologyReq;
import com.ibm.ws.wsfvt.build.tools.configRequirements.MixedCellReq;
import com.ibm.ws.wsfvt.build.tools.configRequirements.NDProductReq;
import com.ibm.ws.wsfvt.build.tools.configRequirements.NDTopologyReq;
import com.ibm.ws.wsfvt.build.tools.configRequirements.NonBaseTopologyReq;
import com.ibm.ws.wsfvt.build.tools.configRequirements.PmiPlatformReq;
import com.ibm.ws.wsfvt.build.tools.configRequirements.Requirement;
import com.ibm.ws.wsfvt.build.tools.configRequirements.SecurityReq;
import com.ibm.ws.wsfvt.build.tools.utils.LogNoExecution;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

public class ConfigRequirement {

	private Cell myCell = null;
    private Set<Requirement> requirements = new HashSet<Requirement>();
    
    private NDTopologyReq ndTopoReq = null;

	private BaseTopologyReq baseTopoReq = null;

	private AdminAgentTopologyReq adminAgentTopoReq = null;
	
	private JobManagerTopologyReq jobManagerTopoReq = null;
	
	private ClusterReq clusterReq = null;

	private MixedCellReq mixedCellReq = null;

	private DistributedReq distReq = null;

	private NDProductReq ndProdReq = null;

	private SecurityReq securityReq = null;

	private AppInstalledReq appInstalledReq = null;

	private PmiPlatformReq pmiReq = null;

	private NonBaseTopologyReq nonBaseReq = null;

	public ConfigRequirement() throws Exception {
		this(TopologyDefaults.getDefaultAppServer().getCell());
	}

	public ConfigRequirement(Cell cell) {
		myCell = cell;
	}

	/**
	 * Log to the SystemOut the requirement that was added
	 * 
	 * @param requirement
	 * @param config
	 */
	private void logRequirementAdded(String requirement, Object config) {
		System.out.println(this.getClass().getSimpleName()
				+ ".executionConstraint [" + requirement + " ("
				+ (config == null ? "" : config.toString()) + ")]");
	}

	/**
	 * Log to the SystemOut the requirement was not satisfied
	 * 
	 * @param string
	 */
	private void logRequirementNotSatisfied(String requirement) {
		System.out.println(this.getClass().getSimpleName()
				+ ".meetsRequirements RequirementNotSatisfied [" + requirement
				+ "]");

	}

    @Deprecated
	public boolean clusterRequired() {
		if(this.clusterReq != null) {
		    return this.clusterReq.getReqVal();
        }
        return false;
	}

    @Deprecated
	public void setRequireCluster(boolean requireCluster) {
        this.clusterReq = new ClusterReq(this.myCell);
        this.clusterReq.setRequirement(requireCluster);
        addRequirement(this.clusterReq);
	}

    @Deprecated
	public boolean distributedRequired() {
        if(this.distReq != null) {
            return this.distReq.getReqVal();
        }
        return false;
	}

    @Deprecated
	public void setRequireDistributed(boolean requireDistributed) {
        this.distReq = new DistributedReq(this.myCell);
        this.distReq.setRequirement(requireDistributed);
        addRequirement(this.distReq);
	}

    @Deprecated
	public boolean mixedCellRequired() {
        if(this.mixedCellReq != null) {
            Map<String, Boolean> vals = this.mixedCellReq.getReqVal();
            boolean returnVal = true;
            for(String req : vals.keySet()) {
                returnVal = returnVal && vals.get(req).booleanValue();
            }
            return returnVal;
        }
        return false;
	}

    @Deprecated
	public void setRequireMixedCell(boolean requireMixedCell) {
        this.mixedCellReq = new MixedCellReq(this.myCell);
        this.mixedCellReq.setReqMixedCell(requireMixedCell);
        addRequirement(this.mixedCellReq);
	}
	
    @Deprecated
	public void setRequireAdminAgent(boolean requireAdminAgent) {
        this.adminAgentTopoReq = new AdminAgentTopologyReq(this.myCell);
        this.adminAgentTopoReq.setRequirement(requireAdminAgent);
		addRequirement(this.adminAgentTopoReq);
	}
	
    @Deprecated
	public void setRequireJobManager(boolean requireJobManager) {
        this.jobManagerTopoReq = new JobManagerTopologyReq(this.myCell);
        this.jobManagerTopoReq.setRequirement(requireJobManager);
		addRequirement(this.jobManagerTopoReq);
	}

    @Deprecated
	public boolean NDTopoRequired() {
		if(this.ndTopoReq != null) {
		    return this.ndTopoReq.getReqVal();
        }
        return false;
	}

    @Deprecated
	public void setRequireND(boolean requireND) {
        this.ndTopoReq = new NDTopologyReq(this.myCell);
        this.ndTopoReq.setRequirement(requireND);
		addRequirement(this.ndTopoReq);
	}

	/**
	 * Require that a topology be either ND, MixedCell, or AdminAgent. Simple
	 * base topology will be excluded
	 * 
	 * @param requireNonBase
	 */
    @Deprecated
	public void setRequireNonBaseTopology(boolean requireNonBase) {
		this.nonBaseReq = new NonBaseTopologyReq(this.myCell);
        this.nonBaseReq.setRequirement(requireNonBase);
        addRequirement(this.nonBaseReq);
	}

    @Deprecated
	public boolean NDProductRequired() {
        if(this.ndProdReq != null) {
            return this.ndProdReq.getReqVal();
        }
        return false;
	}

    @Deprecated
	public void setRequireNDProduct(boolean requireNDProduct) throws Exception {
        this.ndProdReq = new NDProductReq(this.myCell.getManager().getNode().getWASInstall());
        this.ndProdReq.setRequirement(requireNDProduct);
        addRequirement(this.ndProdReq);
	}

    @Deprecated
	public boolean BaseTopoRequired() {
		if(this.baseTopoReq != null) {
		    return this.baseTopoReq.getReqVal();
        }
        return false;
	}

    @Deprecated
	public void setRequireBaseTopo(boolean requireBaseTopo) {
        this.baseTopoReq = new BaseTopologyReq(this.myCell);
        this.baseTopoReq.setRequirement(requireBaseTopo);
		addRequirement(this.baseTopoReq);
	}

    @Deprecated
	public void setRequireWebSphereSecurity(boolean requireSecurity,
			boolean securityEnabled) {
		this.securityReq = new SecurityReq(this.myCell);
        this.securityReq.setReqSecurityEnabled(securityEnabled);
        addRequirement(this.securityReq);
	}

    @Deprecated
	public void setRequireAppInstalled(String appName) {
		if(this.appInstalledReq == null) {
		    this.appInstalledReq = new AppInstalledReq(this.myCell);
        }
        this.appInstalledReq.addAppInstallReq(appName);
        addRequirement(this.appInstalledReq);
	}

    @Deprecated
	public void setRequireJvmPmiCapablePlatform(boolean requireJvmPmi) {
		this.pmiReq = new PmiPlatformReq(this.myCell);
        this.pmiReq.setRequirement(requireJvmPmi);
        addRequirement(this.pmiReq);
	}

	/**
	 * Check if the set configuration requirements are met
	 * 
	 * @return true if the current configuration meets the set requirements
	 */
	public boolean meetsRequirements() throws Exception {
		boolean requirementsMet = true;
        
        for(Requirement req : this.requirements) {
            if(!req.requirementMet()) {
                logRequirementNotSatisfied(req.getRequirementDescr());
                requirementsMet = false;
            }
        }
        
        return requirementsMet;
	}

	/**
	 * Log a test package that is not being executed.
	 * 
	 * @param packageName
	 *            The test package that shows on the JUnit report
	 * @param message
	 *            A message to log
	 */
	public void logPackage(String packageName, String message) {
		LogNoExecution.logPackage(packageName, message);
	}

	/**
	 * Log a test case that is not being executed.
	 * 
	 * @param packageName
	 *            The test package that shows on the JUnit report
	 * @param suiteName
	 *            The test suite name that shows on the JUnit report
	 * @param message
	 *            A message to log
	 */
	public void logTestSuite(String packageName, String suiteName,
			String message) {
		LogNoExecution.logTestSuite(packageName, suiteName, message);
	}

	/**
	 * Log a test case that is not being executed.
	 * 
	 * @param packageName
	 *            The test package that shows on the JUnit report
	 * @param suiteName
	 *            The test suite name that shows on the JUnit report
	 * @param testCase
	 *            The test case name that shows on the JUnit report
	 * @param message
	 *            A message to log
	 */
	public void logTestCase(String packageName, String suiteName,
			String testCase, String message) {
		LogNoExecution.logTestCase(packageName, suiteName, testCase, message);
	}

    /**
     * Add a requirement that must be met
     * 
     * @param requirement The requirement to meet
     */
    public void addRequirement(Requirement requirement) {
        this.requirements.add(requirement);
        logRequirementAdded(requirement.getRequirementDescr(), requirement.getReqVal());
    }
}
