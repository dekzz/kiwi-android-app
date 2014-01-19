package kiwi.database;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.impl.StdCouchDbInstance;

import com.couchbase.touchdb.TDServer;
import com.couchbase.touchdb.ektorp.TouchDBHttpClient;

import android.app.Activity;

/**
 * Database worker class
 * @author lockpick(Luka Rajèeviæ, Davor Tomala)
 *
 */
public class DatabaseConnection extends Activity{
	
	//private static String dDocName = "credentials";
	//private static String viewName = "credentials_view";
	//private static TDDatabase db;
	private static TDServer server = null;
	private static CouchDbConnector couchDbConnector;
	
	/**
	 * Connect to database defined in 'filesDir' path
	 * @param filesDir Path to database
	 * @return 'True' if success, else 'false'
	 */
	public static boolean startTouchDB(String filesDir)
	{
	    try 
	    {
            server = new TDServer(filesDir);
        } 
	    catch (IOException e) 
	    {
            return false;
        }
	    
	    
	    //not needed atm
	    
	    //db = server.getDatabaseNamed(DATABASE_NAME);
	    /*
	    
		TDView view = db.getViewNamed(String.format("%s/%s", dDocName, viewName));
		
		 view.setMapReduceBlocks(new TDViewMapBlock() 
		 {
		      public void map(Map<String, Object> document, TDViewMapEmitBlock emitter) {
		    		  emitter.emit(null, document.get("pass_hash"));
		      }
		  }, new TDViewReduceBlock() {
		          public Object reduce(List<Object> keys, List<Object> values, boolean rereduce) {
		                  return null;
		          }
		  }, "1.0");
		  */
	    
		 return true;
	}
	
	/**
	 * On first login creates user database, 
	 * else checks if user entered valid password
	 * @param dbName Database name which contains users
	 * @param pinCode Password, passphrase, or PIN
	 * @return String info which depends on success or fail
	 */
	public static String processUser(String dbName, String pinCode) 
	{
		 HttpClient httpClient = new TouchDBHttpClient(server);
		 CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
		 couchDbConnector = dbInstance.createConnector(dbName, true); 
		 User user;
		 
		 if (!couchDbConnector.contains(dbName))
		 {
	     	 user = new User();
		     final JsonNode item = user.createUser(dbName, pinCode);
			 couchDbConnector.create(item);
	         loadPredefinedServices();
	         return "Welcome to Kiwi!";
	     }
	     else
	     {
			 JsonNode node = couchDbConnector.get(JsonNode.class, dbName);
			 String storedPassword = node.get("pass_hash").toString().replaceAll("\"", "");
			 
			 if (storedPassword != null)
			 {
				 if(pinCode.equals(storedPassword))
				 {
					 return "OK";
				 }
				 else
				 {
					 return "Wrong password";
				 }
			 }
			 return "Welcome to Kiwi!";
	     }
	}
	
	/**
	 * Adds credentials info for specific service into database
	 * @param dbName Database name for storing credentials
	 * @param serviceName Names the service bounded to credentials data
	 * @param servicePath Service's URI ('https://example.com')
	 * @param username Username (credentials)
	 * @param password Password (credentials)
	 * @param cloud If the service should be replicated to cloud (Currently not in use!)
	 * @return String representing the state
	 */
	public static String addCredentials(String dbName, String serviceName, String servicePath, String username, String password, Boolean cloud) 
	{
		
		 HttpClient httpClient = new TouchDBHttpClient(server);
		 CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
		 couchDbConnector = dbInstance.createConnector(dbName, true);
		 Service service = new Service();
		 
	     final JsonNode item = service.addCredentials(serviceName, servicePath, username, password, cloud);
		 couchDbConnector.create(item);

         return "Service added!";
	}
	
	/**
	 * Edit existing credentials
	 * @param dbName Database name in which we want to edit credentials
	 * @param serviceId ID of service credentials we want to edit
	 * @param serviceRev Revision mark of database item
	 * @param serviceName Edited name of service bounded to credentials data
	 * @param servicePath Edited service URI ('https://example.com')
	 * @param username Edited Username (credentials)
	 * @param password Edited Password (credentials)
	 * @param cloud If the service should be replicated to cloud (Currently not in use!)
	 * @return String representing the state
	 */
	public static String editCredentials(String dbName, String serviceId, String serviceRev, String serviceName, String servicePath, String username, String password, boolean cloud) 
	{ 
		 HttpClient httpClient = new TouchDBHttpClient(server);
		 CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
		 couchDbConnector = dbInstance.createConnector(dbName, true);
		 Service service = new Service();
		 
		 JsonNode[] editCred = getDBitems(dbName);
		 
		 for (int i = 0; i < editCred.length; i++) 
		 {
	        if(serviceId.equals(editCred[i].get("_id").toString().replace("\"", "")))
	        {
	        	JsonNode item = service.editCredentials(serviceId, serviceRev, serviceName, servicePath, username, password, cloud);
	        	couchDbConnector.update(item);

	            return "Credentials edited!";
	        }
		 }

		 return "Editing failed!";
	}
	
	/**
	 * Adds new service
	 * Adds service info into database
	 * @param dbName Database name for storing services
	 * @param serviceName Name of service
	 * @param servicePath Service's URI ('https://example.com')
	 * @return String representing the state
	 */
	public static String addService(String dbName, String serviceName, String servicePath) 
	{ 
		 HttpClient httpClient = new TouchDBHttpClient(server);
		 CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
		 couchDbConnector = dbInstance.createConnector(dbName, true);
		 Service service = new Service();
		 
	     final JsonNode item = service.addService(serviceName, servicePath);
		 couchDbConnector.create(item);

         return "Service created!";

	}
	
	/**
	 * Edit existing service
	 * @param dbName Database name in which we want to edit service info
	 * @param serviceId ID of service we want to edit
	 * @param serviceRev Revision mark of database item
	 * @param serviceName Names of service
	 * @param servicePath Service's URI ('https://example.com')
	 * @return String representing the state
	 */
	public static String editService(String dbName, String serviceId, String serviceRev, String serviceName, String servicePath) 
	{ 
		 HttpClient httpClient = new TouchDBHttpClient(server);
		 CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
		 couchDbConnector = dbInstance.createConnector(dbName, true);
		 Service service = new Service();
		 
		 JsonNode[] editServ = getDBitems(dbName);
		 
		 for (int i = 0; i < editServ.length; i++) 
		 {
	        if(serviceId.equals(editServ[i].get("_id").toString().replace("\"", "")))
	        {
	        	JsonNode item = service.editService(serviceId, serviceRev, serviceName, servicePath);
	        	couchDbConnector.update(item);

	            return "Service edited!";
	        }
		 }
		 
		 return "Editing failed!";
	}
	
	/**
	 * Deletes credentials/service from database
	 * @param dbName Database name in which we want to delete an item
	 * @param serviceId ID of item we want to delete
	 * @return String representing the state
	 */
	public static boolean deleteService(String dbName, String serviceId) 
	{
		 HttpClient httpClient = new TouchDBHttpClient(server);
		 CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
		 couchDbConnector = dbInstance.createConnector(dbName, true); 
		 
		 JsonNode[] delServ = getDBitems(dbName);
		 
		 for (int i = 0; i < delServ.length; i++) 
		 {
	        if(serviceId.equals(delServ[i].get("_id").toString().replace("\"", "")))
	        {
	        	couchDbConnector.delete(delServ[i]);
	        	
	            return true;
	        }
		 }
		 
		 return false;
	}
	
	/**
	 * Fetches all items from database
	 * @param dbName Database name from which we want to fetch items
	 * @return Set of database items
	 */
	public static JsonNode[] getDBitems(String dbName) 
	{
		 HttpClient httpClient = new TouchDBHttpClient(server);
		 CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
		 couchDbConnector = dbInstance.createConnector(dbName, true); 
		 
		 if(couchDbConnector.getAllDocIds().size() == 0)
		 {
			 JsonNode[] empty = new JsonNode[0];
			 return empty;
		 }
		 else
		 {
			 List<String> docIDs = couchDbConnector.getAllDocIds();
			 JsonNode[] nodes = new JsonNode[docIDs.size()];

			 for (int i = 0; i < nodes.length; i++) 
			 {
				nodes[i] = couchDbConnector.get(JsonNode.class, docIDs.get(i));
			 }
			 return nodes;
		 }
	}
	
	/**
	 * Fetches the path (URI) for selected service
	 * @param dbName Database in which are stored services
	 * @param serviceId ID of service for which we want to fetch path (URI)
	 * @return Path (URI) for selected service
	 */
	public static String getServicePath(String dbName, String serviceId) 
	{
		 HttpClient httpClient = new TouchDBHttpClient(server);
		 CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
		 couchDbConnector = dbInstance.createConnector(dbName, true); 
		 
		 JsonNode[] services = getDBitems(dbName);
		 
		 for (int i = 0; i < services.length; i++) 
		 {
	        if(serviceId.equals(services[i].get("_id").toString().replace("\"", "")))
	        {
	            return services[i].get("service_path").toString().replace("\"", "");
	        }
		 }
		 
		 return "";
	}
	
	/**
	 * Deletes the selected database
	 * @param dbName Database name for deletion
	 * @return String representing the state
	 */
	public static String deleteDB(String dbName) 
	{ 
		 HttpClient httpClient = new TouchDBHttpClient(server);
		 CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
		 
		 dbInstance.deleteDatabase(dbName);
		 
         return "DB deleted!";
	}
	
	/**
	 * Fill service database with predefined (connectable) web sites
	 */
	private static void loadPredefinedServices()
	{
		addService("services", "*Fenster Gang", "http://fenstergang.com/users/login");
		addService("services", "*FOI webmail", "https://webmail.foi.hr/webmail/src/login.php");
		addService("services", "*ELF FOI", "https://login.foi.hr/login?service=https%3A%2F%2Felf.foi.hr%2Flogin%2Findex.php");
		addService("services", "*Github", "https://github.com/login");
		addService("services", "*Twitter", "https://mobile.twitter.com/session/new");
	}
}