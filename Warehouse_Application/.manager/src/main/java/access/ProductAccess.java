package access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

import connection.ConnectionFactory;
import model.Product;

/**
 * This class provides the application access to the 
 * "ordermng.db" database. It provides the queries to 
 * perform the CRUD operations on the "product" table.
 * @author Coroian Tudor
 * @since Apr 06, 2020
 * 
 * @see AbstractAccess
 */
public class ProductAccess extends AbstractAccess<Product>{

	/**
	 * This method searches the "product" table in the
	 * "ordermng.db" database for the product with the
	 * name specified in its argument.
	 * @param name : String 
	 * @return Product if found and null otherwise
	 * 
	 */
	public Product findProductByName(String name) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String query = createSelectQuery("prod_name");
		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareStatement(query);
			statement.setString(1, name);
			resultSet = statement.executeQuery();
			if(resultSet == null) return null;
			List<Product> found = createObjects(resultSet);
			if (found == null) return null;
			if(found.size() == 0) return null;
			if(found.get(0).getDeleted() == 1) return null;
			return found.get(0);
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "ProductAccess: findByName " + e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet);
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}
		return null;
	}
}
