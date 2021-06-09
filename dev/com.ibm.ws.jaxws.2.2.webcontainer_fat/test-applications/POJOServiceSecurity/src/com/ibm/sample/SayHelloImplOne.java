package com.ibm.sample;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

@WebService(serviceName = "SayHelloServiceOne",
            endpointInterface = "com.ibm.sample.SayHelloInterface",
            targetNamespace = "http://jaxws2.samples.ibm.com")
public class SayHelloImplOne implements SayHelloInterface {

    @Resource
    WebServiceContext wsc;

    @Override
    public String sayHello(String name) {
        System.out.println("Calling WebServiceContext.isUserInRole(role1) " + wsc.isUserInRole("role1"));
        return "Hello " + name + " from SayHelloServiceOne.";
    }

}
