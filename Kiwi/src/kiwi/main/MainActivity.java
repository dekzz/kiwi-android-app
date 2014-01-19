package kiwi.main;

import kiwi.cryptography.AES;
import kiwi.database.DatabaseConnection;

import org.codehaus.jackson.JsonNode;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Central point that connects credentials, services,
 * web activities, help and more
 * @author lockpick(Luka Rajèeviæ, Davor Tomala)
 *
 */
public class MainActivity extends Activity {
	
	String DB_CREDENTIALS = "credentials";
	String DB_SERVICES = "services";
	static String ITEM_NAME;
	static int SERVICE_NUMBER;
	static String SERVICE_ID;
	int POSITION;
	String SERVICE_PATH_ID;
	String SERVICE_PATH;
	
	JsonNode[] credentialsList;
	JsonNode[] servicesList;
	GridView grid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		credentialsList = DatabaseConnection.getDBitems(DB_CREDENTIALS);
		servicesList = DatabaseConnection.getDBitems(DB_SERVICES);
		
		if(credentialsList.length != 0)
			SERVICE_NUMBER = credentialsList.length + 1;
		else
			SERVICE_NUMBER = 1;
		
		grid = (GridView) findViewById(R.id.gridServices);
        grid.setAdapter(new ImageAdapter());
        
        grid.setOnItemClickListener(new OnItemClickListener() {
               public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	   
            	   POSITION = position;
            	   
            	   if(position == (SERVICE_NUMBER - 1))
            	   {
            		   // Go to service choice
            		   startActivity(new Intent("kiwi.main.SERVICE_CHOICE_ACTIVITY"));
            		   finish();
            	   }
            	   else
            	   {
            		   // Connection to selected service
            		   SERVICE_PATH_ID = credentialsList[position].get("service_path").toString().replace("\"", "");
            		   SERVICE_PATH = DatabaseConnection.getServicePath(DB_SERVICES, SERVICE_PATH_ID).replace("\"", "");
            		   
            		   if (SERVICE_PATH.equals(""))
            		   {
            			   Toast.makeText(getApplicationContext(), "Cannot connect to '" + servicesList[position].get("service_name").toString().replace("\"", "") + "'", Toast.LENGTH_SHORT).show();
		        	   }
		    		   else{
		    			   
		    			   // Choose if you wanna use your credentials to log into web site
		    			   AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					        builder.setMessage("Use login data?").setCancelable(false)
					        	.setPositiveButton("Yes",
					             new DialogInterface.OnClickListener() {
					        		public void onClick(DialogInterface dialog, int id) {
					        			   Intent webView = new Intent("kiwi.main.WEB_ACTIVITY");
					        			   webView.putExtra("strServicePath", SERVICE_PATH);
										   webView.putExtra("strUsername", credentialsList[POSITION].get("username").toString().replace("\"", ""));
										   try {
											   webView.putExtra("strPassword", AES.decrypt(LoginActivity.PASS, credentialsList[POSITION].get("password").toString().replace("\"", "")));
										   } catch (Exception e) {
										   }
										   webView.putExtra("useData", "true");
										   startActivity(webView);
										   finish();
					                    }
					                }).setNegativeButton("No",
					                        new DialogInterface.OnClickListener() {
					                    public void onClick(DialogInterface dialog, int id) {
					                    	Intent webView = new Intent("kiwi.main.WEB_ACTIVITY");
					                    	webView.putExtra("strServicePath", SERVICE_PATH);
					                    	webView.putExtra("useData", "false");
					                    	startActivity(webView);
					                    	finish();
					                    }
					                });
					        AlertDialog alert = builder.create();
					        alert.show();   
		    		   }  
            	   }
               }
        });
        
        grid.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				if(position != (SERVICE_NUMBER - 1))
				{
					POSITION = position;
					ITEM_NAME = credentialsList[position].get("service_name").toString().replace("\"", "");
					SERVICE_ID = credentialsList[position].get("_id").toString().replace("\"", "");
					
					registerForContextMenu(grid);
				}
				else
				{
					unregisterForContextMenu(grid);
				}
				
				return false;
			}
		});
        
	}
	
	 @Override
	    public void onBackPressed() {
		 
		 	// Exit confirmation dialog
	        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
	        builder.setMessage("Do you really want to exit?").setCancelable(true)
	        	.setPositiveButton("Yes",
	             new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog, int id) {
	        			MainActivity.this.finish();
	                    }
	                }).setNegativeButton("No",
	                        new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int id) {
	                    	// Do not exit
	                    }
	                });
	        AlertDialog alert = builder.create();
	        alert.show();
	    }
	
	@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    	super.onCreateContextMenu(menu, v, menuInfo);
    	// Credentials options
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
     * Send selected credentials data to edit panel
     */
    private void editService()
    {
    	Intent credentialsData = new Intent("kiwi.main.EDIT_USER_CREDENTIALS_ACTIVITY");
    	credentialsData.putExtra("strCredId", SERVICE_ID);
    	credentialsData.putExtra("strCredRev", credentialsList[POSITION].get("_rev").toString().replace("\"", ""));
    	credentialsData.putExtra("strCredName", ITEM_NAME);
    	credentialsData.putExtra("strCredUsername", credentialsList[POSITION].get("username").toString().replace("\"", ""));
    	credentialsData.putExtra("strCredPassword", credentialsList[POSITION].get("password").toString().replace("\"", ""));
    	credentialsData.putExtra("strCredPath", credentialsList[POSITION].get("service_path").toString().replace("\"", ""));
    	credentialsData.putExtra("strCredCloud", credentialsList[POSITION].get("cloud").toString().replace("\"", ""));
    	startActivity(credentialsData);
    	finish();
    	
    	//Toast.makeText(getApplicationContext(), "Edited: " + ITEM_NAME, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Deletes the selected credentials
     */
    private void deleteService()
    {
    	// Delete confirmation
    	AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Delete '" + ITEM_NAME + "'?").setCancelable(false)
        	.setPositiveButton("Yes",
             new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int id) {
        			DatabaseConnection.deleteService(DB_CREDENTIALS, SERVICE_ID);
        	    	startActivity(new Intent("kiwi.main.MAIN_ACTIVITY"));
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
	
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case R.id.services:
				startActivity(new Intent("kiwi.main.SERVICE_CHOICE_ACTIVITY"));
				finish();
				return true;
			case R.id.help:
				startActivity(new Intent("kiwi.main.MAIN_HELP_ACTIVITY"));
				finish();
				return true;
		}
		return false;
	}
	
	/**
	 * Fills menu with icons representing credentials
	 * @author lockpick(Luka Rajèeviæ, Davor Tomala)
	 *
	 */
	public class ImageAdapter extends BaseAdapter {

        public int getCount() {
            return SERVICE_NUMBER;
        }
        public Object getItem(int position) {
            return position;
        }
        public long getItemId(int position) {
            return position;
        }
        public View getView(int position, View convertView, ViewGroup parent) {

			View v;
			
			if(convertView == null)
			{
				LayoutInflater li = getLayoutInflater();
				v = li.inflate(R.layout.icon, null);
				TextView tv = (TextView)v.findViewById(R.id.icon_text);
				ImageView iv = (ImageView)v.findViewById(R.id.icon_image);
				
				if(position != (SERVICE_NUMBER - 1))
	            {
	            	iv.setImageResource(R.drawable.service_icon);
	            	tv.setText(credentialsList[position].get("service_name").toString().replace("\"", ""));
	            }
				else
				{
	            	iv.setImageResource(R.drawable.add_icon);
	            	tv.setText("Add new");
				}
			}
			else
			{
				v = convertView;
			}
			
			return v;
        }
        
	}

}
