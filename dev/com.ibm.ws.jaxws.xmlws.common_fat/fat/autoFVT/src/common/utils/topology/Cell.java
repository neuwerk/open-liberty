package common.utils.topology;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import com.ibm.websphere.simplicity.Node;
import com.ibm.websphere.simplicity.WebSphereTopologyType;

/**
 * ACUTE Cell.
 * 
 * @see com.ibm.websphere.simplicity.Cell
 */
public abstract class Cell {

	protected com.ibm.websphere.simplicity.Cell cell;

	/**
	 * Constructs an ACUTE Cell from a Simplicity Cell.
	 * 
	 * @param cell
	 *            a Simplicity cell to wrap ACUTE cell methods around.
	 */
	public Cell(com.ibm.websphere.simplicity.Cell cell) {
		this.cell = cell;
	}

	/**
	 * Return the cell name.
	 * 
	 * @return the cell name
	 * @see com.ibm.websphere.simplicity.Cell#getName()
	 */
	public String getCellName() {
		return this.cell.getName();
	}

	/**
	 * Returns the node containers from the new cell.
	 * 
	 * @return a set of nodes
	 * @see com.ibm.websphere.simplicity.Cell#getNodes()
	 */
	public Set<INodeContainer> getNodeContainers() {
		try {
			Set<Node> nodes = this.cell.getNodes();
			Set<INodeContainer> ret = new HashSet<INodeContainer>();
			for (Node node : nodes) {
				ret.add(getNodeContainer(node));
			}
			return ret;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the root (base app server or deployment manager) node. In
	 * Simplicity, this is <code>Cell.getManager().getNode()</code>.
	 * 
	 * @return the manager node
	 * @see com.ibm.websphere.simplicity.Cell#getManager()
	 */
	public INodeContainer getRootNodeContainer() {
		try {
			return getNodeContainer(this.cell.getManager().getNode());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the root node hostname. In Simplicity, this is
	 * <code>Cell.getManager().getNode().getMachine().getHostname()</code>
	 * 
	 * @return the root host name
	 * @see com.ibm.websphere.simplicity.Cell#getManager()
	 * @see com.ibm.websphere.simplicity.ApplicationServer#getNode()
	 * @see com.ibm.websphere.simplicity.Node#getMachine()
	 * @see com.ibm.websphere.simplicity.Machine#getHostname()
	 */
	public String getRootNodeHostname() {
		try {
			return this.cell.getManager().getNode().getMachine().getHostname();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the root node profile path. In Simplicity, this is
	 * <code>Cell.getManager().getNode().getProfileDir()</code>
	 * 
	 * @return the root host name
	 * @see com.ibm.websphere.simplicity.Cell#getManager()
	 * @see com.ibm.websphere.simplicity.ApplicationServer#getNode()
	 * @see com.ibm.websphere.simplicity.Node#getProfileDir()
	 */
	public String getRootNodeProfilePath() {
		try {
			return this.cell.getManager().getNode().getProfileDir();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the Topology that you can compare to.
	 * 
	 * @return the topology enum
	 * @see com.ibm.websphere.simplicity.Topology
	 */
	public Topology getTopology() {
		try {
			WebSphereTopologyType topology = this.cell.getTopologyType();
			if (topology == WebSphereTopologyType.ADMIN_AGENT) {
				return Topology.ADMIN_AGENT;
			} else if (topology == WebSphereTopologyType.BASE) {
				return Topology.BASE;
			} else if (topology == WebSphereTopologyType.FLEX) {
				return Topology.JOB_MANAGER;
			} else if (topology == WebSphereTopologyType.ND) {
				return Topology.ND;
			} else {
				return Topology.MANAGED;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Determines if the topology is base.
	 * 
	 * @return true if this is a base topology, false otherwise
	 */
	public boolean isTopologyBase() {
		return getTopology() == Topology.BASE;
	}

	/**
	 * Determines if the topology is ND
	 * 
	 * @return true if this is a ND topology, false otherwise
	 */
	public boolean isTopologyND() {
		return getTopology() == Topology.ND;
	}

	/**
	 * Returns the WAS password. In Simplicity, this is
	 * <code>Cell.getConnInfo().getPassword()</code>.
	 * 
	 * @return the WAS password
	 * @see com.ibm.websphere.simplicity.Cell#getConnInfo()
	 * @see com.ibm.websphere.simplicity.ConnectionInfo#getPassword()
	 */
	public String getWasPassword() {
		return this.cell.getConnInfo().getPassword();
	}

	/**
	 * Returns the WAS password. In Simplicity, this is
	 * <code>Cell.getConnInfo().getUser()</code>.
	 * 
	 * @return the WAS user name
	 * @see com.ibm.websphere.simplicity.Cell#getConnInfo()
	 * @see com.ibm.websphere.simplicity.ConnectionInfo#getUser()
	 */
	public String getWasUserName() {
		return this.cell.getConnInfo().getUser();
	}

	/**
	 * Determines if security is enabled. In Simplicity, this is
	 * <code>Cell.getSecurityConfiguration().getGlobalSecurityDomain().isGlobalSecurityEnabled()</code>.
	 * 
	 * @return true if administrative global security is enabled, false
	 *         otherwise.
	 */
	public boolean isSecurityEnabled() {
		try {
			return this.cell.getSecurityConfiguration()
					.getGlobalSecurityDomain().isGlobalSecurityEnabled();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the Simplicity cell that this ACUTE cell is wrapped around.
	 * 
	 * @return
	 */
	public com.ibm.websphere.simplicity.Cell getSimplicityCell() {
		return this.cell;
	}

	/**
	 * Returns the bootstrapping.properties File object. Note that this is not
	 * the same as the ACUTE topologyProps.props so if you use this method, make
	 * sure you are getting the right information.
	 * 
	 * @deprecated
	 * @return the bootstrapping.properties file
	 */
	@Deprecated
	public File getTopologyFile() {
		return com.ibm.websphere.simplicity.Topology.getBootStrappingFile();
	}

	/**
	 * Returns the bootstrapping.properties cell key for this cell.
	 * 
	 * @return the cell key prefix in the config file
	 */
	public String getKey() {
		return this.cell.getBootstrapFileKey();
	}

	/**
	 * Returns the ACUTE node container for the given node.
	 * 
	 * @param node
	 *            the node to get the container for
	 * @return the node container
	 * @throws Exception
	 *             any exception
	 */
	protected INodeContainer getNodeContainer(Node node) throws Exception {
		WebSphereTopologyType topology = this.cell.getTopologyType();
		if (topology == WebSphereTopologyType.ADMIN_AGENT) {
			return new IAdminAgentNodeContainer(this, node);
		} else if (topology == WebSphereTopologyType.BASE) {
			return new IBaseNodeContainer(this, node);
		} else if (topology == WebSphereTopologyType.FLEX) {
			// return Topology.JOB_MANAGER;
			// TODO
			return null;
		} else if (topology == WebSphereTopologyType.ND
				&& node == node.getCell().getManager().getNode()) {
			return new IDmgrNodeContainer(this, node);
		} else {
			return new IManagedNodeContainer(this, node);
		}
	}
}
