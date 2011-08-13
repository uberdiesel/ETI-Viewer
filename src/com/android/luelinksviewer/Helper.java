package com.android.luelinksviewer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

public class Helper {
	public static DefaultHttpClient client = new DefaultHttpClient();
	static String site = "http://www.endoftheinter.net/main.php";
	static String showTopics = "http://boards.endoftheinter.net/showtopics.php?board=";
	static String page = "&page=";
	static String postTopic = "http://boards.endoftheinter.net/postmsg.php?board=";
	static String LOG = "Helper";
	
	public static boolean Login(String username, String password) throws IOException {
		HttpPost post = new HttpPost("https://iphone.endoftheinter.net/#___1__");
		
		// Add your data
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("username", username));
        nameValuePairs.add(new BasicNameValuePair("password", password));
        Log.v(LOG, "Login Values: " + nameValuePairs.toString());
        
        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        
        // Execute HTTP Post Request
		HttpResponse response = client.execute(post);
		HttpEntity entity = response.getEntity();
		
		entity.consumeContent();
		
		if (validateLogin()){
			return true;
		}else {
			return false;
		}
	}

	//Checks if the login was a successful one
	private static boolean validateLogin() {
		List<Cookie> cookies = Helper.client.getCookieStore().getCookies();
		
		//Checks the Client cookies and if there are none, the login wasn't successful
		try {
			int x = 0;
			while (x != 3){
				Log.v(LOG, "Helper Validate Login: " + cookies.get(x).toString());
				if (cookies.get(x).getValue().equals(null) ) {
					return false;
				}
				x++;
			}
			return true;
		}catch (IndexOutOfBoundsException e){
			e.printStackTrace();
			return false;
		}
	}
	public Topic ParseTopic(String Address) {
		Topic topic = new Topic();
		
		
		return topic;
	}

}
