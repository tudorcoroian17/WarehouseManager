package businessLogic;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import access.ProductAccess;
import businessLogic.validators.PriceValidator;
import businessLogic.validators.ProductValidator;
import businessLogic.validators.QuantityValidator;
import businessLogic.validators.Validator;
import model.OrderItem;
import model.Product;

/**
 * This class provides the application with a working
 * layer between the UI and the classes that access the
 * database.
 * @author Coroian Tudor
 * @since Apr 06, 2020
 */
public class ProductBusinessLogic {

	/**
	 * The list of specific validators for this class.
	 */
	private List<Validator<Product>> validators;
	/**
	 * The instance of the ProductAccess.java class to
	 * access the database.
	 */
	private ProductAccess productAccess;
	/**
	 * The instance of the OrderItemBusinessLogic.java
	 * class to cascade the delete operation.
	 */
	private OrderItemBusinessLogic orderItemBLL;
	
	/**
	 * The constructor of this class. 
	 * It instantiates all the specific validators for this 
	 * class and the class that accesses the database
	 */
	public ProductBusinessLogic() {
		validators = new ArrayList<Validator<Product>>();
		validators.add(new ProductValidator());
		validators.add(new QuantityValidator());
		validators.add(new PriceValidator());
		productAccess = new ProductAccess();
		//orderItemBLL = new OrderItemBusinessLogic();
	}
	
	/**
	 * This method returns the product with the id specified
	 * as the parameter or throws NoSuchElementException if
	 * it is not found.
	 * @param id : int
	 * @return the product with the id specified as a 
	 * parameter
	 */
	public Product findProductById(int id) {
		Product product = productAccess.findById(id).get(0);
		if(product == null || product.getDeleted() == 1) {
			throw new NoSuchElementException("The product with the id=" + id + " was not found!");
		}
		return product;
	}
	
	/**
	 * @return a list with all the products in the database
	 * or null if an error is encountered.
	 */
	public List<Product> findAllProducts() {
		List<Product> products = new ArrayList<Product>();
		products = productAccess.findAll();
		if (products.isEmpty()) return null;
		List<Product> toRemove = new ArrayList<Product>();
		for(Product product : products) {
			if (product.getDeleted() == 1) toRemove.add(product);
		}
		products.removeAll(toRemove);
		return products;
	}
	
	/**
	 * This method returns the product with the name specified
	 * as the parameter or throws NoSuchElementException if
	 * it is not found.
	 * @param name : String
	 * @return the product with the name specified as a 
	 * parameter
	 */
	public Product findProductByName(String name) {
		Product product = productAccess.findProductByName(name);
		if (product == null || product.getDeleted() == 1) {
			return null;
		}
		return product;
	}
	
	/**
	 * This method inserts a product specified as the parameter
	 * into the database and if the
	 * entry with the same prod_name as the product specified
	 * in the argument was already inserted in the database. Then,
	 * this method attempts to update the previously found entry.
	 * @param product : Product
	 * @return 0 if the insert/update was done successfully and
	 * -1 otherwise
	 */
	public int insertProduct(Product product){
		for (Validator<Product> validator : validators) {
			validator.validate(product);
		}
		Product productFound = productAccess.findProductByName(product.getProd_name());
		if (productFound == null) {
			return productAccess.insert(product);
		}
		product.setQuantity(productFound.getQuantity() + product.getQuantity());
		productAccess.update(productFound, product);
		return 1;
	}
	
	/**
	 * This method updates the database at the entry specified
	 * as the parameter.
	 * @param product : Product
	 * @return 0 if the update was done successfully and
	 * -1 otherwise
	 */
	public int updateProduct(Product product) {
		for (Validator<Product> validator : validators) {
			validator.validate(product);
		}
		List<Product> productFound = productAccess.findById(product.getId_product());
		if (productFound == null) return -1;//did not find the product to update
		return productAccess.update(productFound.get(0), product);
	}
	
	/**
	 * This method updates the "deleted" column of the product
	 * specified as the parameter to "true" or 1
	 * @param product : Product
	 * @return 0 if the product was deleted successfully and
	 * -1 otherwise
	 */
	public int deleteProduct(Product product) {
		for (Validator<Product> validator : validators) {
			validator.validate(product);
		}
		Product productFound = findProductById(product.getId_product());
		if (productFound == null) return -1;
		cascadeDelete(product);
		return productAccess.delete(product);
	}
	
	/**
	 * This method updates the "deleted" column of the product
	 * with the id specified as the parameter to "true" or 1
	 * @param id : int
	 * @return 0 if the product was deleted successfully and
	 * -1 otherwise
	 */
	public int deleteProductById(int id) {
		Product productFound = findProductById(id);
		if (productFound == null) return -1;
		cascadeDelete(productFound);
		return productAccess.deleteById(id);
	}
	
	/**
	 * This method cascades the delete operation such that
	 * if a product is deleted, all the order items that
	 * contain the product given as the parameter are
	 * deleted as well.
	 * @param product : Product
	 */
	public void cascadeDelete(Product product) {
		orderItemBLL = new OrderItemBusinessLogic();
		List<OrderItem> foundOrderItems = new ArrayList<OrderItem>();
		foundOrderItems = orderItemBLL.findAllOrderItemsFromOrder(product.getProd_name());
		if (foundOrderItems == null) return;
		for (OrderItem orderItem : foundOrderItems) {
			orderItemBLL.deleteOrderItem(orderItem);
		}
	}
	
}
