package soapendpoint.wsfvt.test;

import com.ibm.websphere.simplicity.WebSphereVersion;
import com.ibm.websphere.simplicity.product.InstalledWASProduct.WASProductID;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

/**
 * Some basic utility methods for this test package.
 */
public class TestUtils {

	/**
	 * Tests that all prereqs are met.
	 * 
	 * @return true if prereqs are met, false otherwise
	 * @throws Exception
	 */
	public static boolean isPrereqMet() {
		boolean isCorrectVersion = true;
		try {
			isCorrectVersion = (TopologyDefaults.getDefaultAppServer()
					.getNode().getWASInstall().getWASProduct(WASProductID.ND) != null) ? TopologyDefaults
					.getDefaultAppServer().getNode().getWASInstall()
					.getWASProduct(WASProductID.ND).getVersion().isHigherThan(
							new WebSphereVersion("7.0.0.3"))
					: TopologyDefaults.getDefaultAppServer().getNode()
							.getWASInstall().getWASProduct(WASProductID.BASE)
							.getVersion().isHigherThan(
									new WebSphereVersion("7.0.0.3"));
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
		if (isCorrectVersion) {
			System.out.println("met fix pack requirement");
			return true;
		}
		System.out.println("did not meet fix pack requirement");
		return false;
	}

}
