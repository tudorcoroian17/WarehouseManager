package model;

/**
 * Provides the class "OrderItem" linked to the table 
 * "orderitem" in the ordermng.db database.
 * @author Coroian Tudor
 * @since Apr 06, 2020
 */

public class OrderItem {

	/**
	 * The id of the order item stored in the database.
	 * Must be unique.
	 */
	private int id_orderitem;
	/**
	 * The id of the order to which this order item
	 * belongs.
	 * Must correspond to an existing order id.
	 */
	private int id_order;
	/**
	 * The name of the product (order item).
	 * Multiple entries can have similar id_order if
	 * the product names are different.
	 */
	private String prod_name;
	/**
	 * The quantity of the product.
	 * Must be greater than zero.
	 * Multiple entries can have similar product names
	 * if the id_order is different.
	 */
	private int quantity;
	/**
	 * Flag to show if the order item should be treated 
	 * as deleted.
	 */
	private int deleted;
	
	/**
	 * Constructor for the OrderItem.java class without
	 * any arguments. It is used only for instantiating
	 * the fields with default values.
	 */
	public OrderItem() {
		this.id_orderitem = 0;
		this.id_order = 0;
		this.prod_name = new String();
		this.quantity = 0;
		this.deleted = 0;
	}
	
	/**
	 * Constructor for the Order.java class when the
	 * id_orderitem is known.
	 * @param id : int
	 * @param idOrder : int
	 * @param name : String
	 * @param quantity : int
	 */
	public OrderItem(int id, int idOrder, String name, int quantity) {
		super();
		this.id_orderitem = id;
		this.id_order = idOrder;
		this.prod_name = name;
		this.quantity = quantity;
		this.deleted = 0;
	}
	
	/**
	 * Constructor for the Order.java class when the
	 * id_orderitem is unknown.
	 * @param idOrder : int
	 * @param name : String
	 * @param quantity : int
	 */
	public OrderItem(int idOrder, String name, int quantity) {
		super();
		this.id_order = idOrder;
		this.prod_name = name;
		this.quantity = quantity;
		this.deleted = 0;
	}

	/**
	 * @return the id of this product (order item)
	 */
	public int getId_orderitem() {
		return id_orderitem;
	}

	/**
	 * Sets the id of this product (order item).
	 * @param id_orderitem : int
	 */
	public void setId_orderitem(int id_orderitem) {
		this.id_orderitem = id_orderitem;
	}
	
	/**
	 * @return the id of the order to which this
	 * product (order item) belongs
	 */
	public int getId_order() {
		return id_order;
	}

	/**
	 * Sets the id of the order to which this
	 * product (order item) belongs.
	 * @param id_order : int
	 */
	public void setId_order(int id_order) {
		this.id_order = id_order;
	}

	/**
	 * @return the name of this product (order item).
	 */
	public String getProd_name() {
		return prod_name;
	}

	/**
	 * Sets the name of this product (order item).
	 * @param prod_name : String
	 */
	public void setProd_name(String prod_name) {
		this.prod_name = prod_name;
	}

	/**
	 * @return the quantity of this order item
	 */
	public int getQuantity() {
		return quantity;
	}

	/**
	 * Sets the quantity of this order item.
	 * @param quantity : int
	 */
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return 1 if this order item is deleted and 
	 * 0 otherwise
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
