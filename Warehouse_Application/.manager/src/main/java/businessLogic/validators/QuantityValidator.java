package businessLogic.validators;

import model.Product;

/**
 * @author Coroian Tudor
 * @since Apr 06, 2020
 *
 */
public class QuantityValidator implements Validator<Product>{

	/**
	 * This method validates the price of the product given
	 * as parameter, namely it checks if the price of the
	 * product is greater or equal to zero.
	 * @param object : Product
	 * @throws IllegalArgumentException
	 */
	public void validate(Product object) {
		if(object.getQuantity() < 0) {
			throw new IllegalArgumentException("Quantity of the product is not a valid quantity!");
		}
	}

}
