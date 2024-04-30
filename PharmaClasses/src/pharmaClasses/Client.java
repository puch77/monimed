package pharmaClasses;

import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Client {
	
	private String userId;
	private String name;
	private String pass;
	private String address;
	private String email;
	private String tel;
	private String contactPerson;
	private LocalDate dateFrom;
	private Boolean admin;
	private Boolean active;

	public Client() {
		super();
	}

	public Client(String userId, String name, String pass, String address, String email, String tel,
			String contactPerson, LocalDate dateFrom, Boolean admin, Boolean active) {
		super();
		this.userId = userId;
		this.name = name;
		this.pass = pass;
		this.address = address;
		this.email = email;
		this.tel = tel;
		this.contactPerson = contactPerson;
		this.dateFrom = dateFrom;
		this.admin = admin;
		this.active = active;
	}

	public Client(String xmlString) {
		if (xmlString == null || xmlString.length() == 0)
			return;
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			SAXParser sp = spf.newSAXParser();
			StringReader sr = new StringReader(xmlString);
			XMLHandler xmlHandler = new XMLHandler();
			sp.parse(new InputSource(sr), xmlHandler);
			userId = xmlHandler.getClient().getUserId();
			name = xmlHandler.getClient().getName();
			pass = xmlHandler.getClient().getPass();
			address = xmlHandler.getClient().getAddress();
			email = xmlHandler.getClient().getEmail();
			tel = xmlHandler.getClient().getTel();
			contactPerson = xmlHandler.getClient().getContactPerson();
			dateFrom = xmlHandler.getClient().getDateFrom();
			admin = xmlHandler.getClient().getAdmin();
			active = xmlHandler.getClient().getActive();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}

	public String toXML() {
		StringBuilder sb = new StringBuilder();
		sb.append("<client>");
		sb.append("<clientid>" + userId + "</clientid>");
		if (name != null)
			sb.append("<clientname>" + name + "</clientname>");
		if (pass != null)
			sb.append("<password>" + pass + "</password>");
		if (address != null)
			sb.append("<address>" + address + "</address>");
		if (email != null)
			sb.append("<email>" + email + "</email>");
		if (tel != null)
			sb.append("<telefon>" + tel + "</telefon>");
		if (contactPerson != null)
			sb.append("<contactperson>" + contactPerson + "</contactperson>");
		if (dateFrom != null)
			sb.append("<datefrom>" + dateFrom + "</datefrom>");
		if (admin != null)
			sb.append("<adminrights>" + admin + "</adminrights>");
		if (active != null)
			sb.append("<activeclient>" + active + "</activeclient>");
		sb.append("</client>");
		return sb.toString();
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public LocalDate getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(LocalDate dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Boolean getAdmin() {
		return admin;
	}

	public void setAdmin(Boolean admin) {
		this.admin = admin;
	}

	public final Boolean getActive() {
		return active;
	}

	public final void setActive(Boolean active) {
		this.active = active;
	}

}
