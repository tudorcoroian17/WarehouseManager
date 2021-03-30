package businessLogic.validators;

import model.Product;

/**
 * @author Coroian Tudor
 * @since Apr 06, 2020
 *
 */
public class ProductValidator implements Validator<Product>{

	/**
	 * This method validates the name of the product given
	 * as parameter, namely it checks if the name of the
	 * product is made up of only letters.
	 * @param object : Product
	 * @throws IllegalArgumentException
	 */
	public void validate(Product object) {
		if(!object.getProd_name().matches("[a-zA-z]+")) {
			throw new IllegalArgumentException("Product name is not a valid name!");
		}
	}

}
