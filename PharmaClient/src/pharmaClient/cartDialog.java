package pharmaClient;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import pharmaClasses.Cart;
import pharmaClasses.CartElement;
import pharmaClasses.Client;
import pharmaClasses.ClientList;
import pharmaClasses.Product;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class cartDialog extends Dialog<ButtonType> {

	private Button delBtn = new Button("Delete order");
	private ButtonType pay = new ButtonType("Payment", ButtonData.OK_DONE);
	private ObservableList<CartElement> cartOl;

	@SuppressWarnings("unchecked")
	public cartDialog(String userId, ArrayList<CartElement> orderList) {
		cartOl = FXCollections.observableArrayList(orderList);
		ifCartIsEmpty();
		
		TableColumn<CartElement, String> clientIdCol = new TableColumn<>("ClientId");
		clientIdCol.setCellValueFactory(new PropertyValueFactory<>("clientId"));
		clientIdCol.setPrefWidth(75);

		TableColumn<CartElement, String> nameCol = new TableColumn<>("Product");
		nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProduct().getName()));
		nameCol.setPrefWidth(150);

		TableColumn<CartElement, Double> priceCol = new TableColumn<>("Price");
		priceCol.setCellValueFactory(
				cellData -> new SimpleDoubleProperty(cellData.getValue().getProduct().getPrice()).asObject());
		priceCol.setPrefWidth(75);

		TableColumn<CartElement, Integer> amountCol = new TableColumn<>("Quantity");
		amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
		amountCol.setPrefWidth(75);

		TableColumn<CartElement, LocalDate> dateCol = new TableColumn<>("Date");
		dateCol.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
		dateCol.setPrefWidth(75);

		TableView<CartElement> cartTv = new TableView<>(cartOl);
		cartTv.getColumns().addAll(clientIdCol, nameCol, priceCol, amountCol, dateCol);

		/**
		 * When triggered, this event handler removes the selected CartElement from the cart, 
		 * refreshes the TableView, updates the order list, and checks if the cart is empty.
		 */
		delBtn.setOnAction(e -> {
			CartElement selectedElement = cartTv.getSelectionModel().getSelectedItem();
			if (selectedElement != null) {
				cartOl.remove(cartTv.getSelectionModel().getSelectedItem());
				cartTv.refresh();
				orderList.remove(selectedElement);
				ifCartIsEmpty();
			} else
				new Alert(AlertType.ERROR, "No item selected").showAndWait();
		});

		VBox vb = new VBox(10, cartTv, delBtn);
		this.getDialogPane().setContent(vb);
		this.setTitle("Cart");
		this.setResizable(true);
		this.getDialogPane().getButtonTypes().addAll(pay, new ButtonType("Close", ButtonData.CANCEL_CLOSE));

		/**
		 * This result converter is responsible for processing actions when the payment button is clicked.
		 * It updates the Database with the purchased CartElements, adjusts product amounts, 
		 * generates an invoice in PDF format, and displays a confirmation or error alert.
		 * 
		 * @param arg0 The clicked ButtonType triggering the result conversion.
		 * @return The same ButtonType received as input.
		 */
		this.setResultConverter(new Callback<ButtonType, ButtonType>() { 
			@Override
			public ButtonType call(ButtonType arg0) {
				int invoiceNumberPart1 = -1;
				int invoiceNumberPart2 = 0;
				if (arg0 == pay) {
					try {
						for (CartElement item : orderList) {
							String xmlData = item.toXML();
							ServiceFunctions.post("cartelement", userId, xmlData);
							Product selectedProduct = item.getProduct();
							selectedProduct.setAmount(selectedProduct.getAmount() - item.getAmount());
							String xmlDataProduct = selectedProduct.toXML();
							ServiceFunctions.post("product", String.valueOf(selectedProduct.getId()), xmlDataProduct);
							invoiceNumberPart1++;
						}
						String line = ServiceFunctions.get("cart", userId);
						Cart cart = new Cart(line);
						invoiceNumberPart2 = cart.getElements().stream().max(Comparator.comparingInt(CartElement::getId)).orElse(null).getId();
						invoiceNumberPart1 = invoiceNumberPart2 - invoiceNumberPart1;
					} catch (PharmaException e) {
						new Alert(AlertType.ERROR, e.toString()).showAndWait();
					}

					//create Invoice
					Document document = new Document();
		            try {
		            	String invoiceName = "./../Invoices/invoice_" + userId + "_" + invoiceNumberPart1 + "-" + invoiceNumberPart2 + ".pdf";
						PdfWriter.getInstance(document, new FileOutputStream(invoiceName));
			            document.open();
			            String line = ServiceFunctions.get("client", userId);
						ClientList clientList = new ClientList(line);
						if (!clientList.getClients().isEmpty()) {
							Client client = clientList.getClients().get(0);
							CreatePdf.addBeginning(document, invoiceNumberPart1 + "/" +  invoiceNumberPart2, client);
						}	
						CreatePdf.addTableToInvoice(document, orderList);
						CreatePdf.addEnd(document);
						document.close();
						
						new Alert(AlertType.CONFIRMATION, "The process was completed successfully. The invoice was saved.").showAndWait();
					} catch (FileNotFoundException | DocumentException | PharmaException e) {
						e.printStackTrace();
					}
		            orderList.removeAll(cartOl);
				}
				return arg0;
			}
		});
	}
	/**
	 * Method that checks if the shopping cart is empty and adjusts UI components accordingly.
	 * If the cart is empty: it disables the delete and the payment buttons in the dialog.
	 */
	private void ifCartIsEmpty() {
		if (cartOl.isEmpty()) {
			delBtn.setDisable(true);
			this.getDialogPane().lookupButton(pay).setDisable(true);
		}
	}

}
