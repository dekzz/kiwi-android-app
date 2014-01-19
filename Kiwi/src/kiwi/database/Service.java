package kiwi.database;

import java.util.UUID;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

/**
 * Handles credentials/services DB items
 * @author lockpick(Luka Rajèeviæ, Davor Tomala)
 *
 */
public class Service {
	
	/**
	 * Creates JsonNode item which contains credentials data
	 * @param serviceName Service name bounded to credentials
	 * @param servicePath Service path (URI)
	 * @param username Username (credentials)
	 * @param password Password (credentials)
	 * @param cloud If the service should be replicated to cloud (Currently not in use!)
	 * @return JsonNode item
	 */
	public JsonNode addCredentials(String serviceName, String servicePath, String username, String password, Boolean cloud) {
		
		UUID uuid = UUID.randomUUID();
    	ObjectNode item = JsonNodeFactory.instance.objectNode();

    	item.put("_id", uuid.toString());
    	item.put("service_name", serviceName);
    	item.put("service_path", servicePath.toString());
    	item.put("username", username);
    	item.put("password", password);
    	item.put("cloud", cloud.toString());

    	return item;
	}
	
	/**
	 * Creates JsonNode item which contains service info
	 * @param serviceName Service name
	 * @param servicePath Service path (URI)
	 * @return JsonNode item
	 */
	public JsonNode addService(String serviceName, String servicePath) {
		
		UUID uuid = UUID.randomUUID();
    	ObjectNode item = JsonNodeFactory.instance.objectNode();

    	item.put("_id", uuid.toString());
    	item.put("service_name", serviceName);
    	item.put("service_path", servicePath.toString());

    	return item;
	}
	
/**
 * Creates JsonNode item which contains edited credentials data
 * @param serviceId ID of service credentials we want to edit
 * @param serviceRev Revision mark of database item
 * @param serviceName Edited service name
 * @param servicePath Edited service path (URI)
 * @param username Edited Username
 * @param password Edited Password
 * @param cloud If the service should be replicated to cloud (Currently not in use!)
 * @return JsonNode item with edited data
 */
public JsonNode editCredentials(String serviceId, String serviceRev, String serviceName, String servicePath, String username, String password, Boolean cloud) {
		
    	ObjectNode item = JsonNodeFactory.instance.objectNode();

    	item.put("_id", serviceId);
    	item.put("_rev", serviceRev.toString());
    	item.put("service_name", serviceName);
    	item.put("service_path", servicePath.toString());
    	item.put("username", username);
    	item.put("password", password);
    	item.put("cloud", cloud.toString());

    	return item;
	}

/**
 * Creates JsonNode item which contains edited service info
 * @param serviceId ID of service we want to edit
 * @param serviceRev Revision mark of database item
 * @param serviceName Edited Service name
 * @param servicePath Edited Service path (URI)
 * @return JsonNode with edited data
 */
public JsonNode editService(String serviceId, String serviceRev, String serviceName, String servicePath) {
	
		ObjectNode item = JsonNodeFactory.instance.objectNode();
	
		item.put("_id", serviceId);
		item.put("_rev", serviceRev);
		item.put("service_name", serviceName);
		item.put("service_path", servicePath.toString());
	
		return item;
	}

}
