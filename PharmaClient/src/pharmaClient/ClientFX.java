package pharmaClient;

import java.time.LocalDate;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import pharmaClasses.Client;

public class ClientFX {

	private Client modelClient;
	private SimpleStringProperty userId;
	private SimpleStringProperty name;
	private SimpleStringProperty pass;
	private SimpleStringProperty address;
	private SimpleStringProperty email;
	private SimpleStringProperty tel;
	private SimpleStringProperty contactPerson;
	private SimpleObjectProperty<LocalDate> dateFrom;
	private SimpleBooleanProperty admin;
	private SimpleBooleanProperty active;

	public ClientFX(Client modelClient) {
		super();
		this.modelClient = modelClient;
		userId = new SimpleStringProperty(modelClient.getUserId());
		name = new SimpleStringProperty(modelClient.getName());
		pass = new SimpleStringProperty(modelClient.getPass());
		address = new SimpleStringProperty(modelClient.getAddress());
		email = new SimpleStringProperty(modelClient.getEmail());
		tel = new SimpleStringProperty(modelClient.getTel());
		contactPerson = new SimpleStringProperty(modelClient.getContactPerson());
		dateFrom = new SimpleObjectProperty<>(modelClient.getDateFrom());
		admin = new SimpleBooleanProperty(modelClient.getAdmin());
		active = new SimpleBooleanProperty(modelClient.getActive());

		admin.addListener((obs, oldVal, newVal) -> {
			// Update the underlying modelClient when the checkbox state changes
			modelClient.setAdmin(newVal);
			String xmlData = modelClient.toXML();
			try {
				ServiceFunctions.post("client", modelClient.getUserId(), xmlData);
			} catch (PharmaException e) {
				e.printStackTrace();
			}
		});
		active.addListener((obs, oldVal, newVal) -> {
			// Update the underlying modelClient when the checkbox state changes
			modelClient.setActive(newVal);
			String xmlData = modelClient.toXML();
			try {
				ServiceFunctions.post("client", modelClient.getUserId(), xmlData);
			} catch (PharmaException e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public String toString() {
		return "ClientFX [modelClient=" + modelClient + ", userId=" + userId + ", name=" + name + ", pass=" + pass
				+ ", address=" + address + ", email=" + email + ", tel=" + tel + ", contactPerson=" + contactPerson
				+ ", dateFrom=" + dateFrom + ", admin=" + admin + ", active=" + active + "]";
	}

	public final Client getModelClient() {
		return modelClient;
	}

	public final SimpleStringProperty userIdProperty() {
		return this.userId;
	}

	public final String getUserId() {
		return this.userIdProperty().get();
	}

	public final void setUserId(final String userId) {
		this.userIdProperty().set(userId);
		modelClient.setUserId(userId);
	}

	public final SimpleStringProperty nameProperty() {
		return this.name;
	}

	public final String getName() {
		return this.nameProperty().get();
	}

	public final void setName(final String name) {
		this.nameProperty().set(name);
		modelClient.setName(name);
	}

	public final SimpleStringProperty passProperty() {
		return this.pass;
	}

	public final String getPass() {
		return this.passProperty().get();
	}

	public final void setPass(final String pass) {
		this.passProperty().set(pass);
		modelClient.setPass(pass);
	}

	public final SimpleStringProperty addressProperty() {
		return this.address;
	}

	public final String getAddress() {
		return this.addressProperty().get();
	}

	public final void setAddress(final String address) {
		this.addressProperty().set(address);
		modelClient.setAddress(address);
	}

	public final SimpleStringProperty emailProperty() {
		return this.email;
	}

	public final String getEmail() {
		return this.emailProperty().get();
	}

	public final void setEmail(final String email) {
		this.emailProperty().set(email);
		modelClient.setEmail(email);
	}

	public final SimpleStringProperty telProperty() {
		return this.tel;
	}

	public final String getTel() {
		return this.telProperty().get();
	}

	public final void setTel(final String tel) {
		this.telProperty().set(tel);
		modelClient.setTel(tel);
	}

	public final SimpleStringProperty contactPersonProperty() {
		return this.contactPerson;
	}

	public final String getContactPerson() {
		return this.contactPersonProperty().get();
	}

	public final void setContactPerson(final String contactPerson) {
		this.contactPersonProperty().set(contactPerson);
		modelClient.setContactPerson(contactPerson);
	}

	public final SimpleObjectProperty<LocalDate> dateFromProperty() {
		return this.dateFrom;
	}

	public final LocalDate getDateFrom() {
		return this.dateFromProperty().get();
	}

	public final void setDateFrom(final LocalDate dateFrom) {
		this.dateFromProperty().set(dateFrom);
		modelClient.setDateFrom(dateFrom);
	}

	public final SimpleBooleanProperty adminProperty() {
		return this.admin;
	}

	public final boolean isAdmin() {
		return this.adminProperty().get();
	}

	public final void setAdmin(final boolean admin) {
		this.adminProperty().set(admin);
		modelClient.setAdmin(admin);
	}

	public final SimpleBooleanProperty activeProperty() {
		return this.active;
	}

	public final boolean isActive() {
		return this.activeProperty().get();
	}

	public final void setActive(final boolean active) {
		this.activeProperty().set(active);
		modelClient.setActive(active);
	}

}
