package com.android.luelinksviewer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
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
	
	private static String GetPage (String Address) throws URISyntaxException {
		BufferedReader in = null;
		String HTMLSource = null;
		try {
			HttpGet request = new HttpGet();
			request.setURI(new URI(Address));
			
            HttpResponse response = client.execute(request);
            in = new BufferedReader
            (new InputStreamReader(response.getEntity().getContent()));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.	append(line + NL);
            }
            in.close();
            HTMLSource = sb.toString();
            
            }
		catch (IOException e) {
			e.printStackTrace();
			client = getTolerantClient();
			GetPage(Address);
		}
		finally {
            if (in != null) {
            	try {
            		in.close();
                } catch (IOException e) {
                	e.printStackTrace();
                }
            }
        }
		return HTMLSource;
	}
	
	public int GetPageCount (String Address) throws URISyntaxException {
		//Declare Variables
		Document doc;
		String html;
		
		//Initiate Variables
		html = GetPage(Address);
		doc = Jsoup.parse(html);
		Element pagecount = doc.select("div.infobar").first().select("span").first();
		Log.v(LOG, "Pagecount: " + pagecount.text());
		return Integer.parseInt(pagecount.text());
	}
	
	public ArrayList<Topic> ParseTopics(String Address) throws URISyntaxException {
		ArrayList<Topic> TopicList = new ArrayList<Topic>();
		//Declare Variables
		String html, extraURL;
		Document doc;
		
		//Initiate Variables
		extraURL = "//boards.endoftheinter.net";
		html = GetPage(Address);
		doc = Jsoup.parse(html);
		
		Elements topicList = doc.select("table").select("tr");
		topicList.remove(0);
		for (Element td : topicList){
			Topic topicdata = new Topic();
			
			topicdata.setAddress(td.select("a").eq(0).attr("href").replace(extraURL, ""));
			topicdata.setTitle(td.select("a").eq(0).text());
			if(td.select("b").eq(0).hasText()) {
				topicdata.setisSticky(true);
			}
				
			//topic.put("author_link", td.select("a").eq(1).attr("href").replace(extraURL, ""));
			topicdata.setPoster(td.select("a").eq(1).text());
			
			topicdata.setPostcount(td.select("td").eq(2).text());
			
			
			String split = "[ ]+";
	    	String[] splitpostcount = td.select("td").eq(2).text().split(split);
	    	topicdata.setPostcount(splitpostcount[0]);
	    	
	    	
	    	//Add bookmarking
	    	if (splitpostcount.length > 1) {
	    		int startsplit = splitpostcount[1].lastIndexOf("+") + 1;
	    		int endsplit = splitpostcount[1].lastIndexOf(")");
	    		String bookmarkcount = splitpostcount[1].substring(startsplit, endsplit);
	    		topicdata.setPostBookmark(bookmarkcount); // Add number of "new" messages
	    		topicdata.setisBookmark(true);
	    		
	    		//Set new address for topic
	    		int posts = Integer.parseInt(splitpostcount[0]);
	    		int newposts = Integer.parseInt(bookmarkcount);
	    		if((posts-newposts) > 50) {
	    			int page = (((posts-newposts) + 49) / 50 );
	    			topicdata.setPage(page);
	    		}
	    	}
		TopicList.add(topicdata);
		}
		return TopicList;
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