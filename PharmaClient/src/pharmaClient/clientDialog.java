package pharmaClient;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.LocalDateStringConverter;
import pharmaClasses.Cart;
import pharmaClasses.CartElement;
import pharmaClasses.Client;
import pharmaClasses.Product;
import pharmaClasses.ProductList;

public class clientDialog extends Dialog<ButtonType> {
	private Integer spinnerValue = 100;
	
	private ObservableList<ProductFX> productOl = FXCollections.observableArrayList();
	private ObservableList<CartElementFX> ordersOl = FXCollections.observableArrayList();
	private ObservableList<CartElementFX> detailsOl = FXCollections.observableArrayList();
	
	private ArrayList<CartElement> cart = new ArrayList<CartElement>();
	
	private Map<LocalDate, Double> totalValueMap;
	private Map<LocalDate, List<CartElement>> ordersMap;

	@SuppressWarnings("unchecked")
	public clientDialog(Client client) {
		Tab clientTab = new Tab("Personal");
		clientTab.setGraphic(new ImageView(Paths.get("..\\resources\\client.png").toUri().toString()));
		Tab productTab = new Tab("Catalog");
		productTab.setGraphic(new ImageView(Paths.get("..\\resources\\product.png").toUri().toString()));
		Tab orderTab = new Tab("Orders");
		orderTab.setGraphic(new ImageView(Paths.get("..\\resources\\orders.png").toUri().toString()));
		
		TabPane tabPane = new TabPane();
		tabPane.getTabs().addAll(clientTab, productTab, orderTab);
		tabPane.setSide(Side.TOP);
		
		Button update = new Button("Update");
		update.setPrefWidth(85);
		Button add = new Button("Add to cart");
		add.setPrefWidth(85);
		add.setDisable(true);
		Button pay = new Button("Pay");
		pay.setPrefWidth(85);
		pay.setDisable(true);

		HBox hb = new HBox(10, update, add, pay);
		hb.setAlignment(Pos.BASELINE_CENTER);
		
		BorderPane borderPane = new BorderPane();
		borderPane.setCenter(tabPane);
		borderPane.setBottom(hb);
		
		this.getDialogPane().setContent(borderPane);
		this.getDialogPane().setPrefSize(620, 400);
		this.setResizable(true);
		this.setTitle("ClientPanel");
		this.getDialogPane().getButtonTypes().add(new ButtonType("Close", ButtonData.CANCEL_CLOSE));
		
		clientTab.setOnSelectionChanged(e -> {
			if (clientTab.isSelected()) {
				update.setDisable(false);
				add.setDisable(true);
				pay.setDisable(true);
			}
		});
		productTab.setOnSelectionChanged(e -> {
			if (productTab.isSelected()) {
				update.setDisable(true);
				add.setDisable(false);
				pay.setDisable(true);
				
				getProducts();
			}
		});
		orderTab.setOnSelectionChanged(e -> {
			if (orderTab.isSelected()) {
				update.setDisable(true);
				add.setDisable(true);
				pay.setDisable(true);
				
				detailsOl.clear();
				getOrders(client.getUserId());
			}
		});
		
// Elements of clientTab
		TextField name = new TextField(client.getName());
		name.setPrefWidth(300);
		TextField password = new TextField(client.getPass());
		TextField address = new TextField(client.getAddress());
		TextField email = new TextField(client.getEmail());
		TextField telephone = new TextField(client.getTel());
		TextField contact = new TextField(client.getContactPerson());
		GridPane gpClient = new GridPane();
		
		gpClient.addRow(0, new Label("Company name:"), name);
		gpClient.addRow(1, new Label("Password:"), password);
		gpClient.addRow(2, new Label("Address:"), address);
		gpClient.addRow(3, new Label("Email:"), email);
		gpClient.addRow(4, new Label("Telephone:"), telephone);
		gpClient.addRow(5, new Label("Contact person:"), contact);
		gpClient.setPadding(new Insets(10));
		gpClient.setVgap(5);
		gpClient.setHgap(5);
		gpClient.setAlignment(Pos.CENTER);
		
		setLabelStyle(gpClient);
		clientTab.setContent(setBackgroudImage(gpClient));
        
// Elements of productTab
		TableColumn<ProductFX, String> nameCol = new TableColumn<>("Name");
		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
		nameCol.setCellFactory(column -> createTextFieldTableCell(nameCol));
		nameCol.setPrefWidth(150);

		TableColumn<ProductFX, String> descrCol = new TableColumn<>("Description");
		descrCol.setCellValueFactory(new PropertyValueFactory<>("description"));
		descrCol.setCellFactory(column -> createTextFieldTableCell(descrCol));
		descrCol.setPrefWidth(150);

		TableColumn<ProductFX, Double> priceCol = new TableColumn<>("Price");
		priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
		priceCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
		priceCol.setPrefWidth(50);

		TableColumn<ProductFX, String> categoryCol = new TableColumn<>("Category");
		categoryCol
				.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory().getName()));
		categoryCol.setCellFactory(column -> createTextFieldTableCell(categoryCol));
		categoryCol.setPrefWidth(150);

		TableColumn<ProductFX, Integer> qtyCol = new TableColumn<>("Quantity");
		qtyCol.setCellValueFactory(cellData -> cellData.getValue().amountProperty().asObject());
		qtyCol.setPrefWidth(75);
		qtyCol.setCellFactory(column -> new TableCell<ProductFX, Integer>() {
			private final Spinner<Integer> spinner = new Spinner<>();
			{
				spinner.setEditable(true);
				spinner.valueProperty().addListener((observable, oldValue, newValue) -> { 
					spinnerValue = newValue;
				});
			}
	        /**
	         * Custom implementation of the updateItem method for rendering values as Spinners in a TableCell.
	         *
	         * @param item  The value to be displayed in the cell.
	         * @param empty Indicates whether the cell is empty or contains data.
	         */
			@Override
			protected void updateItem(Integer item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setGraphic(null);
				} else {
					SpinnerValueFactory<Integer> value;
					if(item >= 100)
						value = new SpinnerValueFactory.IntegerSpinnerValueFactory(10, item, 100, 50);
					else 
						value = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, item,	item, 1);
					spinner.setValueFactory(value);
					setGraphic(spinner);
				}
			}
		});

		SortedList<ProductFX> sortedProductList = new SortedList<>(productOl);
		sortedProductList.setComparator((product1, product2) ->
		        product2.getCategory().getName().compareTo(product1.getCategory().getName())
		);
		
		TableView<ProductFX> productTv = new TableView<>(sortedProductList);
		sortedProductList.comparatorProperty().bind(productTv.comparatorProperty());
		
		productTv.getColumns().addAll(nameCol, descrCol, priceCol, categoryCol, qtyCol);
		
		productTv.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		productTv.setPrefHeight(300);
		productTv.setPrefWidth(350);
		productTv.getSortOrder().add(categoryCol);
		categoryCol.setSortType(TableColumn.SortType.ASCENDING);

		productTab.setContent(productTv);

// Elements of orderTab
		//Top TableView
		TableColumn<CartElementFX, String> clientIdCol = new TableColumn<>("ClientId");
		clientIdCol.setCellValueFactory(new PropertyValueFactory<>("clientId"));
		clientIdCol.setPrefWidth(100);
		TableColumn<CartElementFX, LocalDate> dateCol = new TableColumn<>("Date");
		dateCol.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
		dateCol.setCellFactory(TextFieldTableCell.forTableColumn(new LocalDateStringConverter()));
		dateCol.setPrefWidth(150);
		TableColumn<CartElementFX, Double> totalPriceCol = new TableColumn<>("Total price");
		totalPriceCol.setCellValueFactory(cellData -> {
			LocalDate orderDate = cellData.getValue().getOrderDate(); 
			double total = totalValueMap.get(orderDate);									// Get the total amount for the corresponding date
		    String formattedTotal = String.format(Locale.US, "%.2f", total);				// Format the value to display only two decimal places
			
		    return new SimpleObjectProperty<>(Double.parseDouble(formattedTotal));			
		});
		totalPriceCol.setPrefWidth(150);

		SortedList<CartElementFX> sortedList = new SortedList<>(ordersOl);
		sortedList.setComparator(Comparator.comparing(CartElementFX::getOrderDate));
		
		TableView<CartElementFX> ordersTv = new TableView<>();
		ordersTv.setItems(sortedList);
		ordersTv.getColumns().addAll(clientIdCol, dateCol, totalPriceCol);
		sortedList.comparatorProperty().bind(ordersTv.comparatorProperty()); 				// Bind the SortedList comparator to the TableView's comparator
		ordersTv.getSortOrder().add(dateCol);
		dateCol.setSortType(TableColumn.SortType.DESCENDING);
		
		//Bottom TableView
		TableColumn<CartElementFX, Integer> orderIdCol = new TableColumn<>("OrderId");
		orderIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
		orderIdCol.setPrefWidth(50);
		TableColumn<CartElementFX, String> orderNameCol = new TableColumn<>("Product");
		orderNameCol
				.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProduct().getName()));
		orderNameCol.setCellFactory(column -> createTextFieldTableCell(orderNameCol));
		orderNameCol.setPrefWidth(150);
		TableColumn<CartElementFX, String> orderCatCol = new TableColumn<>("Category");
		orderCatCol.setCellValueFactory(
				cellData -> new SimpleStringProperty(cellData.getValue().getProduct().getCategory().getName()));
		orderCatCol.setCellFactory(column -> createTextFieldTableCell(orderCatCol));
		orderCatCol.setPrefWidth(150);
		TableColumn<CartElementFX, Double> orderPriceCol = new TableColumn<>("Price");
		orderPriceCol.setCellValueFactory(
				cellData -> new SimpleDoubleProperty(cellData.getValue().getProduct().getPrice()).asObject());
		orderPriceCol.setPrefWidth(75);
		TableColumn<CartElementFX, Integer> orderAmountCol = new TableColumn<>("Quantity");
		orderAmountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
		orderAmountCol.setPrefWidth(75);
		TableColumn<CartElementFX, Boolean> orderSentCol = new TableColumn<>("isSent");
		orderSentCol.setCellValueFactory(new PropertyValueFactory<>("isSent"));
		orderSentCol.setCellFactory(CheckBoxTableCell.forTableColumn(orderSentCol));
		orderSentCol.setPrefWidth(70);

		TableView<CartElementFX> detailsTv = new TableView<>(detailsOl);
		detailsTv.getColumns().addAll(orderIdCol, orderNameCol, orderCatCol, orderPriceCol, orderAmountCol,
				orderSentCol);

		ordersTv.setOnMouseClicked(e -> {
			detailsOl.clear();
			CartElementFX selectedElement = ordersTv.getSelectionModel().getSelectedItem();
			if (selectedElement != null) {
				LocalDate targetDate = selectedElement.getOrderDate();
				List<CartElement> cartElements = ordersMap.get(targetDate);
				if (cartElements != null)
					cartElements.forEach(k -> detailsOl.add(new CartElementFX(k)));
			}
		});
		
		VBox vb = new VBox(10, ordersTv, detailsTv);
		vb.setPadding(new Insets(5));
		
		orderTab.setContent(vb);
		
// Functionality of: update, add and pay Buttons
		update.setOnMouseClicked(e -> {
			ClientFX selectedClient = new ClientFX(new Client(client.getUserId(), name.getText(), password.getText(),
					address.getText(), email.getText(), telephone.getText(), contact.getText(),
					client.getDateFrom(), client.getAdmin(), client.getActive()));
			String xmlData = selectedClient.getModelClient().toXML();
			try {
				ServiceFunctions.post("client", client.getUserId(), xmlData);
				new Alert(AlertType.CONFIRMATION, "Data updated successfully").showAndWait();
			} catch (PharmaException e1) {
				e1.printStackTrace();
			}
		});

		add.setOnMouseClicked(e -> {
			ProductFX selectedProduct = productTv.getSelectionModel().getSelectedItem();
			if (selectedProduct != null) {
				Product product = selectedProduct.getModelProduct();
				String clientId = client.getUserId();
				CartElement cartElement = new CartElement(0, clientId, product, spinnerValue, LocalDate.now(), false);
				cart.add(cartElement);
			}
			pay.setDisable(false);
		});

		pay.setOnMouseClicked(e -> {
			if(!cart.isEmpty()) {
				new cartDialog(client.getUserId(), cart).showAndWait();
				getProducts();
			} else
				new Alert(AlertType.ERROR, "Please add a product to the cart first").showAndWait();
		});
	}
	
	/**
	 * Method populating the internal list of products (productOl), with all available products.
	 * Sends a GET request to retrieve the product list from the server, and handles a PharmaException indicating an issue with the service call. 	
	 */
	private void getProducts() {
		productOl.clear();
		try {
			String line = ServiceFunctions.get("productlist", "true");
			ProductList products = new ProductList(line);
			for (Product one : products.getProducts()) {
				if(one.getAmount() > 0)
					productOl.add(new ProductFX(one));
			}
		} catch (PharmaException e) {
			new Alert(AlertType.ERROR, e.toString()).showAndWait();
		}
	}

	/**
	 * Method that retrieves and processes client orders, populating ObservableList (ordersOl) and aggregating data.
	 * Sends a GET request to retrieve the order list from the server, and handles a PharmaException indicating an issue with the service call. 
	 * 
	 * @param clientID	The unique identifier of the client for whom orders are retrieved.
	 */
	private void getOrders(String clientID) {
		ordersOl.clear();
		Set<LocalDate> uniqueDates = new HashSet<>();
		totalValueMap = new HashMap<>();
		ordersMap = new HashMap<>();
		try {
			Double totalAmount = 0.0;
			String line = ServiceFunctions.get("cart", clientID);
			Cart cart = new Cart(line);
			for (CartElement one : cart.getElements()) {
				totalAmount = one.getAmount() * one.getProduct().getPrice();
				// Update totalValueMap with order date as key and totalAmount as a value
				totalValueMap.merge(one.getOrderDate(), totalAmount, Double::sum);		
				// Update ordersMap with order date as key and a list of CartElements as a value
				ordersMap.merge(one.getOrderDate(), new ArrayList<>(List.of(one)), (existingList, newList) -> {
					if (existingList == null) {
						return new ArrayList<>(List.of(one));
					} else {
						existingList.addAll(newList);
						return existingList;
					}
				});
				// Populate uniqueDates set and add CartElementFX to ordersOl if an order for the given date does not exist yet.
				if (uniqueDates.add(one.getOrderDate()))
					ordersOl.add(new CartElementFX(one));
			}
		} catch (PharmaException e) {
			new Alert(AlertType.ERROR, e.toString()).showAndWait();
		}
	}
	
	/**
	 * Method setting the background image for a given GridPane within a StackPane.
	 * 
	 * @param grid		Represents a layout to be displayed on top of the background image
	 * @return 			A StackPane with the specified background image and the provided GridPane as content.
	 */
	private StackPane setBackgroudImage(GridPane grid) {
        Image backgroundImage = new Image(Paths.get("..\\resources\\pills.jpg").toUri().toString());
        ImageView backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.setFitWidth(600);
        backgroundImageView.setFitHeight(250);
        backgroundImageView.setOpacity(0.6);
        
        StackPane tabContent = new StackPane();
        tabContent.getChildren().addAll(backgroundImageView, grid);   
        
        return tabContent;	
	}
	
	/**
	 * Method that applies a custom style to all Label nodes within a given GridPane.
	 * 
	 * @param grid		The GridPane containing Label nodes to which the style will be applied.
	 */
	private void setLabelStyle(GridPane grid) {
		for (Node node : grid.getChildren()) {
            if (node instanceof Label) {
            	node.setStyle("-fx-text-fill: black;" + "-fx-font-weight: bold;");
            }
        }
	}
	
	/**
	 * A generic method that creates a custom TextFieldTableCell for a specified TableColumn in a TableView.
	 * 
	 * @param <T> The type parameter of the elements contained within the TableColumn.
	 * @param column The TableColumn for which the custom TextFieldTableCell is created.
	 * @return A TextFieldTableCell configured with a custom updateItem method.
	 */
	<T> TextFieldTableCell<T, String> createTextFieldTableCell(TableColumn<T, String> column) {
	    return new TextFieldTableCell<T, String>(new DefaultStringConverter()) {
	        private final Text text = new Text();

	        /**
	         * Custom implementation of the updateItem method to display text with wrapping.
	         *
	         * @param item  The text content to be displayed in the cell.
	         * @param empty Indicates whether the cell is empty or contains data.
	         */
	        @Override
	        public void updateItem(String item, boolean empty) {
	            super.updateItem(item, empty);
	            if (!(item == null || empty)) {
	                text.setText(item);
	                text.setWrappingWidth(column.getWidth() - 5); // Set the wrapping width of the displayed text with a margin of 5 pixel
	                setGraphic(text);
	            }
	        }
	    };
	}
}
