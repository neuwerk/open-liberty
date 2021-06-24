package annotations.webresult.server;
import javax.jws.*;

// semantically incorrect, should be caught by tooling 
@WebService()
public class WRonVoid {
    @WebResult(partName="fred")
    public void zippo(String s){ }
}
