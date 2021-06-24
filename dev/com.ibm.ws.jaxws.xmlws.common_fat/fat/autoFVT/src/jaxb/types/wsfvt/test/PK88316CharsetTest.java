package jaxb.types.wsfvt.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import jaxb.types.wsfvt.test.books.Book;
import jaxb.types.wsfvt.test.books.ObjectFactory;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import com.ibm.ws.wsfvt.test.framework.FVTTestCase;

/**
 * On special hybrid platforms (namely Solaris/HP-UX), using the
 * "com.sun.xml.bind.marshaller.CharacterEscapeHandler" basically requires
 * access to an internal class. This can cause a compile failure.
 * 
 * The APAR was only for the NamespacePrefixMapper but this is added just in
 * case. There are a few tests for making sure the functionality is as expected.
 */
public class PK88316CharsetTest extends FVTTestCase {

	/**
	 * A constructor to create a test case with a given name.
	 * 
	 * @param name
	 *            The name of the test case to be created
	 */
	public PK88316CharsetTest(String name) {
		super(name);
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            The command line arguments
	 */
	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	/**
	 * This method controls which test methods to run.
	 * 
	 * @return A suite of tests to run
	 */
	public static Test suite() {
		return new TestSuite(PK88316CharsetTest.class);
	}

	/**
	 * A simple character escape.
	 * 
	 */
	public static class CharEscaper implements
			com.sun.xml.bind.marshaller.CharacterEscapeHandler {

		public void escape(char[] arg0, int arg1, int arg2, boolean arg3,
				Writer arg4) throws IOException {
			// System.out.println(new String(arg0) + "-" + arg1 + "-" + arg2 +
			// "-" + arg3);

			if (arg3) {
				arg4.write(new String(arg0, arg1, arg2)
						.replaceAll("a", "aaaaa"));
			} else {
				arg4
						.write(new String(arg0, arg1, arg2).replaceAll("o",
								"oooo"));
			}
		}

	}

	/**
	 * Tests that the Character Escape Handler can be set and used.
	 * 
	 * @throws JAXBException
	 */
	public void testCharEscapeHandler() throws JAXBException {
		// won't run this case on ZOS
		if(isZOS()) {
			System.out.println("Ignored case testCharEscapeHandler if running on z/OS for CharSet/encoding issue");
			return;
		}
		JAXBContext context = JAXBContext.newInstance(ObjectFactory.class
				.getPackage().getName());
		Marshaller m = context.createMarshaller();
		m.setProperty("com.sun.xml.bind.characterEscapeHandler",
				new CharEscaper());
		m.setProperty(Marshaller.JAXB_ENCODING, "US-ASCII");
		Book b = new Book();
		b.setTitle("\"Harry Potter\"");
		b.setIsbn("\"some&thing\"");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		m.marshal(b, baos);
		byte[] marshalledBytes = baos.toByteArray();
		String s = new String(marshalledBytes);
		System.out.println(s);
		assertTrue(s.contains("http://my.aaaaattributes"));
		// be sure to check that the o is not 4 o's because the CharEscaper only
		// changes it for element values not namespace attributes.
		assertTrue(s.contains("http://com.ibm.waaaaas/"));
		assertTrue(s.contains("Harry Pooootter"));
		// assertEquals(
		// "<?xml version=\"1.0\" encoding=\"US-ASCII\" standalone=\"yes\"?>"
		// +
		// "<ns3:Book ns1:isbn=\"\"some&thing\"\" xmlns:ns1=\"http://my.aaaaattributes/\" xmlns:ns2=\"http://hello.world/\" xmlns:ns3=\"http://com.ibm.waaaaas/\">"
		// + "<ns2:mytitle>\"Harry Pooootter\"</ns2:mytitle>"
		// + "</ns3:Book>", s);
	}
}
