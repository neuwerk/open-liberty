package common.utils.topology;

public enum NodeContainerType {

    DEPLOYMENT_MANAGER("DEPLOYMENT_MANAGER"),
    MANAGED("MANAGED"),
    BASE("BASE"),
    JOB_MANAGER("JOB_MANAGER"),
    ADMIN_AGENT("ADMIN_AGENT");
    
    private final String nodeContainerType;
    private NodeContainerType(String nodeContainerType) {
        this.nodeContainerType = nodeContainerType;
    }

    /**
     * Returns the node container type
     * 
     * @return The node container type
     */
    public String getNodeContainerType() {
        return this.nodeContainerType;
    }
}
