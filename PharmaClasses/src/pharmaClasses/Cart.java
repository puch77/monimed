package pharmaClasses;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Cart {

	private ArrayList<CartElement> elements;

	public Cart(ArrayList<CartElement> elements) {
		super();
		this.elements = elements;
	}
	
	public Cart(String xmlString) {
		if (xmlString == null || xmlString.length() == 0)
			return;
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			SAXParser sp = spf.newSAXParser();
			StringReader sr = new StringReader(xmlString);
			XMLHandler xmlHandler = new XMLHandler();
			sp.parse(new InputSource(sr), xmlHandler);
			elements = xmlHandler.getCart();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<CartElement> getElements() {
		return elements;
	}

	public String toXML() {
		StringBuilder sb = new StringBuilder();
		sb.append("<cart>");
		if (elements != null)
			for (CartElement one : elements)
				sb.append(one.toXML());
		sb.append("</cart>");
		return sb.toString();
	}
}
