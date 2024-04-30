package pharmaClasses;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Category {
	private int id;
	private String name;
	
	public Category(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
	public Category() {
		super();
	}
	
	public Category(String xmlString) {
		if (xmlString == null || xmlString.length() == 0)
			return;
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			SAXParser sp = spf.newSAXParser();
			StringReader sr = new StringReader(xmlString);
			XMLHandler xmlHandler = new XMLHandler();
			sp.parse(new InputSource(sr), xmlHandler);
			id = xmlHandler.getCategory().getId();
			name = xmlHandler.getCategory().getName();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String toXML() {
		StringBuilder sb = new StringBuilder();
		sb.append("<category>");
		sb.append("<categoryid>" + id + "</categoryid>");
		if(name != null)
			sb.append("<categoryname>" + name + "</categoryname>");
		sb.append("</category>");
		return sb.toString();
	}
}
