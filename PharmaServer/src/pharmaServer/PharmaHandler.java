package pharmaServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import pharmaClasses.Cart;
import pharmaClasses.CartElement;
import pharmaClasses.Category;
import pharmaClasses.CategoryList;
import pharmaClasses.Client;
import pharmaClasses.ClientList;
import pharmaClasses.Product;
import pharmaClasses.ProductList;
import pharmaClasses.Report;
import pharmaClasses.XMLHandler;

/**
 * It handles incoming HTTP requests for the PharmaServer.
 */
public class PharmaHandler implements HttpHandler {

    /**
     * Handles incoming HTTP requests and routes them to appropriate methods based on the request type.
     *
     * @param exchange The HTTP exchange object representing the incoming request and the outgoing response.
     * @throws IOException If an I/O error occurs while handling the request.
     */
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		// Extract information from the incoming HTTP request
		String requestMethod = exchange.getRequestMethod();
		URI uri = exchange.getRequestURI();
		System.out.println(uri + " " + requestMethod);
		String path = uri.getPath();
		
		// Remove leading '/' from the path
		if (path.startsWith("/")) 
			path = path.substring(1);
		
		// Split the path into individual components
		String[] paths = path.split("/");
		
		// Determine the type of HTTP request and invoke the corresponding method
		if (requestMethod.equalsIgnoreCase("GET"))
			get(exchange, paths);
		else if (requestMethod.equalsIgnoreCase("POST"))
			post(exchange, paths);
		else if (requestMethod.equalsIgnoreCase("DELETE"))
			delete(exchange, paths);
		else {
			// Handle unsupported HTTP methods
			setResponse(exchange, 400, new Report("False HTTP Request " + requestMethod).toXML());
		}
	}

    /**
     * It handles DELETE requests based on the specified path components.
     *
     * @param exchange The HTTP exchange object.
     * @param paths    The components of the request path.
     */
	private void delete(HttpExchange exchange, String[] paths) {
		int statusCode = 204;
		String xmlString = "";
		
		// Check the path components and perform the corresponding action
		if (paths.length == 2 && paths[0].equals("cartelement")) {
			// Delete a cart element
			try {
				Database.deleteCartElement(Integer.parseInt(paths[1]));
			} catch (SQLException e) {
				statusCode = 500;
				xmlString = new Report(e.toString()).toXML();
			}
		} else if (paths.length == 2 && paths[0].equals("client")) {
			// Delete a client
			try {
				Database.deleteClient(paths[1]);
			} catch (SQLException e) {
				statusCode = 500;
				xmlString = new Report(e.toString()).toXML();
			}
		} else if (paths.length == 2 && paths[0].equals("category")) {
			// Delete a category
			try {
				Database.deleteCategory(paths[1]);
			} catch (SQLException e) {
				statusCode = 500;
				xmlString = new Report(e.toString()).toXML();
			}
		} else {
			statusCode = 400;
			xmlString = new Report("False URL").toXML();
		}
		setResponse(exchange, statusCode, xmlString);
	}

    /**
     * It handles POST requests based on the specified path components.
     *
     * @param exchange The HTTP exchange object.
     * @param paths    The components of the request path.
     */
	private void post(HttpExchange exchange, String[] paths) {
		int statusCode = 201;
		String xmlString = "";
		InputStream is = exchange.getRequestBody();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		// Check the path components and perform the corresponding action
		if (paths.length == 2 && paths[0].equals("client")) {
			// Create or update client
			try {
				String line = br.readLine();
				System.out.println("\trequestbody = '" + line + "'");
				if(paths[1].equals("null")) 
					Database.insertClient(new Client(line));
				else
					Database.updateClient(new Client(line));
			} catch (IOException e) {
				statusCode = 500;
				xmlString = new Report(e.toString()).toXML();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else if (paths.length == 2 && paths[0].equals("product")) {
			// Create or update product
			try {
				String line = br.readLine();
				System.out.println("\trequestbody = '" + line + "'");
				if(paths[1].equals("null")) 
					Database.insertProduct(new Product(line));
				else 
					Database.updateProduct(new Product(line));
			} catch (IOException e) {
				statusCode = 500;
				xmlString = new Report(e.toString()).toXML();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else if (paths.length == 2 && paths[0].equals("cartelement") && paths[1].startsWith("Client_")) {
			 // Insert cart element for a specific client
			try {
				String line = br.readLine();
				System.out.println("\trequestbody = '" + line + "'");
				Database.insertCartElement(new CartElement(line));
				System.out.println("Cart element added");
			} catch (IOException e) {
				statusCode = 500;
				xmlString = new Report(e.toString()).toXML();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else if (paths.length == 2 && paths[0].equals("cartelement") && !paths[1].startsWith("Client_")) {
			// Update cart element for a specific client
			try {
				String line = br.readLine();
				System.out.println("\trequestbody = '" + line + "'");
				Database.updateCartElement(new CartElement(line));
				System.out.println("Cart element updated");
			} catch (IOException e) {
				statusCode = 500;
				xmlString = new Report(e.toString()).toXML();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else if (paths.length == 2 && paths[0].equals("category")) {
			// Create category
			try {
				String line = br.readLine();
				System.out.println("\trequestbody = '" + line + "'");
				Database.insertCategory(new Category(line));
				System.out.println("Category added");
			} catch (IOException e) {
				statusCode = 500;
				xmlString = new Report(e.toString()).toXML();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			statusCode = 400;
			xmlString = new Report("False URL").toXML();
		}
		setResponse(exchange, statusCode, xmlString);
	}

    /**
     * It handles GET requests based on the specified path components.
     *
     * @param exchange The HTTP exchange object.
     * @param paths    The components of the request path.
     */	
	private void get(HttpExchange exchange, String[] paths) {
		int statusCode = 200;
		String xmlString = "";
		
		 // Check the path components and perform the corresponding action
		if (paths.length == 1 && paths[0].equals("productlist")) {
			// Get the list of all products
			try {
				ProductList pl = Database.readProducts(null, null);
				xmlString = pl.toXML();
			} catch (SQLException e) {
				statusCode = 500;
				xmlString = new Report(e.toString()).toXML();
			}
		} else if (paths.length == 2 && paths[0].equals("productlist")) {
			// Get the list of products based on the given criteria
			try {
				ProductList pl;
				if(paths[1].equals("true")) 			 				  // all active products
					pl = Database.readProducts(true, null);
				else if(paths[1].equals("false")) 		 				  // all inactive products
					pl = Database.readProducts(false, null);
				else
					pl = Database.readProducts(true, paths[1]); 		  // all active products from category given by paths[1]
				xmlString = pl.toXML();
			} catch (SQLException e) {
				statusCode = 500;
				xmlString = new Report(e.toString()).toXML();
			}
		} else if (paths.length == 1 && paths[0].equals("categorylist")) {
			// Get the list of all categories
			try {
				CategoryList pl = Database.readCategories();
				xmlString = pl.toXML();
			} catch (SQLException e) {
				statusCode = 500;
				xmlString = new Report(e.toString()).toXML();
			}
		} else if (paths.length == 1 && paths[0].equals("clientlist")) { 
			// Get the list of all clients
			try {
				ClientList cl;
				cl = Database.readClients(null, null);
				xmlString = cl.toXML();
			} catch (SQLException e) {
				statusCode = 500;
				xmlString = new Report(e.toString()).toXML();
			}
		} else if (paths.length == 2 && paths[0].equals("client")) { 		
			 // Get the list of an active client with the specified userId
			try {
				ClientList kl = Database.readClients(paths[1], true);
				xmlString = kl.toXML();
			} catch (SQLException e) {
				statusCode = 500;
				xmlString = new Report(e.toString()).toXML();
			}
		} else if (paths.length == 2 && paths[0].equals("clientlist") && (paths[1].equals("true") || paths[1].equals("false"))) {
			// Get the list of all clients, active or inactive
			try {
				ClientList cl;
				if(paths[1].equals("true"))  								// all active clients
					cl = Database.readClients(null, true);
				else 						 								// all inactive clients
					cl = Database.readClients(null, false);
				xmlString = cl.toXML();
			} catch (SQLException e) {
				statusCode = 500;
				xmlString = new Report(e.toString()).toXML();
			}
		} else if (paths.length == 1 && paths[0].equals("cart")) { 			
			// Get the list of all orders
			try {
				Cart cart = Database.readCart(null);
				xmlString = cart.toXML();
			} catch (SQLException e) {
				statusCode = 500;
				xmlString = new Report(e.toString()).toXML();
			}
		} else if (paths.length == 2 && paths[0].equals("cart") && paths[1].startsWith("Client_")) { 
			// Get the list of orders for a specific client with clientId
			try {
				Cart cart = Database.readCart(paths[1]);
				xmlString = cart.toXML();
			} catch (SQLException e) {
				statusCode = 500;
				xmlString = new Report(e.toString()).toXML();
			}
		} else if (paths.length == 2 && paths[0].equals("cart") && paths[1].equals("false")) { 		
			// Get the list of orders that are not sent yet
			try {
				Cart cart = Database.readCart("false");
				xmlString = cart.toXML();
			} catch (SQLException e) {
				statusCode = 500;
				xmlString = new Report(e.toString()).toXML();
			}
		} else {
			statusCode = 400;
			xmlString = new Report("False URL").toXML();
		}
		setResponse(exchange, statusCode, xmlString);
	}

    /**
     * It sets the HTTP response for the given exchange with the specified status code and XML content.
     *
     * @param exchange   The HTTP exchange object.
     * @param statusCode The HTTP status code to set in the response.
     * @param xmlString  The XML content to include in the response body.
     */
	private void setResponse(HttpExchange exchange, int statusCode, String xmlString) {
		// Set the HTTP response headers
		if (xmlString.length() > 0)
			xmlString = XMLHandler.XML_PROLOG_UTF8 + xmlString;
		System.out.println("\tstatusCode = " + statusCode + "\n\tresponsebody = " + xmlString + " ");
		exchange.getResponseHeaders().set("Content-Type", "application/xml; charset=UTF-8");
		
		// Write the response body and close the output stream
		byte[] bytes = xmlString.getBytes(StandardCharsets.UTF_8);
		try {
			exchange.sendResponseHeaders(statusCode, statusCode != 204 ? bytes.length : -1);
			OutputStream os = exchange.getResponseBody();
			if (statusCode != 204)
				os.write(bytes);
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
