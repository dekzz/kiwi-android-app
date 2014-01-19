package kiwi.main;

import kiwi.main.R;
import kiwi.web.JSLogin;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

/**
 * Handles connection to web sites and auto-logins
 * @author lockpick(Luka Rajèeviæ, Davor Tomala)
 *
 */
public class WebActivity extends Activity {
	
	String username;
	String password;
	static String uri;
	WebView webview;
	boolean useJS = true;
	
	private static final FrameLayout.LayoutParams ZOOM_PARAMS = 
			new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 
										 ViewGroup.LayoutParams.WRAP_CONTENT,
										 Gravity.BOTTOM);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_web);
		webview = (WebView) findViewById(R.id.web_view);
		final Bundle extras = getIntent().getExtras();
		
		uri = extras.getString("strServicePath");
		// Add protocol prefix if it does not exist
		if(!uri.startsWith("http"))
		{
			uri = "http://" + uri;
		}
		final String useData = extras.getString("useData");
		if(useData.equals("true"))
		{
			username = extras.getString("strUsername");
			password = extras.getString("strPassword");
		}
		
		// WebView settings
		webview.getSettings().setJavaScriptEnabled(true); 
		webview.getSettings().setSavePassword(false);
		webview.getSettings().setLoadsImagesAutomatically(true);
		webview.getSettings().setUseWideViewPort(true);
		
		// WebView's frame
		FrameLayout mContentView = (FrameLayout) getWindow().getDecorView().findViewById(android.R.id.content);
		final View zoom = this.webview.getZoomControls();
		mContentView.addView(zoom, ZOOM_PARAMS);
		zoom.setVisibility(View.GONE);
		
		// Initialize 'browser' client
		webview.setWebViewClient( new WebViewClient()
		{
			@Override  
			  public boolean shouldOverrideUrlLoading(WebView view, String url) { 
				    return false;  
			  }

			@Override
			  public void onPageFinished(WebView view, String url)  
		      {
				if (useJS && useData.equals("true"))
				{	
					String javaScript = JSLogin.getJS(uri, username, password);

					if(!javaScript.equals(""))
					{
						// If there's ready JavaScript for specific URI, execute it
				        view.loadUrl(javaScript);
				        useJS = false;
					}
				}
		      }
			
		});
		// Load URI
		webview.loadUrl(uri);
	}
	
	@Override
	public void onBackPressed() {
		if(webview.isFocusable() && webview.canGoBack())
		{
			// History back
			webview.goBack();
		}
		else
		{
			// Close web 'browser'
			webview.destroy();
			startActivity(new Intent("kiwi.main.MAIN_ACTIVITY"));
			finish();
		}
	}

}
