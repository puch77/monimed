package pharmaClasses;

import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class CartElement {

	private int id;
	private String clientId;
	private Product product;
	private int amount;
	private LocalDate orderDate;
	private Boolean isSent;
	
	public CartElement(int id, String clientId, Product product, int amount, LocalDate orderDate, Boolean isSent) {
		super();
		this.id = id;
		this.clientId = clientId;
		this.product = product;
		this.amount = amount;
		this.orderDate = orderDate;
		this.isSent = isSent;
	}
	public CartElement() {
		super();
	}
	
	public CartElement(String xmlString) {
		if (xmlString == null || xmlString.length() == 0)
			return;
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			SAXParser sp = spf.newSAXParser();
			StringReader sr = new StringReader(xmlString);
			XMLHandler xmlHandler = new XMLHandler();
			sp.parse(new InputSource(sr), xmlHandler);
			id = xmlHandler.getCartElement().getId();
			clientId = xmlHandler.getCartElement().getClientId();
			product = xmlHandler.getCartElement().getProduct();
			amount = xmlHandler.getCartElement().getAmount();
			orderDate = xmlHandler.getCartElement().getOrderDate();
			isSent = xmlHandler.getCartElement().getIsSent();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public String toXML() {
		StringBuilder sb = new StringBuilder();
		sb.append("<cartelement>");
		sb.append("<cartelementid>" + id + "</cartelementid>");
		sb.append("<cartelementclientid>" + clientId + "</cartelementclientid>");
		sb.append("<product>");
		sb.append("<productid>" + product.getId() + "</productid>");
		if(product.getName() != null)
			sb.append("<name>" + product.getName() + "</name>");
		if(product.getDescription() != null)
			sb.append("<description>" + product.getDescription() + "</description>");
		sb.append("<price>" + product.getPrice() + "</price>");
		if(product.getCategory() != null) {
			sb.append("<category>"); 
			sb.append("<categoryid>" + product.getCategory().getId() + "</categoryid>");
			sb.append("<categoryname>" + product.getCategory().getName() + "</categoryname>");
			sb.append("</category>");
		}
		sb.append("<amount>" + product.getAmount() + "</amount>");
		sb.append("<activeproduct>" + product.getActive() + "</activeproduct>");
		sb.append("</product>");
		sb.append("<cartelementamount>" + amount + "</cartelementamount>");
		sb.append("<cartelementorderdate>" + orderDate + "</cartelementorderdate>");
		sb.append("<cartelementissent>" + isSent + "</cartelementissent>");
		sb.append("</cartelement>");
		return sb.toString();
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public LocalDate getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(LocalDate orderDate) {
		this.orderDate = orderDate;
	}
	public Boolean getIsSent() {
		return isSent;
	}
	public void setIsSent(Boolean isSent) {
		this.isSent = isSent;
	}
	@Override
	public String toString() {
		return "CartElement [id=" + id + ", clientId=" + clientId + ", product=" + product + ", amount=" + amount
				+ ", orderDate=" + orderDate + ", isSent=" + isSent + "]";
	}	
}
