package pharmaClasses;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Product {
	private int id;
	private String name;
	private String description;
	private double price;
	private Category category;
	private int amount;
	private Boolean active;
	
	public Product(int id, String name, String description, double price, Category category, int amount, Boolean active) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.price = price;
		this.category = category;
		this.amount = amount;
		this.active = active;
	}

	public Product() {
		super();
	}

	public Product(String xmlString) {
		if (xmlString == null || xmlString.length() == 0)
			return;
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			SAXParser sp = spf.newSAXParser();
			StringReader sr = new StringReader(xmlString);
			XMLHandler xmlHandler = new XMLHandler();
			sp.parse(new InputSource(sr), xmlHandler);
			id = xmlHandler.getProduct().getId();
			name = xmlHandler.getProduct().getName();
			description = xmlHandler.getProduct().getDescription();
			price = xmlHandler.getProduct().getPrice();
			category = xmlHandler.getCategory();
			amount = xmlHandler.getProduct().getAmount();
			active = xmlHandler.getProduct().getActive();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString() {
		return "Product [id=" + id + ", name=" + name + ", description=" + description + ", price=" + price
				+ ", category=" + (category != null ? category.getName() : null) + ", amount=" + amount + ", active=" + active + "]";
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public final Boolean getActive() {
		return active;
	}

	public final void setActive(Boolean active) {
		this.active = active;
	}

	
	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String toXML() {
		StringBuilder sb = new StringBuilder();
		sb.append("<product>");
		sb.append("<productid>" + id + "</productid>");
		if(name != null)
			sb.append("<name>" + name + "</name>");
		if(description != null)
			sb.append("<description>" + description + "</description>");
		if(price != 0.0)
			sb.append("<price>" + price + "</price>");
		if(category != null) {
			sb.append("<category>"); 
			sb.append("<categoryid>" + category.getId() + "</categoryid>");
			sb.append("<categoryname>" + category.getName() + "</categoryname>");
			sb.append("</category>");
		}
		sb.append("<amount>" + amount + "</amount>");
		sb.append("<activeproduct>" + active + "</activeproduct>");
		sb.append("</product>");
		return sb.toString();
	}
}
