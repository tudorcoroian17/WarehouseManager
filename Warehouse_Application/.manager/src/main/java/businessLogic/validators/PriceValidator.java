package businessLogic.validators;

import model.Product;

/**
 * @author Coroian Tudor
 * @since Apr 06, 2020
 *
 */
public class PriceValidator implements Validator<Product>{

	/**
	 * This method checks the price of the product given 
	 * as parameter, namely it checks if the price of
	 * the product is strictly greater then zero.
	 * @param object : Product
	 * @throws IllegalArgumentException
	 */
	public void validate(Product object) {
		if(object.getPrice() <= 0) {
			throw new IllegalArgumentException("Price of the product is not a valid price!");
		}
	}

}
