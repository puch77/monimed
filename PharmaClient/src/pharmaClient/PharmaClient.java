package pharmaClient;

import java.net.URI;
import java.nio.file.Paths;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import pharmaClasses.ClientList;

public class PharmaClient extends Application {
	
	private TextField txtUser;
	private PasswordField pwd;
	
	/**
	 * The entry point of the JavaFX application.
	 * 
	 * @param args 	The command-line arguments passed to the application.
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Method that initialises and starts the JavaFX application.
	 *
	 * @param primaryStage	The primary stage for the application window.
	 * @throws Exception If an exception occurs during the application startup.
	 */
	@Override
	public void start(Stage arg0) throws Exception {
		Label lblWelcome = new Label("Welcome to MoniMed Depot");
		lblWelcome.setStyle("-fx-text-fill: black; -fx-font-size: 13px; -fx-font-weight: bold;");
		lblWelcome.setAlignment(Pos.CENTER);
		lblWelcome.setMaxWidth(Double.MAX_VALUE);        //  occupy the entire available width in parent container

		Label lblLogin = new Label("Please login");
		lblLogin.setAlignment(Pos.CENTER);
		lblLogin.setMaxWidth(Double.MAX_VALUE);

		URI uri = Paths.get("..\\resources\\pharmaLogo.jpg").toUri();
		String imageUrl = uri.toURL().toString();
		Image image = new Image(imageUrl);
		ImageView ivLogo = new ImageView(image);		// create an ImageView to adjust size
		ivLogo.setFitWidth(250);
		ivLogo.setPreserveRatio(true);

		txtUser = new TextField();
		txtUser.setPromptText("User");
		txtUser.setPrefWidth(100);

		pwd = new PasswordField();
		pwd.setPromptText("Password");
		pwd.setPrefWidth(100);

		Button btnLogin = new Button("Login");
		btnLogin.setDisable(true);
		btnLogin.setAlignment(Pos.CENTER);
		btnLogin.setMaxWidth(Double.MAX_VALUE);
		
		pwd.textProperty().addListener((observable, oldValue, newValue) -> {
			btnLogin.setDisable(newValue.isEmpty());
		});

		pwd.setOnAction(e -> login());
		btnLogin.setOnMouseClicked(e -> login());
		
		GridPane gp = new GridPane();
		gp.setPadding(new Insets(5));
		gp.setVgap(10);
		gp.setHgap(10);

		gp.add(lblWelcome, 0, 0, 2, 1);
		gp.add(lblLogin, 0, 3);
		gp.add(txtUser, 0, 4);
		gp.add(pwd, 0, 5);
		gp.add(btnLogin, 0, 6);
		gp.add(ivLogo, 1, 1, 1, 6);
		
		arg0.setResizable(false);
		Scene scene = new Scene(gp);
		arg0.setScene(scene);
		scene.getRoot().requestFocus();
		arg0.setTitle("PharmaClient");
		arg0.show();
	}

	/*
	 * Handles the login functionality by retrieving and validating user credentials, 
	 * and finally displaying appropriate dialogs based on the user's role (admin or client).
	 * Sends a GET request to retrieve the client credentials from the server, and handles a PharmaException indicating an issue with the service call.
	 */
	private void login() {
		try {
			String line = ServiceFunctions.get("client", txtUser.getText());
			ClientList clientList = new ClientList(line);
			if (!clientList.getClients().isEmpty() && clientList.getClients().get(0).getPass().equals(pwd.getText())
					&& clientList.getClients().get(0).getAdmin()) {
				new adminDialog().showAndWait();
			} else if (!clientList.getClients().isEmpty() && clientList.getClients().get(0).getPass().equals(pwd.getText())
					&& !clientList.getClients().get(0).getAdmin()) {
				new clientDialog(clientList.getClients().get(0)).showAndWait();
			} else {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Login failed");
				alert.setHeaderText("Please provide valid credentials");
				alert.setContentText("If you forgot your password, please contact the Admin");
				alert.showAndWait();
			}
			txtUser.clear();
			pwd.clear();
		} catch (PharmaException e) {
			e.printStackTrace();
		}
	}
}
