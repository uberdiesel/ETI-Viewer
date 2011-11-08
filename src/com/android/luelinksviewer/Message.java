package com.android.luelinksviewer;

import greendroid.widget.AsyncImageView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.util.Linkify;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class Message {
	private String author;
	private String date;
	private String avatarUrl;
	private LinearLayout message;
	private Context ctx;
	
	public Message(Element msg, Context context) {
		super();
		this.ctx = context;
		message = new LinearLayout(ctx);
		GenerateHeader(msg.select("div.message-top").first());
		GenerateAvatar(msg.select(".userpic-holder").select("script"));
		GenerateBody(msg.select(".message"));
		
	}

	private void GenerateHeader(Element header) {
		String split = "[|]";
    	String[] splithead = header.text().split(split);    	
    	author = splithead[0].trim().substring(6);
    	date = splithead[1].trim().substring(8).substring(0, splithead[1].trim().substring(8).length()-6);
	}
	private void GenerateAvatar(Elements avatar) {
		if (!avatar.isEmpty()) {
			String splitter = "[,]";
			String datas[] = avatar.toString().split(splitter);
			String a = datas[1].replace("\\" , "").replace("\"" , "");
			avatarUrl = "http:" + a.trim();
		}
	}
	private void GenerateBody(Elements body) {
		int brpos, quotepos;
		Elements quotes;
			
		quotes = body.select(".quoted-message");
		
		if (!quotes.isEmpty()) {
			//Check for text before the quoted message
			
			brpos = body.toString().indexOf("<br />");
 			quotepos = body.toString().indexOf("<div class");
			if (brpos < quotepos)
				quotes = RemoveBeginText(body);
			Element firstQuotedMessage = quotes.first();
			List<Node> siblings = null;
			siblings = firstQuotedMessage.siblingNodes();
			List<Node> elementsBetween = new ArrayList<Node>();
			Element currentQuotedMessage = firstQuotedMessage;
			for (int i = 1; i < siblings.size(); i++) {
	            Node sibling = siblings.get(i);
	            // see if this Node is a quoted message
	            if (!isQuotedMessage(sibling)) {
	                elementsBetween.add(sibling);
	                //Log.v("LOG", "A Sibling:");
	                //Log.v("LOG", sibling.toString());
	            } else {
	            	//TODO: There is a bug here
	            	Log.v("LOG", "Count: " + Integer.toString(elementsBetween.size()));
	            	processElementsBetween(currentQuotedMessage, elementsBetween);
	                currentQuotedMessage = (Element) sibling;
	                elementsBetween.clear();
	            }
	        }
	        if (!elementsBetween.isEmpty()) {
	        	processElementsBetween(currentQuotedMessage, elementsBetween);
	        }
		}
		else {
			try {
				ArrayList<String> images = new ArrayList<String>();
				Element ele = body.select(".message").first();
				for (Element img : ele.select("a[imgsrc]")) {
					images.add(img.attr("imgsrc"));
					
				}
				Whitelist wlist = new Whitelist();
 	 			wlist.addTags("br", "img", "a").addAttributes(":all", "imgsrc");
 	 			String clean = Jsoup.clean(body.toString(), wlist);
				getMessage(images, clean.split("<br />"));
			}
			catch (Exception e) {e.printStackTrace();}
		}
	}
	
	private Elements RemoveBeginText(Elements msg) {
			int startpos, endpos;
		String message, newmsg;
		ArrayList<String> images = new ArrayList<String>();
		Document messageDoc, newmsgDoc;
		
		startpos = msg.toString().indexOf("class=\"message\">") + 16;
		endpos = msg.toString().indexOf("<div");
		message = msg.toString().substring(startpos, endpos);
		newmsg = msg.toString().replaceFirst(message, " ");
		
		messageDoc = Jsoup.parseBodyFragment(message);
		newmsgDoc = Jsoup.parseBodyFragment(newmsg);
		
		//Post the pre-quote data
		for (Element img : messageDoc.select("a[imgsrc]")) {
			images.add(img.attr("imgsrc"));
			
		}
		Whitelist wlist = new Whitelist();
			wlist.addTags("br", "img", "a").addAttributes(":all", "imgsrc");
			String clean = Jsoup.clean(message, wlist);
		getMessage(images, clean.split("<br />"));
		
		//Get the new msg, with the initial post removed
		return newmsgDoc.select(".quoted-message");
	}
		private boolean isQuotedMessage(Node node) {
 	        if (node instanceof Element) {
 	            Element el = (Element) node;
 	            return "div".equals(el.tagName()) && el.hasClass("quoted-message");
 	        }
 	        return false;
 	    }
 		private void processElementsBetween(Element quote, List<Node> elementsBetween) {
 			createQuote(quote, 5);
 			ArrayList<String> images = new ArrayList<String>();
 			List<Element> imgs = filterImages(elementsBetween);
 			for (Element img : imgs) {
 				//Get the images in order they were posted
 				images.add(img.attr("imgsrc"));
 			}
 			for (Node node : elementsBetween) {
 				Whitelist wlist = new Whitelist();
 	 			wlist.addTags("br", "img", "a").addAttributes(":all", "imgsrc");
 	 			String clean = Jsoup.clean(node.toString(), wlist);
				getMessage(images, clean.split("<br />"));
 			}
		}
 		
 		private List<Element> filterImages(List<Node> nodes) {
 			String tagName = "img";
 	        List<Element> els = new ArrayList<Element>();
 	        for (Iterator<Node> it = nodes.iterator(); it.hasNext();) {
 	            Node n = it.next();
 	            if (n instanceof Element) {
 	                Element el = (Element) n;
 	                if (el.tagName().equals(tagName)) {
 	                    els.add(el);
 	                }
 	            }
 	        }
 	        return els;
 	    }


 		private void getMessage(ArrayList<String> images, String[] parts) {
 			
 			try {
 	 			int y = 0;
 	 			for (String post : parts){
 	 				String text = post.replace("<br />", "").replace("<span>", "")
 	 						.replace("</span>", "").replace("&lt;", "<").replace("&gt;", ">")
 	 						.replace("<a>", "").replace("</a>", "").replace("&quot;", "''").trim();
 	 				
 	 				if (text.contains("<a imgsrc")){
 	 					//If an image is here, post it from the list of captured image urls
 	 					createImage(images.get(y));
 	 					y++;
 	 					
 	 				}else if (post.contains("<span>") && post.contains("&lt;") && post.contains("&gt;")){
 	 					//If thre's a spoiler in the text
 	 					createPost(text);
 	 					
 	 					//TODO: Add a view to the spoiler. Make the button use a .addview(VIEW, INDEX) to show where to add
 	 					//the view
 	 					
 	 					
 	 					/*for (Element sp : spoilerDoc.select("body")){
 	 						Log.v("SpoilFor", sp.html());
 	 						
 	 						if (sp.html().contains("<span>") && sp.html().contains("&lt;")){
 	 							//Create a special spoiler
 	 							createSpoiler(sp.html());
 	 							
 	 						}else {
 	 							//Sets textview with post body
 	 							createPost(sp.html().replace("<br />", "").replace("<span>", "")
 	 									.replace("</span>", "").replace("&lt;", "<").replace("&gt;", ">"));
 	 							
 	 						}
 	 					}*/
 	 				}else
 	 					createPost(text);
 	 			}
 			}
 			catch (Exception e) { e.printStackTrace(); } 
 		}
 		private void createPost(String string) {
 			//Creates a new textview for the post body text
 			//Adds it to the overall layout
 			Log.v("Image url", string);
 			TextView tvPostBody = new TextView(ctx);
 			tvPostBody.setText(string);
 			tvPostBody.setAutoLinkMask(Linkify.ALL);
 			tvPostBody.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
 			//tvPostBody.setBackgroundColor(Color.rgb(179, 223, 252));
 			tvPostBody.setTextColor(Color.WHITE);
 			tvPostBody.setPadding(5, 0, 5, 0);
 			message.addView(tvPostBody);
 		}
 		
 		private void createImage(String i) {
 			//Creates an asyncimageview, adds to overall layout 			
 			AsyncImageView img = new AsyncImageView(ctx);
 			img.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
 			//img.setBackgroundColor(Color.rgb(179, 223, 252));
 			img.setDrawingCacheBackgroundColor(Color.rgb(179, 223, 252));
 			img.setScaleType(ScaleType.FIT_START);
 			img.setUrl(i);
 			message.addView(img);
 		}

 		private void createSpoiler(String string) {
 			//Creates a new textview for spoiler
 			//Adds it to the overall layout
 			string = string.replace("<span>", "").replace("</span>", "").replace("&lt;", "<").replace("&gt;", ">");
 			Log.v("SpoilerRep", string);
 			
 			TextView tvPostBody = new TextView(ctx);
 			tvPostBody.setText(string);
 			tvPostBody.setAutoLinkMask(Linkify.ALL);
 			tvPostBody.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
 			//tvPostBody.setBackgroundColor(Color.rgb(179, 223, 252));
 			tvPostBody.setTextColor(Color.BLACK);
 			tvPostBody.setPadding(5, 0, 5, 0);
 			
 			//layout.addView(tvPostBody);
 		}
private void createQuote(Element quote, int margin) {
 			
 			//Declare Variables
 			LinearLayout.LayoutParams headerLayout;
 			TextView Header;
 			Element quoteHead, nextQuote;
 			Elements moreQuotes; 			
 			
 			//Initiate Variables
 			headerLayout = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
 			headerLayout.setMargins(margin, 2, 0, 2);
 			Header = new TextView(ctx);
 			nextQuote = null;
 			
 			//Grab header. Remove quote and header
 			quoteHead = quote.select(".message-top").first();
 			try {
 				//TODO: FIX THE NULL POINTER EXCEPTION HERE:
 				
	 			quote.select(".message-top").first().remove();
 			}
 			catch (Exception e) { e.printStackTrace(); }
 			
 			//Get next quotes
 			moreQuotes = quote.select(".quoted-message");
 			if (moreQuotes.size() > 1) {
 				//FIX BELOW
 				nextQuote = quote.select(".quoted-message").get(1);
 				quote.select(".quoted-message").get(1).remove();
 				createQuote(nextQuote, margin*2);
 			}
 			
 			//Set Layouts
 			Header.setBackgroundColor(Color.rgb(104, 150, 213));
 			Header.setPadding(15, 5, 5, 0);
 			Header.setTextColor(Color.BLACK);
 			Header.setLayoutParams(headerLayout);
 			//Set Text for header
 			Header.setText(quoteHead.text());
 			Header.setTextSize(10);
 			
 			//Set Text for Quote
 			Whitelist wlist = new Whitelist();
			wlist.addTags("br", "img");
			String clean = Jsoup.clean(quote.html(), wlist);
			String[] parts = clean.split("we t43un t5sunsrnu4sy63abt32vf2w3r9u2-=p2vo.t");
			
			//Add Header, loop for each msg to add
			message.addView(Header);
				
			for (String x : parts){
				TextView Post = new TextView(ctx);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
				params.setMargins(margin, 2, 0, 2);
				x = x.replace("<br />", "").replace("<span>", "").replace("</span>", "");
				
				Post.setBackgroundColor(Color.rgb(221, 227, 235));
	 			Post.setPadding(15, 0, 5, 0);
	 			Post.setLayoutParams(params);
	 			Post.setTextColor(Color.BLACK);
	 			
	 			Post.setText(x);
	 			Post.setAutoLinkMask(Linkify.ALL);
	 			
				message.addView(Post);
			}
			
 			
 		}
	public String getAuthor() {	return this.author; }
	public String getDate() {return this.date; }
	public String getAvatarUrl() { return this.avatarUrl; }
	public LinearLayout getMessage() { return this.message; }
}
