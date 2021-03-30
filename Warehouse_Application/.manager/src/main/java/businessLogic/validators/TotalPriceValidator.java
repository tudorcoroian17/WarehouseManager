package businessLogic.validators;

import model.Orders;

/**
 * @author Coroian Tudor
 * @since Apr 06, 2020
 *
 */
public class TotalPriceValidator implements Validator<Orders>{

	/**
	 * This method validates the total price of the order
	 * given as parameter, namely it checks if the price
	 * is greater or equal to zero.
	 * @param object : Orders
	 * @throws IllegalArgumentException
	 */
	public void validate(Orders object) {
		if(object.getTotal() < 0) {
			throw new IllegalArgumentException("Total price of the product is not a valid price!");
		}
	}
}
