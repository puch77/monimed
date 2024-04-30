package pharmaClasses;

import java.time.LocalDate;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/*
 * Class responsible for handling XML parsing events.
 */
public class XMLHandler extends DefaultHandler {

	public static final String XML_PROLOG_UTF8 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>";
	
	private String text;
	private ArrayList<Product> products;
	private Product product;
	private Report report;
	private ArrayList<Client> clients;
	private Client client;
	private CartElement cartElement;
	private ArrayList<CartElement> cart;
	private Category category;
	private ArrayList<Category> categories;
	
	/**
     * Override method to handle the start of an XML element.
     */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		switch(qName.toUpperCase()) {
		case "PRODUCTLIST":
			products = new ArrayList<Product>();
			break;
		case "PRODUCT":
			product = new Product();
			break;
		case "REPORT":
			report = new Report();
			break;
		case "CLIENTLIST":
			clients = new ArrayList<Client>();
			break;
		case "CLIENT":
			client = new Client();
			break;
		case "CART":
			cart = new ArrayList<CartElement>();
			break;
		case "CARTELEMENT":
			cartElement = new CartElement();
			break;
		case "CATEGORY":
			category = new Category();
			break;
		case "CATEGORYLIST":
			categories = new ArrayList<Category>();
			break;
		}
	}

	/**
     * Override method to handle the end of an XML element.
     */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		switch(qName.toUpperCase()) {
		case "PRODUCTID":
			product.setId(Integer.parseInt(text));
			break;
		case "NAME":
			product.setName(text);
			break;
		case "DESCRIPTION":
			product.setDescription(text);
			break;
		case "PRICE":
			product.setPrice(Double.parseDouble(text));
			break;
		case "AMOUNT":
			product.setAmount(Integer.parseInt(text));
			break;
		case "ACTIVEPRODUCT":
			product.setActive(Boolean.parseBoolean(text));
			break;
		case "PRODUCT":
			if(products != null)
				products.add(product);
			else if(cartElement != null)
				cartElement.setProduct(product);
			break;			
		case "CATEGORY":
			if(categories != null)
				categories.add(category);
			else if(product != null)
				product.setCategory(category);
			break;
		case "CATEGORYID":
			category.setId(Integer.parseInt(text));
			break;
		case "CATEGORYNAME":
			category.setName(text);
			break;
		case "TEXT":
			report.setText(text);
			break;
		case "CLIENTID":
			client.setUserId(text);
			break;
		case "CLIENTNAME":
			client.setName(text);
			break;
		case "PASSWORD":
			client.setPass(text);
			break;
		case "ADDRESS":
			client.setAddress(text);
			break;
		case "EMAIL":
			client.setEmail(text);
			break;
		case "TELEFON":
			client.setTel(text);
			break;
		case "CONTACTPERSON":
			client.setContactPerson(text);
			break;
		case "DATEFROM":
			client.setDateFrom(LocalDate.parse(text));
			break;
		case "ADMINRIGHTS":
			client.setAdmin(Boolean.parseBoolean(text));
			break;
		case "ACTIVECLIENT":
			client.setActive(Boolean.parseBoolean(text));
			break;
		case "CLIENT":
			if(clients != null)
				clients.add(client);
			break;			
		case "CARTELEMENTID":
			cartElement.setId(Integer.parseInt(text));
			break;
		case "CARTELEMENTCLIENTID":
			cartElement.setClientId(text);
			break;
		case "CARTELEMENTAMOUNT":
			cartElement.setAmount(Integer.parseInt(text));
			break;
		case "CARTELEMENTORDERDATE":
			cartElement.setOrderDate(LocalDate.parse(text));
			break;
		case "CARTELEMENTISSENT":
			cartElement.setIsSent(Boolean.parseBoolean(text));
			break;
		case "CARTELEMENT":
			if(cart != null) {
				cart.add(cartElement);
			}
			break;
		}
	}
	
	/**
     * Override method to handle characters within an XML element.
     */
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		text = new String(ch, start, length);
	}

	public final Product getProduct() {
		return product;
	}

	public ArrayList<Product> getProducts() {
		return products;
	}

	public Report getReport() {
		return report;
	}

	public ArrayList<Client> getClients() {
		return clients;
	}

	public Client getClient() {
		return client;
	}

	public CartElement getCartElement() {
		return cartElement;
	}

	public ArrayList<CartElement> getCart() {
		return cart;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}	
	
	public ArrayList<Category> getCategories() {
		return categories;
	}
}
