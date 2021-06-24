package common.utils.topology;

import java.io.File;

import com.ibm.websphere.simplicity.Machine;
import com.ibm.websphere.simplicity.Topology;

import com.ibm.websphere.simplicity.OperatingSystem;

/**
 * The ACUTE Machine wrapper for Simplicity. Stores machine specific info.
 * 
 * @see com.ibm.websphere.simplicity.Machine
 */
public class IMachine {

	protected Machine machine;
	protected OperatingSystem os;

	/**
	 * Constructs an ACUTE machine from a Simplicity machine.
	 * 
	 * @param machine
	 */
	public IMachine(Machine machine) {
		this.machine = machine;
		try {
			this.os = this.machine.getOperatingSystem();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the default executable file script suffix.
	 * 
	 * @return the executable file suffix
	 * @see com.ibm.websphere.simplicity.Machine#getOperatingSystem()
	 * @see com.ibm.websphere.simplicity.OperatingSystem#getDefaultScriptSuffix()
	 */
	public String getExecFileSuffix() {
		return this.os.getDefaultScriptSuffix();
	}

	/**
	 * Returns the default file separator for this machine.
	 * 
	 * @return the default file separator
	 * @see com.ibm.websphere.simplicity.Machine#getOperatingSystem()
	 * @see com.ibm.websphere.simplicity.OperatingSystem#getFileSeparator()
	 */
	public String getFileSeparator() {
		return this.os.getFileSeparator();
	}

	/**
	 * Returns the hostname for this machine.
	 * 
	 * @return the hostname
	 * @see com.ibm.websphere.simplicity.Machine#getHostname()
	 */
	public String getHostname() {
		return this.machine.getHostname();
	}

	/**
	 * Returns the key in the bootstrapping.properties for this machine.
	 * 
	 * @return the key for this machine
	 * @see com.ibm.websphere.simplicity.Machine#getBootstrapFileKey()
	 */
	public String getKey() {
		return this.machine.getBootstrapFileKey();
	}

	/**
	 * Returns the default machine line separator.
	 * 
	 * @return the default line separator
	 * @see com.ibm.websphere.simplicity.Machine#getOperatingSystem()
	 * @see com.ibm.websphere.simplicity.OperatingSystem#getLineSeparator()
	 */
	public String getLineSeparator() {
		return this.os.getLineSeparator();
	}

	/**
	 * Returns the ACUTE OperatingSystem enum for this machine.
	 * 
	 * @return the ACUTE OperatingSystem enum
	 */
	public OperatingSystem getOperatingSystem() {
		return OperatingSystem.valueOf(this.os.toString());
	}

	/**
	 * Returns the default machine line separator.
	 * 
	 * @return the default line separator
	 * @see com.ibm.websphere.simplicity.Machine#getOperatingSystem()
	 * @see com.ibm.websphere.simplicity.OperatingSystem#getLineSeparator()
	 */
	public String getOsEncoding() {
		return this.os.getDefaultEncoding();
	}

	/**
	 * Returns the path separator for this machine.
	 * 
	 * @return the path separator
	 * @see com.ibm.websphere.simplicity.Machine#getOperatingSystem()
	 * @see com.ibm.websphere.simplicity.OperatingSystem#getPathSeparator()
	 */
	public String getPathSeparator() {
		return this.os.getPathSeparator();
	}

	/**
	 * Returns the bootstrapping.properties file.
	 * 
	 * @return the bootstrapping.properties file.
	 */
	public File getTopologyFile() {
		return Topology.getBootStrappingFile();
	}
}
