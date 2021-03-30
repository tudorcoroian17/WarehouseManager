package model;

/**
 * Provides the class "Product" linked to the table 
 * "product" in the ordermng.db database.
 * @author Coroian Tudor
 * @since Apr 06, 2020
 */

public class Product {

	/**
	 * The id of the product stored in the database. 
	 * Must be unique.
	 */
	private int id_product;
	/**
	 * The name of the product stored in the database. 
	 * Must be unique.
	 */
	private String prod_name;
	/**
	 * The quantity of the product stored in the database. 
	 * Must be greater or equal to zero.
	 */
	private int quantity;
	/**
	 * The price of the product stored in the database. 
	 * Must be greater than zero.
	 */
	private double price;
	/**
	 * Flag to show if the product should be treated 
	 * as deleted.
	 */
	private int deleted;
	
	/**
	 * Constructor for the Product.java class without
	 * any arguments. It is used only for instantiating
	 * the fields with default values.
	 */
	public Product() {
		this.id_product = 0;
		this.prod_name = new String("");
		this.quantity = 0;
		this.price = 0;
		this.deleted = 0;
	}
	
	/**
	 * Constructor for the Product.java class when the
	 * id_product is known.
	 * @param id : int 
	 * @param name : String
	 * @param quantity : int
	 * @param price : double
	 */
	public Product (int id, String name, int quantity, double price) {
		super();
		this.id_product = id;
		this.prod_name = name;
		this.quantity = quantity;
		this.price = price;
		this.deleted = 0;
	}
	
	/**
	 * Constructor for the Product.java class when the
	 * id_product is unknown.
	 * @param name : String
	 * @param quantity : int
	 * @param price : double
	 */
	public Product (String name, int quantity, double price) {
		super();
		this.prod_name = name;
		this.quantity = quantity;
		this.price = price;
		this.deleted = 0;
	}

	/**
	 * @return the id of this product
	 */
	public int getId_product() {
		return id_product;
	}

	/**
	 * Sets the id of this product. 
	 * @param id_product : int
	 */
	public void setId_product(int id_product) {
		this.id_product = id_product;
	}

	/**
	 * @return the name of this product
	 */
	public String getProd_name() {
		return prod_name;
	}

	/**
	 * Sets the name of this product. 
	 * @param prod_name : String
	 */
	public void setProd_name(String prod_name) {
		this.prod_name = prod_name;
	}

	/**
	 * @return the quantity of this product
	 */
	public int getQuantity() {
		return quantity;
	}

	/**
	 * Sets the quantity of this product.
	 * @param quantity : int
	 */
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return the price of this product
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * Sets the price of this product.
	 * @param price : double
	 */
	public void setPrice(double price) {
		this.price = price;
	}

	/**
	 * @return 1 if this product is deleted and 0 otherwise
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
