package pharmaClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import pharmaClasses.Report;
import pharmaClasses.XMLHandler;

/**
 * This class provides static methods for interacting with a server via HTTP requests.
 */
public class ServiceFunctions {

	// The base URL for all HTTP requests
	private static final String BASIS_URL = "http://localhost:4711/";
	
    /**
     * Method that sends a GET request to the server and retrieves the response.
     *
     * @param item The item to request from the server.
     * @param id   The optional ID parameter for the request.
     * @return The server's response as a String.
     * @throws PharmaException If an error occurs during the HTTP request.
     */
	public static String get(String item, String id) throws PharmaException {
		String url = BASIS_URL + item;
		
		 // Append the ID to the URL if provided
		if(id != null && id.length() > 0) {
			String encodedId = URLEncoder.encode(id, StandardCharsets.UTF_8);
			url += "/" + encodedId;
		}
		
		try {
			// Build and send the HTTP GET request
			URI uri = new URI(url);
			HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
			HttpClient client = HttpClient.newHttpClient();
			HttpResponse<byte[]> response = client.send(request, BodyHandlers.ofByteArray()); 
			
			// Process the response
			int statusCode = response.statusCode();
			String line = new String(response.body());
			if(statusCode == 200)
				return line;
			else
				throw new PharmaException(new Report(line).getText());
		} catch (URISyntaxException | IOException | InterruptedException e) {
			throw new PharmaException(e.toString());
		}
		
	}
	
    /**
     * It sends a POST request to the server with XML data.
     *
     * @param item   The item to post to the server.
     * @param id     The ID parameter for the request.
     * @param detail The XML data to be sent in the request body.
     * @throws PharmaException If an error occurs during the HTTP request.
     */
	public static void post(String item, String id, String detail) throws 
	PharmaException {
		String url = BASIS_URL + item + "/" + id;
		
		// Add XML prolog to the XML data to be sent
		detail = XMLHandler.XML_PROLOG_UTF8 + detail;
		
		try {
			 // Build the HTTP POST request
			URI uri = new URI(url);
			HttpRequest request = HttpRequest.newBuilder(uri).header("Content-Type", "application/xml; charset=UTF-8")
					.POST(BodyPublishers.ofString(detail, StandardCharsets.UTF_8)).build();				        
	        HttpClient client = HttpClient.newHttpClient();
			
			System.out.println("\nHTTP Request URI: " + uri);
	        System.out.println("HTTP Request Method: " + request.method());
	        System.out.println("HTTP Request Headers: " + request.headers());
	        System.out.println("HTTP Request Body: " + detail);
	        
	        // Send the request
	        HttpResponse<byte[]> response = client.send(request, BodyHandlers.ofByteArray()); 
			
			System.out.println("HTTP Response Code: " + response.statusCode());
	        System.out.println("HTTP Response Headers: " + response.headers());
	        System.out.println("HTTP Response Body: " + new String(response.body()));
	        
	        //  Process the response
			int statusCode = response.statusCode();
			if(statusCode != 201)
				throw new PharmaException(new Report(new String(response.body())).getText());
		} catch (URISyntaxException | IOException | InterruptedException e) {
			throw new PharmaException(e.toString());
		}	
	}
	
    /**
     * It sends a DELETE request to the server.
     *
     * @param item The item to delete from the server.
     * @param id   The optional ID parameter for the request.
     * @throws PharmaException If an error occurs during the HTTP request.
     */
	public static void delete(String item, String id) throws PharmaException {
		String url = BASIS_URL + item;
		
		// Append the ID to the URL if provided
		if(id != null && id.length() > 0)
			url += "/" + id;
		
		try {
			// Build and send the HTTP DELETE request
			URI uri = new URI(url);
			HttpRequest request = HttpRequest.newBuilder(uri).DELETE().build();
			HttpClient client = HttpClient.newHttpClient();
			HttpResponse<byte[]> response = client.send(request, BodyHandlers.ofByteArray()); 
			
			// Check for success, throw exception if not
			int statusCode = response.statusCode();
			if(statusCode != 204)
				throw new PharmaException(new Report(new String(response.body())).getText());
		} catch (URISyntaxException | IOException | InterruptedException e) {
			throw new PharmaException(e.toString());
		}
	}
}
