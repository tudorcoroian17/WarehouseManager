package access;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import connection.ConnectionFactory;

/**
 * This class provides the backbone for all the *Access.java
 * classes. It provides methods and queries in order to 
 * perform the CRUD operations on all the tables in the
 * "ordermng.db" database.
 * @author Coroian Tudor
 * @since Apr 06, 2020
 *
 * @param <T> : the object for which this class is reflected 
 */
public class AbstractAccess<T> {

	/**
	 * The logger of this class.
	 */
	protected static final Logger LOGGER = Logger.getLogger(AbstractAccess.class.getName());
	/**
	 * The type of the class specified as the parameter.
	 */
	private final Class<T> type;
	
	/**
	 * The constructor of this class.
	 * It instantiates the type of the class specified
	 * as the parameter.
	 */
	@SuppressWarnings("unchecked")
	public AbstractAccess() {
		this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

	}
	
	
	/**
	 * This method builds a SELECT query with a single
	 * condition, of the form:
	 * SELECT * FROM T WHERE field=?
	 * @param field : String
	 * @return a string with the query 
	 */
	protected String createSelectQuery(String field) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("SELECT ");
		stringBuilder.append(" * ");
		stringBuilder.append(" FROM ");
		stringBuilder.append(type.getSimpleName());
		stringBuilder.append(" WHERE " + field + "=?");
		return stringBuilder.toString();
	}
	
	/**
	 * This method builds a SELECT query without any
	 * conditions, of the form:
	 * SELECT * FROM T
	 * @return a string with the query
	 */
	private String createSelectAllQuery() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("SELECT ");
		stringBuilder.append(" * ");
		stringBuilder.append(" FROM ");
		stringBuilder.append(type.getSimpleName());
		return stringBuilder.toString();
	}

	/**
	 * This method builds an INSERT query of the form:
	 * INSERT INTO T (column1, column2, ...) VALUES (value1, value2, ...)
	 * @param fields : List of String
	 * @return a string with the query
	 */
	private String createInsertQuery(List<String> fields) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("INSERT INTO ");
		stringBuilder.append(type.getSimpleName());
		stringBuilder.append(" (");
		for(String field : fields) {
			stringBuilder.append(field + ",");
		}
		stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		stringBuilder.append(") ");
		stringBuilder.append(" VALUES (");
		for(@SuppressWarnings("unused") String field : fields) {
			stringBuilder.append("?,");
		}
		stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		stringBuilder.append(")");
		return stringBuilder.toString();
	}
	
	/**
	 * This method builds an UPDATE query of the form:
	 * UPDATE T SET (column1=value1, column2=value2, ...) WHERE id = ?
	 * @param fields : List of String
	 * @return a string with the query
	 */
	private String createUpdateQuery(List<String> fields) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("UPDATE ");
		stringBuilder.append(type.getSimpleName());
		stringBuilder.append(" SET ");
		for(String field : fields) {
			stringBuilder.append(field + "=" + "?" + ",");
		}
		stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		stringBuilder.append(" WHERE ");
		stringBuilder.append(fields.toArray()[0]);
		stringBuilder.append("=?");
		return stringBuilder.toString();
	}
	
	/**
	 * This method builds and UPDATE query of the form:
	 * UPDATE T SET deleted = true WHERE id = ?
	 * @param field : String
	 * @return a string with the query
	 */
	private String createDeleteQuery(String field) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("UPDATE ");
		stringBuilder.append(type.getSimpleName());
		stringBuilder.append(" SET deleted = true");
		stringBuilder.append(" WHERE ");
		stringBuilder.append(field + "=?");
		return stringBuilder.toString();
	}
	
	/**
	 * This method returns a list with all the entries
	 * that have as id the id specified as the parameter.
	 * In case it finds only one element, it will return
	 * a list with that element.
	 * @param id : int
	 * @return a list with all the entries found or
	 * null in case of errors
	 */
	public List<T> findById(int id) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String query = createSelectQuery(type.getDeclaredFields()[0].getName());
		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareStatement(query);
			statement.setInt(1, id);
			resultSet = statement.executeQuery();
			return createObjects(resultSet);
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, type.getName() + "Access: findById " + e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet);
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}
		return null;
	}
	
	/**
	 * This method returns a list with all the entries
	 * in the table specified as a generic parameter.
	 * @return a list with all the entries in the table
	 * or null in case of errors
	 */
	public List<T> findAll() {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String query = createSelectAllQuery();		
		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareStatement(query);
			resultSet = statement.executeQuery();	
			return createObjects(resultSet);
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, type.getName() + "Access: findAll " + e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet);
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}
		return null;
	}

	
	/**
	 * This method inserts a new entry in the database, with
	 * the values specified in the object argument.
	 * @param object : T
	 * @return 0 if the object was inserted successfully 
	 * and -1 otherwise
	 */
	public int insert (T object) {
		Connection connection = null;
		PreparedStatement statement = null;
		List<String> fields = new ArrayList<String>();
		for(Field field : type.getDeclaredFields()) {
			field.setAccessible(true);
			fields.add(field.getName());
		}
		String query = createInsertQuery(fields);
		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareCall(query);
			Object value;
			int i = 1;
			for(Field field : object.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				value = field.get(object);
				statement.setObject(i, value);
				i++;
			}
			statement.executeUpdate();
			return 0;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} finally {
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}
		return -1;
	}
	
	/**
	 * This method updates the entry specified as the 
	 * objectOld argument with the values specified in
	 * the objectNew argument.
	 * @param objectOld : T
	 * @param objectNew : T
	 * @return 0 if the object was updated successfully 
	 * and -1 otherwise
	 */
	public int update (T objectOld, T objectNew) {
		Connection connection = null;
		PreparedStatement statement = null;
		List<String> fields = new ArrayList<String>();
		for(Field field : type.getDeclaredFields()) {
			field.setAccessible(true);
			fields.add(field.getName());
		}
		String query = createUpdateQuery(fields);
		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareCall(query);
			Object value;
			int i = 1;
			for(Field field : objectNew.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				value = field.get(objectNew);
				statement.setObject(i, value);
				i++;
			}
			Field field = objectOld.getClass().getDeclaredFields()[0];
			field.setAccessible(true);
			value = field.get(objectOld);
			statement.setObject(i, value);
			statement.executeUpdate();
			return 0;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} finally {
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}
		return -1;
	}
	
	/**
	 * This method updates the deleted column of the table
	 * for the entry with the same id as the one specified
	 * as the argument.
	 * @param id : int
	 * @return 0 if the object was deleted successfully
	 * and -1 otherwise
	 */
	public int deleteById(int id) {
		Connection connection = null;
		PreparedStatement statement = null;
		Field field = type.getDeclaredFields()[0];
		field.setAccessible(true);
		String query = createDeleteQuery(field.getName());
		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareCall(query);
			statement.setInt(1, id);
			statement.executeUpdate();
			return id;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} finally {
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}
		return -1;
	}
	
	/**
	 * This method updates the deleted column of the table
	 * for the entry specified as the object parameter.
	 * @param object : T
	 * @return 0 if the object was deleted successfully
	 * and -1 otherwise
	 * @see deleteById(int id)
	 */
	public int delete (T object) {
		int id;
		try {
			Field field = object.getClass().getDeclaredFields()[0];
			field.setAccessible(true);
			id = field.getInt(object);
			return deleteById(id);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	
	/**
	 * This method creates a list from the result set of a
	 * query.
	 * @param resultSet : ResultSet
	 * @return a list with all the entries in the result set
	 * or null in case of errors
	 */
	protected List<T> createObjects(ResultSet resultSet) {
		List<T> list = new ArrayList<T>();
		try {
			while(resultSet.next()) {
				T instance = type.newInstance();
				for (Field field : type.getDeclaredFields()) {
					Object value = resultSet.getObject(field.getName());
					PropertyDescriptor propertyDescriptor = new PropertyDescriptor(field.getName(), type);
					Method method = propertyDescriptor.getWriteMethod();
					method.invoke(instance, value);
				}
				list.add(instance);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IntrospectionException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		if (list.isEmpty()) return null;
		return list;
	}
}
