package kiwi.main;


import android.os.Bundle;
import android.view.Window;

import android.app.Activity;
import android.content.Intent;

/**
 * Intro animation
 * @author lockpick(Luka Rajèeviæ, Davor Tomala)
 *
 */
public class SplashActivity extends Activity {
	
  @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash);
        
        // Delayed login activity
        Thread timer = new Thread(){
        	public void run()
        	{
        		try
        		{
        			int timer = 0;
        			while (timer < 2000)
        			{
        				sleep(100);
        				timer += 100;
        			}
        			startActivity(new Intent("kiwi.main.LOGIN_ACTIVITY"));
        		} 
        		catch (InterruptedException e){
				}
        		finally
        		{
        			finish();
        		}
        	}
        };
        timer.start();
        
    };

    
   
	
	

	
  
}
