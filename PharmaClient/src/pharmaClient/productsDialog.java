package pharmaClient;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.DefaultStringConverter;

public class productsDialog extends Dialog<ButtonType> {

	@SuppressWarnings("unchecked")
	public productsDialog(ObservableList<ProductFX> productsOl) {
		
		TableColumn<ProductFX, String> nameCol = new TableColumn<>("Name");
		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
		nameCol.setOnEditCommit(t -> { 
			ProductFX selectedProduct = t.getRowValue();
			selectedProduct.setName(t.getNewValue());
			updateProduct(selectedProduct);
		});
		nameCol.setCellFactory(column -> createTextFieldTableCell(nameCol));
		nameCol.setPrefWidth(150);

		TableColumn<ProductFX, String> descrCol = new TableColumn<>("Description");
		descrCol.setCellValueFactory(new PropertyValueFactory<>("description"));
		descrCol.setOnEditCommit(t -> {
			ProductFX selectedProduct = t.getRowValue();
			selectedProduct.setDescription(t.getNewValue());
			updateProduct(selectedProduct);
		});
		descrCol.setCellFactory(column -> createTextFieldTableCell(descrCol));
		descrCol.setPrefWidth(150);

		TableColumn<ProductFX, Double> priceCol = new TableColumn<>("Price");
		priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
		priceCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
		priceCol.setOnEditCommit(t -> {
			ProductFX selectedProduct = t.getRowValue();
			selectedProduct.setPrice(t.getNewValue());
			updateProduct(selectedProduct);
		});
		priceCol.setPrefWidth(60);

		TableColumn<ProductFX, String> categoryCol = new TableColumn<>("Category");
		categoryCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory().getName()));
		categoryCol.setPrefWidth(100);
		categoryCol.setCellFactory(column -> createTextFieldTableCell(categoryCol));
		categoryCol.setEditable(false);
		
		TableColumn<ProductFX, Integer> amountCol = new TableColumn<>("Quantity");
		amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
		amountCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		amountCol.setOnEditCommit(t -> {
			ProductFX selectedProduct = t.getRowValue();
			selectedProduct.setAmount(t.getNewValue());
			updateProduct(selectedProduct);
		});
		amountCol.setPrefWidth(80);

		TableColumn<ProductFX, Boolean> activeCol = new TableColumn<>("isActive");
		activeCol.setCellValueFactory(new PropertyValueFactory<>("active"));
		activeCol.setCellFactory(CheckBoxTableCell.forTableColumn(activeCol));
		activeCol.setPrefWidth(70);

		SortedList<ProductFX> sortedProductList = new SortedList<>(productsOl);
		sortedProductList.setComparator((product1, product2) ->
		        product2.getCategory().getName().compareTo(product1.getCategory().getName())
		);
		TableView<ProductFX> productTv = new TableView<>(sortedProductList);
		sortedProductList.comparatorProperty().bind(productTv.comparatorProperty());		// synchronising the comparators between the SortedList the TableView
		
		productTv.getColumns().addAll(nameCol, descrCol, priceCol, categoryCol, amountCol, activeCol);
		
		productTv.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		productTv.setEditable(true);
		productTv.getSortOrder().add(categoryCol);
		categoryCol.setSortType(TableColumn.SortType.ASCENDING);

		VBox vb = new VBox(10, productTv);
		this.getDialogPane().setContent(vb);
		this.setResizable(true);
		this.setTitle("The product list");
		this.getDialogPane().getButtonTypes().add(new ButtonType("Close", ButtonData.CANCEL_CLOSE));
	}
	
	/**
	 * Method updating information of a product on the server (Database) by sending a POST request, based on the provided ProductFX object.
	 * Handles a PharmaException, indicating an issue with the service call. 
	 * 
	 * @param selectedProduct	The ProductFX object representing the selected product to be updated.
	 */
	private void updateProduct(ProductFX selectedProduct) {
		{
			String xmlData = selectedProduct.getModelProduct().toXML();
			try {
				ServiceFunctions.post("product", String.valueOf(selectedProduct.getModelProduct().getId()), xmlData);
			} catch (PharmaException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method that creates a custom TextFieldTableCell for a specified TableColumn in a TableView.
	 * 
	 * @param column The TableColumn for which the custom TextFieldTableCell is created.
	 * @return A TextFieldTableCell configured with a custom updateItem method.
	 */
	TextFieldTableCell<ProductFX, String> createTextFieldTableCell(TableColumn<ProductFX, String> column) {
	    return new TextFieldTableCell<ProductFX, String>(new DefaultStringConverter()) {
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
