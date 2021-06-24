package common.utils.topology;

import com.ibm.websphere.simplicity.product.InstalledWASProduct;

public class WASProduct {
    
    private InstalledWASProduct product;
    
    public WASProduct(InstalledWASProduct product) {
        this.product = product;
    }
    
    public String getVersion() {
        return this.product.getVersion().toString();
    }

    public enum ProductID {
        BASE,
        ND,
        WEBSERVICES;
    }
}
