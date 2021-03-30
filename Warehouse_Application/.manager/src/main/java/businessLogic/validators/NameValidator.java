package businessLogic.validators;

import model.Client;

/**
 * @author Coroian Tudor
 * @since Apr 06, 2020
 *
 */
public class NameValidator implements Validator<Client>{

	/**
	 * This method checks the name of the client given
	 * as parameter, namely it checks if the name of 
	 * the client is made up of letters only or contains
	 * at most two white spaces.
	 * @param object : Client
	 * @throws IllegalArgumentException
	 */
	public void validate(Client object) {
		if(!object.getName().matches("^[A-Za-z ]+$")) {
			throw new IllegalArgumentException("Client name is not a valid name!");
		}
	}

}
