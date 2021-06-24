package common.utils.topology;

import java.util.Set;

import com.ibm.websphere.simplicity.product.InstalledWASProduct;
import com.ibm.websphere.simplicity.product.WASInstallation;
import com.ibm.websphere.simplicity.product.InstalledWASProduct.WASProductID;

import common.utils.topology.WASProduct.ProductID;

/**
 * The WAS installation
 * 
 * @see com.ibm.websphere.simplicity.Node#getWASInstall()
 * @see com.ibm.websphere.simplicity.product.WASInstallation
 */
public class IWASInstall extends IInstall {

	/**
	 * Constructs an ACUTE IWASInstall from a Simplicity WASInstallation.
	 * 
	 * @param wasInstall
	 */
	public IWASInstall(WASInstallation wasInstall) {
		super(wasInstall);
	}
	
	public WASProduct getWASProduct(ProductID id) {
	    try {
	        Set<InstalledWASProduct> products = ((WASInstallation)this.install).getWASProducts();
	        for(InstalledWASProduct product : products) {
	            if((ProductID.BASE.equals(id) && product.getProductId().equals(WASProductID.BASE))
	                || (ProductID.ND.equals(id) && product.getProductId().equals(WASProductID.ND))
	                || (ProductID.WEBSERVICES.equals(id) && product.getProductId().equals(WASProductID.WEBSERVICES)))
	                return new WASProduct(product);
	        }
	    } catch(Exception e) {
	        throw new RuntimeException(e);
	    }
	    return null;
	}
}
