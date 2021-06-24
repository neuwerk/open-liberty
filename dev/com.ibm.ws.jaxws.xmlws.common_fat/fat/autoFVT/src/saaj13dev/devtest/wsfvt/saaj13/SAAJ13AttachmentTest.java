//
// @(#) 1.2 WautoFVT/src/saaj13dev/devtest/wsfvt/saaj13/SAAJ13AttachmentTest.java, WAS.websvcs.fvt, WSFP.WFVT, a0710.10 10/3/06 15:02:56 [3/14/07 17:27:11]
//
// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5655-N01, 5733-W60, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// ----------------------------------------------------------------------------
// 05/26/06 scheu       LIDB4238           Create 
// 09/20/06 scheu       391915          C028 change
// 10/02/06 scheu       391915          SAAJ 1.3 Spec Changes


package saaj13dev.devtest.wsfvt.saaj13;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import com.ibm.ws.wsfvt.build.tools.ConfigRequirement;


/**
 * SAAJ 1.3 Attachment Tests
 *
 */
public class SAAJ13AttachmentTest extends TestBase {

    String xmlStringMTOM;
    String xmlStringMTOMRaw =
         "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
         "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
         "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
         "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
         " <soapenv:Header>\n" +
         " </soapenv:Header>\n" +
         " <soapenv:Body>\n" +
         "  <m:data xmlns:m='http://example.org' xmlns:xmlmime='http://www.w3.org/2004/11/xmlmine'>\n" +
         "    <m:image xmlmime:contentType='image/jpg'>\n" +
         "      <xop:Include xmlns:xop='http://www.w3.org/2004/08/xop/include' href='cid:http://example.org/myImage'/>" +
         "    </m:image>\n" +
         "  </m:data>\n" +
         " </soapenv:Body>\n" +
         "</soapenv:Envelope>";
    String xmlStringSWAREF;
    String xmlStringSWAREFRaw =
         "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
         "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
         "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
         "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
         " <soapenv:Header>\n" +
         " </soapenv:Header>\n" +
         " <soapenv:Body>\n" +
         "  <m:data xmlns:m='http://example.org' xmlns:xmlmime='http://www.w3.org/2004/11/xmlmine'>\n" +
         "    <m:image xmlmime:contentType='image/jpg'>cid:http://example.org/myImage</m:image>\n" +
         "  </m:data>\n" +
         " </soapenv:Body>\n" +
         "</soapenv:Envelope>";

        public SAAJ13AttachmentTest() {
            super();
            xmlStringMTOM = removeMixedContent(xmlStringMTOMRaw);
            xmlStringSWAREF = removeMixedContent(xmlStringSWAREFRaw);
        }

        public SAAJ13AttachmentTest(String arg0) {
            super(arg0);
            xmlStringMTOM = removeMixedContent(xmlStringMTOMRaw);
            xmlStringSWAREF = removeMixedContent(xmlStringSWAREFRaw);
        }
        
        protected void suiteSetup(ConfigRequirement cr) throws java.lang.Exception {
            System.out.println("Do not need suiteSetup since no application is installed");    
        }

        
        /**
         * @testStrategy Test that validates the new methods added for C032
         * @throws Exception
         */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
        public void testC032() throws Exception {
                // Create a Message
                MessageFactory factory = MessageFactory.newInstance();
                SOAPMessage message = factory.createMessage();
                
                // Add a plain text attachment
                
                int numAttachments = 1;
                Object[] initialContent = new Object[numAttachments];
                
                AttachmentPart attachment = message.createAttachmentPart();
                initialContent[0] =  "SAAJ 1.3 Rocks!";
                attachment.setContent(initialContent[0], "text/plain");
                attachment.setContentId("saaj_rocks");
                message.addAttachmentPart(attachment);
                
                // Validate the number of attachments
                assertTrue(message.countAttachments()==numAttachments);
                
                // Walk the attachment and make sure the content matches the initial string content
                java.util.Iterator it = message.getAttachments();
                int i = 0;
                while (it.hasNext()) {
                        attachment = (AttachmentPart) it.next();
                        Object content = attachment.getContent();
                        assertEquals(initialContent[i], content);
                        i++;
                }
                
                // Now repeat the above code, but interject code that gets and sets
                // the content as "raw data"
                it = message.getAttachments();
                i =0;
                while (it.hasNext()) {
                        attachment = (AttachmentPart) it.next();
                        
                        InputStream is = attachment.getRawContent();
                        attachment.setRawContent(is, attachment.getDataHandler().getContentType());
                        
                        Object content = attachment.getContent();
                        assertEquals(content,initialContent[i]);
                        i++;
                }
                
                // Now repeat the above code, but interject code that gets and sets
                // the content as "base64 data"
                it = message.getAttachments();
                i=0;
                while (it.hasNext()) {
                        attachment = (AttachmentPart) it.next();
                        
                        InputStream is = attachment.getBase64Content();
                        attachment.setBase64Content(is, attachment.getDataHandler().getContentType());
                        
                        Object content = attachment.getContent();
                        assertEquals(content,initialContent[i]);
                        i++;
                }
                

                // Now repeat the above code, but interject code that gets and sets
                // the content as "raw bytes"
                it = message.getAttachments();
                i = 0;
                while (it.hasNext()) {
                        attachment = (AttachmentPart) it.next();
                        
                        byte[] b = attachment.getRawContentBytes();
                        attachment.setRawContentBytes(b, 0, b.length, attachment.getDataHandler().getContentType());
                        
                        Object content = attachment.getContent();
                        assertEquals(content,initialContent[i]);
                        i++;
                }
                
                message.writeTo(System.out);
                
                message.removeAllAttachments();
                assertTrue(message.countAttachments()==0);
        }

       /**
         * @testStrategy Test to validate SOAPMessage.removeAttachments(MimeHeader)
         * @throws Exception
         */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
        public void testC028_1() throws Exception {
                AttachmentPart attachmentId = null;
                // Create a Message
                MessageFactory factory = MessageFactory.newInstance();
                SOAPMessage message = factory.createMessage();
                
                // Add a plain text attachment
                
                int numAttachments = 2;
                Object[] initialContent = new Object[numAttachments];
                
                AttachmentPart attachment = message.createAttachmentPart();
                initialContent[0] =  "SAAJ 1.3 Rocks!";
                attachment.setContent(initialContent[0], "text/plain");
                attachment.setContentId("<saaj_rocks>");
                // Check the ID with < >
                attachmentId = attachment;
                String strId = attachmentId.getContentId();
                assertTrue( "ID '<saaj_rocks>' should change to 'saaj_rock' but not" ,
                            strId.equals( "saaj_rocks" ) );

                message.addAttachmentPart(attachment);

                attachment = message.createAttachmentPart();
                initialContent[1] =  "Hello World";
                attachment.setContent(initialContent[1], "text/html");
                attachment.setContentId("hello_world");

                message.addAttachmentPart(attachment);
                
                // Validate the number of attachments
                assertTrue(message.countAttachments()==numAttachments);

                // double check the ID quoted with <>
                MimeHeaders headersId = new MimeHeaders();
                headersId.setHeader("content-id", "saaj_rocks");
                java.util.Iterator iterId = message.getAttachments( headersId );
                int iCount = 0;
                while( iterId.hasNext()){
                        attachment = (AttachmentPart) iterId.next();
                        iCount++;
                }
                assertTrue( "Ought to get 1 AttachmentPart id as 'saaj_rock' but get " + iCount,
                            iCount == 1 );

                // This should not remove any attributes
                MimeHeaders headers1 = new MimeHeaders();
                headers1.setHeader("content-type", "bogus");
                message.removeAttachments(headers1);
                assertTrue("Failed 'bogus header' test", message.countAttachments()==numAttachments);

                // This should remove one of the headers
                MimeHeaders headers2 = new MimeHeaders();
                headers2.setHeader("CoNtEnT-TyPe", "text/html");
                message.removeAttachments(headers2);
                assertTrue("Failed 'CoNtEnT-TyPe header' test", message.countAttachments()==(numAttachments-1));
                
                // Write it out
                message.writeTo(System.out);
                
                message.removeAllAttachments();
                assertTrue(message.countAttachments()==0);
        }

    /**
     * @testStrategy Test to validate SOAPMessage.removeAttachments(MimeHeader)
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testC028_MTOM() throws Exception {
        // Create an MTOM Message from the text
        MessageFactory mf = MessageFactory.newInstance();
        SOAPMessage sm =
            mf.createMessage(new MimeHeaders(), new ByteArrayInputStream(xmlStringMTOM.getBytes()));
        SOAPPart sp = sm.getSOAPPart();
        SOAPEnvelope se = (SOAPEnvelope)sp.getEnvelope();
        assertTrue(se != null);

        // Add the mtom attachment
        AttachmentPart ap  = sm.createAttachmentPart();
        File   file = new File("./saaj/client/util/websvcs.JPG");
        FileInputStream fis = new FileInputStream( file );
        ap.setContent  ( fis, "image/jpeg" );
        ap.setContentId( "http://example.org/myImage" );
        sm.addAttachmentPart( ap );

        // Get the XOP IncludeSOAPElement
        SOAPElement x = (SOAPElement) se.getBody();
        x = (SOAPElement) x.getFirstChild();  // data
        x = (SOAPElement) x.getFirstChild();  // image
        x = (SOAPElement) x.getFirstChild();  // include
        
        assertTrue(x != null);
        assertTrue(x.getLocalName().equals("Include"));
        System.out.println("Element=" + x);
        
        // Get the Attachment Part
        ap = sm.getAttachment(x);
        assertTrue(ap != null);
        sm.writeTo(System.out);
    }

    /**
     * @testStrategy Test to validate SOAPMessage.getAttachment
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testC028_SWAREF() throws Exception {
        // Create an SWAREF Message from the text
        MessageFactory mf = MessageFactory.newInstance();
        SOAPMessage sm =
            mf.createMessage(new MimeHeaders(), new ByteArrayInputStream(xmlStringSWAREF.getBytes()));
        SOAPPart sp = sm.getSOAPPart();
        SOAPEnvelope se = (SOAPEnvelope)sp.getEnvelope();
        assertTrue(se != null);

        // Add the swaref attachment
        AttachmentPart ap  = sm.createAttachmentPart();
        File   file = new File("./saaj/client/util/websvcs.JPG");
        FileInputStream fis = new FileInputStream( file );
        ap.setContent  ( fis, "image/jpeg" );
        ap.setContentId( "http://example.org/myImage" );
        sm.addAttachmentPart( ap );

        // Get the SWAREF IncludeSOAPElement
        SOAPElement x = (SOAPElement) se.getBody();
        x = (SOAPElement) x.getFirstChild();  // data
        x = (SOAPElement) x.getFirstChild();  // image
        System.out.println("Element=" + x);
        
        assertTrue(x != null);
        assertTrue(x.getLocalName().equals("image"));

        // Get the Attachment Part
        ap = sm.getAttachment(x);
        assertTrue(ap != null);
        sm.writeTo(System.out);

    }

/**
  * removeMixedContent
  * brain dead code that can be used to remove mixed content
  * from xml messages.
  * @param input xml string
  * @return String with mixed content removed.
  */
 private String removeMixedContent(String input) {
     String[] parts = input.split(">\\s*<");
     StringBuffer result = new StringBuffer();
     for (int i=0; i < parts.length; i++) {
         result.append(parts[i]);
         if ((i+1) < parts.length) {
             result.append("><");
         }
     }
     return result.toString();
 }
}

