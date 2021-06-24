package common.utils.topology;

import com.ibm.websphere.simplicity.product.Installation;

/**
 * Determines the install properties for a product.
 * 
 * @see com.ibm.websphere.simplicity.product.WASInstallation
 */
public abstract class IInstall {

	protected Installation install;
	protected IMachine machine;

	/**
	 * Constructs an install from a Simplicity Installation.
	 * 
	 * @param install
	 *            the Simplicity installation info
	 */
	public IInstall(Installation install) {
		this.install = install;
		this.machine = new IMachine(install.getMachine());
	}

	/**
	 * Returns the install root of the product.
	 * 
	 * @return the install root
	 * @see com.ibm.websphere.simplicity.Node#getWASInstall()
	 * @see com.ibm.websphere.simplicity.product.WASInstallation#getInstallRoot()
	 */
	public String getInstallRoot() {
		return this.install.getInstallRoot();
	}

	/**
	 * Returns an ACUTE IMachine for an installation.
	 * 
	 * @return the ACUTE IMachine where the product is installed to
	 */
	public IMachine getMachine() {
		return this.machine;
	}
}
