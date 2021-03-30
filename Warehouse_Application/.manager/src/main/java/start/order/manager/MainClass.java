package start.order.manager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import businessLogic.*;
import model.*;
import presentation.ReadFile;
import presentation.WriteFile;

/**
 * The main class of the application. 
 * @author Coroian Tudor
 * @since Apr 06, 2020
 */
public class MainClass {
	
	/**
	 * Global id for the client. It is updated at each insert
	 * regardless if the insert is done successfully or not.
	 */
	private static int clientID = 0;
	/**
	 * Global id for the product. It is updated at each insert
	 * regardless if the insert is done successfully or not.
	 */
	private static int productID = 0;
	/**
	 * Global id for the order. It is updated at each insert
	 * regardless if the insert is done successfully or not.
	 */
	private static int orderID = 0;
	/**
	 * Global id for the order item. It is updated at each insert
	 * regardless if the insert is done successfully or not.
	 */
	private static int orderItemID = 0;
	
	
	/**
	 * Global id to differentiate between reports.
	 */
	private static int reportNB = 0;
	/**
	 * Global id to differentiate between receipts.
	 */
	private static int receiptNB = 0;
	
	
	/**
	 * Instance of the reader class. It is used to parse the input
	 * file
	 */
	public static ReadFile reader;
	/**
	 * Instance of the writer class. It is used to generate the 
	 * output pdfs.
	 */
	public static WriteFile logger;
	
	
	/**
	 * Instance of the date format class. It is used to differentiate
	 * between the logger files generated with each execution of the 
	 * program.
	 */
	private static final DateFormat sdf = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
	/**
	 * Instance of the date format class. It is used to differentiate
	 * between different logger entries in the generated logger file.
	 */
	private static final DateFormat stf = new SimpleDateFormat("HH:mm:ss");
	
	/**
	 * Main method of the application.
	 * It parses the input file, interprets the command from each individual
	 * line and the executes the appropiate code for each method
	 * @param args
	 */
	public static void main( String[] args ) {
    	  
		String titleHead = randomString();
		Date date = new Date();
    	String title = "logger@" + sdf.format(date).toString() + ".pdf";
    	
    	WriteFile receipt;
    	String receiptName = new String();
    	receiptName = receiptName.concat(titleHead + "@");
    	
    	WriteFile report;
    	String reportName = new String();
    	reportName = reportName.concat(titleHead + "@");
    	    	
    	reader = new ReadFile(args[0]);
    	logger = new WriteFile(title);
    	
    	reader.read();

    	ClientBusinessLogic clientBLL = new ClientBusinessLogic();
    	ProductBusinessLogic productBLL = new ProductBusinessLogic();
    	OrderBusinessLogic orderBLL = new OrderBusinessLogic();
    	OrderItemBusinessLogic orderItemBLL = new OrderItemBusinessLogic();
    	    	
    	for(int i = 0; i < reader.getLinesRead(); i++) {
    		String temp = reader.getDataRead()[i];
    		String[] brokenLine = temp.split(" ");
    		if (brokenLine[0].contentEquals("Insert")) {
    			if (brokenLine[1].contentEquals("client:")) {
    				//insert client
    				String name = brokenLine[2] + " " + brokenLine[3];
    				String city = brokenLine[4];
    				name = name.replace(",", "");
    				Client client = new Client(++clientID, name, city);
					Date d = new Date();
    				switch(clientBLL.insertClient(client)) {
    				case 0: //successful insert
    					logger.write(stf.format(d).toString() + ": Client " + name + " was inserted successfully.");
    					break;
    				case 1: //update existing entry
    					logger.writeUpdate(stf.format(d).toString() + ": Client " + name + " from " + city + " was already inserted in the database. It was updated instead.");
    					break;
    				default: //error
    					logger.writeWarning(stf.format(d).toString() + ": An error occurred while inserting client " + name + " . Operation aborted.");
    				}
    			}
    			if (brokenLine[1].contentEquals("product:")) {
    				//insert product
    				String name = brokenLine[2];
    				name = name.replace(",", "");
    				brokenLine[3] = brokenLine[3].replace(",", "");
    				int quantity = Integer.parseInt(brokenLine[3]);
    				double price = Double.parseDouble(brokenLine[4]);
    				Product product = new Product(++productID, name, quantity, price);
    				Date d = new Date();
    				switch(productBLL.insertProduct(product)) {
    				case 0: //successful insert
    					logger.write(stf.format(d).toString() + ": Product " + name + " was inserted successfully");
    					break;
    				case 1: //update existing entry
    					logger.writeUpdate(stf.format(d).toString() + ": Product " + name + " was already inserted in the database. It was updated instead.");
    					break;
    				default: //error
    					logger.writeWarning(stf.format(d).toString() + ": An error occurred while inserting product " + name + ". Operation aborted");
    				}
    			}
    		} else if (brokenLine[0].contentEquals("Order:")) {
    			String name = brokenLine[1] + " " + brokenLine[2];  
				name = name.replace(",", "");
				String product = brokenLine[3];
				product = product.replace(",", "");
				int quantity = Integer.parseInt(brokenLine[4]);
				List<Client> clients = clientBLL.findClientByName(name);
				Client client = new Client();
				if (clients != null) {
					client = clients.get(0);
				}
				Date d = new Date();
				if (client == null) {
					System.out.println("An error occurred. Check " + title);
					logger.writeError(stf.format(d).toString() + ": Client " + name + " was not inserted in the databse. Creating new order aborted.");
					continue;
				}
				Orders order = new Orders(++orderID, client.getId_client());
				OrderItem orderItem = new OrderItem(++orderItemID, order.getId_order(), product, quantity);
				List<Orders> orderFound = orderBLL.findOrdersByClientId(client);
				if (orderFound == null || orderFound.size() == 0) {
					switch(orderBLL.insertOrder(order)) {
					case 0:
						logger.write(stf.format(d).toString() + ": Order of " + name + " was created successfully.");
						switch(orderItemBLL.insertOrderItem(orderItem)) {
						case -4:
							receiptName = receiptName + "Receipt_" + name + "_" + ++receiptNB + ".pdf";
							receipt = new WriteFile(receiptName);
							receipt.writeError("Not enough " + product + " in stock.");
							receipt.getDocument().close();
							System.out.println("An error occurred. Check " + title);
							logger.writeError(stf.format(d).toString() + ": Not enough " + product + " in stock. Operation aborted.");
							orderBLL.deleteOrder(order);
							break;
						case -3:
							receiptName = receiptName + "Receipt_" + name + "_" + ++receiptNB + ".pdf";
							receipt = new WriteFile(receiptName);
							receipt.writeError("Product " + product + " was not inserted in the database");
							receipt.getDocument().close();
							System.out.println("An error occurred. Check " + title);
							logger.writeError(stf.format(d).toString() + ": Product " + product + " was not inserted in the database. Operation aborted.");
							orderBLL.deleteOrder(order);
							break;
						case -2:
							receiptName = receiptName + "Receipt_" + name + "_" + ++receiptNB + ".pdf";
							receipt = new WriteFile(receiptName);
							receipt.writeError("Order with id= " + orderItem.getId_order() + " was not created in the database. Operation aborted.");
							receipt.getDocument().close();
							System.out.println("An error occurred. Check " + title);
							logger.writeError(stf.format(d).toString() + ": Order with id= " + orderItem.getId_order() + " was not created in the database. Operation aborted.");
							orderBLL.deleteOrder(order);
							break;
						case 0:
							receiptName = receiptName + "Receipt_" + name + "_" + ++receiptNB + ".pdf";
							receipt = new WriteFile(receiptName);
							receipt.writeR("Client: " + name);
							receipt.writeR("Product bought: " + product);
							receipt.writeR("Quantity: " + quantity);
							receipt.writeR("Price: " + productBLL.findProductByName(product).getPrice() * quantity);
							receipt.getDocument().close();
							logger.write(stf.format(d).toString() + ": Order item " + product + " was inserted in the database.");
							logger.write(stf.format(d).toString() + ": Order with id= " + orderItem.getId_order() + " was updated successfully.");
							break;
						case 1:
							receiptName = receiptName + "Receipt_" + name + "_" + ++receiptNB + ".pdf";
							receipt = new WriteFile(receiptName);
							receipt.writeR("Client: " + name);
							receipt.writeR("Product bought: " + product);
							receipt.writeR("Quantity: " + quantity);
							receipt.writeR("Price: " + productBLL.findProductByName(product).getPrice() * quantity);
							receipt.getDocument().close();
							logger.writeUpdate(stf.format(d).toString() + ": Product " + orderItem.getProd_name() + " from the order " + orderItem.getId_order() + " was already inserted in the database. It was updated instead.");
							break;
						default:
							logger.writeWarning(stf.format(d).toString() + ": An error occurred while inserting order item " + product + ". Operation aborted.");							
						}
						break;
					case 1:
						logger.writeUpdate(stf.format(d).toString() + ": Order with id= " + order.getId_order() + " was already inserted in the database. It was updated instead.");
						break;
					default:
						logger.writeWarning(stf.format(d).toString() + ": An error occurred while inserting order with id= " + order.getId_order() + ". Operation aborted.");
					} 
				} else {
					order.setId_order(orderFound.get(0).getId_order());
					orderItem = new OrderItem(++orderItemID, order.getId_order(), product, quantity);
					switch(orderItemBLL.insertOrderItem(orderItem)) {
					case -4:
						receiptName = receiptName + "Receipt_" + name + "_" + ++receiptNB + ".pdf";
						receipt = new WriteFile(receiptName);
						receipt.writeError("Not enough " + product + " in stock.");
						receipt.getDocument().close();
						System.out.println("An error occurred. Check " + title);
						logger.writeError(stf.format(d).toString() + ": Not enough " + product + " in stock. Operation aborted.");
						break;
					case -3:
						receiptName = receiptName + "Receipt_" + name + "_" + ++receiptNB + ".pdf";
						receipt = new WriteFile(receiptName);
						receipt.writeError("Product " + product + " was not inserted in the database");
						receipt.getDocument().close();
						System.out.println("An error occurred. Check " + title);
						logger.writeError(stf.format(d).toString() + ": Product " + product + " was not inserted in the database. Operation aborted.");
						break;
					case -2:
						receiptName = receiptName + "Receipt_" + name + "_" + ++receiptNB + ".pdf";
						receipt = new WriteFile(receiptName);
						receipt.writeError("Order with id= " + orderItem.getId_order() + " was not created in the database. Operation aborted.");
						receipt.getDocument().close();
						System.out.println("An error occurred. Check " + title);
						logger.writeError(stf.format(d).toString() + ": Order with id= " + orderItem.getId_order() + " was not created in the database. Operation aborted.");
						break;
					case 0:
						receiptName = receiptName + "Receipt_" + name + "_" + ++receiptNB + ".pdf";
						receipt = new WriteFile(receiptName);
						receipt.writeR("Client: " + name);
						receipt.writeR("Product bought: " + product);
						receipt.writeR("Quantity: " + quantity);
						receipt.writeR("Price: " + productBLL.findProductByName(product).getPrice() * quantity);
						receipt.getDocument().close();
						logger.write(stf.format(d).toString() + ": Order item " + product + " was inserted in the database.");
						logger.write(stf.format(d).toString() + ": Order with id= " + orderItem.getId_order() + " was updates successfully.");
						break;
					case 1:
						receiptName = receiptName + "Receipt_" + name + "_" + ++receiptNB + ".pdf";
						receipt = new WriteFile(receiptName);
						receipt.writeR("Client: " + name);
						receipt.writeR("Product bought: " + product);
						receipt.writeR("Quantity: " + quantity);
						receipt.writeR("Price: " + productBLL.findProductByName(product).getPrice() * quantity);
						receipt.getDocument().close();
						logger.writeUpdate(stf.format(d).toString() + ": Product " + orderItem.getProd_name() + " from the order " + orderItem.getId_order() + " was already inserted in the database. It was updated instead.");
						break;
					default:
						logger.writeWarning(stf.format(d).toString() + ": An error occurred while inserting order item " + product + ". Operation aborted.");							
					}
				}
    		} else if (brokenLine[0].contentEquals("Delete")) {
    			if(brokenLine[1].contentEquals("client:")) {
    				//delete client
    				Date d = new Date();
    				String name = brokenLine[2] + " " + brokenLine[3];
    				name = name.replace(",", "");
    				String city = brokenLine[4];
    				Client client = new Client(name, city);
    				client = clientBLL.findClient(client);
    				if(client == null) {
    					System.out.println("An error occurred. Check " + title);
    					logger.writeError(stf.format(d).toString() + ": Client " + name + " was not found in the database. Operation aborted.");
    				} else {
    					switch(clientBLL.deleteClient(client)) {
        				case -1:
        					System.out.println("An error occurred. Check " + title);
        					logger.writeError(stf.format(d).toString() + ": Client " + name + " was not found in the database. Operation aborted.");
        					break;
        				default:
        					logger.write(stf.format(d).toString() + ": Client " + name + " was deleted successfully.");
        				}
    				}
    			} else if (brokenLine[1].contentEquals("product:")) {
    				//delete product
    				Date d = new Date();
    				String prodName = brokenLine[2];
    				Product product = productBLL.findProductByName(prodName);
    				if(product == null) {
    					System.out.println("An error occurred. Check " + title);
    					logger.writeError(stf.format(d).toString() + ": Product " + prodName + " was not found int the database.");
    				} else {
    					switch(productBLL.deleteProduct(product)) {
        				case -1:
        					System.out.println("An error occurred. Check " + title);
        					logger.writeError(stf.format(d).toString() + ": Product " + prodName + " was not found int the database.");
        					break;
        				default:
        					logger.write(stf.format(d).toString() + ": Product " + prodName + " was deleted successfully.");
        				}
    				}
    			}
    		} else if (brokenLine[0].contentEquals("Report")) {
    			if (brokenLine[1].contentEquals("client")) {
    				//generate report for clients
    				Date d = new Date();
    				List<Client> foundClients = clientBLL.findAllClients();
    				if (foundClients != null) {
    					reportName = reportName + "Report_Clients_" + ++reportNB + ".pdf";
    					report = new WriteFile(reportName);
    					report.writeTableClients(foundClients);
    					report.getDocument().close();
        				logger.write(stf.format(d).toString() + ": Report for clients was generated successfully.");
    				} else logger.writeWarning(stf.format(d).toString() + ": Table \"client\" is empty.");
    			} else if (brokenLine[1].contentEquals("order")){
    				//generate report for orders
    				Date d = new Date();
    				List<Client> foundClients = clientBLL.findAllClients();
    				List<Orders> foundOrders = orderBLL.findAllOrders();
    				List<OrderItem> foundOrderItems = orderItemBLL.findAllOrderItems();
    				if((foundClients != null && foundOrders != null && foundOrderItems != null) || (foundClients.size() != 0 && foundOrders.size() != 0 && foundOrderItems.size() != 0)) {
    					reportName = reportName + "Report_Orders_" + ++reportNB + ".pdf";
    					report = new WriteFile(reportName);
    					report.writeTableOrders(foundOrders, foundClients, foundOrderItems);
    					report.getDocument().close();
        				logger.write(stf.format(d).toString() + ": Report for orders was generated successfully.");
    				} else logger.writeWarning(stf.format(d).toString() + ": Table \"order\" is empty.");
    			} else if (brokenLine[1].contentEquals("product")) {
    				//generate report for products
    				List<Product> foundProducts = productBLL.findAllProducts();
    				Date d = new Date();
    				if (foundProducts != null) {
    					reportName = reportName + "Report_Products_" + ++reportNB + ".pdf";
    					report = new WriteFile(reportName);
    					report.writeTableProducts(foundProducts);
    					report.getDocument().close();
        				logger.write(stf.format(d).toString() + ": Report for products was generated successfully.");
    				} else logger.writeWarning(stf.format(d).toString() + ": Table \"product\" is empty.");
    			}
    		}
    		receiptName = titleHead;
    		reportName = titleHead;
    	}
		logger.getDocument().close();
		logger.getWriter().close();
		
    }
	
	/**
	 * This method generates a random string of letters to serve
	 * as a title head for all the files generated by the application.
	 * @return a random string of letters
	 */
	public static String randomString() {
	    int leftLimit = 48; // numeral '0'
	    int rightLimit = 122; // letter 'z'
	    int targetStringLength = 5;
	    Random random = new Random();
	 
	    String generatedString = random.ints(leftLimit, rightLimit + 1)
	      .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
	      .limit(targetStringLength)
	      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
	      .toString();
	 
	    return generatedString;
	}
}
