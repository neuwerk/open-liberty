package jaxb.types.wsfvt.test.books;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "http://com.ibm.was/", name = "Book")
@XmlAccessorOrder(value = XmlAccessOrder.ALPHABETICAL)
public class Book {

	private String title;

	private String isbn;

	@XmlElement(namespace = "http://hello.world/", name = "mytitle")
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIsbn() {
		return isbn;
	}

	@XmlAttribute(namespace = "http://my.attributes/", required = true, name = "isbn")
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

}
