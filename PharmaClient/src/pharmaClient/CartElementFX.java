package pharmaClient;

import java.time.LocalDate;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import pharmaClasses.CartElement;
import pharmaClasses.Product;

public class CartElementFX {

	private CartElement modelCartElement;
	private SimpleIntegerProperty id;
	private SimpleStringProperty clientId;
	private SimpleObjectProperty<Product> product;
	private SimpleIntegerProperty amount;
	private SimpleObjectProperty<LocalDate> orderDate;
	private SimpleBooleanProperty isSent;

	public CartElementFX(CartElement modelCartElement) {
		super();
		this.modelCartElement = modelCartElement;
		id = new SimpleIntegerProperty(modelCartElement.getId());
		clientId = new SimpleStringProperty(modelCartElement.getClientId());
		product = new SimpleObjectProperty<>(modelCartElement.getProduct());
		amount = new SimpleIntegerProperty(modelCartElement.getAmount());
		orderDate = new SimpleObjectProperty<>(modelCartElement.getOrderDate());
		isSent = new SimpleBooleanProperty(modelCartElement.getIsSent());
		
		isSent.addListener((obs, oldVal, newVal) -> {
			// Update the underlying modelCartElement when the checkbox state changes
			modelCartElement.setIsSent(newVal);
			String xmlData = modelCartElement.toXML();
			try {
				ServiceFunctions.post("cartelement", String.valueOf(modelCartElement.getId()), xmlData);
			} catch (PharmaException e) {
				e.printStackTrace();
			}
		});
	}

	public final SimpleIntegerProperty idProperty() {
		return this.id;
	}

	public final int getId() {
		return this.idProperty().get();
	}

	public final void setId(final int id) {
		this.idProperty().set(id);
		modelCartElement.setId(id);
	}

	public final SimpleStringProperty clientIdProperty() {
		return this.clientId;
	}

	public final String getClientId() {
		return this.clientIdProperty().get();
	}

	public final void setClientId(final String clientId) {
		this.clientIdProperty().set(clientId);
		modelCartElement.setClientId(clientId);
	}

	public final ObjectProperty<Product> productProperty() {
		return this.product;
	}

	public final Product getProduct() {
		return this.productProperty().get();
	}

	public final void setProduct(final Product product) {
		this.productProperty().set(product);
		modelCartElement.setProduct(product);
	}

	public final SimpleIntegerProperty amountProperty() {
		return this.amount;
	}

	public final int getAmount() {
		return this.amountProperty().get();
	}

	public final void setAmount(final int amount) {
		this.amountProperty().set(amount);
		modelCartElement.setAmount(amount);
	}

	public final SimpleObjectProperty<LocalDate> orderDateProperty() {
		return this.orderDate;
	}

	public final LocalDate getOrderDate() {
		return this.orderDateProperty().get();
	}

	public final void setOrderDate(final LocalDate orderDate) {
		this.orderDateProperty().set(orderDate);
		modelCartElement.setOrderDate(orderDate);
	}

	public final SimpleBooleanProperty isSentProperty() {
		return this.isSent;
	}

	public final boolean isIsSent() {
		return this.isSentProperty().get();
	}

	public final void setIsSent(final boolean isSent) {
		this.isSentProperty().set(isSent);
		modelCartElement.setIsSent(isSent);
	}

	public CartElement getModelCartElement() {
		return modelCartElement;
	}

	public void setModelCartElement(CartElement modelCartElement) {
		this.modelCartElement = modelCartElement;
	}

}
