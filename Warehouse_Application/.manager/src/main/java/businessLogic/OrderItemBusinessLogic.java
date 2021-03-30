package businessLogic;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import businessLogic.validators.QuantityOrderItemValidator;
import businessLogic.validators.Validator;
import model.Orders;
import model.OrderItem;
import model.Product;
import access.OrderItemAccess;

/**
 * This class provides the application with a working
 * layer between the UI and the classes that access the
 * database.
 * @author Coroian Tudor
 * @since Apr 06, 2020
 */
public class OrderItemBusinessLogic {

	/**
	 * The list of specific validators for this class.
	 */
	private List<Validator<OrderItem>> validators;
	/**
	 * The instance of the OrderItemAccess.java class to
	 * access the database.
	 */
	private OrderItemAccess orderItemAccess;
	/**
	 * The instance of the OrderBusinessLogic.java class to
	 * access the database, specifically the "order" table.
	 */
	private OrderBusinessLogic orderBLL;
	/**
	 * The instance of the ProductBusinessLogic.java class to
	 * access the database, specifically the "product" table.
	 */
	private ProductBusinessLogic productBLL;
	
	/**
	 * The constructor of this class. 
	 * It instantiate all the specific validators for this 
	 * class and the classes that access the database
	 */
	public OrderItemBusinessLogic() {
		validators = new ArrayList<Validator<OrderItem>>();
		validators.add(new QuantityOrderItemValidator());
		orderItemAccess = new OrderItemAccess();
		//orderBLL = new OrderBusinessLogic();
		//productBLL = new ProductBusinessLogic();
	}
	
	/**
	 * This method returns the order item with the id specified
	 * as the parameter or throws an NoSuchElementException if
	 * it is not found.
	 * @param id : int
	 * @return the order item with the id specified as a 
	 * parameter
	 */
	public OrderItem findOrderItemById(int id) {
		OrderItem orderItem = orderItemAccess.findById(id).get(0);
		if (orderItem == null || orderItem.getDeleted() == 1) {
			throw new NoSuchElementException("The order item with id=" + id + " was not found!");
		}
		return orderItem;
	}
	
	/**
	 * @return a list with all the order items in the database
	 * or null if an error is encountered.
	 */
	public List<OrderItem> findAllOrderItems() {
		List<OrderItem> orderItems = new ArrayList<OrderItem>();
		orderItems = orderItemAccess.findAll();
		if (orderItems.isEmpty()) return null;
		List<OrderItem> toRemove = new ArrayList<OrderItem>();
		for(OrderItem orderItem : orderItems) {
			if (orderItem.getDeleted() == 1) toRemove.add(orderItem);
		}
		orderItems.removeAll(toRemove);
		return orderItems;
	}
	
	/**
	 * @param id : int
	 * @return a list with all the order items in the database
	 * that have the id_order equal to the one specified as the
	 * parameter or null in case of errors
	 */
	public List<OrderItem> findAllOrderItemsFromOrder(int id) {
		List<OrderItem> orderItems = new ArrayList<OrderItem>();
		orderItems = orderItemAccess.findOrderItemByOrderId(id);
		List<OrderItem> toRemove = new ArrayList<OrderItem>();
		if(orderItems == null) return null;
		if(orderItems.isEmpty()) return null;
		for(OrderItem orderItem : orderItems) {
			if (orderItem.getDeleted() == 1) toRemove.add(orderItem);
		}
		orderItems.removeAll(toRemove);
		return orderItems;
	}
	
	/**
	 * @param name : String
	 * @return a list with all the order items in the database
	 * that have the prod_name equal to the one specified as the
	 * parameter or null in case of errors
	 */
	public List<OrderItem> findAllOrderItemsFromOrder(String name) {
		List<OrderItem> orderItems = new ArrayList<OrderItem>();
		orderItems = orderItemAccess.findOrderItemByProductName(name);
		List<OrderItem> toRemove = new ArrayList<OrderItem>();
		if(orderItems == null) return null;
		if(orderItems.isEmpty()) return null;
		for(OrderItem orderItem : orderItems) {
			if (orderItem.getDeleted() == 1) toRemove.add(orderItem);
		}
		orderItems.removeAll(toRemove);
		return orderItems;
	}
	
	/**
	 * This method inserts a new entry in the "orderitem" table. 
	 * If an entry with the same id_order as the one passed as the parameter
	 * is found, the method updates the "order" table, specifically the 
	 * entry with the same id as the id_order and then inserts the new 
	 * entry in the "orderitem" table.
	 * If an entry with the same id_order and prod_name as the one passed
	 * as the parameter is found, the method updates the "order" table, 
	 * specifically the entry with the same id as the id_order and updates the
	 * quantity of the entry that was found to be similar to the argument.
	 * @param orderItem : OrderItem
	 * @return 0 in case of successful insert/update or a negative number
	 * otherwise
	 */
	public int insertOrderItem(OrderItem orderItem) {
		productBLL = new ProductBusinessLogic();
		orderBLL = new OrderBusinessLogic();
		for(Validator<OrderItem> validator : validators) {
			validator.validate(orderItem);
		}
		Orders order = orderBLL.findOrderById(orderItem.getId_order());
		if(order == null) return -2; //did not find the order in which to put this item
		Product product = productBLL.findProductByName(orderItem.getProd_name());
		if(product == null)	return -3; //did not find the product from this item
		
		if(order != null) order.setTotal(order.getTotal() + orderItem.getQuantity() * product.getPrice());
		if(product.getQuantity() < orderItem.getQuantity()) return -4; //not enough stock;
		orderBLL.updateOrder(order);
		product.setQuantity(product.getQuantity() - orderItem.getQuantity());
		productBLL.updateProduct(product);
		
		List<OrderItem> ordersWithSameOrderId = findAllOrderItemsFromOrder(orderItem.getId_order());
		if(ordersWithSameOrderId == null) return orderItemAccess.insert(orderItem);
		List<OrderItem> ordersWithSameProdName = findAllOrderItemsFromOrder(orderItem.getProd_name());
		if(ordersWithSameProdName != null) {
			for(OrderItem found : ordersWithSameProdName) {
				if(found.getId_order() == orderItem.getId_order()) {
					orderItem.setQuantity(found.getQuantity() + orderItem.getQuantity());
					orderItemAccess.update(found, orderItem);
					return 1;
				}
			}
			return orderItemAccess.insert(orderItem);
		}
		return orderItemAccess.insert(orderItem);
	}
	
	/**
	 * This method updates the database at the entry specified
	 * as the parameter.
	 * @param orderItem : OrderItem
	 * @return 0 if the update was done successfully and
	 * -1 otherwise
	 */
	public int updateOrderItem(OrderItem orderItem) {
		productBLL = new ProductBusinessLogic();
		orderBLL = new OrderBusinessLogic();
		for(Validator<OrderItem> validator : validators) {
			validator.validate(orderItem);
		}
		OrderItem orderItemFound = findOrderItemById(orderItem.getId_orderitem());
		if(orderItemFound == null) return -1;
		Orders orderFound = new Orders();
		orderFound = orderBLL.findOrderById(orderItem.getId_order());
		if(orderFound == null) return -1;
		Product productFound = new Product();
		productFound = productBLL.findProductByName(orderItem.getProd_name());
		if(productFound == null) return -1;
		orderFound.setTotal(orderFound.getTotal() - orderItemFound.getQuantity() * productFound.getPrice());
		orderFound.setTotal(orderFound.getTotal() + orderItem.getQuantity() * productFound.getPrice());
		if(productFound.getQuantity() + orderItemFound.getQuantity() < orderItem.getQuantity()) return -4; //not enough stock
		productFound.setQuantity(productFound.getQuantity() + orderItemFound.getQuantity() - orderItem.getQuantity());
		productBLL.updateProduct(productFound);
		orderBLL.updateOrder(orderFound);
		return orderItemAccess.update(orderItemFound, orderItem);
	}
	
	/**
	 * This method updates the "deleted" column of the order item
	 * specified as the parameter to "true" or 1.
	 * @param orderItem : OrderItem
	 * @return 0 if the order item was deleted successfully and
	 * -1 otherwise
	 */
	public int deleteOrderItem(OrderItem orderItem) {
		OrderItem orderItemFound = findOrderItemById(orderItem.getId_orderitem());
		if(orderItemFound == null) return -1;
		cascadeDelete(orderItem);
		return orderItemAccess.delete(orderItem);
	}
	
	/**
	 * This method updates the "deleted" column of the order item
	 * with the id specified as the parameter to "true" or 1.
	 * @param id : int
	 * @return 0 if the order item was deleted successfully and
	 * -1 otherwise
	 */
	public int deleteOrderItem(int id) {
		OrderItem orderItemFound = findOrderItemById(id);
		if(orderItemFound == null) return -1;
		cascadeDelete(orderItemFound);
		return orderItemAccess.deleteById(id);
	}
	
	/**
	 * This method cascades the delete operation such that
	 * if an order item is deleted, the total prices of that
	 * corresponding order is updated.
	 * @param orderItem : OrderItem
	 */
	public void cascadeDelete(OrderItem orderItem) {
		productBLL = new ProductBusinessLogic();
		orderBLL = new OrderBusinessLogic();
		Orders order = new Orders();
		order = orderBLL.findOrderById(orderItem.getId_order());
		Product product = new Product();
		product = productBLL.findProductByName(orderItem.getProd_name());
		if (order == null) return;
		if (product == null) return;
		order.setTotal(order.getTotal() - product.getPrice() * orderItem.getQuantity());
		if(order.getTotal() < 0) order.setTotal(0);
		orderBLL.updateOrder(order);
	}
}
