package kiwi.main;

import com.couchbase.touchdb.router.TDURLStreamHandlerFactory;

import kiwi.cryptography.SHA1;
import kiwi.database.DatabaseConnection;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * Handles login functionality and application data reset
 * @author lockpick(Luka Rajèeviæ, Davor Tomala)
 *
 */
public class LoginActivity extends Activity {
	
	String filesDir;
	DatabaseConnection db;
	private static String DATABASE_NAME = "user";
	public static String PASS;

	{
		TDURLStreamHandlerFactory.registerSelfIgnoreError();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);
        
        filesDir = getFilesDir().getAbsolutePath();
        Button btnLogin = (Button) findViewById(R.id.login_button);
        ImageButton btnDBreset = (ImageButton) findViewById(R.id.db_reset);
        final EditText pass = (EditText) findViewById(R.id.pin_textbox);
        
	    btnLogin.setOnClickListener(new View.OnClickListener() {	
			public void onClick(View v) {
				if (pass.getText().toString().equals(""))
				{
					Toast.makeText(getApplicationContext(), "Enter password", Toast.LENGTH_SHORT).show();
				}
				else
				{
					// Hash the password
					String hashedPass = SHA1.hash(pass.getText().toString());
					
					if(DatabaseConnection.startTouchDB(filesDir))
						Log.e("DB", "Connected to DB");
					else
						Log.e("DB", "Failed to connect");
					
					// Verify user
					String status = DatabaseConnection.processUser(DATABASE_NAME, hashedPass);
					
					if(status == "OK" || status == "Welcome to Kiwi!")
					{
						PASS = pass.getText().toString();
						startActivity(new Intent("kiwi.main.MAIN_ACTIVITY"));
					}
					
					if(status.equals("Wrong password"))
						Toast.makeText(getApplicationContext(), "Wrong password", Toast.LENGTH_SHORT).show();
				}
			}
		});
	    
	    btnDBreset.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				// Popup for database reset confirmation
				AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
		        builder.setMessage("Reset database?").setCancelable(false)
		        	.setPositiveButton("Yes",
		             new DialogInterface.OnClickListener() {
		        		public void onClick(DialogInterface dialog, int id) {
		        			// Remove all databases
		        			DatabaseConnection.deleteDB("user");
		        			DatabaseConnection.deleteDB("credentials");
		        			DatabaseConnection.deleteDB("services");
		                    }
		                }).setNegativeButton("No",
		            	  new DialogInterface.OnClickListener() {
		                    public void onClick(DialogInterface dialog, int id) {
		                    	
		                    }
		                });
		        
		        AlertDialog alert = builder.create();
		        alert.show();
				
			}
		});
	}
	
}
