package pharmaClasses;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ClientList {
	
	private ArrayList<Client> clients;

	public ClientList(ArrayList<Client> clients) {
		super();
		this.clients = clients;
	}
	
	public ClientList(String xmlString) {
		if (xmlString == null || xmlString.length() == 0)
			return;
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			SAXParser sp = spf.newSAXParser();
			StringReader sr = new StringReader(xmlString);
			XMLHandler xmlHandler = new XMLHandler();
			sp.parse(new InputSource(sr), xmlHandler);
			clients = xmlHandler.getClients();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<Client> getClients() {
		return clients;
	}

	public void setClients(ArrayList<Client> clients) {
		this.clients = clients;
	}
	
	public String toXML() {
		StringBuilder sb = new StringBuilder();
		sb.append("<clientlist>");
		if (clients != null)
			for (Client one : clients)
				sb.append(one.toXML());
		sb.append("</clientlist>");
		return sb.toString();
	}	
}
