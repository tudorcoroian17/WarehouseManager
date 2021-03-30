package model;

/**
 * Provides the class "Order" linked to the table 
 * "order" in the ordermng.db database.
 * @author Coroian Tudor
 * @since Apr 06, 2020
 */

public class Orders {

	/**
	 * The id of the order stored in the database.
	 * Must be unique.
	 */
	private int id_order;
	/**
	 * The id of the client that made the order.
	 */
	private int id_client;
	/**
	 * The total price of the order.
	 */
	private double total;
	/**
	 * Flag to show if the order should be treated 
	 * as deleted.
	 */
	private int deleted;
	
	/**
	 * Constructor for the Order.java class without
	 * any arguments. It is used only for instantiating
	 * the fields with default values.
	 */
	public Orders() {
		this.id_order = 0;
		this.id_client = 0;
		this.total = 0;
		this.deleted = 0;
	}
	
	/**
	 * Constructor for the Order.java class when the
	 * id_order is known.
	 * @param id : int 
	 * @param client : int
	 */
	public Orders(int id, int client) {
		super();
		this.id_order = id;
		this.id_client = client;
		this.total = 0;
		this.deleted = 0;
	}
	
	/**
	 * Constructor for the Order.java class when the
	 * id_order is unknown.
	 * @param client : int
	 */
	public Orders(int client) {
		super();
		this.id_client = client;
		this.total = 0;
		this.deleted = 0;
	}

	/**
	 * @return the id of the order
	 */
	public int getId_order() {
		return id_order;
	}

	/**
	 * Sets the id of this order.
	 * @param id_order : int
	 */
	public void setId_order(int id_order) {
		this.id_order = id_order;
	}

	/**
	 * @return the id of the client that made the order
	 */
	public int getId_client() {
		return id_client;
	}

	/**
	 * Sets the id of the client that made the order.
	 * @param id_client : int
	 */
	public void setId_client(int id_client) {
		this.id_client = id_client;
	}

	/**
	 * @return the total price to be payed
	 */
	public double getTotal() {
		return total;
	}

	/**
	 * Sets the total price of this order.
	 * @param total : double
	 */
	public void setTotal(double total) {
		this.total = total;
	}

	/**
	 * @return 1 if this order is deleted and 0 otherwise
	 */
	public int getDeleted() {
		return deleted;
	}

	/**
	 * Sets the deleted flag of this class.
	 * @param deleted : int
	 */
	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}
	
	
}
