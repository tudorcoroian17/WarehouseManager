package businessLogic.validators;

import model.OrderItem;

/**
 * @author Coroian Tudor
 * @since Apr 06, 2020
 *
 */
public class QuantityOrderItemValidator implements Validator<OrderItem>{

	/**
	 * This method validates the quantity of the order item
	 * given as parameter, namely it checks if the quatity is 
	 * greater or equal to zero.
	 * @param object : OrderItem
	 * @throws IllegalArgumentException
	 */
	public void validate(OrderItem object) {
		if(object.getQuantity() < 0) {
			throw new IllegalArgumentException("Quantity of the product is not a valid quantity!");
		}		
	}

}
