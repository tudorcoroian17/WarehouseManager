package access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

import connection.ConnectionFactory;
import model.Client;

/**
 * This class provides the application access to the 
 * "ordermng.db" database. It provides the queries to 
 * perform the CRUD operations on the "client" table.
 * @author Coroian Tudor
 * @since Apr 06, 2020
 * 
 * @see AbstractAccess
 */
public class ClientAccess extends AbstractAccess<Client>{

	/**
	 * This method searches the "client" table in the
	 * "ordermng.db" database for the client with the
	 * name specified in its argument.
	 * @param name : String 
	 * @return Client if found and null otherwise
	 */
	public List<Client> findClientByName(String name) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String query = createSelectQuery("name");
		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareStatement(query);
			statement.setString(1, name);
			resultSet = statement.executeQuery();
			if (resultSet == null) return null;
			List<Client> found = createObjects(resultSet);
			if(found == null) return null;
			return found;
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "ClientAccess: findByName " + e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet);
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}
		return null;
	}
	
	/** 
	 * This method searches the "client" table in the
	 * "ordermng.db" database for the client from the
	 * city specified in its argument.
	 * @param city : String 
	 * @return Client if found and null otherwise
	 */
	public List<Client> findClientByCity(String city) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String query = createSelectQuery("city");
		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareStatement(query);
			statement.setString(1, city);
			resultSet = statement.executeQuery();
			if (resultSet == null) return null;
			List<Client> found = createObjects(resultSet);
			if(found == null) return null;
			return found;
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "ClientAccess: findByName " + e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet);
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}
		return null;
	}	

}
