package kiwi.main;

import kiwi.cryptography.AES;
import kiwi.database.DatabaseConnection;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Add user credentials to specific service
 * @author lockpick(Luka Rajèeviæ, Davor Tomala)
 *
 */
public class AddUserCredentialsActivity extends Activity {
	
	private static String DATABASE_NAME = "credentials";
	private Button saveButton;
	private Boolean cloud;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.add_user_credentials);
		
		final Bundle extras = getIntent().getExtras();
		
		//get values entered by user
		saveButton = (Button) findViewById(R.id.button_save);
		final EditText txtServiceName = (EditText) findViewById(R.id.txt_service);
		final EditText txtUsername = (EditText) findViewById(R.id.txt_username);
		final EditText txtPassword = (EditText) findViewById(R.id.txt_password);
		CheckBox chkCloud = (CheckBox) findViewById(R.id.cbx_cloud);	
		
		txtServiceName.setText(extras.getString("strServiceName"));
		cloud = chkCloud.isChecked();
		
		saveButton.setOnClickListener(new OnClickListener() {
			
			/* (non-Javadoc)
			 * Saves the user credentials info for specific service
			 * with encrypted password into database
			 */
			public void onClick(View v) {
				
				if(txtServiceName.getText().toString().equals(""))
				{
					Toast.makeText(getApplicationContext(), "* field required!", Toast.LENGTH_SHORT).show();
				}
				else
				{
					String encrypted = "";
					try {
						// User's password encryption
						encrypted = AES.encrypt(LoginActivity.PASS, txtPassword.getText().toString());
					} catch (Exception e) {
					}
					
					// Store data in database
					String addServiceResult = DatabaseConnection.addCredentials(
																		DATABASE_NAME, 
																		txtServiceName.getText().toString(),
																		extras.getString("strServiceId"),
																		txtUsername.getText().toString(),
																		encrypted, 
																		cloud);
					Toast.makeText(getApplicationContext(), addServiceResult, Toast.LENGTH_SHORT).show();
					
					startActivityFromChild(null, new Intent("kiwi.main.MAIN_ACTIVITY"), -1);
					
					/*check if connected to cloud (internet connection == true)
					if(cloud)
					{
						//replication
					}
					else 
						//save credentials into queue so it can be replicated later
					*/
				}
				
			}
		});
	}
	
	public void onBackPressed() {
		startActivity(new Intent("kiwi.main.SERVICE_CHOICE_ACTIVITY"));
		finish();
    }
	
}
