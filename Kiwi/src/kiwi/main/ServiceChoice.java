package kiwi.main;

import java.util.ArrayList;

import org.codehaus.jackson.JsonNode;

import kiwi.database.DatabaseConnection;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Handles work with services, editing services,
 * adding credentials to services, etc.
 * @author lockpick(Luka Rajèeviæ, Davor Tomala)
 *
 */
public class ServiceChoice extends Activity{
	
	private static String DATABASE_NAME = "services";
	static String ITEM_NAME;
	static String SERVICE_ID;
	int POSITION;
	String SERVICE_PATH_ID;
	ArrayList<String> listItems = new ArrayList<String>();
	ArrayList<String> listPaths = new ArrayList<String>();
    //DEFINING STRING ADAPTER WHICH WILL HANDLE DATA OF LISTVIEW
    ArrayAdapter<String> adapter;
    JsonNode[] services;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.services);

        final ListView serviceList = updateList();
        services = DatabaseConnection.getDBitems(DATABASE_NAME);
        
        serviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				if(position == 0)
				{
					// Add new service
					serviceData("", "", "", "");
					
				}
				else
				{
					// Collect service data and pass it to credentials adding form
					ITEM_NAME = listItems.get(position);
					SERVICE_PATH_ID = services[position-1].get("_id").toString().replace("\"", "");
					
					Intent credentials = new Intent("kiwi.main.ADD_USER_CREDENTIALS_ACTIVITY");
					credentials.putExtra("strServiceId", SERVICE_PATH_ID);
					credentials.putExtra("strServiceName", ITEM_NAME);
					credentials.putExtra("strServicePath", listPaths.get(position-1));
					
					startActivity(credentials);
					finish();
				}

			}
                
		});	
    
        serviceList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				// Popup options for services
				if(position != 0)
				{
					POSITION = position;
					ITEM_NAME = listItems.get(position);
					SERVICE_ID = services[position-1].get("_id").toString().replace("\"", "");
					registerForContextMenu(serviceList);
				}
				else
				{
					unregisterForContextMenu(serviceList);
				}
				
				return false;
			}
		});
		      
    }
    
    @Override
    public void onBackPressed() {
    	startActivity(new Intent("kiwi.main.MAIN_ACTIVITY"));
		finish();
    }

    /**
     * Handles adding new service and editing an existing one
     * @param serviceId ID of service which is being edited
     * @param serviceRev Revision mark of item in database
     * @param newServiceName Edited Service name
     * @param newServicePath Edited Service path
     */
    private void serviceData(final String serviceId, final String serviceRev, final String newServiceName, final String newServicePath) {
		AlertDialog.Builder addServiceDialog = new AlertDialog.Builder(ServiceChoice.this);
		LinearLayout layout = new LinearLayout(ServiceChoice.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		final EditText serviceName = new EditText(ServiceChoice.this);
		final EditText servicePath = new EditText(ServiceChoice.this);
		
		if(newServiceName.equals(""))
		{
			// Popup for adding a new service
			serviceName.setHint("service name");
			serviceName.setSingleLine(true);
			servicePath.setHint("service path");
			servicePath.setSingleLine(true);
			servicePath.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
			addServiceDialog.setTitle("Add new");
		}
		else
		{
			// Popup for editing an existing service
			serviceName.setText(newServiceName);
			servicePath.setText(newServicePath);
			addServiceDialog.setTitle("Edit " + services[POSITION-1].get("service_name").toString().replace("\"", ""));
		}
		
		layout.addView(serviceName);
		layout.addView(servicePath);
		
		addServiceDialog.setIcon(R.drawable.add_icon);
		addServiceDialog.setView(layout);
		
		addServiceDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int whichButton) {
		        
		    	if(!serviceName.getText().equals(""))
		    	{
		    		if(newServiceName.equals("") && newServicePath.equals(""))
		    		{
		    			// Add new service to database
			            String createServiceResult = DatabaseConnection.addService(
							            										DATABASE_NAME, 
							            										serviceName.getText().toString(), 
							            										servicePath.getText().toString().trim());
						Toast.makeText(ServiceChoice.this, createServiceResult, Toast.LENGTH_SHORT).show();
		    		}
		    		else
		    		{
		    			// Save edited service to database
		    			String editServiceResult = DatabaseConnection.editService(
		    																	DATABASE_NAME, 
		    																	serviceId, 
		    																	serviceRev, 
		    																	serviceName.getText().toString(), 
		    																	servicePath.getText().toString().trim());
		    			Toast.makeText(ServiceChoice.this, editServiceResult, Toast.LENGTH_SHORT).show();
		    		}
					
					updateList();
		    	}
		    	else
		    	{
		    		Toast.makeText(getApplicationContext(), "Enter service name!", Toast.LENGTH_SHORT).show();
		    	}
		    }
		});

		addServiceDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int whichButton) {
		        dialog.cancel();
		    }
		});
		addServiceDialog.show();
		
	}
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    	super.onCreateContextMenu(menu, v, menuInfo);
    	// Service options
    	menu.setHeaderTitle(ITEM_NAME);
    	menu.add(0, v.getId(), 0, "Edit");
    	menu.add(0, v.getId(), 0, "Delete");
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	if(item.getTitle() == "Edit")
    	{
    		editService();
    	}
    	else if(item.getTitle() == "Delete")
    	{
    		deleteService();
    	}
    	else {return false;}
    	return true;
    }
    
    /**
     * Sends selected service data to edit panel
     */
    private void editService()
    {
    	serviceData(services[POSITION-1].get("_id").toString().replace("\"", ""), 
	    			services[POSITION-1].get("_rev").toString().replace("\"", ""), 
	    			services[POSITION-1].get("service_name").toString().replace("\"", ""), 
	    			services[POSITION-1].get("service_path").toString().replace("\"", ""));
    	//Toast.makeText(getApplicationContext(), "Edit: " + ITEM_NAME, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Deletes the seleceted service 
     */
    private void deleteService()
    {
    	// Delete confirmation
    	AlertDialog.Builder builder = new AlertDialog.Builder(ServiceChoice.this);
        builder.setMessage("Delete '" + ITEM_NAME + "'?").setCancelable(false)
        	.setPositiveButton("Yes",
             new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int id) {
        			DatabaseConnection.deleteService(DATABASE_NAME, SERVICE_ID);
        	    	startActivity(new Intent("kiwi.main.SERVICE_CHOICE_ACTIVITY"));
        	    	Toast.makeText(getApplicationContext(), "Deleted '" + ITEM_NAME + "'!", Toast.LENGTH_SHORT).show();
        	    	finish();
                    }
                }).setNegativeButton("No",
            	  new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    	//do not exit
                    }
                });
        
        AlertDialog alert = builder.create();
        alert.show();
    }

	/**
	 * Collects services from database and update a list view
	 * @return Collection of services in a ListView
	 */
	private ListView updateList() {
		services = DatabaseConnection.getDBitems(DATABASE_NAME);
        
		listItems.clear();
		listPaths.clear();
		
        listItems.add("Add new service...");
        for (int i = 0; i < services.length; i++) {
        	listItems.add(services[i].get("service_name").toString().replace("\"", ""));
        	listPaths.add(services[i].get("service_path").toString().replace("\"", ""));
		}
		   
        adapter = new ArrayAdapter<String>(this, R.layout.list_item, listItems);

        ListView serviceList = (ListView)findViewById(R.id.serviceList);
        serviceList.setAdapter(adapter);
		return serviceList;
	}

}
