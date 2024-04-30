package pharmaClasses;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class CategoryList {

	private ArrayList<Category> categories;

	public CategoryList(ArrayList<Category> categories) {
		super();
		this.categories = categories;
	}

	public CategoryList(String xmlString) {
		if (xmlString == null || xmlString.length() == 0)
			return;
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			SAXParser sp = spf.newSAXParser();
			StringReader sr = new StringReader(xmlString);
			XMLHandler xmlHandler = new XMLHandler();
			sp.parse(new InputSource(sr), xmlHandler);
			categories = xmlHandler.getCategories();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<Category> getCategories() {
		return categories;
	}

	public void setCategories(ArrayList<Category> categories) {
		this.categories = categories;
	}

	public String toXML() {
		StringBuilder sb = new StringBuilder();
		sb.append("<categorylist>");
		if (categories != null)
			for (Category one : categories)
				sb.append(one.toXML());
		sb.append("</categorylist>");
		return sb.toString();
	}
}
