package common.utils.topology;

import com.ibm.websphere.simplicity.Cluster;

public class ICluster {
    
    protected Cell cell;
    protected Cluster cluster;

    public ICluster(Cell cell, Cluster cluster) {
        this.cell = cell;
        this.cluster = cluster;
    }
}
