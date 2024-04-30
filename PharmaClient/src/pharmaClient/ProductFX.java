package pharmaClient;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import pharmaClasses.Category;
import pharmaClasses.Product;

public class ProductFX {

	private Product modelProduct;
	private SimpleIntegerProperty id;
	private SimpleStringProperty name;
	private SimpleStringProperty description;
	private SimpleDoubleProperty price;
	private SimpleObjectProperty<Category> category;
	private SimpleIntegerProperty amount;
	private SimpleBooleanProperty active;

	public ProductFX(Product modelProduct) {
		super();
		this.modelProduct = modelProduct;
		id = new SimpleIntegerProperty(modelProduct.getId());
		name = new SimpleStringProperty(modelProduct.getName());
		description = new SimpleStringProperty(modelProduct.getDescription());
		price = new SimpleDoubleProperty(modelProduct.getPrice());
		category = new SimpleObjectProperty<>(modelProduct.getCategory());
		amount = new SimpleIntegerProperty(modelProduct.getAmount());
		active = new SimpleBooleanProperty(modelProduct.getActive());
		
		active.addListener((obs, oldVal, newVal) -> {
			// Update the underlying modelProduct when the checkbox state changes
			modelProduct.setActive(newVal);
			String xmlData = modelProduct.toXML();
			try {
				ServiceFunctions.post("product", String.valueOf(modelProduct.getId()), xmlData);
			} catch (PharmaException e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public String toString() {
		return "ProductFX [modelProduct=" + modelProduct + ", id=" + id + ", name=" + name + ", description="
				+ description + ", price=" + price + ", category=" + (category != null ? category.get().getName() : null) 
				+ ", amount=" + amount + ", active=" + active + "]";
	}

	public final Product getModelProduct() {
		return modelProduct;
	}

	public final SimpleIntegerProperty idProperty() {
		return this.id;
	}

	public final int getId() {
		return this.idProperty().get();
	}

	public final void setId(final int id) {
		this.idProperty().set(id);
		modelProduct.setId(id);
	}

	public final SimpleStringProperty nameProperty() {
		return this.name;
	}

	public final String getName() {
		return this.nameProperty().get();
	}

	public final void setName(final String name) {
		this.nameProperty().set(name);
		modelProduct.setName(name);
	}

	public final SimpleStringProperty descriptionProperty() {
		return this.description;
	}

	public final String getDescription() {
		return this.descriptionProperty().get();
	}

	public final void setDescription(final String description) {
		this.descriptionProperty().set(description);
		modelProduct.setDescription(description);
	}

	public final SimpleDoubleProperty priceProperty() {
		return this.price;
	}

	public final double getPrice() {
		return this.priceProperty().get();
	}

	public final void setPrice(final double price) {
		this.priceProperty().set(price);
		modelProduct.setPrice(price);
	}

	public final ObjectProperty<Category> categoryProperty() {
		return this.category;
	}

	public final Category getCategory() {
		return this.categoryProperty().get();
	}

	public final void setCategory(final Category category) {
		this.categoryProperty().set(category);
		modelProduct.setCategory(category);
	}

	public final SimpleIntegerProperty amountProperty() {
		return this.amount;
	}

	public final int getAmount() {
		return this.amountProperty().get();
	}

	public final void setAmount(final int amount) {
		this.amountProperty().set(amount);
		modelProduct.setAmount(amount);
	}

	public final SimpleBooleanProperty activeProperty() {
		return this.active;
	}

	public final boolean isActive() {
		return this.activeProperty().get();
	}

	public final void setActive(final boolean active) {
		this.activeProperty().set(active);
		modelProduct.setActive(active);
	}

}
