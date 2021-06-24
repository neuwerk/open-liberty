package common.utils.topology;

public class IAdminAgentCell extends Cell {

    public IAdminAgentCell(com.ibm.websphere.simplicity.Cell cell) {
        super(cell);
    }
    
    public IAdminAgentNodeContainer getAdminAgentNodeContainer() {
        return (IAdminAgentNodeContainer)getRootNodeContainer();
    }
}
