package kiwi.main;

import kiwi.cryptography.AES;
import kiwi.database.DatabaseConnection;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Edit user credentials data
 * @author lockpick(Luka Rajèeviæ, Davor Tomala)
 *
 */
public class EditUserCredentialsActivity extends Activity {
	
	private static String DATABASE_NAME = "credentials";
	private Button saveButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.add_user_credentials);
		
		final Bundle extras = getIntent().getExtras();
		
		// get values entered by user
		saveButton = (Button) findViewById(R.id.button_save);
		final EditText txtServiceName = (EditText) findViewById(R.id.txt_service);
		final EditText txtUsername = (EditText) findViewById(R.id.txt_username);
		final EditText txtPassword = (EditText) findViewById(R.id.txt_password);
		final CheckBox chkCloud = (CheckBox) findViewById(R.id.cbx_cloud);
		
		txtServiceName.setText(extras.getString("strCredName"));
		txtUsername.setText(extras.getString("strCredUsername"));
		txtPassword.setText(extras.getString("strCredPassword"));
		
		String decrypted = "";
		try {
			// User's password decryption
			decrypted = AES.decrypt(LoginActivity.PASS, txtPassword.getText().toString());
		} catch (Exception e) {
		}
		
		txtPassword.setText(decrypted);
		txtPassword.setInputType(InputType.TYPE_CLASS_TEXT);
		
		if(extras.getString("strCredCloud").equals("true"))
			chkCloud.setChecked(true);
		else
			chkCloud.setChecked(false);
		
		saveButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				if(txtServiceName.getText().toString().equals(""))
				{
					Toast.makeText(getApplicationContext(), "* field required!", Toast.LENGTH_SHORT).show();
				}
				else
				{
					String encrypted = "";
					try {
						// Encrypt the new password
						encrypted = AES.encrypt(LoginActivity.PASS, txtPassword.getText().toString());
					} catch (Exception e) {
					}
					
					// Store edited data to database
					String editCredentialsResult = DatabaseConnection.editCredentials(
																		DATABASE_NAME,
																		extras.getString("strCredId"),
																		extras.getString("strCredRev"),
																		txtServiceName.getText().toString(),
																		extras.getString("strCredPath"),
																		txtUsername.getText().toString(),
																		encrypted, 
																		chkCloud.isChecked());
																		
					Toast.makeText(getApplicationContext(), editCredentialsResult, Toast.LENGTH_SHORT).show();
					
					startActivity(new Intent("kiwi.main.MAIN_ACTIVITY"));
					
					/*check if connected to cloud (internet connection == true)
						if(cloud)
						{
							//replication
						}
						else 
							save credentials into queue so it can be replicated later
					*/
				}
				
			}
		});
	}
	
	@Override
    public void onBackPressed() {
    	startActivity(new Intent("kiwi.main.MAIN_ACTIVITY"));
		finish();
    }
	
}
