package kiwi.database;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

/**
 * Handles users within the database
 * @author lockpick(Luka Rajèeviæ, Davor Tomala)
 *
 */
public class User {

	/**
	 * Creates JsonNode item with user info
	 * @param dbName Name of database which contains user
	 * @param hashedPass User's secured password
	 * @return JsonNode item with user info
	 */
	public JsonNode createUser(String dbName, String hashedPass) {
		
    	ObjectNode item = JsonNodeFactory.instance.objectNode();

    	item.put("_id", dbName);
    	item.put("pass_hash", hashedPass);

    	return item;
	}

}
