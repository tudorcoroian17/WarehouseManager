package businessLogic.validators;

import model.Client;

/**
 * @author Coroian Tudor
 * @since Apr 06, 2020
 *
 */
public class CityValidator implements Validator<Client>{

	/**
	 * This method checks the name of the city of the
	 * client given as parameter, namely it checks if the 
	 * city name is made up of only letters or contains
	 * at most one dash line.
	 * @param object : Client
	 * @throws IllegalArgumentException
	 */
	public void validate(Client object) {
		if(!object.getCity().matches("[a-zA-z-]+")) {
			throw new IllegalArgumentException("City name is not a valid name!");
		}
	}

}
