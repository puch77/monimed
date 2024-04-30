package pharmaClient;

import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.converter.DefaultStringConverter;
import pharmaClasses.Cart;
import pharmaClasses.CartElement;
import pharmaClasses.Category;
import pharmaClasses.CategoryList;
import pharmaClasses.Client;
import pharmaClasses.Product;
import pharmaClasses.ProductList;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

public class adminDialog extends Dialog<ButtonType> {

	private Client client;
	private Product product;
	private CategoryList categories;
	private ObservableList<CartElementFX> ordersOl = FXCollections.observableArrayList();
	private ObservableList<String> categoryOl = FXCollections.observableArrayList();
	private ObservableList<ProductFX> inventoryOl = FXCollections.observableArrayList();

	@SuppressWarnings("unchecked")
	public adminDialog() {
		Tab clientTab = new Tab("Clients");
		clientTab.setGraphic(new ImageView(Paths.get("..\\resources\\client.png").toUri().toString()));
		Tab productTab = new Tab("Products");
		productTab.setGraphic(new ImageView(Paths.get("..\\resources\\product.png").toUri().toString()));
		Tab orderTab = new Tab("Orders");
		orderTab.setGraphic(new ImageView(Paths.get("..\\resources\\orders.png").toUri().toString()));
		Tab reportTab = new Tab("Reports");
		reportTab.setGraphic(new ImageView(Paths.get("..\\resources\\report.png").toUri().toString()));

		TabPane tabPane = new TabPane();
		tabPane.getTabs().addAll(clientTab, productTab, orderTab, reportTab);
		tabPane.setSide(Side.TOP);

		Button clear = new Button("Clear");
		clear.setPrefWidth(100);
		Button add = new Button("Add");
		add.setPrefWidth(100);
		Button show = new Button("Show & Edit");
		show.setPrefWidth(100);

		HBox hb = new HBox(10, clear, add, show);
		hb.setAlignment(Pos.BASELINE_CENTER);

		BorderPane bp = new BorderPane();
		bp.setCenter(tabPane);
		bp.setBottom(hb);

		this.getDialogPane().setContent(bp);
		this.setResizable(true);
		this.setTitle("AdminPanel");
		this.getDialogPane().getButtonTypes().add(new ButtonType("Close", ButtonData.CANCEL_CLOSE));

		clientTab.setOnSelectionChanged(e -> {
			if (clientTab.isSelected()) {
				add.setDisable(false);
				show.setDisable(false);
				clear.setDisable(false);
			}
		});
		productTab.setOnSelectionChanged(e -> {
			if (productTab.isSelected()) {
				add.setDisable(false);
				show.setDisable(false);
				clear.setDisable(false);

				getCategories();
			}
		});
		orderTab.setOnSelectionChanged(e -> {
			if (orderTab.isSelected()) {
				add.setDisable(true);
				show.setDisable(true);
				clear.setDisable(true);

				getOrders("false", null, null);
			}
		});
		reportTab.setOnSelectionChanged(e -> {
			if (reportTab.isSelected()) {
				add.setDisable(true);
				show.setDisable(true);
				clear.setDisable(true);

				reportTab.setContent(getSalesElements());
			}
		});

// Elements of clientTab
		TextField name = new TextField();
		name.setPrefWidth(300);
		TextField password = new TextField();
		TextField address = new TextField();
		TextField email = new TextField();
		TextField telephone = new TextField();
		TextField contact = new TextField();
		DatePicker date = new DatePicker(LocalDate.now());
		CheckBox rights = new CheckBox();
		rights.setSelected(false);

		GridPane gpClients = new GridPane();
		gpClients.addRow(0, new Label("Company name:"), name);
		gpClients.addRow(1, new Label("Password:"), password);
		gpClients.addRow(2, new Label("Address:"), address);
		gpClients.addRow(3, new Label("Email:"), email);
		gpClients.addRow(4, new Label("Telephone:"), telephone);
		gpClients.addRow(5, new Label("Contact person:"), contact);
		gpClients.addRow(6, new Label("Client from:"), date);
		gpClients.addRow(7, new Label("Admin rights?"), rights);
		gpClients.setPadding(new Insets(10));
		gpClients.setVgap(10);
		gpClients.setHgap(10);
		gpClients.setAlignment(Pos.CENTER);

		setLabelStyle(gpClients);
		clientTab.setContent(setBackgroudImage(gpClients));

// Elements of productTab
		TextField productName = new TextField();
		productName.setPrefWidth(300);
		TextArea description = new TextArea();
		description.setPrefRowCount(3);
		description.setPrefWidth(100);
		description.setWrapText(true);
		description.setPromptText("max. 200 characters");

		// adding event filter to prevent typing when the limit is reached
		description.addEventFilter(KeyEvent.KEY_TYPED, e -> {
			if (description.getText().length() >= 200) {
				e.consume(); // consume the event to prevent further typing
			}
		});

		// adding listener to monitor changes in the content (in case of copy_paste)
		description.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.length() > 200) {
				description.setText(oldValue); // revert to the previous value
			}
		});

		TextField price = new TextField();
		price.setPromptText("0.00");
		TextField category = new TextField();
		category.setPromptText("type and press Enter");
		category.setPrefWidth(150);
		category.setVisible(false);
		Button categoryBtn = new Button("New category");
		categoryBtn.setPrefWidth(100);

		ComboBox<String> categoryCb = new ComboBox<>(categoryOl);
		categoryCb.setVisibleRowCount(4);
		TextField quantity = new TextField();
		GridPane gpProducts = new GridPane();
		gpProducts.addRow(0, new Label("Name:"), productName);
		gpProducts.addRow(1, new Label("Description:"), description);
		gpProducts.addRow(2, new Label("Price per item (in EUR):"), price);
		gpProducts.addRow(3, new Label("Category:"), categoryCb);
		gpProducts.addRow(4, new Label("Total quantity:"), quantity);
		gpProducts.add(categoryBtn, 3, 3);
		gpProducts.add(category, 3, 3);
		gpProducts.setPadding(new Insets(10));
		gpProducts.setVgap(5);
		gpProducts.setHgap(5);
		gpProducts.setAlignment(Pos.CENTER);

		setLabelStyle(gpProducts);
		productTab.setContent(setBackgroudImage(gpProducts));

		categoryBtn.setOnMouseClicked(e -> {
			categoryBtn.setVisible(false);
			category.setVisible(true);
			categoryCb.requestFocus();
		});
		category.setOnKeyPressed(e -> {
			String newCategoryName = category.getText();
			if (e.getCode() == KeyCode.ENTER) {
				if (!newCategoryName.isEmpty()) {
					Category newCategory = new Category(0, newCategoryName);
					String xmlData = newCategory.toXML();
					try {
						ServiceFunctions.post("category", null, xmlData);
					} catch (PharmaException e1) {
						e1.printStackTrace();
					}
					category.clear();
				} else {
					new Alert(AlertType.ERROR, "Please type a new category").showAndWait();
				}
				getCategories();
				category.setVisible(false);
				categoryBtn.setVisible(true);
			} else
				new Alert(Alert.AlertType.ERROR, "Please press Enter");
		});

		tabPane.setOnMouseClicked(e -> {
			category.setVisible(false);
			categoryBtn.setVisible(true);
		});

// Functionality of: clear, add and show Buttons
		clear.setOnAction(e -> {
			if (clientTab.isSelected()) {
				name.clear();
				password.clear();
				address.clear();
				email.clear();
				telephone.clear();
				contact.clear();
				date.setValue(LocalDate.now());
				rights.setSelected(false);
			} else if (productTab.isSelected()) {
				productName.clear();
				description.clear();
				price.clear();
				categoryCb.setValue(null);
				quantity.clear();
				category.clear();
			}
		});
		add.setOnAction(e -> {
			if (clientTab.isSelected()) {
				if (!name.getText().isEmpty() && !password.getText().isEmpty() && !address.getText().isEmpty()
						&& !email.getText().isEmpty() && !telephone.getText().isEmpty()
						&& !contact.getText().isEmpty()) {
					client = new Client(null, name.getText(), password.getText(), address.getText(), email.getText(),
							telephone.getText(), contact.getText(), date.getValue(), rights.isSelected(), true);
					String xmlData = client.toXML();

					try {
						ServiceFunctions.post("client", null, xmlData);
						new Alert(AlertType.INFORMATION,
								"Client \"" + name.getText() + "\" has been successfully inserted ").showAndWait();
					} catch (PharmaException ex) {
						ex.printStackTrace();
						new Alert(AlertType.ERROR, ex.toString()).showAndWait();
					}
				} else {
					new Alert(AlertType.ERROR, "Please fill the form").showAndWait();
				}
			} else if (productTab.isSelected()) {
				if (!productName.getText().isEmpty() && !description.getText().isEmpty() && !price.getText().isEmpty()
						&& !quantity.getText().isEmpty()) {
					int catId = findCategoryIdByCategoryName(categoryCb.getValue());
					product = new Product(0, productName.getText(), description.getText(),
							Double.parseDouble(price.getText()), new Category(catId, categoryCb.getValue()),
							Integer.parseInt(quantity.getText()), true);
					String xmlData = product.toXML();
					try {
						ServiceFunctions.post("product", null, xmlData);
						new Alert(AlertType.INFORMATION,
								"Product \"" + productName.getText() + "\" has been successfully inserted ")
								.showAndWait();
					} catch (PharmaException ex) {
						ex.printStackTrace();
						new Alert(AlertType.ERROR, ex.toString()).showAndWait();
					}
				} else {
					new Alert(AlertType.ERROR, "Please fill the form").showAndWait();
				}
			}
		});

		show.setOnAction(e -> {
			if (clientTab.isSelected())
				new clientsDialog().showAndWait();
			else if (productTab.isSelected()) {
				getProducts("All");
				new productsDialog(inventoryOl).showAndWait();
			}
		});

// Elements of ordersTab
		TableColumn<CartElementFX, Integer> orderIdCol = new TableColumn<>("OrderId");
		orderIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
		orderIdCol.setPrefWidth(50);
		TableColumn<CartElementFX, String> orderClientIdCol = new TableColumn<>("ClientId");
		orderClientIdCol.setCellValueFactory(new PropertyValueFactory<>("clientId"));
		orderClientIdCol.setPrefWidth(75);
		TableColumn<CartElementFX, LocalDate> orderDateCol = new TableColumn<>("Date");
		orderDateCol.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
		orderDateCol.setPrefWidth(75);
		TableColumn<CartElementFX, String> orderNameCol = new TableColumn<>("Product");
		orderNameCol
				.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProduct().getName()));
		orderNameCol.setPrefWidth(150);
		orderNameCol.setCellFactory(column -> {
			return new TextFieldTableCell<CartElementFX, String>(new DefaultStringConverter()) {
				private final Text text = new Text();

				@Override
				public void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					if (!(item == null || empty)) {
						text.setText(item);
						text.setWrappingWidth(orderNameCol.getWidth() - 5); // setting margin of 5 pixels
						setGraphic(text);
					}
				}
			};
		});
		orderNameCol.setEditable(false);
		TableColumn<CartElementFX, String> orderCatCol = new TableColumn<>("Category");
		orderCatCol.setCellValueFactory(
				cellData -> new SimpleStringProperty(cellData.getValue().getProduct().getCategory().getName()));
		orderCatCol.setPrefWidth(150);
		TableColumn<CartElementFX, Double> orderPriceCol = new TableColumn<>("Unit price");
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

		TableView<CartElementFX> ordersTv = new TableView<>(ordersOl);
		ordersTv.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		ordersTv.setEditable(true);
		ordersTv.getColumns().addAll(orderIdCol, orderClientIdCol, orderDateCol, orderNameCol, orderCatCol,
				orderPriceCol, orderAmountCol, orderSentCol);

		VBox vb = new VBox(10, ordersTv);
		vb.setPadding(new Insets(5));
		orderTab.setContent(vb);

// Elements of reportsTab
		reportTab.setContent(getSalesElements());
	}

	/**
	 * Creates an Accordion component containing inventory and sales panels.
	 * 
	 * @return An Accordion with inventory and sales panels.
	 */
	private Accordion getSalesElements() {
		Accordion accordion = new Accordion();
		accordion.setPadding(new Insets(10));
		accordion.getPanes().add(inventory());
		accordion.getPanes().add(sales());

		return accordion;
	}

	/**
	 * Method managing and displaying information about the inventory in a panel.
	 * 
	 * @return TitledPane component
	 */
	@SuppressWarnings("unchecked")
	private TitledPane inventory() {
		inventoryOl.clear();

		getCategories();
		ComboBox<String> categoryCb = new ComboBox<>(categoryOl);
		categoryCb.setOnMouseClicked(e -> {
			if (!categoryCb.getItems().contains("All"))
				categoryCb.getItems().add(0, "All");
		});
		categoryCb.setVisibleRowCount(6);

		Button searchBtn = new Button("Search");
		Button saveBtn = new Button("Save as PDF");

		TableColumn<ProductFX, Integer> idCol = new TableColumn<>("Product ID");
		idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
		idCol.setPrefWidth(150);
		TableColumn<ProductFX, String> nameCol = new TableColumn<>("Name");
		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
		nameCol.setPrefWidth(225);
		TableColumn<ProductFX, Double> priceCol = new TableColumn<>("Unit price");
		priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
		priceCol.setPrefWidth(150);
		TableColumn<ProductFX, Integer> amountCol = new TableColumn<>("Quantity");
		amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
		amountCol.setPrefWidth(150);
		amountCol.setCellFactory(column -> {
			return new TableCell<>() {
				@Override
				protected void updateItem(Integer item, boolean empty) {
					super.updateItem(item, empty);

					if (item == null || empty) {
						setText(null);
						setStyle("");
					} else {
						setText(item.toString());
						if (item < 150) { // the condition for making the cell red
							setTextFill(Color.RED);
						} else {
							setTextFill(Color.BLACK);
						}
					}
				}
			};
		});
		TableView<ProductFX> inventoryTv = new TableView<>(inventoryOl);
		inventoryTv.getColumns().addAll(idCol, nameCol, priceCol, amountCol);

		HBox hb = new HBox(10, new Label("Categories"), categoryCb, searchBtn, saveBtn);
		hb.setPadding(new Insets(5));
		VBox vb = new VBox(10, hb, inventoryTv);
		vb.setPadding(new Insets(5));

		searchBtn.setOnAction(e -> getProducts(categoryCb.getSelectionModel().getSelectedItem()));
		saveBtn.setOnAction(e -> saveReportAsPdf(categoryCb.getSelectionModel().getSelectedItem()));

		return new TitledPane("Inventory", vb);
	}

	/**
	 * Method managing and displaying information about the sales in a panel.
	 * 
	 * @return TitledPane component
	 */
	@SuppressWarnings("unchecked")
	private TitledPane sales() {
		ordersOl.clear();

		DatePicker dateFrom = new DatePicker(LocalDate.now().minusMonths(1));
		dateFrom.setPrefWidth(120);
		DatePicker dateTo = new DatePicker(LocalDate.now());
		dateTo.setPrefWidth(120);
		Button searchBtn = new Button("Search");
		Button saveBtn = new Button("Save as PDF");

		RadioButton tableRBtn = new RadioButton("table");
		RadioButton chartRBtnQty = new RadioButton("chartQty");
		RadioButton chartRBtnValue = new RadioButton("chartVal");

		ToggleGroup switchRBtn = new ToggleGroup();
		tableRBtn.setToggleGroup(switchRBtn);
		chartRBtnQty.setToggleGroup(switchRBtn);
		chartRBtnValue.setToggleGroup(switchRBtn);
		tableRBtn.setSelected(true);

		TableColumn<CartElementFX, Integer> idCol = new TableColumn<>("Order ID");
		idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
		idCol.setPrefWidth(70);
		TableColumn<CartElementFX, String> nameCol = new TableColumn<>("Client");
		nameCol.setCellValueFactory(new PropertyValueFactory<>("clientId"));
		nameCol.setPrefWidth(110);
		TableColumn<CartElementFX, String> productCol = new TableColumn<>("Product");
		productCol
				.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProduct().getName()));
		productCol.setPrefWidth(295);
		TableColumn<CartElementFX, Double> priceCol = new TableColumn<>("Unit price");
		priceCol.setCellValueFactory(
				cellData -> new SimpleDoubleProperty(cellData.getValue().getProduct().getPrice()).asObject());
		priceCol.setPrefWidth(70);
		TableColumn<CartElementFX, Integer> amountCol = new TableColumn<>("Quantity");
		amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
		amountCol.setPrefWidth(60);
		TableColumn<CartElementFX, LocalDate> dateCol = new TableColumn<>("Date");
		dateCol.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
		dateCol.setPrefWidth(75);

		TableView<CartElementFX> ordersTv = new TableView<>(ordersOl);
		ordersTv.getColumns().addAll(idCol, nameCol, productCol, priceCol, amountCol, dateCol);

		HBox hbSales = new HBox(10, new Label("From:"), dateFrom, new Label("To:"), dateTo, searchBtn, saveBtn,
				tableRBtn, chartRBtnQty, chartRBtnValue);
		hbSales.setPadding(new Insets(5));

		VBox vbSales = new VBox();
		vbSales.getChildren().setAll(hbSales, ordersTv);
		vbSales.setPadding(new Insets(5));

		switchRBtn.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == tableRBtn) {
				saveBtn.setDisable(false);
				vbSales.getChildren().setAll(hbSales, ordersTv);
			} else if (newValue == chartRBtnQty) {
				saveBtn.setDisable(true);
				vbSales.getChildren().setAll(hbSales, createChart(ordersOl, "qty"));
			} else {
				saveBtn.setDisable(true);
				vbSales.getChildren().setAll(hbSales, createChart(ordersOl, "euro"));
			}
		});

		searchBtn.setOnAction(e -> {
			getOrders(null, dateFrom.getValue(), dateTo.getValue());
			if (chartRBtnQty.isSelected())
				vbSales.getChildren().setAll(hbSales, createChart(ordersOl, "qty"));
			else if (chartRBtnValue.isSelected())
				vbSales.getChildren().setAll(hbSales, createChart(ordersOl, "euro"));
		});
		saveBtn.setOnAction(e -> saveReportAsPdf(null));

		return new TitledPane("Sales", vbSales);
	}

	/**
	 * Method that generates a BarChart based on the provided ObservableList of
	 * CartElementFX, to visualise data related to sales.
	 * 
	 * @param ol An ObservableList containing CartElementFX objects, representing
	 *           data to be visualised in the chart.
	 * @return A BarChart with category (String) on the X-axis and numerical
	 *         (Number) values on the Y-axis.
	 */

	private BarChart<String, Number> createChart(ObservableList<CartElementFX> ol, String serie) {
		CategoryAxis xAxis = new CategoryAxis();
		NumberAxis yAxis = new NumberAxis();

		BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
		barChart.setLegendSide(Side.TOP);

		XYChart.Series<String, Number> dataSerie = new XYChart.Series<>();
		if (serie.equals("qty"))
			dataSerie.setName("Quantity ordered");
		else
			dataSerie.setName("Value of orders (in EUR)");

		// Clear existing data
		dataSerie.getData().clear();

		// Creating a map to store quantity and value of orders
		Map<String, Integer> qtyMap = new TreeMap<>();
		Map<String, Double> valueMap = new TreeMap<>();

		// Populating the map from data
		for (CartElementFX item : ol) {
			LocalDate orderDate = item.getOrderDate();
			Integer quantity = item.getAmount();
			Double total = quantity * item.getProduct().getPrice();

			// If the data is not in the map, add a new entry; otherwise update the quantity
			qtyMap.merge(String.valueOf(orderDate), quantity, Integer::sum);
			valueMap.merge(String.valueOf(orderDate), total, Double::sum);
		}

		// Populating dataSeries from the maps
		if (serie.equals("qty")) {
			for (Map.Entry<String, Integer> entry : qtyMap.entrySet()) {
				dataSerie.getData().add(new XYChart.Data<String, Number>(entry.getKey(), entry.getValue()));
			}
		} else {
			for (Map.Entry<String, Double> entry : valueMap.entrySet()) {
				dataSerie.getData().add(new XYChart.Data<String, Number>(entry.getKey(), entry.getValue()));
			}
		}

		// adding the series to the chart
		barChart.getData().add(dataSerie);

		return barChart;
	}

	/**
	 * Method generating and saving PDF reports either for sales or inventory. Based
	 * on the String category, it recognises if the report is for sales or for
	 * inventory purposes. It displays a confirmation or warning alert depending on
	 * the success or failure of the operation.
	 * 
	 * @param category String parameter indicating the category for which the report
	 *                 should be generated. If null, a sales report is generated;
	 *                 otherwise, an inventory report is generated for the specified
	 *                 category.
	 */
	private void saveReportAsPdf(String category) {
		if (category == null && !ordersOl.isEmpty()) {
			try {
				Document document = new Document();
				String reportName = "./../Reports/sales_" + LocalDate.now() + ".pdf";
				File reportFile = new File(reportName);
				PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(reportFile, false));
				document.open();
				document.add(new Paragraph("Sales report on " + LocalDate.now() + "\n\n"));
				CreatePdf.addTableToSalesReport(document, ordersOl);
				document.close();
				writer.close();

				new Alert(AlertType.CONFIRMATION, "Report saved successfully").showAndWait();
			} catch (IOException | DocumentException e) {
				e.printStackTrace();
			}
		} else if (category != null && !inventoryOl.isEmpty()) {
			try {
				Document document = new Document();
				String reportName = "./../Reports/inventory_" + LocalDate.now() + ".pdf";
				File reportFile = new File(reportName);
				PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(reportFile, false));
				document.open();
				document.add(new Paragraph("Inventory report on " + LocalDate.now() + "\n\n"));
				document.add(new Paragraph("Product category: " + category + "\n\n"));
				CreatePdf.addTableToInventoryReport(document, inventoryOl);
				document.close();
				writer.close();

				new Alert(AlertType.CONFIRMATION, "Report saved successfully").showAndWait();
			} catch (IOException | DocumentException e) {
				e.printStackTrace();
			}
		} else if (category == null && ordersOl.isEmpty())
			new Alert(AlertType.WARNING, "First select a time period and press Search").showAndWait();
		else
			new Alert(AlertType.WARNING, "First select a category and press Search").showAndWait();

	}

	/**
	 * Method populating the internal list of orders (ordersOl) based on specified
	 * criteria. Sends a GET request to retrieve the order list from the server, and
	 * handles a PharmaException indicating an issue with the service call.
	 * 
	 * @param isSent Filter parameter indicating whether to retrieve sent or unsent
	 *               orders. Use "true" for sent orders, "false" for unsent orders,
	 *               or null for all orders.
	 * @param from   Start date for filtering orders. Can be null to include all
	 *               dates.
	 * @param to     End date for filtering orders. Can be null to include all
	 *               dates.
	 */
	private void getOrders(String isSent, LocalDate from, LocalDate to) {
		ordersOl.clear();
		try {
			String line;
			if (isSent != null && isSent.equals("false"))
				line = ServiceFunctions.get("cart", "false");
			else if (isSent != null && isSent.equals("true"))
				line = ServiceFunctions.get("cart", "true");
			else
				line = ServiceFunctions.get("cart", null);
			Cart cart = new Cart(line);
			for (CartElement one : cart.getElements())
				ordersOl.add(new CartElementFX(one));
			if (from != null && to != null)
				ordersOl.removeIf(e -> !e.getOrderDate().isAfter(from.minusDays(1))
						|| !e.getOrderDate().isBefore(to.plusDays(1)));
		} catch (PharmaException e) {
			new Alert(AlertType.ERROR, e.toString()).showAndWait();
		}
	}

	/**
	 * Method populating the internal list of products (inventoryOl), based on the
	 * provided category name. Sends a GET request to retrieve the product list from
	 * the server, and handles a PharmaException indicating an issue with the
	 * service call.
	 * 
	 * @param categoryName Filter parameter indicating which category of products to
	 *                     retrieve. Use "All" to retrieve all products.
	 */
	private void getProducts(String categoryName) {
		if (categoryName != null) {
			inventoryOl.clear();
			int categoryId = findCategoryIdByCategoryName(categoryName);
			try {
				String line;
				if (categoryId > 0)
					line = ServiceFunctions.get("productlist", String.valueOf(categoryId));
				else
					line = ServiceFunctions.get("productlist", null);
				ProductList products = new ProductList(line);
				for (Product one : products.getProducts())
					inventoryOl.add(new ProductFX(one));
			} catch (PharmaException e) {
				new Alert(AlertType.ERROR, e.toString()).showAndWait();
			}
		} else
			new Alert(AlertType.WARNING, "Please select a category").showAndWait();
	}

	/**
	 * Method populating the internal list of categories (categoryOl). Sends a GET
	 * request to retrieve the category list from the server, and handles a
	 * PharmaException indicating an issue with the service call.
	 */
	private void getCategories() {
		categoryOl.clear();
		try {
			String line = ServiceFunctions.get("categorylist", null);
			categories = new CategoryList(line);
			for (Category one : categories.getCategories())
				categoryOl.add(one.getName());
		} catch (PharmaException e) {
			new Alert(AlertType.ERROR, e.toString()).showAndWait();
		}
	}

	/**
	 * Method that searches for a category ID based on the provided category name
	 * within a collection of Category objects.
	 * 
	 * @param categoryName A string representing the name of the category for which
	 *                     the ID needs to be found.
	 * @return An integer representing the ID of the category; or -1 indicating that
	 *         the name was not found
	 */
	private int findCategoryIdByCategoryName(String categoryName) {
		for (Category category : categories.getCategories()) {
			if (category.getName().equals(categoryName)) {
				return category.getId();
			}
		}
		return -1;
	}

	/**
	 * Method setting the background image for a given GridPane within a StackPane.
	 * 
	 * @param grid Represents a layout to be displayed on top of the background
	 *             image
	 * @return A StackPane with the specified background image and the provided
	 *         GridPane as content.
	 */
	private StackPane setBackgroudImage(GridPane grid) {
		Image backgroundImage = new Image(Paths.get("..\\resources\\pills.jpg").toUri().toString());
		ImageView backgroundImageView = new ImageView(backgroundImage);
		backgroundImageView.setFitWidth(700);
		backgroundImageView.setFitHeight(350);
		backgroundImageView.setOpacity(0.8);

		StackPane tabContent = new StackPane();
		tabContent.getChildren().addAll(backgroundImageView, grid);

		return tabContent;
	}

	/**
	 * Method that applies a custom style to all Label nodes within a given
	 * GridPane.
	 * 
	 * @param grid The GridPane containing Label nodes to which the style will be
	 *             applied.
	 */
	private void setLabelStyle(GridPane grid) {
		for (Node node : grid.getChildren()) {
			if (node instanceof Label) {
				node.setStyle("-fx-text-fill: black;" + "-fx-font-weight: bold;");
			}
		}
	}
}
