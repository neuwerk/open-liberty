package common.utils.topology;

public class IBaseCell extends Cell {

    public IBaseCell(com.ibm.websphere.simplicity.Cell cell) {
        super(cell);
    }
    
    public IBaseNodeContainer getBaseNode() {
        return (IBaseNodeContainer)getRootNodeContainer();
    }
}
