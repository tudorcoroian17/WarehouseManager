package presentation;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.CMYKColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import model.Client;
import model.OrderItem;
import model.Orders;
import model.Product;

/**
 * This class provides the application with a writer
 * that outputs the results of the computations in a 
 * .pdf file.
 * @author Coroian Tudor
 * @since Apr 06, 2020
 *
 */
public class WriteFile {

	/**
	 * Instance of the document class. Holds a reference
	 * to the document in which writer will output the
	 * results.
	 */
	private Document document;
	/**
	 * Instance of the pdf writer class.
	 */
	private PdfWriter writer;
	private int nbLines = 0;
	
	/**
	 * Instance of the font class.
	 * It is used for writing an update message to the logger file.
	 */
	private static Font blueFont = FontFactory.getFont(FontFactory.COURIER, 10, Font.ITALIC, new CMYKColor(190, 100, 0, 40));
	/**
	 * Instance of the font class.
	 * It is used for writing an error message to the logger file.
	 * 
	 */
	private static Font redFont = FontFactory.getFont(FontFactory.COURIER, 10, Font.BOLD, new CMYKColor(0, 255, 240, 40));
	/**
	 * Instance of the font class.
	 * It is used for writing an warning message to the logger file.
	 * 
	 */
	private static Font yellowFont = FontFactory.getFont(FontFactory.COURIER, 10, Font.ITALIC, new CMYKColor(0, 0, 255, 0));
	/**
	 * Instance of the font class.
	 * It is used for writing an successful message to the logger file.
	 * 
	 */
	private static Font blackFont = FontFactory.getFont(FontFactory.COURIER, 10, Font.NORMAL, new CMYKColor(0, 0, 0, 255));
	/**
	 * 
	 * Instance of the font class.
	 * It is used for writing the receipts after executing each order.
	 */
	private static Font blackFont2 = FontFactory.getFont(FontFactory.COURIER, 12, Font.BOLD, new CMYKColor(0, 0, 0, 255));
	
	/**
	 * Constructor of this class. 
	 * It creates a new file, with the title passed as the parameter.
	 * @param fileTitle : String
	 */
	public WriteFile(String fileTitle) {
		this.document = new Document();
		try {
			this.writer = PdfWriter.getInstance(document, new FileOutputStream(fileTitle));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method used for writing the receipt, one line at a time.
	 * The written text is the one given as the parameter of the method. 
	 * @param data : String
	 */
	public void writeR(String data) {
		document.open();
		try {
			document.add(new Paragraph(data, blackFont2));
		} catch (DocumentException e) {
			e.printStackTrace();
		}
}
	
	/**
	 * Method used for written a successful operation message
	 * in the logger file. The written text is the one given
	 * as the parameter of the method.
	 * @param data : String
	 */
	public void write(String data) {
			document.open();
			nbLines++;
			if (nbLines > 45) document.newPage();
			try {
				document.add(new Paragraph(data, blackFont));
			} catch (DocumentException e) {
				e.printStackTrace();
			}
	}
	
	/**
	 * Method used for written an error message
	 * in the logger file. The written text is the one given
	 * as the parameter of the method.
	 * @param data : String
	 */
	public void writeError(String data) {
		document.open();
		if (nbLines > 45) document.newPage();
		nbLines++;
		try {
			document.add(new Paragraph(data, redFont));
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method used for written a warning message
	 * in the logger file. The written text is the one given
	 * as the parameter of the method.
	 * @param data : String
	 */
	public void writeWarning(String data) {
		document.open();
		if (nbLines > 45) document.newPage();
		nbLines++;
		try {
			document.add(new Paragraph(data, yellowFont));
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method used for written an update message
	 * in the logger file. The written text is the one given
	 * as the parameter of the method.
	 * @param data : String
	 */
	public void writeUpdate(String data) {
		document.open();
		if (nbLines > 45) document.newPage();
		nbLines++;
		try {
			document.add(new Paragraph(data, blueFont));
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method used to generate the report for the table "client".
	 * @param clients : List of Client
	 */
	public void writeTableClients(List<Client> clients) {
		document.open();
		PdfPTable table = new PdfPTable(3);
		table.setWidthPercentage(100);
		PdfPCell header = new PdfPCell();
		header.setBackgroundColor(BaseColor.LIGHT_GRAY);
		header.setBorderWidth(3);
		header.setHorizontalAlignment(Element.ALIGN_CENTER);
		header.setVerticalAlignment(Element.ALIGN_MIDDLE);
		header.setPhrase(new Phrase("ID"));
		table.addCell(header);
		header.setPhrase(new Phrase("Name"));
		table.addCell(header);
		header.setPhrase(new Phrase("City"));
		table.addCell(header);
		for(Client client : clients) {
			PdfPCell cell1 = new PdfPCell();
			cell1.setBorderWidth(3);
			cell1.setPhrase(new Phrase(Integer.toString(client.getId_client())));
			table.addCell(cell1);
			cell1.setPhrase(new Phrase(client.getName()));
			table.addCell(cell1);
			cell1.setPhrase(new Phrase(client.getCity()));
			table.addCell(cell1);
		}
		try {
			document.add(table);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Method used to generate the report for the table "product".
	 * @param products : List of Product
	 */
	public void writeTableProducts(List<Product> products) {
		document.open();
		PdfPTable table = new PdfPTable(4);
		table.setWidthPercentage(100);
		PdfPCell header = new PdfPCell();
		header.setBackgroundColor(BaseColor.LIGHT_GRAY);
		header.setBorderWidth(3);
		header.setHorizontalAlignment(Element.ALIGN_CENTER);
		header.setVerticalAlignment(Element.ALIGN_MIDDLE);
		header.setPhrase(new Phrase("ID"));
		table.addCell(header);
		header.setPhrase(new Phrase("Name"));
		table.addCell(header);
		header.setPhrase(new Phrase("Quantity"));
		table.addCell(header);
		header.setPhrase(new Phrase("Price"));
		table.addCell(header);
		for(Product product : products) {
			PdfPCell cell1 = new PdfPCell();
			cell1.setBorderWidth(3);
			cell1.setPhrase(new Phrase(Integer.toString(product.getId_product())));
			table.addCell(cell1);
			cell1.setPhrase(new Phrase(product.getProd_name()));
			table.addCell(cell1);
			cell1.setPhrase(new Phrase(Integer.toString(product.getQuantity())));
			table.addCell(cell1);
			Double rounded = Math.round(product.getPrice() * 20.0) / 20.0;
			cell1.setPhrase(new Phrase(Double.toString(rounded)));
			table.addCell(cell1);
		}
		try {
			document.add(table);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Method used to generate the report for the table "orders".
	 * It generates a table with all the orders that were made
	 * and were not deleted with the id of each order, the name 
	 * of the client that made the order, a sub-table with all 
	 * the items in that order and the total price of the order.
	 * @param orders : List of Orders
	 * @param clients : List of Client
	 * @param orderItems : List of OrderItem
	 */
	public void writeTableOrders(List<Orders> orders, List<Client> clients, List<OrderItem> orderItems) {
		document.open();
		PdfPTable table = new PdfPTable(4);
		table.setWidthPercentage(100);
		PdfPCell header = new PdfPCell();
		header.setBackgroundColor(BaseColor.LIGHT_GRAY);
		header.setBorderWidth(3);
		header.setHorizontalAlignment(Element.ALIGN_CENTER);
		header.setVerticalAlignment(Element.ALIGN_MIDDLE);
		header.setPhrase(new Phrase("ID"));
		table.addCell(header);
		header.setPhrase(new Phrase("Client Name"));
		table.addCell(header);
		header.setPhrase(new Phrase("Order Item"));
		table.addCell(header);
		header.setPhrase(new Phrase("Total"));
		table.addCell(header);
		for(Orders order : orders) {
			PdfPCell cell1 = new PdfPCell();
			cell1.setPadding(0);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setBorderWidth(3);
			
			cell1.setPhrase(new Phrase(Integer.toString(order.getId_order())));
			table.addCell(cell1);
			
			for(int i = 0; i < clients.size(); i++) {
				if(order.getId_client() == clients.get(i).getId_client()) {
					cell1.setPhrase(new Phrase(clients.get(i).getName()));
				}
			}
			table.addCell(cell1);
			
			PdfPTable tableOrderItems = new PdfPTable(2);
			PdfPCell headerOrderItems = new PdfPCell();
			headerOrderItems.setBackgroundColor(BaseColor.LIGHT_GRAY);
			headerOrderItems.setBorderWidth(3);
			headerOrderItems.setPhrase(new Phrase("Product"));
			tableOrderItems.addCell(headerOrderItems);
			headerOrderItems.setPhrase(new Phrase("Quantity"));
			tableOrderItems.addCell(headerOrderItems);
			for(int i = 0; i < orderItems.size(); i++) {
				PdfPCell cell2 = new PdfPCell();
				cell2.setBorderWidth(3);
				
				if(order.getId_order() == orderItems.get(i).getId_order()) {
					cell2.setPhrase(new Phrase(orderItems.get(i).getProd_name()));
					tableOrderItems.addCell(cell2);
					cell2.setPhrase(new Phrase(Integer.toString(orderItems.get(i).getQuantity())));
					tableOrderItems.addCell(cell2);
				}
			}
			cell1.addElement(tableOrderItems);
			table.addCell(cell1);
			
			Double rounded = Math.round(order.getTotal() * 20.0) / 20.0;
			cell1.setPhrase(new Phrase(Double.toString(rounded)));
			table.addCell(cell1);
		}
		try {
			document.add(table);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @return the document of the writer
	 */
	public Document getDocument() {
		return document;
	}

	/**
	 * Sets the document of the writer.
	 * @param document : Document
	 */
	public void setDocument(Document document) {
		this.document = document;
	}

	/**
	 * @return the writer
	 */
	public PdfWriter getWriter() {
		return writer;
	}

	/**
	 * Sets the instance of the writer.
	 * @param writer : PdfWriter
	 */
	public void setWriter(PdfWriter writer) {
		this.writer = writer;
	}
}
