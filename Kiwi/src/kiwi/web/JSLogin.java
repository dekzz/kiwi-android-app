package kiwi.web;

/**
 * JavaScripts for predefined web sites (auto-login)
 * @author lockpick(Luka Rajèeviæ, Davor Tomala)
 *
 */
public class JSLogin {
	
	/**
	 * Checks if there is predefined JavaScript for specific web site
	 * and fills it with user credentials, which enables auto-login
	 * @param url Selected URL
	 * @param username Username (credentials)
	 * @param password Password (credentials)
	 * @return JavaScript for auto-login
	 */
	public static String getJS(String url, String username, String password)
	{
		if (url.equals("http://fenstergang.com/users/login"))
		{
			return "javascript:(function() { " +  
	    		"document.getElementById('UserUsername').value = '" + username + "';" +
	    		"document.getElementById('UserPassword').value = '" + password + "';" +
	    		"s = document.getElementsByTagName('input');" + 
	    		"s[3].click();" +
	            "})()";
		}
		
		if (url.equals("https://mobile.twitter.com/session/new"))
		{

			return "javascript:(function() { " +  
	    		"document.getElementById('username').value = '" + username + "';" +
	    		"document.getElementById('password').value = '" + password + "';" +
	    		"s = document.getElementsByTagName('input');" + 
	    		"s[3].click();" +
	            "})()";	
		}
		
		else if (url.equals("https://github.com/login"))
		{
			return "javascript:(function() { " +  
	    		"document.getElementById('login_field').value = '" + username + "';" +
	    		"document.getElementById('password').value = '" + password + "';" +
	    		"document.getElementsByName('commit')[0].click();" + 
	            "})()";
		}
		
		else if (url.equals("https://webmail.foi.hr/webmail/src/login.php"))
		{
			return "javascript:(function() { " +  
	    		"document.getElementsByName('login_username')[0].value = '" + username + "';" +
	    		"document.getElementsByName('secretkey')[0].value = '" + password + "';" +
	    		"s = document.getElementsByTagName('input');" + 
	    		"s[4].click();" +
	            "})()";
		}
		
		else if (url.equals("https://login.foi.hr/login?service=https%3A%2F%2Felf.foi.hr%2Flogin%2Findex.php"))
		{
			return "javascript:(function() { " +  
	    		"document.getElementById('username').value = '" + username + "';" +
	    		"document.getElementById('password').value = '" + password + "';" +
	    		"document.getElementsByName('submit')[0].click();" +
	            "})()";
		}
		
		return "";

	}
	
}
