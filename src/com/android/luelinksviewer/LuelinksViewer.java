package com.android.luelinksviewer;

import greendroid.app.GDApplication;

import java.sql.Date;
import java.util.List;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.Log;

public class LuelinksViewer extends GDApplication {

	String SAVED_COOKIES = "SAVED_COOKIES";
	String SAVED_PREFERENCES = "SAVED_PREFERENCES";
    SharedPreferences cookies;
	SharedPreferences.Editor cookie_editor;
	SharedPreferences preferences;
	SharedPreferences.Editor preference_editor;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	public Class<?> getHomeActivityClass() {
        return MainView.class;
    }
	
	public void onCreate() {
		super.onCreate();
		
		cookies = getSharedPreferences(SAVED_COOKIES, 0);
		cookie_editor = cookies.edit();
		preferences = getSharedPreferences(SAVED_PREFERENCES, 0);
		preference_editor = preferences.edit();
		
		if (restoreCookies()){	
			//Restore cookies, TRUE if successful restore
		}else {					
			//FALSE if unsuccessful
			Intent intent = new Intent(this, Login.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
	}
	public void onLowMemory() {
		super.onLowMemory();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		saveCookies();
	}
	
	public void saveLogin(String username, String password) {
		preference_editor.putString("username", username);
		preference_editor.putString("password", password);
		preference_editor.commit();
	}
	public String getSavedUsername() {
		return preferences.getString("username", "");
	}
	public String getSavedPassword() {
		return preferences.getString("password", "");
	}
	
	public void saveCookies() {
		List<Cookie> cookies = Helper.client.getCookieStore().getCookies();
		
		for (Cookie c : cookies){
			Log.v("Saving Cookie", c.toString());
			
			cookie_editor.putString(c.getName(), c.getValue());
			cookie_editor.putString(c.getName() + "DOMAIN", c.getDomain());
			cookie_editor.putString(c.getName() + "PATH", c.getPath());
			try {
				cookie_editor.putLong(c.getName() + "EXPIRY", c.getExpiryDate().getTime());
			}catch (NullPointerException e){
			}
			
		}
		// Commit the edits!
		cookie_editor.commit();
		
		Log.v("Save cookies", "SAVE COMPLETE");
	}
	
	private boolean restoreCookies() {
		//Builds new cookie and place to store them
		CookieStore cookieStore = new BasicCookieStore();
		BasicClientCookie c = new BasicClientCookie("PHPSESSID", cookies.getString("PHPSESSID", ""));
		c.setDomain(cookies.getString("PHPSESSID" + "DOMAIN", ""));
		c.setPath(cookies.getString("PHPSESSID" + "PATH", ""));
		try {
			Date date = new Date(cookies.getLong("PHPSESSID" + "EXPIRY", 0));
			c.setExpiryDate(date);
		}catch (NullPointerException e){
			//e.printStackTrace();
		}
		
		Log.v("Restore Cookie", c.toString());
		cookieStore.addCookie(c);
		
		
		c = new BasicClientCookie("userid", cookies.getString("userid", ""));
		c.setDomain(cookies.getString("userid" + "DOMAIN", ""));
		c.setPath(cookies.getString("userid" + "PATH", ""));
		try {
			Date date = new Date(cookies.getLong("userid" + "EXPIRY", 0));
			c.setExpiryDate(date);
		}catch (NullPointerException e){
			//e.printStackTrace();
		}
		
		Log.v("Restore Cookie", c.toString());
		cookieStore.addCookie(c);
		
		
		c = new BasicClientCookie("session",
				cookies.getString("session", ""));
		c.setDomain(cookies.getString("session" + "DOMAIN", ""));
		c.setPath(cookies.getString("session" + "PATH", ""));
		try {
			Date date = new Date(cookies.getLong("session" + "EXPIRY", 0));
			c.setExpiryDate(date);
		}catch (NullPointerException e){
			//e.printStackTrace();
		}
		
		
		Log.v("Restore Cookie", c.toString());
		cookieStore.addCookie(c);
		
		if (cookies.getString("PHPSESSID", "").equals("") || cookies.getString("userid", "").equals("") || cookies.getString("session", "").equals("")){
			//If any of the cookie values are not there, unsuccessful restore
			return false;
		}
		
		//Sets the cookies to the httpclient
		Helper.client.setCookieStore(cookieStore);
		return true;
	}

	public void clearCookies() {
		cookie_editor.clear();
		cookie_editor.commit();
	}
}