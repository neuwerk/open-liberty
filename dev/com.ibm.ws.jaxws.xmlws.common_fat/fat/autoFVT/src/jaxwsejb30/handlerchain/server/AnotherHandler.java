package jaxwsejb30.handlerchain.server;

public class AnotherHandler extends WASSOAPHandler {
    public AnotherHandler(){
        super();
        super.setParams("AnotherHandler", "Server");
    }

}
