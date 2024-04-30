package pharmaServer;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import pharmaClasses.Cart;
import pharmaClasses.CartElement;
import pharmaClasses.Category;
import pharmaClasses.CategoryList;
import pharmaClasses.Client;
import pharmaClasses.ClientList;
import pharmaClasses.Product;
import pharmaClasses.ProductList;

public class Database {
	private static final String DB_LOCATION = "..\\DB";
	private static final String CONNECTION = "jdbc:derby:" + DB_LOCATION + ";create = true";

	private static final String PRODUCT_TABLE = "Product";
	private static final String PRODUCT_TABLE_ID = "ProductID";
	private static final String PRODUCT_TABLE_NAME = "ProductName";
	private static final String PRODUCT_TABLE_DESCRIPTION = "ProductDescription";
	private static final String PRODUCT_TABLE_PRICE = "ProductPrice";
	private static final String PRODUCT_TABLE_CATEGORYID = "ProductCategoryID";
	private static final String PRODUCT_TABLE_AMOUNT = "ProductAmount";
	private static final String PRODUCT_TABLE_ACTIVE = "isProductActive";

	private static final String CLIENT_TABLE = "Client";
	private static final String CLIENT_TABLE_ID = "ClientId";
	private static final String CLIENT_TABLE_NAME = "ClientName";
	private static final String CLIENT_TABLE_PASS = "ClientPass";
	private static final String CLIENT_TABLE_ADDRESS = "ClientAddress";
	private static final String CLIENT_TABLE_EMAIL = "ClientEmail";
	private static final String CLIENT_TABLE_TEL = "ClientTelefon";
	private static final String CLIENT_TABLE_CONTACTPERSON = "ClientContactPerson";
	private static final String CLIENT_TABLE_DATEFROM = "ClientDateFrom";
	private static final String CLIENT_TABLE_RIGHTS = "ClientRights";
	private static final String CLIENT_TABLE_ACTIVE = "isClientActive";

	private static final String CART_TABLE = "Cart";
	private static final String CART_TABLE_ID = "CartId";
	private static final String CART_TABLE_CLIENTID = "CartClientId";
	private static final String CART_TABLE_PRODUCTID = "CartProductId";
	private static final String CART_TABLE_AMOUNT = "CartProductAmount";
	private static final String CART_TABLE_ORDERDATE = "CartProductOrderDate";
	private static final String CART_TABLE_SENT = "CartProductStatus";

	private static final String CATEGORY_TABLE = "Category";
	private static final String CATEGORY_TABLE_ID = "CategoryId";
	private static final String CATEGORY_TABLE_NAME = "CategoryName";

	/**
	 * Creates the client table in the database if it does not exist.
	 *
	 * @throws SQLException If an error occurs while executing SQL statements.
	 */
	public static void createClientTable() throws SQLException {
		try (Connection conn = DriverManager.getConnection(CONNECTION);
				Statement stat = conn.createStatement();
				ResultSet rs = conn.getMetaData().getTables(null, null, CLIENT_TABLE.toUpperCase(),
						new String[] { "TABLE" })) {

			if (rs.next()) {
				return;
			}
			String ct = "CREATE TABLE " + CLIENT_TABLE + " (" + CLIENT_TABLE_ID + " VARCHAR(25) PRIMARY KEY, "
					+ CLIENT_TABLE_NAME + " VARCHAR(200), " + CLIENT_TABLE_PASS + " VARCHAR(20), "
					+ CLIENT_TABLE_ADDRESS + " VARCHAR(200), " + CLIENT_TABLE_EMAIL + " VARCHAR(50), "
					+ CLIENT_TABLE_TEL + " VARCHAR(50), " + CLIENT_TABLE_CONTACTPERSON + " VARCHAR(200), "
					+ CLIENT_TABLE_DATEFROM + " DATE, " + CLIENT_TABLE_RIGHTS + " BOOLEAN, " + CLIENT_TABLE_ACTIVE
					+ " BOOLEAN)";

			stat.execute(ct);
			System.out.println(CLIENT_TABLE + " created");
		} catch (SQLException e) {
			throw e;
		}
	}

	/**
	 * Inserts a new client record into the client table of the database. It uses a
	 * PreparedStatement to safely insert the client information into the database.
	 *
	 * @param client The Client object containing the information to be inserted
	 *               into the database.
	 * @throws SQLException If an error occurs while executing SQL statements.
	 */
	public static void insertClient(Client client) throws SQLException {
		String insert = "INSERT INTO " + CLIENT_TABLE + " (" + CLIENT_TABLE_ID + ", " + CLIENT_TABLE_NAME + ", "
				+ CLIENT_TABLE_PASS + ", " + CLIENT_TABLE_ADDRESS + ", " + CLIENT_TABLE_EMAIL + ", " + CLIENT_TABLE_TEL
				+ ", " + CLIENT_TABLE_CONTACTPERSON + ", " + CLIENT_TABLE_DATEFROM + ", " + CLIENT_TABLE_RIGHTS + ", "
				+ CLIENT_TABLE_ACTIVE + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (Connection conn = DriverManager.getConnection(CONNECTION);
				PreparedStatement stat = conn.prepareStatement(insert)) {

			String nextClientID = getNextClientID(client.getAdmin());
			stat.setString(1, nextClientID);
			stat.setString(2, client.getName());
			stat.setString(3, client.getPass());
			stat.setString(4, client.getAddress());
			stat.setString(5, client.getEmail());
			stat.setString(6, client.getTel());
			stat.setString(7, client.getContactPerson());
			stat.setDate(8, Date.valueOf(client.getDateFrom()));
			stat.setBoolean(9, client.getAdmin());
			stat.setBoolean(10, client.getActive());
			stat.executeUpdate();

			System.out.println("Client inserted successfully");
		} catch (SQLException e) {
			throw e;
		}
	}

	/**
	 * Retrieves the next available client ID based on the existing IDs in the
	 * client table.
	 *
	 * This method generates the next client ID by examining the existing client IDs
	 * in the client table. If the table contains client IDs with the specified
	 * prefix (either "Client_" or "Admin_"), the method finds the maximum numeric
	 * part, increments it, and appends it to the prefix. If the table is empty, the
	 * method starts with 1.
	 *
	 * @param isAdmin A boolean indicating whether the client has the admin rights
	 *                (true for "Admin_", false for "Client_").
	 * @return The next available client ID as a String.
	 * @throws SQLException If an error occurs while executing SQL statements.
	 */
	public static String getNextClientID(Boolean isAdmin) throws SQLException {
		String prefix = isAdmin ? "Admin_" : "Client_";
		String query = "SELECT MAX(CASE WHEN " + CLIENT_TABLE_ID + " LIKE '" + prefix + "%' THEN " + CLIENT_TABLE_ID
				+ " ELSE NULL END) FROM " + CLIENT_TABLE;

		try (Connection conn = DriverManager.getConnection(CONNECTION);
				Statement statement = conn.createStatement();
				ResultSet resultSet = statement.executeQuery(query)) {
			if (resultSet.next()) {
				String maxID = resultSet.getString(1);
				System.out.println(maxID);
				if (maxID != null) {
					// Extract the numeric part, increment, and append to "Client_"
					int numericPart = Integer.parseInt(maxID.substring(prefix.length()));
					return prefix + (numericPart + 1);
				}
			}
		}
		// If the table is empty, start with "Client_1" or "Admin_1"
		return prefix + "1";
	}

	/**
	 * Retrieves a list of clients from the client table based on specified
	 * criteria. If a specific user ID is provided, it retrieves information for
	 * that user. If the 'isActive' parameter is provided, it filters clients based
	 * on their active status. Otherwise, it retrieves all clients.
	 *
	 * @param user     The user ID for which to retrieve information (optional).
	 * @param isActive A boolean indicating whether to filter clients based on their
	 *                 active status (optional).
	 * @return A ClientList object containing the retrieved list of clients.
	 * @throws SQLException If an error occurs while executing SQL statements.
	 */
	public static ClientList readClients(String user, Boolean isActive) throws SQLException {
		String select = "SELECT * FROM " + CLIENT_TABLE;
		if (user != null) {
			select += " WHERE " + CLIENT_TABLE_ID + "=?";
			if (isActive != null) {
				select += " AND " + CLIENT_TABLE_ACTIVE + "=?";
			}
		} else if (isActive != null) {
			select += " WHERE " + CLIENT_TABLE_ACTIVE + "=?";
		}

		try (Connection conn = DriverManager.getConnection(CONNECTION);
				PreparedStatement stat = conn.prepareStatement(select)) {

			int index = 1;
			if (user != null)
				stat.setString(index++, user);

			if (isActive != null)
				stat.setBoolean(index, isActive);

			try (ResultSet rs = stat.executeQuery()) {
				ArrayList<Client> clients = new ArrayList<>();
				while (rs.next()) {
					clients.add(new Client(rs.getString(CLIENT_TABLE_ID), rs.getString(CLIENT_TABLE_NAME),
							rs.getString(CLIENT_TABLE_PASS), rs.getString(CLIENT_TABLE_ADDRESS),
							rs.getString(CLIENT_TABLE_EMAIL), rs.getString(CLIENT_TABLE_TEL),
							rs.getString(CLIENT_TABLE_CONTACTPERSON), rs.getDate(CLIENT_TABLE_DATEFROM).toLocalDate(),
							rs.getBoolean(CLIENT_TABLE_RIGHTS), rs.getBoolean(CLIENT_TABLE_ACTIVE)));
				}
				return new ClientList(clients);
			}
		} catch (SQLException e) {
			throw e;
		}
	}

	/**
	 * Deletes a client from the client table based on the provided user ID.
	 *
	 * @param userID The user ID of the client to be deleted.
	 * @throws SQLException If an error occurs while executing SQL statements.
	 */
	public static void deleteClient(String userID) throws SQLException {
		try (Connection connection = DriverManager.getConnection(CONNECTION)) {
			String deleteQuery = "DELETE FROM " + CLIENT_TABLE + " WHERE " + CLIENT_TABLE_ID + " = ?";

			try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
				preparedStatement.setString(1, userID);
				int rowsAffected = preparedStatement.executeUpdate();
				if (rowsAffected > 0) {
					System.out.println("\nClient with ID " + userID + " was deleted.");
				} else {
					System.out.println("\nNone Client with ID " + userID + " was found.");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Updates the information of a client in the client table based on the provided
	 * Client object.
	 *
	 * @param client The Client object containing updated information.
	 * @throws SQLException If an error occurs while executing SQL statements.
	 */
	public static void updateClient(Client client) throws SQLException {
		try (Connection connection = DriverManager.getConnection(CONNECTION)) {
			String updateQuery = "UPDATE " + CLIENT_TABLE + " SET " + CLIENT_TABLE_NAME + " = ?, " + CLIENT_TABLE_PASS
					+ " = ?, " + CLIENT_TABLE_ADDRESS + " = ?, " + CLIENT_TABLE_EMAIL + " = ?, " + CLIENT_TABLE_TEL
					+ " = ?, " + CLIENT_TABLE_CONTACTPERSON + " = ?, " + CLIENT_TABLE_DATEFROM + " = ?, "
					+ CLIENT_TABLE_RIGHTS + " = ?, " + CLIENT_TABLE_ACTIVE + " = ? " + "WHERE " + CLIENT_TABLE_ID
					+ " = ?";

			try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
				preparedStatement.setString(1, client.getName());
				preparedStatement.setString(2, client.getPass());
				preparedStatement.setString(3, client.getAddress());
				preparedStatement.setString(4, client.getEmail());
				preparedStatement.setString(5, client.getTel());
				preparedStatement.setString(6, client.getContactPerson());
				preparedStatement.setDate(7, Date.valueOf(client.getDateFrom()));
				preparedStatement.setBoolean(8, client.getAdmin());
				preparedStatement.setBoolean(9, client.getActive());
				preparedStatement.setString(10, client.getUserId());

				int rowsUpdated = preparedStatement.executeUpdate();

				if (rowsUpdated > 0)
					System.out.println("Client with ID " + client.getUserId() + " updated successfully.");
				else
					System.out.println("No records were updated for Client with ID " + client.getUserId());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates the category table in the database if it does not exist.
	 *
	 * @throws SQLException If an error occurs while executing SQL statements.
	 */
	public static void createCategoryTable() throws SQLException {
		try (Connection conn = DriverManager.getConnection(CONNECTION);
				Statement stat = conn.createStatement();
				ResultSet rs = conn.getMetaData().getTables(null, null, CATEGORY_TABLE.toUpperCase(),
						new String[] { "TABLE" })) {

			// set preallocation size for identity to 1
			stat.executeUpdate(
					"CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.language.sequence.preallocator', '1')");

			if (rs.next()) {
				return;
			}
			String ct = "CREATE TABLE " + CATEGORY_TABLE + " (" + CATEGORY_TABLE_ID
					+ " INTEGER GENERATED ALWAYS AS IDENTITY, " + CATEGORY_TABLE_NAME + " VARCHAR(200), "
					+ "PRIMARY KEY (" + CATEGORY_TABLE_ID + "))";

			stat.execute(ct);
			System.out.println(CATEGORY_TABLE + " created");
		} catch (SQLException e) {
			throw e;
		}
	}

	/**
	 * Inserts a new category record into the category table of the database. It
	 * uses a PreparedStatement to safely insert the category information into the
	 * database.
	 *
	 * @param category The Category object containing the information to be inserted
	 *                 into the database.
	 * @throws SQLException If an error occurs while executing SQL statements.
	 */
	public static void insertCategory(Category category) throws SQLException {
		String insert = "INSERT INTO " + CATEGORY_TABLE + " (" + CATEGORY_TABLE_NAME + ") VALUES (?)";

		try (Connection conn = DriverManager.getConnection(CONNECTION);
				PreparedStatement stat = conn.prepareStatement(insert)) {

			stat.setString(1, category.getName());

			// Execute the statement and get the auto-generated keys
			int output = stat.executeUpdate();
			if (output > 0)
				System.out.println("Product inserted succesfully");
			else
				throw new SQLException("Inserting product failed");

		} catch (SQLException e) {
			System.err.println("Error inserting product: " + e.getMessage());
			throw e;
		}
	}

	/**
	 * Retrieves a list of categories from the category table.
	 *
	 * @return A CategoryList object containing the retrieved list of categories.
	 * @throws SQLException If an error occurs while executing SQL statements.
	 */
	public static CategoryList readCategories() throws SQLException {
		String select = "SELECT * FROM " + CATEGORY_TABLE;

		try (Connection conn = DriverManager.getConnection(CONNECTION);
				PreparedStatement stat = conn.prepareStatement(select);
				ResultSet rs = stat.executeQuery()) {
			ArrayList<Category> categories = new ArrayList<>();
			while (rs.next()) {
				categories.add(new Category(rs.getInt(CATEGORY_TABLE_ID), rs.getString(CATEGORY_TABLE_NAME)));
			}
			System.out.println("\nCategoryList was filled.");

			return new CategoryList(categories);
		} catch (SQLException e) {
			throw e;
		}
	}

	/**
	 * Deletes a category from the category table based on the provided name.
	 *
	 * @param name The name of the category to be deleted.
	 * @throws SQLException If an error occurs while executing SQL statements.
	 */
	public static void deleteCategory(String name) throws SQLException {
		try (Connection connection = DriverManager.getConnection(CONNECTION)) {
			String deleteQuery = "DELETE FROM " + CATEGORY_TABLE + " WHERE " + CATEGORY_TABLE_NAME + " = ?";
			try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
				preparedStatement.setString(1, name);
				int rowsAffected = preparedStatement.executeUpdate();
				if (rowsAffected > 0)
					System.out.println("\nCategory with name " + name + " was deleted.");
				else
					System.out.println("Category with name " + name + " not found.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retrieves the name of a category from the category table based on the
	 * provided category ID.
	 *
	 * @param categoryId The ID of the category for which to retrieve the name.
	 * @return The name of the category, or null if the category with the given ID
	 *         is not found.
	 * @throws SQLException If an error occurs while executing SQL statements.
	 */
	public static String readCategoryName(int categoryId) throws SQLException {
		String select = "SELECT * FROM " + CATEGORY_TABLE + " WHERE " + CATEGORY_TABLE_ID + " = ?";

		try (Connection conn = DriverManager.getConnection(CONNECTION);
				PreparedStatement stat = conn.prepareStatement(select)) {

			stat.setInt(1, categoryId);

			try (ResultSet rs = stat.executeQuery()) {
				return rs.next() ? rs.getString(CATEGORY_TABLE_NAME) : null;
			}

		} catch (SQLException e) {
			throw e;
		}
	}

	/**
	 * Creates the product table in the database if it does not exist.
	 *
	 * @throws SQLException If an error occurs while executing SQL statements.
	 */
	public static void createProductTable() throws SQLException {
		try (Connection conn = DriverManager.getConnection(CONNECTION);
				Statement stat = conn.createStatement();
				ResultSet rs = conn.getMetaData().getTables(null, null, PRODUCT_TABLE.toUpperCase(),
						new String[] { "TABLE" })) {

			if (rs.next()) {
				return;
			}
			String ct = "CREATE TABLE " + PRODUCT_TABLE + " (" + PRODUCT_TABLE_ID
					+ " INTEGER GENERATED ALWAYS AS IDENTITY, " + PRODUCT_TABLE_NAME + " VARCHAR(200), "
					+ PRODUCT_TABLE_DESCRIPTION + " VARCHAR(200), " + PRODUCT_TABLE_PRICE + " DOUBLE, "
					+ PRODUCT_TABLE_AMOUNT + " INTEGER, " + PRODUCT_TABLE_ACTIVE + " BOOLEAN, "
					+ PRODUCT_TABLE_CATEGORYID + " INTEGER, " + "PRIMARY KEY (" + PRODUCT_TABLE_ID + "),"
					+ "FOREIGN KEY (" + PRODUCT_TABLE_CATEGORYID + ") REFERENCES " + CATEGORY_TABLE + "("
					+ CATEGORY_TABLE_ID + "))";

			stat.execute(ct);
			System.out.println(PRODUCT_TABLE + " created");
		} catch (SQLException e) {
			throw e;
		}
	}

	/**
	 * Inserts a new product record into the product table of the database. It uses
	 * a PreparedStatement to safely insert the product information into the
	 * database.
	 *
	 * @param product The Product object containing the information to be inserted
	 *                into the database.
	 * @throws SQLException If an error occurs while executing SQL statements.
	 */
	public static void insertProduct(Product product) throws SQLException {
		String insert = "INSERT INTO " + PRODUCT_TABLE + " (" + PRODUCT_TABLE_NAME + "," + PRODUCT_TABLE_DESCRIPTION
				+ "," + PRODUCT_TABLE_PRICE + "," + PRODUCT_TABLE_AMOUNT + "," + PRODUCT_TABLE_ACTIVE + ","
				+ PRODUCT_TABLE_CATEGORYID + ") VALUES (?, ?, ?, ?, ?, ?)";

		try (Connection conn = DriverManager.getConnection(CONNECTION);
				PreparedStatement stat = conn.prepareStatement(insert)) {

			stat.setString(1, product.getName());
			stat.setString(2, product.getDescription());
			stat.setDouble(3, product.getPrice());
			stat.setInt(4, product.getAmount());
			stat.setBoolean(5, product.getActive());
			stat.setInt(6, product.getCategory().getId());

			int output = stat.executeUpdate();
			if (output > 0) {
				System.out.println("Product inserted successfully");
			} else {
				throw new SQLException("Inserting product failed.");
			}
		} catch (SQLException e) {
			System.err.println("Error inserting product: " + e.getMessage());
			throw e;
		}
	}

	/**
	 * Retrieves a list of products from the product table based on specified
	 * criteria. If the 'isActive' parameter is provided, it filters products based
	 * on their active status. If a specific product category ID is provided, it
	 * filters products based on that category. Otherwise, it retrieves all
	 * products.
	 *
	 * @param isActive A boolean indicating whether to filter products based on
	 *                 their active status (optional).
	 * @param category The ID of the product category for filtering (optional).
	 * @return A ProductList object containing the retrieved list of products.
	 * @throws SQLException If an error occurs while executing SQL statements.
	 */
	public static ProductList readProducts(Boolean isActive, String category) throws SQLException {
		if (category != null && category.contains("+"))
			category = category.replaceAll("\\+", " ");

		String select = "SELECT * FROM " + PRODUCT_TABLE;
		if (isActive != null && category == null) {
			select += " WHERE " + PRODUCT_TABLE_ACTIVE + "=?";
		} else if (isActive != null && category != null) {
			select += " WHERE " + PRODUCT_TABLE_ACTIVE + "=?" + " AND " + PRODUCT_TABLE_CATEGORYID + "=?";
		}

		try (Connection conn = DriverManager.getConnection(CONNECTION);
				PreparedStatement stat = conn.prepareStatement(select)) {
			if (isActive != null && category == null)
				stat.setBoolean(1, isActive);
			else if (isActive != null && category != null) {
				stat.setBoolean(1, isActive);
				stat.setInt(2, Integer.valueOf(category));
			}

			try (ResultSet rs = stat.executeQuery()) {
				ArrayList<Product> products = new ArrayList<>();
				while (rs.next()) {
					products.add(new Product(rs.getInt(PRODUCT_TABLE_ID), rs.getString(PRODUCT_TABLE_NAME),
							rs.getString(PRODUCT_TABLE_DESCRIPTION), rs.getDouble(PRODUCT_TABLE_PRICE),
							new Category(rs.getInt(PRODUCT_TABLE_CATEGORYID),
									readCategoryName(rs.getInt(PRODUCT_TABLE_CATEGORYID))),
							rs.getInt(PRODUCT_TABLE_AMOUNT), rs.getBoolean(PRODUCT_TABLE_ACTIVE)));
				}

				return new ProductList(products);
			}
		} catch (SQLException e) {
			throw e;
		}
	}

	/**
	 * Deletes a product from the product table based on the provided product ID.
	 *
	 * @param id The ID of the product to be deleted.
	 * @throws SQLException If an error occurs while executing SQL statements.
	 */
	public static void deleteProduct(int id) throws SQLException {
		String deleteQuery = "DELETE FROM " + PRODUCT_TABLE + " WHERE " + PRODUCT_TABLE_ID + " = ?";

		try (Connection connection = DriverManager.getConnection(CONNECTION);
				PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
			preparedStatement.setInt(1, id);
			int rowsAffected = preparedStatement.executeUpdate();
			if (rowsAffected > 0) {
				System.out.println("\nProduct with ID " + id + " was deleted.");
			} else {
				System.out.println("\nNo product with ID " + id + " was found.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Updates the information of a product in the product table based on the
	 * provided Product object.
	 *
	 * @param product The Product object containing updated information.
	 * @throws SQLException If an error occurs while executing SQL statements.
	 */
	public static void updateProduct(Product product) throws SQLException {
		try (Connection connection = DriverManager.getConnection(CONNECTION)) {
			String updateQuery = "UPDATE " + PRODUCT_TABLE + " SET " + PRODUCT_TABLE_NAME + " = ?, "
					+ PRODUCT_TABLE_DESCRIPTION + " = ?, " + PRODUCT_TABLE_PRICE + " = ?, " + PRODUCT_TABLE_AMOUNT
					+ " = ?, " + PRODUCT_TABLE_ACTIVE + " = ?, " + PRODUCT_TABLE_CATEGORYID + " = ? " + "WHERE "
					+ PRODUCT_TABLE_ID + " = ?";

			try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
				preparedStatement.setString(1, product.getName());
				preparedStatement.setString(2, product.getDescription());
				preparedStatement.setDouble(3, product.getPrice());
				preparedStatement.setInt(4, product.getAmount());
				preparedStatement.setBoolean(5, product.getActive());
				preparedStatement.setInt(6, product.getCategory().getId());
				preparedStatement.setInt(7, product.getId());

				int rowsUpdated = preparedStatement.executeUpdate();

				if (rowsUpdated > 0)
					System.out.println("Product with ID " + product.getId() + " updated successfully.");
				else
					System.out.println("No records were updated for product with ID " + product.getId());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates the order table in the database if it does not exist.
	 *
	 * @throws SQLException If an error occurs while executing SQL statements.
	 */
	public static void createCartTable() throws SQLException {
		try (Connection conn = DriverManager.getConnection(CONNECTION);
				Statement stat = conn.createStatement();
				ResultSet rs = conn.getMetaData().getTables(null, null, CART_TABLE.toUpperCase(),
						new String[] { "TABLE" })) {

			if (rs.next()) {
				return;
			}
			String ct = "CREATE TABLE " + CART_TABLE + " (" + CART_TABLE_ID + " INTEGER GENERATED ALWAYS AS IDENTITY, "
					+ CART_TABLE_CLIENTID + " VARCHAR(25), " + CART_TABLE_PRODUCTID + " INTEGER, " + CART_TABLE_AMOUNT
					+ " INTEGER, " + CART_TABLE_ORDERDATE + " DATE, " + CART_TABLE_SENT + " BOOLEAN, " + "PRIMARY KEY ("
					+ CART_TABLE_ID + ")," + "FOREIGN KEY (" + CART_TABLE_CLIENTID + ") REFERENCES " + CLIENT_TABLE
					+ "(" + CLIENT_TABLE_ID + ")," + "FOREIGN KEY (" + CART_TABLE_PRODUCTID + ") REFERENCES "
					+ PRODUCT_TABLE + "(" + PRODUCT_TABLE_ID + ")" + ")";

			stat.execute(ct);
			System.out.println(CART_TABLE + " created");
		} catch (SQLException e) {
			throw e;
		}
	}

	/**
	 * Inserts a new cart element record into the cart table of the database. It
	 * uses a PreparedStatement to safely insert the cart element information into
	 * the database.
	 *
	 * @param cartElement The CartElement object containing the information to be
	 *                    inserted into the database.
	 * @throws SQLException If an error occurs while executing SQL statements.
	 */
	public static void insertCartElement(CartElement cartElement) throws SQLException {
		String insert = "INSERT INTO " + CART_TABLE + " (" + CART_TABLE_CLIENTID + "," + CART_TABLE_PRODUCTID + ","
				+ CART_TABLE_AMOUNT + "," + CART_TABLE_ORDERDATE + "," + CART_TABLE_SENT + ") VALUES (?, ?, ?, ?, ?)";

		try (Connection conn = DriverManager.getConnection(CONNECTION);
				PreparedStatement stat = conn.prepareStatement(insert)) {
			stat.setString(1, cartElement.getClientId());
			stat.setInt(2, cartElement.getProduct().getId());
			stat.setInt(3, cartElement.getAmount());
			stat.setDate(4, Date.valueOf(cartElement.getOrderDate()));
			stat.setBoolean(5, cartElement.getIsSent());
			stat.executeUpdate();

			System.out.println("Cart element inserted successfully");
		} catch (SQLException e) {
			throw e;
		}
	}

	/**
	 * Retrieves a cart containing orders from the cart table based on the client
	 * ID.
	 *
	 * @param clientId The client ID for which to retrieve cart elements (optional).
	 *                 If 'false' is provided, it filters unsent cart elements.
	 * @return A Cart object containing the retrieved list of cart elements.
	 * @throws SQLException If an error occurs while executing SQL statements.
	 */
	public static Cart readCart(String clientId) throws SQLException {
		String select = "SELECT * FROM " + CART_TABLE + " INNER JOIN " + PRODUCT_TABLE + " ON " + CART_TABLE + "."
				+ CART_TABLE_PRODUCTID + "=" + PRODUCT_TABLE + "." + PRODUCT_TABLE_ID;
		if (clientId != null && clientId.startsWith("Client_"))
			select += " WHERE " + CART_TABLE_CLIENTID + "=?";
		else if (clientId != null && clientId.equals("false"))
			select += " WHERE " + CART_TABLE_SENT + "=?";

		try (Connection conn = DriverManager.getConnection(CONNECTION);
				PreparedStatement stat = conn.prepareStatement(select)) {
			if (clientId != null && clientId.startsWith("Client_"))
				stat.setString(1, clientId);
			else if (clientId != null && clientId.equals("false"))
				stat.setBoolean(1, false);

			try (ResultSet rs = stat.executeQuery()) {
				ArrayList<CartElement> elements = new ArrayList<>();
				while (rs.next()) {
					CartElement cartElement = new CartElement(rs.getInt(CART_TABLE_ID),
							rs.getString(CART_TABLE_CLIENTID),
							new Product(rs.getInt(PRODUCT_TABLE_ID), rs.getString(PRODUCT_TABLE_NAME),
									rs.getString(PRODUCT_TABLE_DESCRIPTION), rs.getDouble(PRODUCT_TABLE_PRICE),
									new Category(rs.getInt(PRODUCT_TABLE_CATEGORYID),
											readCategoryName(rs.getInt(PRODUCT_TABLE_CATEGORYID))),
									rs.getInt(PRODUCT_TABLE_AMOUNT), rs.getBoolean(PRODUCT_TABLE_ACTIVE)),
							rs.getInt(CART_TABLE_AMOUNT), rs.getDate(CART_TABLE_ORDERDATE).toLocalDate(),
							rs.getBoolean(CART_TABLE_SENT));
					elements.add(cartElement);
				}
				return new Cart(elements);
			}
		} catch (SQLException e) {
			throw e;
		}
	}

	/**
	 * Deletes an order from the cart table based on the provided order ID.
	 *
	 * @param id The ID of the order to be deleted.
	 * @throws SQLException If an error occurs while executing SQL statements.
	 */
	public static void deleteCartElement(int id) throws SQLException {
		String deleteQuery = "DELETE FROM " + CART_TABLE + " WHERE " + CART_TABLE_ID + " = ?";

		try (Connection connection = DriverManager.getConnection(CONNECTION);
				PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
			preparedStatement.setInt(1, id);
			int rowsAffected = preparedStatement.executeUpdate();
			if (rowsAffected > 0) {
				System.out.println("\nCart with ID " + id + " was deleted.");
			} else {
				System.out.println("Cart with ID " + id + " not found.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Updates the information of an order in the cart table based on the provided
	 * CartElement object.
	 *
	 * @param cartElement The CartElement object containing updated information.
	 * @throws SQLException If an error occurs while executing SQL statements.
	 */
	public static void updateCartElement(CartElement cartElement) throws SQLException {
		String updateQuery = "UPDATE " + CART_TABLE + " SET " + CART_TABLE_CLIENTID + " = ?, " + CART_TABLE_PRODUCTID
				+ " = ?, " + CART_TABLE_AMOUNT + " = ?, " + CART_TABLE_ORDERDATE + " = ?, " + CART_TABLE_SENT + " = ? "
				+ "WHERE " + CART_TABLE_ID + " = ?";

		try (Connection connection = DriverManager.getConnection(CONNECTION);
				PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
			preparedStatement.setString(1, cartElement.getClientId());
			preparedStatement.setInt(2, cartElement.getProduct().getId());
			preparedStatement.setInt(3, cartElement.getAmount());
			preparedStatement.setDate(4, Date.valueOf(cartElement.getOrderDate()));
			preparedStatement.setBoolean(5, cartElement.getIsSent());
			preparedStatement.setInt(6, cartElement.getId());

			int rowsUpdated = preparedStatement.executeUpdate();

			if (rowsUpdated > 0) {
				System.out.println("CartElement with ID " + cartElement.getId() + " updated successfully.");
			} else {
				System.out.println("No records were updated for CartElement with ID " + cartElement.getId());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
