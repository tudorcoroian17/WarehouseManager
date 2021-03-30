package businessLogic;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import access.ClientAccess;
import businessLogic.validators.CityValidator;
import businessLogic.validators.NameValidator;
import businessLogic.validators.Validator;
import model.Client;
import model.Orders;

/**
 * This class provides the application with a working
 * layer between the UI and the classes that access the
 * database.
 * @author Coroian Tudor
 * @since Apr 06, 2020
 */
public class ClientBusinessLogic {

	/**
	 * The list of specific validators for this class.
	 */
	private List<Validator<Client>> validators;
	/**
	 * The instance of the ClientAccess.java class to
	 * access the database.
	 */
	private ClientAccess clientAccess;
	/**
	 * The instance of the OrderBusinessLogic.java
	 * class to cascade the delete operation.
	 */
	private OrderBusinessLogic orderBLL;
	
	/**
	 * The constructor of this class. 
	 * It instantiate all the specific validators for this 
	 * class and the class that accesses the database
	 */
	public ClientBusinessLogic() {
		validators = new ArrayList<Validator<Client>>();
		validators.add(new NameValidator());
		validators.add(new CityValidator());
		orderBLL = new OrderBusinessLogic();
		clientAccess = new ClientAccess();
	}
	
	/**
	 * This method returns the client with the id specified
	 * as the parameter or throws an NoSuchElementException if
	 * it is not found.
	 * @param id : int
	 * @return the client with the id specified as a 
	 * parameter
	 */
	public Client findClientById(int id) {
		Client client = clientAccess.findById(id).get(0);
		if (client == null || client.getDeleted() == 1) {
			throw new NoSuchElementException("The client with id=" + id + " was not found!");
		}
		return client;
	}
	
	/**This method returns the client with the name specified
	 * as the parameter or null in case the client is not in
	 * the "ordermng.db" database.
	 * @param name : String
	 * @return the client or null
	 */
	public List<Client> findClientByName(String name) {
		List<Client> clients = clientAccess.findClientByName(name);
		if (clients == null) return null;
		if (clients.size() == 0) return null;
		List<Client> toRemove = new ArrayList<Client>();
		for(Client client : clients) {
			if (client.getDeleted() == 1) toRemove.add(client);
		}
		clients.removeAll(toRemove);
		return clients;
	}
	
	/**This method returns the client from the city specified
	 * as the parameter or null in case the client is not in
	 * the "ordermng.db" database.
	 * @param city : String
	 * @return the client or null
	 */
	public List<Client> findClientByCity(String city) {
		List<Client> clients = clientAccess.findClientByCity(city);
		if (clients == null) return null;
		List<Client> toRemove = new ArrayList<Client>();
		for(Client client : clients) {
			if (client.getDeleted() == 1) toRemove.add(client);
		}
		clients.removeAll(toRemove);
		return clients;
	}
	
	/**
	 * This method returns the exact client found in the
	 * database or null in case it is not found.
	 * @param client : Client
	 * @return the found client or null
	 */
	public Client findClient(Client client) {
		List<Client> clientsByName = this.findClientByName(client.getName());
		List<Client> clientsByCity = this.findClientByCity(client.getCity());
		if (clientsByName == null || clientsByCity == null) {
			return null;
		}
		for(Client clientDum : clientsByName) {
			for(Client clientDumm : clientsByCity) {
				if (clientDum.getCity().contentEquals(clientDumm.getCity())) {
					return clientDum;
				}
			}
		}
		return null;
	}
	
	/**
	 * @return a list with all the clients in the database
	 * or null if an error is encountered.
	 */
	public List<Client> findAllClients() {
		List<Client> clients = new ArrayList<Client>();
		clients = clientAccess.findAll();
		if (clients.isEmpty()) return null;
		List<Client> toRemove = new ArrayList<Client>();
		for(Client client : clients) {
			if (client.getDeleted() == 1) toRemove.add(client);
		}
		clients.removeAll(toRemove);
		return clients;
	}
	
	/**
	 * This method inserts a client specified as the parameter
	 * into the database and if the
	 * entry with the same name and city as the client specified
	 * in the argument was already inserted in the database. Then,
	 * this method attempts to update the previously found entry.
	 * @param client : Client
	 * @return 0 if the insert was done successfully, 1 if the
	 * entry was updated and -1 otherwise
	 */
	public int insertClient(Client client){
		for (Validator<Client> validator : validators) {
			validator.validate(client);
		}
		Client clientFound = findClient(client);
		if (clientFound == null) {
			return clientAccess.insert(client);
		}
		cascadeUpdate(client);
		clientAccess.update(clientFound, client);
		return 1; //client was updated
	}
	
	/**
	 * This method updates the database at the entry specified
	 * as the parameter.
	 * @param client : Client
	 * @return 0 if the update was done successfully and
	 * -1 otherwise
	 */
	public int updateClient(Client client) {
		for (Validator<Client> validator : validators) {
			validator.validate(client);
		}
		Client clientFound = findClient(client);
		if (clientFound == null) return -1;
		cascadeUpdate(client);
		return clientAccess.update(clientFound, client);
	}
	
	/**
	 * This method updates the "deleted" column of the client
	 * specified as the parameter to "true" or 1
	 * @param client : Client
	 * @return 0 if the client was deleted successfully and
	 * -1 otherwise
	 */
	public int deleteClient(Client client) {
		Client clientFound = findClient(client);
		if (clientFound == null) return -1;
		cascadeDelete(client);
		return clientAccess.delete(client);
	}
	
	/**
	 * This method updates the "deleted" column of the client
	 * with the id specified as the parameter to "true" or 1
	 * @param id : int
	 * @return 0 if the client was deleted successfully and
	 * -1 otherwise
	 */
	public int deleteClientById(int id) {
		Client clientFound = findClientById(id);
		if (clientFound == null) return -1;
		cascadeDelete(clientFound);
		return clientAccess.deleteById(id);
	}
	
	/**
	 * This method cascades the delete operation such that
	 * if a client is deleted, all the orders made by that
	 * client are deleted as well.
	 * @param client : Client
	 */
	public void cascadeDelete(Client client) {
		List<Orders> foundOrders = new ArrayList<Orders>();
		foundOrders = orderBLL.findOrdersByClientId(client);
		if (foundOrders == null) return;
		for(Orders order : foundOrders) {
			orderBLL.deleteOrder(order);
		}
	}
	
	/**
	 * This method cascades the update operation such that
	 * if the id of the client given as the parameter is changed,
	 * it should change across all the orders that were made by 
	 * that client.
	 * @param client : Client
	 */
	public void cascadeUpdate(Client client) {
		List<Orders> foundOrders = new ArrayList<Orders>();
		foundOrders = orderBLL.findOrdersByClientId(client);
		if (foundOrders == null) return;
		for(Orders order : foundOrders) {
			if(order.getId_client() == client.getId_client()) continue;
			order.setId_client(client.getId_client());
			orderBLL.updateOrder(order);
		}
	}
	
}
