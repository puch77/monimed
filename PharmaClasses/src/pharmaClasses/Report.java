package pharmaClasses;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;



public class Report {

	private String text;

	public Report(String text) {
		super();
		if(text == null || text.length() == 0)
			return;
		if(!text.startsWith("<"))
			this.text = text;
		else {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			try {
				SAXParser sp = spf.newSAXParser();
				StringReader sr = new StringReader(text);
				XMLHandler xmlHandler = new XMLHandler();
				sp.parse(new InputSource(sr), xmlHandler);
				text = xmlHandler.getReport().getText();
			} catch (ParserConfigurationException | SAXException | IOException e) {
				e.printStackTrace();
			}
		}
	}

	public Report() {
		super();
	}
	
	public String toXML() {
		return "<report><text>" + text + "</text></report>";
	}

	@Override
	public String toString() {
		return "Report [text=" + text + "]";
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
}
