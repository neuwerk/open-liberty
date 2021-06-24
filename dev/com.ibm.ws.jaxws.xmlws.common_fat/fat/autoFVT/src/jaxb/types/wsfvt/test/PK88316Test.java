package jaxb.types.wsfvt.test;

import java.io.ByteArrayOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

import jaxb.types.wsfvt.test.books.Book;
import jaxb.types.wsfvt.test.books.ObjectFactory;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import com.ibm.ws.wsfvt.test.framework.FVTTestCase;

/**
 * On special hybrid platforms (namely Solaris/HP-UX), using the
 * "com.sun.xml.bind.marshaller.NamespacePrefixMapper" basically requires access
 * to an internal class. This can cause a compile failure.
 * 
 * The APAR was because the class could not be compiled so that's the first
 * hurdle. There are a few tests for making sure the functionality is as
 * expected.
 */
public class PK88316Test extends FVTTestCase {

	/**
	 * A constructor to create a test case with a given name.
	 * 
	 * @param name
	 *            The name of the test case to be created
	 */
	public PK88316Test(String name) {
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
		return new TestSuite(PK88316Test.class);
	}

	/**
	 * NamespacePrefixMapper that only override what's necessary. In this case,
	 * if a namespace occurs, then it will try to use the prefix returned in the
	 * {@link NamespacePrefixMapper#getPreferredPrefix(String, String, boolean)}
	 * method.
	 */
	public static class NamespacePrefixMapperSimple extends
			com.sun.xml.bind.marshaller.NamespacePrefixMapper {

		@Override
		public String getPreferredPrefix(String arg0, String arg1, boolean arg2) {
			if ("http://com.ibm.was/".equals(arg0)) {
				return "itswas";
			} else if ("http://my.prefix/".equals(arg0)) {
				return "myprefix";
			} else if ("".equals(arg0)) {
				return "thedefaultns";
			}
			return arg1;
		}

	}

	/**
	 * Tests that the NamespacePrefixMapper can be set and used.
	 * 
	 * @throws JAXBException
	 */
	public void testNamespacePrefixMapperUsed() throws JAXBException {
		// won't run this case on ZOS
		if(isZOS()) {
			System.out.println("Ignored case testNamespacePrefixMapperUsed if running on z/OS for CharSet/encoding issue");
			return;
		}
		JAXBContext context = JAXBContext.newInstance(ObjectFactory.class
				.getPackage().getName());
		Marshaller m = context.createMarshaller();
		m.setProperty("com.sun.xml.bind.namespacePrefixMapper",
				new NamespacePrefixMapperSimple());
		Book b = new Book();
		b.setTitle("Harry Potter");
		b.setIsbn("something");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		m.marshal(b, baos);
		byte[] marshalledBytes = baos.toByteArray();
		String s = new String(marshalledBytes);
		System.out.println(s);
		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
						+ "<itswas:Book xmlns:ns1=\"http://my.attributes/\" xmlns:ns2=\"http://hello.world/\" xmlns:itswas=\"http://com.ibm.was/\" ns1:isbn=\"something\">"
						+ "<ns2:mytitle>Harry Potter</ns2:mytitle>"
						+ "</itswas:Book>", s);
	}

	/**
	 * A NamespacePrefixMapper that also says to declare a few prefixes in the
	 * root. Any namespace returned in {#link
	 * {@link #getPreDeclaredNamespaceUris()} will also get a suggested prefix
	 * from {@link #getPreferredPrefix(String, String, boolean)}.
	 */
	static class NamespacePrefixMapperPredeclare extends
			com.sun.xml.bind.marshaller.NamespacePrefixMapper {

		@Override
		public String getPreferredPrefix(String arg0, String arg1, boolean arg2) {
			if ("http://com.ibm.was/".equals(arg0)) {
				return "itswas";
			} else if ("http://my.prefix/".equals(arg0)) {
				return "myprefix";
			}
			return arg1;
		}

		@Override
		public String[] getPreDeclaredNamespaceUris() {
			return new String[] { "http://my.prefix/" };
		}
	}

	/**
	 * Tests that the NamespacePrefixMapper can be set and used.
	 * 
	 * @throws JAXBException
	 */
	public void testNamespacePrefixMapperPredeclaredUsed() throws JAXBException {
		// won't run this case on ZOS
		if (isZOS()) {
			System.out.println("Ignored case testNamespacePrefixMapperPredeclaredUsed() if running on z/OS for CharSet/encoding issue");
			return;
		}
		JAXBContext context = JAXBContext.newInstance(ObjectFactory.class
				.getPackage().getName());
		Marshaller m = context.createMarshaller();
		m.setProperty("com.sun.xml.bind.namespacePrefixMapper",
				new NamespacePrefixMapperPredeclare());
		Book b = new Book();
		b.setTitle("Harry Potter");
		b.setIsbn("something");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		m.marshal(b, baos);
		byte[] marshalledBytes = baos.toByteArray();
		String s = new String(marshalledBytes);
		System.out.println(s);
		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
						+ "<itswas:Book xmlns:ns1=\"http://my.attributes/\" xmlns:ns2=\"http://hello.world/\" xmlns:itswas=\"http://com.ibm.was/\" "
						+ "xmlns:myprefix=\"http://my.prefix/\" ns1:isbn=\"something\">"
						+ "<ns2:mytitle>Harry Potter</ns2:mytitle>"
						+ "</itswas:Book>", s);
	}

	/**
	 * A NamespacePrefixMapper that also says to declare a few prefixes in the
	 * root. The array returned in {@link #getPreDeclaredNamespaceUris2()}
	 * returns pairs of prefixes and namespaces that will be declared in the
	 * root. (For optimization purposes?)
	 */
	static class NamespacePrefixMapperPredeclare2 extends
			com.sun.xml.bind.marshaller.NamespacePrefixMapper {

		@Override
		public String getPreferredPrefix(String arg0, String arg1, boolean arg2) {
			if ("http://com.ibm.was/".equals(arg0)) {
				return "itswas";
			} else if ("http://my.prefix/".equals(arg0)) {
				return "myprefix";
			}
			return arg1;
		}

		@Override
		public String[] getPreDeclaredNamespaceUris2() {
			return new String[] { "exampleprefix", "http://com.example/",
					"myprefix2", "http://my.prefix/" };
		}
	}

	/**
	 * Tests that the NamespacePrefixMapper can be set and used.
	 * 
	 * @throws JAXBException
	 */
	public void testNamespacePrefixMapperPredeclared2Used()
			throws JAXBException {
		// won't run this case on ZOS
		if (isZOS()) {
			System.out.println("Ignored case testNamespacePrefixMapperPredeclared2Used if running on z/OS for CharSet/encoding issue");
			return;
		}
		JAXBContext context = JAXBContext.newInstance(ObjectFactory.class
				.getPackage().getName());
		Marshaller m = context.createMarshaller();
		m.setProperty("com.sun.xml.bind.namespacePrefixMapper",
				new NamespacePrefixMapperPredeclare2());
		Book b = new Book();
		b.setTitle("Harry Potter");
		b.setIsbn("something");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		m.marshal(b, baos);
		byte[] marshalledBytes = baos.toByteArray();
		String s = new String(marshalledBytes);

		System.out.println(s);
		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
						+ "<itswas:Book xmlns:ns1=\"http://my.attributes/\" xmlns:ns2=\"http://hello.world/\" xmlns:itswas=\"http://com.ibm.was/\""
						+ " xmlns:exampleprefix=\"http://com.example/\""
						+ " xmlns:myprefix2=\"http://my.prefix/\" ns1:isbn=\"something\">"
						+ "<ns2:mytitle>Harry Potter</ns2:mytitle>"
						+ "</itswas:Book>", s);
	}

	/**
	 * A NamespacePrefixMapper that says that a few prefixes are already
	 * predeclared. This is really for JAXB fragments and
	 * {@link #getContextualNamespaceDecls()} would only be applicable in
	 * certain marshaling parameters, (@link
	 * {@link Marshaller#marshal(Object, java.io.OutputStream)} in particular.
	 * It basically says, I already declared this namespace to a certain prefix
	 * in the content already outputted via the OutputStream (hence contextual).
	 */
	static class NamespacePrefixMapperContextual extends
			com.sun.xml.bind.marshaller.NamespacePrefixMapper {

		@Override
		public String getPreferredPrefix(String arg0, String arg1, boolean arg2) {
			if ("http://com.ibm.was/".equals(arg0)) {
				return "itswas2";
			} else if ("http://my.prefix/".equals(arg0)) {
				return "myprefix";
			}
			return arg1;

		}

		@Override
		public String[] getContextualNamespaceDecls() {
			return new String[] { "itswas456", "http://com.ibm.was/" };
		}
	}

	/**
	 * Tests that the NamespacePrefixMapper can be set and used.
	 * 
	 * @throws JAXBException
	 */
	public void testNamespacePrefixMapperContextualUsed() throws JAXBException {
		// won't run this case on ZOS
		if (isZOS()) {
			System.out.println("Ignored case testNamespacePrefixMapperContextualUsed if running on z/OS for CharSet/encoding issue");
			return;
		}
		JAXBContext context = JAXBContext.newInstance(ObjectFactory.class
				.getPackage().getName());
		Marshaller m = context.createMarshaller();
		m.setProperty("com.sun.xml.bind.namespacePrefixMapper",
				new NamespacePrefixMapperContextual());
		Book b = new Book();
		b.setTitle("Harry Potter");
		b.setIsbn("something");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		m.marshal(b, baos);
		byte[] marshalledBytes = baos.toByteArray();
		String s = new String(marshalledBytes);

		System.out.println(s);
		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
						+ "<itswas456:Book xmlns:ns2=\"http://my.attributes/\" xmlns:ns3=\"http://hello.world/\" ns2:isbn=\"something\">"
						+ "<ns3:mytitle>Harry Potter</ns3:mytitle>"
						+ "</itswas456:Book>", s);
	}
}
