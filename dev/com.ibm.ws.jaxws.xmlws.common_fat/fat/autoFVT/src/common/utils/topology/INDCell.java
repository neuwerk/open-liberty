package common.utils.topology;

import java.util.HashSet;
import java.util.Set;

import com.ibm.websphere.simplicity.Cluster;
import com.ibm.websphere.simplicity.Node;
import com.ibm.websphere.simplicity.WebSphereVersion;
import com.ibm.websphere.simplicity.product.InstalledWASProduct;

public class INDCell extends Cell {

    public INDCell(com.ibm.websphere.simplicity.Cell cell) {
        super(cell);
    }
    
    public Set<ICluster> getClusters() {
        try {
            Set<Cluster> clusters = this.cell.getClusters();
            Set<ICluster> ret = new HashSet<ICluster>();
            for(Cluster cluster : clusters) {
                ret.add(new ICluster(this, cluster));
            }
            return ret;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public IDmgrNodeContainer getDeploymentManager() {
        return (IDmgrNodeContainer)getRootNodeContainer();
    }
    
    public Set<IManagedNodeContainer> getManagedNodes() {
        try {
            Set<Node> nodes = this.cell.getNodes();
            Set<IManagedNodeContainer> ret = new HashSet<IManagedNodeContainer>();
            for(Node node : nodes) {
                ret.add(new IManagedNodeContainer(this, node));
            }
            return ret;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public boolean isTopologyMixedCell() {
        try {
            WebSphereVersion baseVersion = this.cell.getManager().getNode().getBaseProductVersion();
            Set<InstalledWASProduct> baseProducts = this.cell.getManager().getNode().getInstalledWASProducts();
            WebSphereVersion version = null;
            Set<Node> nodes = this.cell.getNodes();
            Set<InstalledWASProduct> products = null;
            for(Node node : nodes) {
                version = node.getBaseProductVersion();
                if(!version.equals(baseVersion)) {
                    return true;
                }
                products = node.getInstalledWASProducts();
                if(products.size() != baseProducts.size()) {
                    return true;
                }
            }
            return false;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}
