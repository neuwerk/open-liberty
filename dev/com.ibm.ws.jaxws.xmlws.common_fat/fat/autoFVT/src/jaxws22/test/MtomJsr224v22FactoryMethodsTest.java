//
// @(#) 1.4 autoFVT/src/jaxws22/test/MtomJsr224v22FactoryMethodsTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 7/30/10 16:39:32 [8/8/12 06:57:37]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// ----------------------------------------------------------------------------
// 03/03/10 btiffany                    New File
// 07/30/10 btiffany                    improve test comments
//

package jaxws22.test;


import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.ws.*;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import com.ibm.websphere.simplicity.PortType;
import com.ibm.ws.wsfvt.build.tools.ConfigRequirement;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;
import com.ibm.ws.wsfvt.test.framework.FVTTestCase;

import javax.xml.ws.handler.Handler;
import javax.xml.ws.soap.MTOMFeature;
import javax.xml.ws.WebServiceFeature;
import javax.xml.soap.*;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.axis2.transport.http.SOAPMessageFormatter;
import javax.xml.ws.soap.*;

// these are wsimport generated classes produced by buildtest.xml from the test app. 
//import jaxws22.client.mtom.com.test.*;
import jaxws22.client.mtom.*;  

/**
 * This class will test that the new factory methods in jaxws 2.2 work
 * and that conformance statements are met. 
 * 
 * note: 645812 - service.create of anything with a feature should fail, even though api's are defined. 
 * 
 * Since: version 8
 * RTC Story: 18112, task 23604
 *  
 */
public class MtomJsr224v22FactoryMethodsTest extends FVTTestCase {   
   
    static String hostAndPort= null;
    CommonMTOMClient client = new CommonMTOMClient();
    MessageCaptureHandler mch = null;
    
    /** 
     * A constructor to create a test case with a given name.
     * @param name The name of the test case to be created
     */
    public MtomJsr224v22FactoryMethodsTest(String name) {
        super(name);
    }

    /** 
     * The main method.  Only used for debugging.
     * @param args The command line arguments
     */
    public static void main(String[] args) throws Exception  {
      
       // TestRunner.run(suite());
        
        MtomJsr224v22FactoryMethodsTest t = new MtomJsr224v22FactoryMethodsTest("x");
        t.suiteSetup2();
       // t. testCreate2argnullforBill();
        t. testCreate2arg();
       
    }
    
    /** 
     * This method controls which test methods to run.
     * @return A suite of tests to run
     */
    public static Test suite() {
        return new TestSuite(MtomJsr224v22FactoryMethodsTest.class);   
    }
    
    
    /**
     * test 4.1.1.1  3 arg constructor produces a properly configured service.
     *  645812 - we conclude this should throw a webservice exception
     *
     */
    public void testCreate3argWithFeature()throws Exception {
        QName q = new QName("http://test.com/", "MTOMDDOnlyService");
        URL u = new URL( hostAndPort+"/"+ "webservicefeaturetests/MTOMDDOnlyService/MTOMDDOnlyService.wsdl");
       
        // call the 3-arg with an mtomfeature.
        try{ 
            Service s = Service.create(u, q, new MTOMFeature(true,1000));      
        } catch (WebServiceException e){
            System.out.println("caught expected exception");
            return;
        }
        fail("service should not have been created, webservice exception should have been thrown");
    }
    /**
     * test 4.1.1.1  3 arg constructor produces a properly configured service.
     *  This should not throw an exception. 
     *  
     *  645812- patch did not fix this case but Bill is going to fix it, so
     *  we will leave this test in.  
     *
     */
    public void testCreate3argNoFeature()throws Exception {
        QName q = new QName("http://test.com/", "MTOMDDOnlyService");
        URL u = new URL( hostAndPort+"/"+ "webservicefeaturetests/MTOMDDOnlyService/MTOMDDOnlyService.wsdl");
        Service s = Service.create(u, q, new WebServiceFeature[] {});            
        MTOMDDOnlyIF port = s.getPort(MTOMDDOnlyIF.class);
        addHandler((BindingProvider)port);  // install the monitoring handler
        byte [] b = this.genByteArray(2000);
        byte[] c = port.echobyte(b);  // invoke service, handler will capture soap messages
       // System.out.println("outbound message: "+ mch.getOutboundMsgAsString());
        //System.out.println("inbound message: "+ mch.getInboundMsgAsString());
        assertFalse("expected mtom to be disabled but was not", checkRequestforMTOMUsage());
        assertTrue("echo did not work correctly ",this.compareByteArrays(b, c));
        System.out.println("it worked");
    }    
    
 
    
    /**
     * test 4.1.1.1 2 arg constructor produces a properly configured service.
     * 645812 - we conclude this should throw a webservice exception 
     *
     */
    public void testCreate2arg()throws Exception{
        System.out.println("================testCreate2arg============");
        QName q = new QName("http://test.com/", "MTOMDDOnlyService");
        URL u = new URL( hostAndPort+"/"+ "webservicefeaturetests/MTOMDDOnlyService/MTOMDDOnlyService.wsdl");
       
        // call the 2-arg with an mtomfeature. 
        try{
            Service s = Service.create(q, new MTOMFeature(true, 64));
        } catch (WebServiceException e){
            System.out.println("caught expected exception");
            return;
        }
        fail("service should not have been created, webservice exception should have been thrown");     
        // that's about as far as we can get since there's no way to add wsdl.
        // a more-end-to-end test will need to use a dispatch client. 
       
    }
    
    /**
     * test 4.1.1.1 - new conformance statement that unrecognized
     * features must result in a webserviceexception on calling create
     * 
     * looks like we don't throw the exception until we hit getport.
     * When 645812 is fixed, this should fall into line, as everything will throw an exception. 
     *
     */
    public void testCreateWithInvalidFeatureThrowsWebServiceException()throws Exception{
        class BogusFeature extends WebServiceFeature{
            public String getID(){ return "bogus"; }
        }
        QName q = new QName("http://test.com/", "MTOMDDOnlyService");
        URL u = new URL( hostAndPort+"/"+ "webservicefeaturetests/MTOMDDOnlyService/MTOMDDOnlyService.wsdl");
       
        // call the 3-arg with an mtomfeature.
        Service s = null;
        try{
            s = Service.create(u, q, new BogusFeature());  
        } catch (WebServiceException e){            
            System.out.println("caught expected exception:");
            e.printStackTrace(System.out);
            return;
        }
        fail("did not catch expected exception");
                
      
    }
    
    /**
     * Service.create of anything involving a feature should fail. 
     * @throws Exception
     */
    public void testCreateWithConflictingDuplicateFeature() throws Exception{
        QName q = new QName("http://test.com/", "MTOMDDOnlyService");
        URL u = new URL( hostAndPort+"/"+ "webservicefeaturetests/MTOMDDOnlyService/MTOMDDOnlyService.wsdl");
       
        // call the 3-arg with an mtomfeature.
        try{
            Service s = Service.create(u, q, new MTOMFeature(true, 64), new MTOMFeature(false,1800));   
        } catch (WebServiceException e){            
            System.out.println("caught expected exception:");
            e.printStackTrace(System.out);
            return;
        }
        fail("did not catch expected exception");
        
        /*
        MTOMDDOnlyIF port = s.getPort(MTOMDDOnlyIF.class);
        addHandler((BindingProvider)port);  // install the monitoring handler
        byte [] b = this.genByteArray(65);
        byte[] c = port.echobyte(b);  // invoke service, handler will capture soap messages        
        System.out.println("it didn't explode, so that's good enough for this ill-defined case");
        */        
        
    }
    
    /**
     * test 4.1.1.2 - that methods in class generated by tooling work.
     * They should, since they just call the dynamic ones, but we'll
     * check just in case the generated service class is packing any
     * surprises.
     * 
     * Since this just calls the superclass, we expect an exception per
     * 645812
     *
     */
    public void testStaticCreate1arg()throws Exception{
      try{  
        jaxws22.client.mtom.MTOMDDOnlyService s =
             new jaxws22.client.mtom.MTOMDDOnlyService(new MTOMFeature(true, 64));
      } catch (WebServiceException e){
          System.out.println("caught expected exception:");
          e.printStackTrace(System.out);
          return;
      }
      fail("did not catch expected exception");
        
    }
    
    /**
     * same as above but we'll try the respectbinding feature
     * @throws Exception
     */
    public void testStaticCreate1argRespectBinding()throws Exception{
        try{  
          jaxws22.client.mtom.MTOMDDOnlyService s =
               new jaxws22.client.mtom.MTOMDDOnlyService(new RespectBindingFeature(true));
        } catch (WebServiceException e){
            System.out.println("caught expected exception:");
            e.printStackTrace(System.out);
            return;
        }
        fail("did not catch expected exception");
          
      }
    
    /**
     * test 4.1.1.2 - that methods in class generated by tooling work.
     * They should, since they just call the dynamic ones, but we'll
     * check just in case the generated service class is packing any
     * surprises.
     * 
     * Since this just calls the superclass, we expect an exception per
     * 645812
     *
     */
    public void testStaticCreate2arg()throws Exception{
        URL u = new URL( hostAndPort+"/"+ "webservicefeaturetests/MTOMDDOnlyService/MTOMDDOnlyService.wsdl");
        try{  
            jaxws22.client.mtom.MTOMDDOnlyService s =
                 new jaxws22.client.mtom.MTOMDDOnlyService(u, new MTOMFeature(true, 64));
          } catch (WebServiceException e){
              System.out.println("caught expected exception:");
              e.printStackTrace(System.out);
              return;
          }
          fail("did not catch expected exception");        
    }
    
    /**
     * same as above but use addressing feature
     * @throws Exception
     */
    public void testStaticCreate2argAddressing()throws Exception{
        URL u = new URL( hostAndPort+"/"+ "webservicefeaturetests/MTOMDDOnlyService/MTOMDDOnlyService.wsdl");
        try{  
            jaxws22.client.mtom.MTOMDDOnlyService s =
                 new jaxws22.client.mtom.MTOMDDOnlyService(u, new AddressingFeature(true));
          } catch (WebServiceException e){
              System.out.println("caught expected exception:");
              e.printStackTrace(System.out);
              return;
          }
          fail("did not catch expected exception");        
    }
    
    /**
     * there have been problems with processing ?wsdl in the past, so let's just do a
     * a quick test for that. 
     * @throws Exception
     */
    public void testStaticCreate2argqmarkwsdl()throws Exception{
        URL u = new URL( hostAndPort+"/"+ "webservicefeaturetests/MTOMDDOnlyService/?wsdl");    
            jaxws22.client.mtom.MTOMDDOnlyService s =
                 new jaxws22.client.mtom.MTOMDDOnlyService(u);
            
            MTOMDDOnlyIF port = s.getPort(MTOMDDOnlyIF.class);
            addHandler((BindingProvider)port);  // install the monitoring handler
            byte [] b = this.genByteArray(255);
            byte[] c = port.echobyte(b);  // invoke service, handler will capture soap messages
          //  System.out.println("outbound message: "+ mch.getOutboundMsgAsString());
          //  System.out.println("inbound message: "+ mch.getInboundMsgAsString());
            assertTrue("echo did not work", this.compareByteArrays(b,c));
            System.out.println("it worked"); 
    }
    
    /**
     * test 4.1.1.2 - that methods in class generated by tooling work.
     * They should, since they just call the dynamic ones, but we'll
     * check just in case the generated service class is packing any
     * surprises.
     *
     */
    public void testStaticCreate3arg()throws Exception{
        URL u = new URL( hostAndPort+"/"+ "webservicefeaturetests/MTOMDDOnlyService/MTOMDDOnlyService.wsdl");
        QName q = new QName("http://test.com/", "MTOMDDOnlyService");
        try{  
            jaxws22.client.mtom.MTOMDDOnlyService s =
                 new jaxws22.client.mtom.MTOMDDOnlyService(u, q, new MTOMFeature(true, 64));
          } catch (WebServiceException e){
              System.out.println("caught expected exception:");
              e.printStackTrace(System.out);
              return;
          }
          fail("did not catch expected exception");    
    }
    
    /**
     * Test 4.1.1.2 - that methods in class generated by tooling work.
     * spec sec 4.2.3, check that these new getport methods work.
     * This is a new method in the generated code that calls getPort and passes
     * a feature. 
     *
     */
    public void testStaticGetPort2arg()throws Exception{
        URL u = new URL( hostAndPort+"/"+ "webservicefeaturetests/MTOMDDOnlyService/MTOMDDOnlyService.wsdl");
        QName q = new QName("http://test.com/", "MTOMDDOnlyService");      
        jaxws22.client.mtom.MTOMDDOnlyService s =
             new jaxws22.client.mtom.MTOMDDOnlyService(u, q);
        MTOMDDOnlyIF port = s.getPort(MTOMDDOnlyIF.class, new MTOMFeature(true, 1000));
        addHandler((BindingProvider)port);  // install the monitoring handler
        byte [] b = this.genByteArray(1100);
        byte[] c = port.echobyte(b);  // invoke service, handler will capture soap messages
      //  System.out.println("outbound message: "+ mch.getOutboundMsgAsString());
      //  System.out.println("inbound message: "+ mch.getInboundMsgAsString());
        assertTrue("echo did not work", this.compareByteArrays(b,c));
        assertTrue("expected mtom to be used but was not", checkRequestforMTOMUsage());
        System.out.println("it worked");                
    }
    
    /**
     * spec sec 4.2.3, check that these new getport methods work.
     *
     */
    public void testDynamicGetPort2arg()throws Exception{
        QName q = new QName("http://test.com/", "MTOMDDOnlyService");
        URL u = new URL( hostAndPort+"/"+ "webservicefeaturetests/MTOMDDOnlyService/MTOMDDOnlyService.wsdl");
        Service s = Service.create(u, q);       
        // add the feature on getport.  Unlike service.create, which should not work, this should.
        MTOMDDOnlyIF port = s. getPort(MTOMDDOnlyIF.class, new MTOMFeature(true,64));
        addHandler((BindingProvider)port);  // install the monitoring handler
        byte [] b = this.genByteArray(75);
        byte[] c = port.echobyte(b);  // invoke service, handler will capture soap messages
      //  System.out.println("outbound message: "+ mch.getOutboundMsgAsString());
      //  System.out.println("inbound message: "+ mch.getInboundMsgAsString());
        assertTrue("expected mtom to be used but was not", checkRequestforMTOMUsage());
        assertTrue("echo did not work correctly ",this.compareByteArrays(b, c));
        System.out.println("it worked\n\n");
        
    }
    
    public void testDynamicGetPort2argBelowThreshold()throws Exception{
        System.out.println("threshold 64, size 60");
        QName q = new QName("http://test.com/", "MTOMDDOnlyService");
        URL u = new URL( hostAndPort+"/"+ "webservicefeaturetests/MTOMDDOnlyService/MTOMDDOnlyService.wsdl");
        Service s = Service.create(u, q);       
        // add the feature on getport.  Unlike service.create, which should not work, this should.
        MTOMDDOnlyIF port = s. getPort(MTOMDDOnlyIF.class, new MTOMFeature(true,64));
        addHandler((BindingProvider)port);  // install the monitoring handler
        byte [] b = this.genByteArray(60);
        byte[] c = port.echobyte(b);  // invoke service, handler will capture soap messages
      System.out.println("outbound message: "+ mch.getOutboundMsgAsString());
        System.out.println("inbound message: "+ mch.getInboundMsgAsString());
        assertTrue("echo did not work correctly ",this.compareByteArrays(b, c));
        assertFalse("mtom should not have been used", checkRequestforMTOMUsage());
        
        System.out.println("it worked");
        
    }
    
    /**
     * spec sec 4.2.3, check that these new getport methods work.
     *
     */
    public void testDynamicGetPort3arg()throws Exception{
        QName q = new QName("http://test.com/", "MTOMDDOnlyService");
        QName pqn = new QName("http://test.com/", "MTOMDDOnlyPort");
        URL u = new URL( hostAndPort+"/"+ "webservicefeaturetests/MTOMDDOnlyService/MTOMDDOnlyService.wsdl");
        Service s = Service.create(u, q);       
        // add the feature on getport.  Unlike service.create, which should not work, this should.
        MTOMDDOnlyIF port = s. getPort(pqn, MTOMDDOnlyIF.class, new MTOMFeature(true,64));
        addHandler((BindingProvider)port);  // install the monitoring handler
        byte [] b = this.genByteArray(75);
        byte[] c = port.echobyte(b);  // invoke service, handler will capture soap messages
       // System.out.println("outbound message: "+ mch.getOutboundMsgAsString());
       // System.out.println("inbound message: "+ mch.getInboundMsgAsString());
        assertTrue("expected mtom to be used but was not", checkRequestforMTOMUsage());
        assertTrue("echo did not work correctly ",this.compareByteArrays(b, c));
        System.out.println("it worked");
        
    }
    
    /**
     * try a very large message and make sure we don't have any legacy 
     * thresholds above which mtom is automatically enabled. 
     *
     */
    public void testDynamicGetPort2argDisabledWithLargeMessage()throws Exception{
        QName q = new QName("http://test.com/", "MTOMDDOnlyService");
        URL u = new URL( hostAndPort+"/"+ "webservicefeaturetests/MTOMDDOnlyService/MTOMDDOnlyService.wsdl");
        Service s = Service.create(u, q);       
        // add the feature on getport.  Unlike service.create, which should not work, this should.
        MTOMDDOnlyIF port = s.getPort(MTOMDDOnlyIF.class);
        addHandler((BindingProvider)port);  // install the monitoring handler
        byte [] b = this.genByteArray(65575);
        byte[] c = port.echobyte(b);  // invoke service, handler will capture soap messages
        //System.out.println("outbound message: "+ mch.getOutboundMsgAsString());
        //System.out.println("inbound message: "+ mch.getInboundMsgAsString());
        assertFalse("expected mtom to not be used but was ", checkRequestforMTOMUsage());
        assertTrue("echo did not work correctly ",this.compareByteArrays(b, c));
        System.out.println("it worked");
        
    }
 
    
    /**
     * from 4.1.1.1, an invalid feature should cause some sort of exception.
     * @throws Exception
     */
    public void testGetPortWithInvalidFeature()throws Exception{
        class BogusFeature extends WebServiceFeature{
            public String getID(){ return "bogus"; }
        }
        QName q = new QName("http://test.com/", "MTOMDDOnlyService");
        URL u = new URL( hostAndPort+"/"+ "webservicefeaturetests/MTOMDDOnlyService/MTOMDDOnlyService.wsdl");
        Service s = Service.create(u, q );
        
        
        // add the feature on getport.  If feature was not bogus, it should work. 
        try{
            MTOMDDOnlyIF port = s. getPort(MTOMDDOnlyIF.class, new BogusFeature());
        } catch (WebServiceException e){
            System.out.println("caught expected exception:");
            e.printStackTrace(System.out);
            return;
        }
        fail("did not catch expected exception");                
         
    }
    

    
    /**
     * spec sec 4.2.3, check that these new getport methods work.
     * 
     * This is complicated, I need to see if Jeff added a unit test. 
     * But I only need to check the webservicefeature part of it. 
     *
     */
    public void testDynamicGetPortWithEndpointReference()throws Exception{
        QName q = new QName("http://test.com/", "MTOMDDOnlyService");
        QName portqn = new QName("http://test.com/", "MTOMDDOnlyPort");
        URL u = new URL( hostAndPort+"/"+ "webservicefeaturetests/MTOMDDOnlyService/MTOMDDOnlyService.wsdl");
        Service s = Service.create(u, q);       
        
        
        // we need to get the endpoint reference so we can use it to get the port again.
        // Convoluted, but easiest way to test that api.
        MTOMDDOnlyIF port = s.getPort(portqn, MTOMDDOnlyIF.class);
        EndpointReference epr = ((BindingProvider)port).getEndpointReference();
        
        // now try to get the port again
        port = s.getPort(epr, MTOMDDOnlyIF.class, new MTOMFeature(true,64));
        addHandler((BindingProvider)port);  // install the monitoring handler
        byte [] b = this.genByteArray(75);
        byte[] c = port.echobyte(b);  // invoke service, handler will capture soap messages
       // System.out.println("outbound message: "+ mch.getOutboundMsgAsString());
       // System.out.println("inbound message: "+ mch.getInboundMsgAsString());
        assertTrue("expected mtom to be used but was not", checkRequestforMTOMUsage());
        assertTrue("echo did not work correctly ",this.compareByteArrays(b, c));
        System.out.println("it worked");
        
    }
    
    /**
     * The consensus was that it doesn't always make sense to put a mtom feature on a dispatch
     * method, since the method is by nature low level and controlling the composition of the
     * message itself.  However, there might be a case or two, where the dispatch involves a 
     * SOAPMessage or JAXBContext, that we should try to honor. We'll test those.
     * 
     * SOAPMessage isn't one of them, because it has direct control of attachments,
     * therefore this test is invalid, skipping
     *
     */
    
    // todo: modify to use a jaxbcontext as soon as I get this simpler case working.
    // unless, to our considerable surprise, it mtom's the thing. 
    public void _testDispatchWithSoapMessage() throws Exception {
        QName svcQName = new QName("http://test.com/", "MTOMDDOnlyService");       
        QName portName = new QName("http://test.com/", "MTOMDDOnlyPort");
        URL u = new URL( hostAndPort+"/"+ "webservicefeaturetests/MTOMDDOnlyService/MTOMDDOnlyService.wsdl");
        Service s = Service.create(u, svcQName);   
        
        // let's see if this has any chance of working....
        Dispatch<SOAPMessage> dsp = s.createDispatch(
                portName,
                javax.xml.soap.SOAPMessage.class,
                Service.Mode.MESSAGE,
                new MTOMFeature(true,64));
        
        mch = addHandler((BindingProvider)dsp);
        
        SOAPMessage msg = null;
        String msgString="<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body><ns2:echobyte xmlns:ns2=\"http://test.com/\"><arg0>AAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8gISIjJCUmJygpKissLS4vMDEyMzQ1Njc4OTo7PD0+P0BBQkNERUZHSElKS0xNTk9QUVJTVFVWV1hZWltcXV5fYGFiY2RlZmdoaWprbG1ub3BxcnN0dXZ3eHl6e3x9fn+AgYKDhIWGh4iJiouMjY6PkJGSk5SVlpeYmZqbnJ2en6ChoqOkpaanqKmqq6ytrq+wsbKztLW2t7i5uru8vb6/wMHCw8TFxsfIycrLzM3Oz9DR0tPU1dbX2Nna29zd3t/g4eLj5OXm5+jp6uvs7e7v8PHy8/T19vf4+fr7/P3+</arg0></ns2:echobyte></soapenv:Body></soapenv:Envelope>";
        msg = toSOAPMessage(msgString);
        
        // a way to print the msg
        //mch.outboundmsg = msg;
        //System.out.println(mch.getOutboundMsgAsString());
        
        SOAPMessage result = dsp.invoke(msg);
        
        //System.out.println(mch.getInboundMsgAsString());
        //System.out.println(mch.getOutboundMsgAsString());
        
        checkRequestforMTOMUsage();
    }
    
    /**
     * The consensus was that it doesn't always make sense to put a mtom feature on a dispatch
     * method, since the method is by nature low level and controlling the composition of the
     * message itself.  However, there might be a case where the dispatch involves a 
     * JAXBContext, that we should try to honor. We'll test that.
     * 
     *  This test creates a dispatch client which with an mtom feature with the threshold set to 21 bytes. 
     *  Outbound jaxb objects greater than 21 bytes in size should be sent as an mtom attachment. 
     *  A jaxb object is created from a  65-byte array and then transmitted by the client.
     *  The outbound request is inspected to see if mtom was used. 
     */
    public void testDispatchWithJAXBContext() throws Exception {
        QName svcQName = new QName("http://test.com/", "MTOMDDOnlyService");       
        QName portName = new QName("http://test.com/", "MTOMDDOnlyPort");
        URL u = new URL( hostAndPort+"/"+ "webservicefeaturetests/MTOMDDOnlyService/MTOMDDOnlyService.wsdl");
        Service s = Service.create(u, svcQName);
        
        // create context using objectfactory that was produced from wsimport. 
        JAXBContext context = JAXBContext.newInstance(new Class[] {
                jaxws22.client.mtom.ObjectFactory.class });
        
    
        // create the client and attach the feature using createdispatch(port, context,mode, feature) api.
        Dispatch dsp  = s.createDispatch(portName,
                context,
                Service.Mode.PAYLOAD,
                new MTOMFeature(true,21));
        
        mch = addHandler((BindingProvider)dsp);  // add the monitoring handler.
        
        Unmarshaller um = context.createUnmarshaller();
        
        // this is the xml representation of a byte array 65 bytes long. 
        // I couldn't figure out how to marshal a byte array into a jaxb object directly,
        // i.e. new jaxbobject(byte[] foo) does not exist, so did it this way instead. 
        // Looks strange but gets it done for testing purposes. 
        
        String msgString="<ns2:echobyte xmlns:ns2=\"http://test.com/\"><arg0>AAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8gISIjJCUmJygpKissLS4vMDEyMzQ1Njc4OTo7PD0+P0BBQkNERUZHSElKS0xNTk9QUVJTVFVWV1hZWltcXV5fYGFiY2RlZmdoaWprbG1ub3BxcnN0dXZ3eHl6e3x9fn+AgYKDhIWGh4iJiouMjY6PkJGSk5SVlpeYmZqbnJ2en6ChoqOkpaanqKmqq6ytrq+wsbKztLW2t7i5uru8vb6/wMHCw8TFxsfIycrLzM3Oz9DR0tPU1dbX2Nna29zd3t/g4eLj5OXm5+jp6uvs7e7v8PHy8/T19vf4+fr7/P3+</arg0></ns2:echobyte>";
        ByteArrayInputStream input = new ByteArrayInputStream(msgString.getBytes());
        Object jaxbObject = um.unmarshal(input);
        Object  result = dsp.invoke(jaxbObject);        
             
        assertTrue("mtom should have been used but was not", checkRequestforMTOMUsage());
    }
    
    /**
     * Method used to convert Strings to SOAPMessages. We will use a detection
     * routine to see if SOAP 1.2 support is present. If it is we will enable
     * dynamic protocol selection, otherwise we will default to SOAP 1.1 (SAAJ
     * 1.2)
     * 
     * Recycled from Sedov's jaxws dispatch tests. 
     * 
     * @param msgString
     * @return
     */
    private static SOAPMessage toSOAPMessage(String msgString) {
        

        if (msgString == null) return null;
        String SOAP11_NAMESPACE = "http://schemas.xmlsoap.org/soap/envelope";

        SOAPMessage message = null;
        try {

            MessageFactory factory = null;

            // Force the usage of specific MesasgeFactories
            if (msgString.indexOf(SOAP11_NAMESPACE) >= 0) {
                factory = MessageFactory
                        .newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
            } else {
                factory = MessageFactory
                        .newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
            }

            message = factory.createMessage();
            message.getSOAPPart().setContent(
                    (Source) new StreamSource(new StringReader(msgString)));
            message.saveChanges();
        } catch (SOAPException e) {
            System.out.println("toSOAPMessage Exception encountered: " + e);
            e.printStackTrace();
        }

        return message;
    }


 
    // perform setup for the testcase - vanilla junit method
    protected void setUp() throws Exception {
    }
    
    // make sure everything is running at the end of each test
    public void tearDown()throws Exception{
    }
    
    // our nonportable test setup method
    protected void suiteSetup(ConfigRequirement cr) throws Exception {
        System.out.println("suiteSetup() called");
        suiteSetup2();
    }
    
    // for debug, a method we can call outside junit. 
    private void suiteSetup2()throws Exception {
        String host = TopologyDefaults.getDefaultAppServer().getNode().getHostname();
        String port = TopologyDefaults.getDefaultAppServer().getPortNumber(PortType.WC_defaulthost).toString();                 
        
        hostAndPort  = "http://" + host + ":" + port ;       
        System.out.println("hostAndPort = "+hostAndPort );
    }
    
//  our nonportable test teardownp method
    protected void suiteTeardown() throws Exception {
        System.out.println("suiteTeardown() called");
    }
    
    private byte [] genByteArray(int size){
        byte [] ba = new byte[size];
        int i = 0;
        byte j = 0;
        for(i=0; i < size; i++){
            ba[i]=j++;
            if( j>250 ){ j=0;}
        }
      // System.out.println("request: "+size+" actual: "+ba.length);
       return ba;
    }
    
    private boolean compareByteArrays(byte[] expect, byte [] actual){
        if (expect.length != actual.length ){
            System.out.println("length mismatch, expect =" +expect.length + 
                    " actual = " + actual.length);
            return false;
        }
        int max = expect.length;
        for(int i=0; i < max; i++){
            if (expect[i] != actual [i]){
                System.out.println("content mismatch at offset "+i);
                return false;
            }            
        }
        return true;
    }
    
    /**
     * look for the mime boundary in the soap message, which would indicate mtom
     * was probably used.   (No way to tell for sure without a wire monitor)
     * @return
     */
    private boolean checkRequestforMTOMUsage(){
        if (mch.getOutboundMsgAsString().contains("_Part_")){ return true; }
        return false;
    }
    
    /**
     * install a handler on a port.  We'll use the handler to capture the soap message.
     * Much easier than traffic monitoring, etc.   
     * @param port
     */
    private   MessageCaptureHandler addHandler(BindingProvider port){
        // set binding handler chain
        Binding binding = ((BindingProvider)port).getBinding();

        // can create new list or use existing one
        List<Handler> handlerList = binding.getHandlerChain();

        if (handlerList == null) {
            handlerList = new ArrayList<Handler>();
        }

        MessageCaptureHandler mch = new MessageCaptureHandler();
        handlerList.add(mch);        

        binding.setHandlerChain(handlerList);
        
        this.mch = mch;
        return mch;
        
       
    }
    
 

}    