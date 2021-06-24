package jaxb.types.wsfvt.test.books;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {

	public Book createBook() {
		return new Book();
	}

}
