package pharmaClient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import javafx.collections.ObservableList;
import pharmaClasses.CartElement;
import pharmaClasses.Client;

public class CreatePdf {

	private static Font bigBoldItalicFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLDITALIC);
	private static Font normalFont = new Font(Font.FontFamily.HELVETICA, 12);
	private static Font normalBoldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
	private static Font smallBoldFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
	private static Font smallFont = new Font(Font.FontFamily.HELVETICA, 10);

	/**
	 * Method that adds the beginning section of an invoice to the specified PDF Document.
	 * This method adds the company information, invoice title, and invoice number to the document.
	 * 
	 * @param document The PDF Document to which the beginning section is added.
	 * @param number   The unique invoice number.
	 * @param client The client object. 
	 * @throws DocumentException If an error occurs while adding content to the PDF document.
	 */
	static void addBeginning(Document document, String number, Client client) throws DocumentException {
		Paragraph par1 = new Paragraph();
		addEmptyLine(par1, 3);
		par1.add(new Paragraph("INVOICE", bigBoldItalicFont));
		par1.add(new Paragraph("MoniMed", normalBoldFont));
		par1.add(new Paragraph("Hetzendorferstr. 112", smallBoldFont));
		par1.add(new Paragraph("1120 Vienna", smallBoldFont));
		addEmptyLine(par1, 1);

		Paragraph par2 = new Paragraph("Invoice no. " + client.getUserId() + "_" + number + " generated on: " + new Date(), normalFont);
		par2.setAlignment(Element.ALIGN_CENTER);
		par1.add(par2);
		addEmptyLine(par1, 2);
		
		par2 = new Paragraph("BILL/SHIP TO:", normalBoldFont);
		par2.setAlignment(Element.ALIGN_RIGHT);
		par1.add(par2);
		
		par1.setIndentationRight(50f);
		par1.setIndentationLeft(50f);
		
		par2 = new Paragraph(client.getUserId(), normalBoldFont);
		par2.setAlignment(Element.ALIGN_RIGHT);
		par1.add(par2);
		
		par2 = new Paragraph(client.getName(), normalBoldFont);
		par2.setAlignment(Element.ALIGN_RIGHT);
		par1.add(par2);
		
		par2 = new Paragraph(client.getAddress(), smallFont);
		par2.setAlignment(Element.ALIGN_RIGHT);
		par1.add(par2);
		
		par2 = new Paragraph(client.getContactPerson(), smallFont);
		par2.setAlignment(Element.ALIGN_RIGHT);
		par1.add(par2);
		
		addEmptyLine(par1, 1);

		document.add(par1);
	}


	/**
	 * Method that adds the end section (due date information) of an invoice to the specified PDF Document.
	 * 
	 * @param document The PDF Document to which the end section is added.
	 * @throws DocumentException If an error occurs while adding content to the PDF document.
	 */
	static void addEnd(Document document) throws DocumentException {

		LocalDate currentDate = LocalDate.now();
		LocalDate dueDate = currentDate.plusDays(7);

		Paragraph dueTo = new Paragraph("DUE DATE: " + dueDate, smallBoldFont);
		dueTo.setAlignment(Element.ALIGN_RIGHT);
		dueTo.setIndentationRight(50f);

		document.add(dueTo);
	}
	
	/**
	 * Method that adds a table representing order details to the specified PDF Document.
	 * It creates columns for item name, quantity, unit price and amount.
	 * It populates the table with order details from the provided list of CartElements.
	 * The total order value is calculated and displayed at the end of the table.
	 * 
	 * @param document  The PDF Document to which the table is added.
	 * @param orderList The list of CartElements representing the order details.
	 * @throws DocumentException If an error occurs while adding content to the PDF document.
	 */
	static void addTableToInvoice(Document document, ArrayList<CartElement> orderList) throws DocumentException {
		Paragraph parTable = new Paragraph();

		PdfPTable table = new PdfPTable(4);
		float[] columnWidths = { 49f, 17f, 17f, 17f };
		table.setWidths(columnWidths);
		table.setPaddingTop(3);

		PdfPCell c1 = new PdfPCell(new Phrase("Item", normalFont));
		c1.setHorizontalAlignment(Element.ALIGN_LEFT);
		c1.setBorderWidthRight(0f);
		c1.setBorderWidthLeft(0f);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("Quantity", normalFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		c1.setBorderWidthRight(0f);
		c1.setBorderWidthLeft(0f);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("Unit price", normalFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		c1.setBorderWidthRight(0f);
		c1.setBorderWidthLeft(0f);
		;
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("Amount", normalFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		c1.setBorderWidthRight(0f);
		c1.setBorderWidthLeft(0f);
		table.addCell(c1);
		table.setHeaderRows(1);

		double total = 0.0;
		for (CartElement one : orderList) {
			c1 = new PdfPCell(new Phrase(one.getProduct().getName(), normalFont));
			c1.setBorderWidthRight(0f);
			c1.setBorderWidthLeft(0f);
			table.addCell(c1);
			c1 = new PdfPCell(new Phrase(String.valueOf(one.getAmount()), normalFont));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			c1.setBorderWidthRight(0f);
			c1.setBorderWidthLeft(0f);
			table.addCell(c1);
			c1 = new PdfPCell(new Phrase(String.valueOf(one.getProduct().getPrice()), normalFont));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			c1.setBorderWidthRight(0f);
			c1.setBorderWidthLeft(0f);
			table.addCell(c1);
			c1 = new PdfPCell(new Phrase(
					String.format(Locale.US, "%.2f", one.getAmount() * one.getProduct().getPrice()), normalFont));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			c1.setBorderWidthRight(0f);
			c1.setBorderWidthLeft(0f);
			table.addCell(c1);
			total += one.getAmount() * one.getProduct().getPrice();
		}
		c1 = new PdfPCell(new Phrase(""));
		c1.setBorderWidth(0f);
		table.addCell(c1);
		c1 = new PdfPCell(new Phrase(""));
		c1.setBorderWidth(0f);
		table.addCell(c1);
		c1 = new PdfPCell(new Phrase("TOTAL", normalBoldFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		c1.setBorderWidth(0f);
		table.addCell(c1);
		c1 = new PdfPCell(new Phrase("€ " + String.format(Locale.US, "%.2f", total), normalBoldFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		c1.setBorderWidth(0f);
		table.addCell(c1);

		parTable.add(table);
		addEmptyLine(parTable, 2);

		document.add(parTable);
	}

	/**
	 * Method that adds a table representing sales details to the specified PDF Document.
	 * It creates columns for order details, including order ID, client ID, date, product name, category, price and quantity. 
	 * It populates the table with data from the provided ObservableList of CartElementFX representing orders.
	 * 
	 * @param document  The PDF Document to which the table is added.
	 * @param ol The ObservableList of CartElementFX containing sales order details.
	 * @throws DocumentException If an error occurs while adding content to the PDF document.
	 */
	static void addTableToSalesReport(Document document, ObservableList<CartElementFX> ol) throws DocumentException {
		PdfPTable table = new PdfPTable(7);
		float[] columnWidths = { 10f, 12f, 17f, 22f, 15f, 12f, 12f };
		table.setWidths(columnWidths);
		table.setPaddingTop(3);

		table.addCell("ID");
		table.addCell("ClientID");
		table.addCell("Date");
		table.addCell("Name");
		table.addCell("Category");
		table.addCell("Price");
		table.addCell("Qty");

		for (CartElementFX order : ol) {
			table.addCell(String.valueOf(order.getId()));
			table.addCell(String.valueOf(order.getClientId()));
			table.addCell(String.valueOf(order.getOrderDate()));
			table.addCell(order.getProduct().getName());
			table.addCell(order.getProduct().getCategory().getName());
			table.addCell(String.valueOf(order.getProduct().getPrice()));
			table.addCell(String.valueOf(order.getAmount()));
		}
		document.add(table);

	}
	
	/**
	 * Method that adds a table representing inventory details to the specified PDF Document.
	 * It creates columns for order details, including product ID, product name, price and quantity. 
	 * It populates the table with data from the provided ObservableList of ProductFX.
	 * 
	 * @param document  The PDF Document to which the table is added.
	 * @param ol The ObservableList of ProductFX containing product details.
	 * @throws DocumentException If an error occurs while adding content to the PDF document.
	 */
	static void addTableToInventoryReport(Document document, ObservableList<ProductFX> ol) throws DocumentException {
		PdfPTable table = new PdfPTable(5);
		float[] columnWidths = { 15f, 15f, 40f, 15f, 15f };
		table.setWidths(columnWidths);
		table.setPaddingTop(3);

		table.addCell(""); 
		table.addCell("Product ID");
		table.addCell("Product name");
		table.addCell("Unit price");
		table.addCell("Qty");

		int index = 1;
		for (ProductFX product : ol) {
			table.addCell(String.valueOf(index++));
			table.addCell(String.valueOf(product.getId()));
			table.addCell(product.getName());
			table.addCell(String.valueOf(product.getPrice()));
			table.addCell(String.valueOf(product.getAmount()));
		}
		document.add(table);

	}

	/**
	 * Method that adds empty lines to a Paragraph for spacing in a PDF document.
	 * It appends a specified number of empty lines to the provided Paragraph to create visual separation in the generated PDF document.
	 * 
	 * @param paragraph The Paragraph to which empty lines are added.
	 * @param number    The number of empty lines to add to the Paragraph.
	 */
	static void addEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(" "));
		}
	}
}
