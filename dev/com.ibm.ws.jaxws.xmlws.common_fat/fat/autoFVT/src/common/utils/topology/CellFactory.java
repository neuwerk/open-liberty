package common.utils.topology;

import com.ibm.websphere.simplicity.WebSphereTopologyType;

public class CellFactory {

    public static Cell getCell(com.ibm.websphere.simplicity.Cell cell) {
        try {
            if(cell.getTopologyType() == WebSphereTopologyType.BASE) {
                return new IBaseCell(cell);
            } else if(cell.getTopologyType() == WebSphereTopologyType.ND) {
                return new INDCell(cell);
            } else if(cell.getTopologyType() == WebSphereTopologyType.ADMIN_AGENT) {
                return new IAdminAgentCell(cell);
            } else {
                return null;
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}
