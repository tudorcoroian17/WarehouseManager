package access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

import connection.ConnectionFactory;
import model.Orders;

/**
 * This class provides the application access to the 
 * "ordermng.db" database. It provides the queries to 
 * perform the CRUD operations on the "order" table.
 * @author Coroian Tudor
 * @since Apr 06, 2020
 * 
 * @see AbstractAccess<T>
 */
public class OrderAccess extends AbstractAccess<Orders>{

	/**
	 * This method returns a list with all the orders of the
	 * client whose id is equal to the one specified as the
	 * parameter of the method.
	 * @param id : int
	 * @return a list with all the orders of the client
	 * with id_client equal with id or null in case of errors
	 */
	public List<Orders> findOrderByClientId(int id) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String query = createSelectQuery("id_client");
		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareStatement(query);
			statement.setInt(1, id);
			resultSet = statement.executeQuery();
			if (resultSet == null) return null;
			List<Orders> found = createObjects(resultSet);
			if (found == null) return null;
			if(found.size() == 0) return null;
			return found;
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "OrderAccess: findOrderByClientId " + e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet);
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}
		return null;
	}
}
