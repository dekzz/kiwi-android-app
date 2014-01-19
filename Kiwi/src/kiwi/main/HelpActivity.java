package kiwi.main;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

/**
 * Help menu
 * @author lockpick(Luka Rajèeviæ, Davor Tomala)
 *
 */
public class HelpActivity extends Activity
{	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.help);
		
	}
	
	 @Override
	    public void onBackPressed() {
		 	startActivity(new Intent("kiwi.main.MAIN_ACTIVITY"));
		 	finish();
	    }
}
