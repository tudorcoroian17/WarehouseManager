package access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

import connection.ConnectionFactory;
import model.OrderItem;

/**
 * This class provides the application access to the 
 * "ordermng.db" database. It provides the queries to 
 * perform the CRUD operations on the "orderitem" table.
 * @author Coroian Tudor
 * @since Apr 06, 2020
 * 
 * @see AbstractAccess<T>
 */
public class OrderItemAccess extends AbstractAccess<OrderItem>{

	/**
	 * This method searches the "orderitem" table in the 
	 * "ordermng.db" database for all the order items that have
	 * the id_order equal to the one specified as the parameter.
	 * @param id : int
	 * @return a list with all the found order items or null
	 * in case of errors
	 * 
	 */
	public List<OrderItem> findOrderItemByOrderId(int id) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String query = createSelectQuery("id_order");
		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareStatement(query);
			statement.setInt(1, id);
			resultSet = statement.executeQuery();
			if (resultSet == null) return null;
			List<OrderItem> found = createObjects(resultSet);
			if (found == null) return null;
			if(found.size() == 0) return null;
			return found;
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "OrderItemAccess: findOrderItemByOrderId " + e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet);
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}
		return null;
	}
	
	/**
	 * This method searches the "orderitem" table in the 
	 * "ordermng.db" database for all the order items that have
	 * the prod_name equal to the one specified as the parameter.
	 * @param name : String
	 * @return a list with all the found order items or null
	 * in case of errors
	 * 
	 */
	public List<OrderItem> findOrderItemByProductName(String name) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String query = createSelectQuery("prod_name");
		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareStatement(query);
			statement.setString(1, name);
			resultSet = statement.executeQuery();
			if (resultSet == null) return null;
			List<OrderItem> found = createObjects(resultSet);
			if(found == null) return null;
			if(found.size() == 0) return null;
			return found;
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "OrderItemAccess: findOrderItemByProductName " + e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet);
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}
		return null;
	}
}
