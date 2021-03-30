package businessLogic;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import access.OrderAccess;
import businessLogic.validators.TotalPriceValidator;
import businessLogic.validators.Validator;
import model.Client;
import model.OrderItem;
import model.Orders;

/**
 * This class provides the application with a working
 * layer between the UI and the classes that access the
 * database.
 * @author Coroian Tudor
 * @since Apr 06, 2020
 */
public class OrderBusinessLogic {

	/**
	 * The list of specific validators for this class.
	 */
	private List<Validator<Orders>> validators;
	/**
	 * The instance of the ClientAccess.java class to
	 * access the database.
	 */
	private OrderAccess orderAccess;
	/**
	 * The instance of the OrderItemBusinessLogic.java
	 * class to cascade the delete operation.
	 */
	private OrderItemBusinessLogic orderItemBLL;
	
	/**
	 * The constructor of this class. 
	 * It instantiate all the specific validators for this 
	 * class and the class that accesses the database
	 */
	public OrderBusinessLogic() {
		validators = new ArrayList<Validator<Orders>>();
		validators.add(new TotalPriceValidator());
		orderAccess = new OrderAccess();
	}
	
	/**
	 * This method returns the order with the id specified
	 * as the parameter or throws an NoSuchElementException if
	 * it is not found.
	 * @param id : int
	 * @return the order with the id specified as a 
	 * parameter
	 */
	public Orders findOrderById(int id) {
		List<Orders> orders = orderAccess.findById(id);
		if (orders == null) return null;
		if (orders.isEmpty()) return null;
		Orders order = orderAccess.findById(id).get(0);
		if (order == null || order.getDeleted() == 1) {
			throw new NoSuchElementException("The order with id=" + id + "was not found!");
		}
		return order;
	}
	
	/**
	 * @return a list with all the orders in the database
	 * or null if an error is encountered.
	 */
	public List<Orders> findAllOrders() {
		List<Orders> orders = new ArrayList<Orders>();
		orders = orderAccess.findAll();
		if (orders.isEmpty()) return null;
		List<Orders> toRemove = new ArrayList<Orders>();
		for(Orders order : orders) {
			if (order.getDeleted() == 1) toRemove.add(order);
		}
		orders.removeAll(toRemove);
		return orders;
	}
	
	/**
	 * This method returns a list with all the orders of the
	 * client whose id is equal to the one specified as the
	 * parameter of the method.
	 * @param client : Client
	 * @return a list with all the orders of the client
	 * with id_client equal with id or null in case of errors
	 */
	public List<Orders> findOrdersByClientId(Client client) {
		List<Orders> foundOrders = new ArrayList<Orders>();
		foundOrders = orderAccess.findOrderByClientId(client.getId_client());
		if (foundOrders == null) return null;
		if (foundOrders.isEmpty()) return null;
		List<Orders> toRemove = new ArrayList<Orders>();
		for(Orders order : foundOrders) {
			if (order.getDeleted() == 1) toRemove.add(order);
		}
		foundOrders.removeAll(toRemove);
		return foundOrders;
	}
	
	/**
	 * This method inserts an order specified as the parameter
	 * into the database and if the
	 * entry with the same id as the order specified
	 * in the argument was already inserted in the database. Then,
	 * this method attempts to update the previously found entry.
	 * @param order : Order
	 * @return 0 if the insert was done successfully, 1 if the
	 * entry was updated and -1 in case of errors
	 */
	public int insertOrder(Orders order){
		for (Validator<Orders> validator : validators) {
			validator.validate(order);
		}
		Orders orderFound = findOrderById(order.getId_order());
		if(orderFound == null) return orderAccess.insert(order);
		orderAccess.update(orderFound, order);
		return 1;
	}
	
	/**
	 * This method updates the database at the entry specified
	 * as the parameter.
	 * @param order : Order
	 * @return 0 if the update was done successfully and
	 * -1 otherwise
	 */
	public int updateOrder(Orders order) {
		for (Validator<Orders> validator : validators) {
			validator.validate(order);
		}
		Orders orderFound = findOrderById(order.getId_order());
		if(orderFound == null) return -1;
		return orderAccess.update(orderFound, order);
	}
	
	/**
	 * This method updates the "deleted" column of the
	 * client specified as the parameter to "true" or 1.
	 * @param order : Order
	 * @return 0 if the update was done successfully and
	 * -1 otherwise
	 */
	public int deleteOrder(Orders order) {
		Orders orderFound = findOrderById(order.getId_order());
		if(orderFound == null) return -1;
		cascadeDelete(order);
		return orderAccess.delete(order);
	}
	
	/**
	 * This method updates the "deleted" column of the order
	 * with the id specified as the parameter to "true" or 1
	 * @param id : int
	 * @return 0 if the order was deleted successfully and
	 * -1 otherwise
	 */
	public int deleteOrderById(int id) {
		Orders orderFound = findOrderById(id);
		if (orderFound == null) return -1;
		cascadeDelete(orderFound);
		return orderAccess.deleteById(id);
	}
	
	/**
	 * This method cascades the delete operation done on
	 * an order such that all the order items that belonged
	 * to the order specified as the parameter are deleted as
	 * well.
	 * @param order : Order
	 */
	public void cascadeDelete(Orders order) {
		orderItemBLL = new OrderItemBusinessLogic();
		List<OrderItem> foundOrderItems = new ArrayList<OrderItem>();
		foundOrderItems = orderItemBLL.findAllOrderItemsFromOrder(order.getId_order());
		if(foundOrderItems == null) return;
		for(OrderItem orderItem : foundOrderItems) {
			orderItemBLL.deleteOrderItem(orderItem);
		}
	}
	
	/**
	 * This method cascades the update operation such that
	 * if the id of the order given as the parameter is changed,
	 * it should change across all the order items that are
	 * part of this order.
	 * @param order : Order
	 */
	public void cascadeUpdate(Orders order) {
		orderItemBLL = new OrderItemBusinessLogic();
		List<OrderItem> foundOrderItems = new ArrayList<OrderItem>();
		foundOrderItems = orderItemBLL.findAllOrderItemsFromOrder(order.getId_order());
		if(foundOrderItems == null) return;
		for(OrderItem orderItem : foundOrderItems) {
			if(orderItem.getId_order() == order.getId_order()) continue;
			orderItem.setId_order(order.getId_order());
			orderItemBLL.updateOrderItem(orderItem);
		}
	}
	
	
	
}
