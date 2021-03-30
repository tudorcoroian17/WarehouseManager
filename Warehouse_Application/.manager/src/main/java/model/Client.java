package model;

/**
 * Provides the class "Client" linked to the table 
 * "client" in the ordermng.db database.
 * @author Coroian Tudor
 * @since Apr 06, 2020
 */
public class Client {

	
	/**
	 * The id of the client stored in the database.
	 * Must be unique.
	 */
	private int id_client;
	/**
	 * The name of the client stored in the database.
	 * Multiple entries can have similar names if the
	 * "city" column is different.
	 */
	private String name;
	/**
	 * The city of the client stored in the database.
	 * Multiple entries can have similar city names if 
	 * the "name" column is different.
	 */
	private String city;
	/**
	 * Flag to show if the client should be treated 
	 * as deleted.
	 */
	private int deleted;
	
	/**
	 * Constructor for the Client.java class without
	 * any arguments. It is used only for instantiating
	 * the fields with default values.
	 */
	public Client() {
		this.id_client = 0;
		this.name = new String();
		this.city = new String();
		this.deleted = 0;
	}
		
	/**
	 * Constructor for the Client.java class when the
	 * id_client is known.
	 * @param id : int
	 * @param name : String
	 * @param city : String
	 */
	public Client(int id, String name, String city) {
		super();
		this.id_client = id;
		this.name = name;
		this.city = city;
		this.deleted = 0;
	}
	
	/**
	 * Constructor for the Client.java class when the
	 * id_client is unknown.
	 * @param name : String
	 * @param city : String
	 */
	public Client (String name, String city) {
		super();
		this.name = name;
		this.city = city;
		this.deleted = 0;
	}

	/**
	 * @return the id of this client
	 */
	public int getId_client() {
		return id_client;
	}

	/**
	 * Sets the id of this client.
	 * @param id : int
	 */
	public void setId_client(int id) {
		this.id_client = id;
	}

	/**
	 * @return the name of this client
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this client.
	 * @param name : String
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the city from which this client is
	 */
	public String getCity() {
		return city;
	}

	/**
	 * Sets the city from which this client is.
	 * @param city : String
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return 1 if this client is deleted and 0 otherwise
	 */
	public int getDeleted() {
		return deleted;
	}

	/**
	 * Sets the deleted flag of this class.
	 * @param deleted : int
	 */
	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}
	
	
}
