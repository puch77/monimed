package pharmaServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.sun.net.httpserver.HttpServer;

import pharmaClasses.Client;
import pharmaClasses.ClientList;

public class PharmaServer {

	public static void main(String[] args) {
		InetAddress inet;
		try {
			// Create an HTTP server that listens on port 4711
			inet = InetAddress.getByName("localhost");
			InetSocketAddress addr = new InetSocketAddress(inet, 4711);
			HttpServer server = HttpServer.create(addr, 0);
			
			// Create a context for the / path and set the PharmaHandler as the handler
			server.createContext("/", new PharmaHandler());
			server.setExecutor(Executors.newCachedThreadPool());
			
			// Start the server
			server.start();
			System.out.println("PharmaServer has started!");	
			
			/**
			 *  Creates necessary database tables if they do not exist
			 */
			Database.createCategoryTable();
			Database.createProductTable();	
			Database.createClientTable();
			Database.createCartTable();
			
			/**
			 *  Check if there is an admin client with ID "Admin_1"
			 *  If no admin client is found, insert a default admin client into the database
			 */			
			ClientList cl = Database.readClients("Admin_1", true);
			if(cl.getClients().isEmpty())
				Database.insertClient(new Client(null, "company_name", "12345", "address", "email", "+43-XXXXXXXXX", "contact_name", 
					LocalDate.now(), true, true));
			
			// Wait for user input before stopping the server
			System.in.read();
			server.stop(0);
			((ExecutorService) server.getExecutor()).shutdown();
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
	}

}
