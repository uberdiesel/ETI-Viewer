package com.android.luelinksviewer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.AbstractVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class Helper {
	public static DefaultHttpClient client = new DefaultHttpClient();
	static String site = "http://www.endoftheinter.net/main.php";
	static String showTopics = "http://boards.endoftheinter.net/showtopics.php?board=";
	static String page = "&page=";
	static String postTopic = "http://boards.endoftheinter.net/postmsg.php?board=";
	static String LOG = "Helper";

	public static boolean Login(String username, String password) throws IOException {
		HttpPost post = new HttpPost("http://iphone.endoftheinter.net/#___1__");
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
	
	private static Document getResponseDocument(HttpResponse response) {
		InputStream in = null;
		try {
			in = response.getEntity().getContent();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		StringBuffer result = new StringBuffer();
		
		String line;
		try {
			while ((line = br.readLine()) != null){
				result.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Document d = Jsoup.parse(result.toString());
        return d;
	}
	
	public static Document GetPage (String Address) throws URISyntaxException, ClientProtocolException, IOException {
		HttpGet get = new HttpGet(Address);
        
        // Execute HTTP Post Request
		HttpResponse response = client.execute(get);
		HttpEntity entity = response.getEntity();
		
		Document d = getResponseDocument(response);
		entity.consumeContent();
		
		return d;
	}	
	
	public ArrayList<Message> ParseMessages(String Address) throws URISyntaxException, ClientProtocolException, IOException {
		ArrayList<Message> MessageList = new ArrayList<Message>();
		//Declare Variables
		String html, extraURL;
		Document doc;
				
		//Initiate Variables
		extraURL = "//boards.endoftheinter.net";
		doc = GetPage(Address);

		
		Elements postList = doc.select("#u0_1").select(".message-container");
        
		String extraUserUrl = "//endoftheinter.net/";
		
		for (Element msg : postList){
			//Parse every post 
			MessageList.add(parsePost(msg));
			
		}
		return MessageList;
	}
	private Message parsePost(Element msg) {
		Message post = new Message();
		//Makes the user bar textview----------------USERBAR----------------
		String userbar = msg.select(".message-top").first().text();		//Everything in the user bar.
			//Remove unnecessary  | Filter | Message Detail | Quote  from userbar
		
		Elements remove = msg.select(".message-top").first().select("a");
		remove.remove(0);
		
		for (Element e : remove){
			userbar = userbar.replace(e.text(), "");
		}
		
		userbar = userbar.replace(" |  |  | ", "");	
		
		Log.v(LOG, userbar);
		
		
		return post;
		
	}
	public Bitmap drawable_from_url(String Address) throws java.net.MalformedURLException, java.io.IOException {
	    Bitmap x;

	    HttpURLConnection connection = (HttpURLConnection)new URL(Address) .openConnection();

	    connection.connect();
	    InputStream input = connection.getInputStream();

	    x = BitmapFactory.decodeStream(input);
	    return x;
	}
	
	public static DefaultHttpClient getTolerantClient() {
	    DefaultHttpClient client = new DefaultHttpClient();
	    SSLSocketFactory sslSocketFactory = (SSLSocketFactory) client
	            .getConnectionManager().getSchemeRegistry().getScheme("https")
	            .getSocketFactory();
	    final X509HostnameVerifier delegate = sslSocketFactory.getHostnameVerifier();
	    if(!(delegate instanceof MyVerifier)) {
	        sslSocketFactory.setHostnameVerifier(new MyVerifier(delegate));
	    }
	    return client;
	}
}

class MyVerifier extends AbstractVerifier {

    private final X509HostnameVerifier delegate;

    public MyVerifier(final X509HostnameVerifier delegate) {
        this.delegate = delegate;
    }

    @Override
    public void verify(String host, String[] cns, String[] subjectAlts)
                throws SSLException {
        boolean ok = false;
        try {
            delegate.verify(host, cns, subjectAlts);
        } catch (SSLException e) {
            for (String cn : cns) {
                if (cn.startsWith("*.")) {
                    try {
                          delegate.verify(host, new String[] { 
                                cn.substring(2) }, subjectAlts);
                          ok = true;
                    } catch (Exception e1) { }
                }
            }
            if(!ok) throw e;
        }
    }
}