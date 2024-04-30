package pharmaClasses;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ProductList {
	private ArrayList<Product> products;

	public ProductList(ArrayList<Product> products) {
		super();
		this.products = products;
	}

	public ProductList() {
		super();
	}

	public ProductList(String xmlString) {
		if (xmlString == null || xmlString.length() == 0)
			return;
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			SAXParser sp = spf.newSAXParser();
			StringReader sr = new StringReader(xmlString);
			XMLHandler xmlHandler = new XMLHandler();
			sp.parse(new InputSource(sr), xmlHandler);
			products = xmlHandler.getProducts();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "ProductList [products=" + products + "]";
	}

	public ArrayList<Product> getProducts() {
		return products;
	}

	public void setProducts(ArrayList<Product> products) {
		this.products = products;
	}

	public String toXML() {
		StringBuilder sb = new StringBuilder();
		sb.append("<productlist>");
		if (products != null)
			for (Product one : products)
				sb.append(one.toXML());
		sb.append("</productlist>");
		return sb.toString();
	}
}
