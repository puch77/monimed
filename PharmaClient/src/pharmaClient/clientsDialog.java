package pharmaClient;

import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import pharmaClasses.Client;
import pharmaClasses.ClientList;
import javafx.util.converter.LocalDateStringConverter;

public class clientsDialog extends Dialog<ButtonType> {

	private ObservableList<ClientFX> clientOl = FXCollections.observableArrayList();
	
	@SuppressWarnings("unchecked")
	public clientsDialog() {
		
		getClients();

		TableView<ClientFX> clientTv = new TableView<>(clientOl);
		clientTv.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		clientTv.setEditable(true);
		
		TableColumn<ClientFX, String> nameCol = new TableColumn<>("Name");
		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nameCol.setOnEditCommit(t -> {
            ClientFX selectedClient = t.getRowValue();
            selectedClient.setName(t.getNewValue());
            updateClient(selectedClient);
        });
		nameCol.setPrefWidth(200);
		
		TableColumn<ClientFX, String> userCol = new TableColumn<>("User");
		userCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
		userCol.setPrefWidth(60);
		
		TableColumn<ClientFX, String> passCol = new TableColumn<>("Password");
		passCol.setCellValueFactory(new PropertyValueFactory<>("pass"));
		passCol.setCellFactory(TextFieldTableCell.forTableColumn());
		passCol.setOnEditCommit(t -> {
            ClientFX selectedClient = t.getRowValue();
            selectedClient.setPass(t.getNewValue());
            updateClient(selectedClient);
        });
		passCol.setPrefWidth(60);
		
		TableColumn<ClientFX, String> addressCol = new TableColumn<>("Address");
		addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
		addressCol.setCellFactory(TextFieldTableCell.forTableColumn());
		addressCol.setOnEditCommit(t -> {
            ClientFX selectedClient = t.getRowValue();
            selectedClient.setAddress(t.getNewValue());
            updateClient(selectedClient);
        });
		addressCol.setPrefWidth(250);
		
		TableColumn<ClientFX, String> emailCol = new TableColumn<>("Email");
		emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
		emailCol.setCellFactory(TextFieldTableCell.forTableColumn());
		emailCol.setOnEditCommit(t -> {
            ClientFX selectedClient = t.getRowValue();
            selectedClient.setEmail(t.getNewValue());
            updateClient(selectedClient);
        });
		emailCol.setPrefWidth(175);
		
		TableColumn<ClientFX, String> telCol = new TableColumn<>("Telephone");
		telCol.setCellValueFactory(new PropertyValueFactory<>("tel"));
		telCol.setCellFactory(TextFieldTableCell.forTableColumn());
		telCol.setOnEditCommit(t -> {
            ClientFX selectedClient = t.getRowValue();
            selectedClient.setTel(t.getNewValue());
            updateClient(selectedClient);
        });
		telCol.setPrefWidth(125);
		
		TableColumn<ClientFX, String> contactCol = new TableColumn<>("Contact");
		contactCol.setCellValueFactory(new PropertyValueFactory<>("contactPerson"));
		contactCol.setCellFactory(TextFieldTableCell.forTableColumn());
		contactCol.setOnEditCommit(t -> {
            ClientFX selectedClient = t.getRowValue();
            selectedClient.setContactPerson(t.getNewValue());
            updateClient(selectedClient);
        });
		contactCol.setPrefWidth(150);
		
		TableColumn<ClientFX, LocalDate> dateCol = new TableColumn<>("Date");
		dateCol.setCellValueFactory(new PropertyValueFactory<>("dateFrom"));
		dateCol.setCellFactory(TextFieldTableCell.forTableColumn(new LocalDateStringConverter()));
		dateCol.setOnEditCommit(t -> {
		    ClientFX selectedClient = t.getRowValue();
		    selectedClient.setDateFrom(t.getNewValue());
		    updateClient(selectedClient);
		});
		dateCol.setPrefWidth(75);
		
		TableColumn<ClientFX, Boolean> rightsCol = new TableColumn<>("isAdmin");
		rightsCol.setCellValueFactory(new PropertyValueFactory<>("admin"));
		rightsCol.setCellFactory(CheckBoxTableCell.forTableColumn(rightsCol));
		rightsCol.setPrefWidth(70);

		TableColumn<ClientFX, Boolean> activeCol = new TableColumn<>("isActive");
		activeCol.setEditable(true);
		activeCol.setCellValueFactory(new PropertyValueFactory<>("active"));
		activeCol.setCellFactory(CheckBoxTableCell.forTableColumn(activeCol));
		activeCol.setPrefWidth(70);
    
		clientTv.getColumns().addAll(nameCol, userCol, passCol, addressCol, emailCol, telCol, contactCol, dateCol,
				rightsCol, activeCol);
		
		clientTv.setOnMouseClicked(event -> {
		    int rowIndex = clientTv.getSelectionModel().getSelectedIndex();

		    if (rowIndex >= 0) {
		        clientTv.setEditable(true);
		        clientTv.getSelectionModel().select(rowIndex);
		    }
		});

		VBox vb = new VBox(10, clientTv);
		this.getDialogPane().setContent(vb);
		this.setResizable(true);
		this.setTitle("The client list");
		this.getDialogPane().getButtonTypes().add(new ButtonType("Close", ButtonData.CANCEL_CLOSE));
	}

	/**
	 * Method updating information of a client on the server (Database) by sending a POST request, based on the provided ClientFX object.
	 * Handles a PharmaException, indicating an issue with the service call. 
	 * 
	 * @param selectedClient	The selectedClient object representing the selected client to be updated.
	 */
	private void updateClient(ClientFX selectedClient) {
        String xmlData = selectedClient.getModelClient().toXML();
        try {
			ServiceFunctions.post("client", selectedClient.getModelClient().getUserId(), xmlData);
		} catch (PharmaException e) {
			e.printStackTrace();
		}		
	}

	/**
	 * Method populating the internal list of clients (clientOl).
	 * Sends a GET request to retrieve the client list from the server, and handles a PharmaException indicating an issue with the service call. 
	 */
	private void getClients() {
		try {
			String line = ServiceFunctions.get("clientlist", null);
			ClientList clients = new ClientList(line);
			for (Client one : clients.getClients())
				clientOl.add(new ClientFX(one));
		} catch (PharmaException e) {
			new Alert(AlertType.ERROR, e.toString()).showAndWait();
		}		
	}
}
