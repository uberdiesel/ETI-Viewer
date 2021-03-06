package com.android.luelinksviewer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView.ScaleType;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import greendroid.app.GDActivity;
import greendroid.image.ImageProcessor;
import greendroid.widget.ActionBarItem;
import greendroid.widget.AsyncImageView;
import greendroid.widget.QuickAction;
import greendroid.widget.QuickActionGrid;
import greendroid.widget.QuickActionWidget;
import greendroid.widget.ActionBarItem.Type;
import greendroid.widget.QuickActionWidget.OnQuickActionClickListener;

public class DisplayTopic extends GDActivity implements OnScrollListener{
	private ArrayList<Message> MessageList = new ArrayList<Message>();
	private String address, title;
	private int page, pagecount;
	private QuickActionWidget mGrid;
	private ImageProcessor mImageProcessor;
	private ProgressDialog pd;
	
	private LinearLayout layout;
	private ScrollView scrollview;
	private ListView messagelist;
	
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Define Variables
		Bundle b;
		
		//Initiate Variables
		b = getIntent().getExtras();
		address = b.getString("URL");
		title = b.getString("title");
		page = b.getInt("page");
		
		//Prepare view
        //Setup views
        layout = new LinearLayout(this);
        layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        layout.setOrientation(1);
        
        scrollview = new ScrollView(this);
        scrollview.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		
        try {
			Display();	
		} catch (Exception e) {	e.printStackTrace();}
        
        setActionBarContentView(scrollview);
        
		addActionBarItem(Type.Edit);
		
	}
    
    public void onShowGrid(View v) {
        mGrid.show(v);
    }
    @Override
    public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {

        switch (position) {
            case 0:
                onShowGrid(item.getItemView());
                break;

            default:
                return super.onHandleActionBarItemClick(item, position);
        }

        return true;
    }
    private void prepareQuickActionGrid() {
        mGrid = new QuickActionGrid(this);
         mGrid.addQuickAction(new MyQuickAction(this, R.drawable.gd_action_bar_compose, R.string.post_topic));
        if(page == 1) //Page = 1, display refresh
         mGrid.addQuickAction(new MyQuickAction(this, R.drawable.gd_action_bar_refresh, R.string.refresh));
        else
         mGrid.addQuickAction(new MyQuickAction(this, R.drawable.ic_action_backarrow, R.string.previous_page));
        if(page != pagecount)
         mGrid.addQuickAction(new MyQuickAction(this, R.drawable.ic_action_nextarrow, R.string.next_page));
        if(page == pagecount && page != 1)
         mGrid.addQuickAction(new MyQuickAction(this, R.drawable.ic_action_backarrow, R.string.previous_page));
        mGrid.addQuickAction(new MyQuickAction(this, R.drawable.gd_action_bar_export, R.string.goto_page));
        mGrid.setOnQuickActionClickListener(mActionListener);
    }
    
    private OnQuickActionClickListener mActionListener = new OnQuickActionClickListener() {
    	public void onQuickActionClicked(QuickActionWidget widget, int position) {
    		try {
	    		switch (position) {
	    			case 0:
	    				//TODO: Post Message
	    				break;
	    			case 1:
	    				if(page == 1) {
	    					//Refresh page
	    					Display();
	    					break;
	    				}
	    				//Previous page
	    				page--;
	    				Display();
	             		break;
	    			case 2:
	    				if(page != pagecount) {
	    					//Next Page
	    					page++;
	    					Display();
	    					break;
	    				}
	    				if(page == pagecount && page != 1) {
	    					//Previous Page
	    					page--;
	    					Display();
	    					break;
	    				}
	    				//Goto Page
	    				Display();
	    				break;
	    			case 3:
	    				//Goto Page
	    				GetPage();
	    				break;
	            }
    		}
    		catch (Exception e) {
    			
    		}
        }
    };
    
    private void GetPage() {
        final EditText input = new EditText(DisplayTopic.this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        new AlertDialog.Builder(DisplayTopic.this)
        	.setTitle("Go to Page")
        	.setMessage("Enter page (1-" + pagecount + ")")
        	.setView(input)
        	.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int whichButton) {
        			int np = Integer.parseInt(input.getText().toString());
        			if(np > pagecount && np < 1) {
        				Toast.makeText(getApplicationContext(), "Invalid Page Number", Toast.LENGTH_SHORT).show();
        			}
        			else {
        				page = np;
        				try {
							Display();
						} catch (InterruptedException e) { e.printStackTrace();
						} catch (ExecutionException e) { e.printStackTrace();
						}
        			}
        		}
        	})
        	.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int whichButton) {
        			// Do nothing.
        		}
        	}).show();	
	}
    
    private static class MyQuickAction extends QuickAction {
        private static final ColorFilter BLACK_CF = new LightingColorFilter(Color.BLACK, Color.BLACK);
        public MyQuickAction(Context ctx, int drawableId, int titleId) {
            super(ctx, buildDrawable(ctx, drawableId), titleId);
        }
        private static Drawable buildDrawable(Context ctx, int drawableId) {
            Drawable d = ctx.getResources().getDrawable(drawableId);
            d.setColorFilter(BLACK_CF);
            return d;
        }
        
    }
    
	private void Display() throws InterruptedException, ExecutionException {
		try {
			new LoadMessageList().execute(address + "&page=" + Integer.toString(page));
		} catch (Exception e) {	e.printStackTrace(); }
		prepareQuickActionGrid();
	}
	
	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
		
    }

    public void onScrollStateChanged(AbsListView listView, int scrollState) {
        if (messagelist == listView) {
            searchAsyncImageViews(listView, scrollState == OnScrollListener.SCROLL_STATE_FLING);
        }
    }

    private void searchAsyncImageViews(ViewGroup viewGroup, boolean pause) {
        final int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            AsyncImageView image = (AsyncImageView) viewGroup.getChildAt(i).findViewById(R.id.messageavatar);
            if (image != null) {
                image.setPaused(pause);
            }
        }
    }
    
    
	
	private class LoadMessageList extends AsyncTask <String, Integer, Document > {
 		@Override
 		protected void onPreExecute(){
 			//UI Thread, run before executing
 			pd = ProgressDialog.show(DisplayTopic.this, "Loading", "Fetching  Messages");	//opens progress dialog
 			layout.removeAllViews();
 			scrollview.removeAllViews();
 			MessageList.clear();		
 		}
 		
 		@Override
 		protected Document doInBackground(String... addr) {
 			//what to do in the background			
 			Document doc = null; 			
 			try {
 				doc = Helper.GetPage(addr[0]);
 			} catch (Exception e) {	e.printStackTrace(); }
 			return doc;
 		}
 		
 		protected void onPostExecute(Document doc){
 			setTitle(Integer.toString(page) + ": " + title);
			Element pc = doc.select("div.infobar").first().select("span").first();
 			pagecount = Integer.parseInt(pc.text());
 			
			try {
				Elements postList = doc.select("#u0_1").select(".message-container");
				for (Element post : postList){
					MessageList.add(new Message(post, DisplayTopic.this));
				}
				for (Message msg : MessageList) {
					GenerateMessage(msg);
				}
			}catch (NullPointerException e){ }
			
			scrollview.addView(layout);
			pd.dismiss();
			//ba.notifyDataSetChanged();
		}
 		private void GenerateMessage(Message msg) {
 			GenerateHeader(msg.getAuthor(), msg.getDate(), msg.getAvatarUrl());
 			layout.addView(msg.getMessage());
 		}
 		private void GenerateHeader(String a, String d, String avatarurl) {
 			//Declare Views
 			RelativeLayout header;
 			AsyncImageView avatar;
 			TextView author, date;
 			RelativeLayout.LayoutParams  avalayout, authlayout, datelayout;
 			
 			//Initiate Variables
 			header = new RelativeLayout(DisplayTopic.this);
 			avatar = new AsyncImageView(DisplayTopic.this);
 			avatar.setImageProcessor(mImageProcessor);

 			author = new TextView(DisplayTopic.this);
 			date = new TextView(DisplayTopic.this);
 			avalayout = new RelativeLayout.LayoutParams(110, 100);
 			authlayout = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
 			datelayout = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
 			
 			//Set IDs
 			header.setId(01);
 			avatar.setId(02);
 			author.setId(03);
 			date.setId(04);
 			
 			//Set Layout Rules
 			authlayout.addRule(RelativeLayout.RIGHT_OF, 02);
 			datelayout.addRule(RelativeLayout.BELOW, 03);
 			datelayout.addRule(RelativeLayout.RIGHT_OF, 02);
 			avalayout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
 			avatar.setPadding(0, 0, 10, 0);
 			
 			//Set Layouts
 			header.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
 			header.setBackgroundColor(Color.rgb(104, 150, 213));
 			avatar.setLayoutParams(avalayout);
 			author.setLayoutParams(authlayout);
 			date.setLayoutParams(datelayout);
 			
 			//Make the Views
 			avatar.setUrl(avatarurl);
 			author.setText(a);
 			author.setTextColor(Color.rgb(0, 0, 0));
 			author.setTextSize(14);
 			author.setTypeface(null, Typeface.BOLD);
 			date.setTextColor(Color.rgb(0, 0, 0));
 			date.setText(d);
 			
 			//Add Views
 			header.addView(avatar);
 			header.addView(author);
 			header.addView(date);
 			layout.addView(header);
 			
 		}
 		

 		private void GetBody(Elements msg) {
 			//Declare Variables
 			int brpos, quotepos;
 			Elements quotes;
 			
 			
			quotes = msg.select(".quoted-message");
			
			if (!quotes.isEmpty()) {
				//Check for text before the quoted message
				
				brpos = msg.toString().indexOf("<br />");
	 			quotepos = msg.toString().indexOf("<div class");
				if (brpos < quotepos)
					quotes = RemoveBeginText(msg);
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
					Element ele = msg.select(".message").first();
					for (Element img : ele.select("a[imgsrc]")) {
						images.add(img.attr("imgsrc"));
						
					}
					Whitelist wlist = new Whitelist();
	 	 			wlist.addTags("br", "img", "a").addAttributes(":all", "imgsrc");
	 	 			String clean = Jsoup.clean(msg.toString(), wlist);
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
 			
 			TextView tvPostBody = new TextView(DisplayTopic.this);
 			tvPostBody.setText(string);
 			tvPostBody.setAutoLinkMask(Linkify.ALL);
 			tvPostBody.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
 			//tvPostBody.setBackgroundColor(Color.rgb(179, 223, 252));
 			tvPostBody.setTextColor(Color.WHITE);
 			tvPostBody.setPadding(5, 0, 5, 0);
 			layout.addView(tvPostBody);
 		}
 		
 		private void createImage(String i) {
 			//Creates an asyncimageview, adds to overall layout 			
 			AsyncImageView img = new AsyncImageView(DisplayTopic.this);
 			img.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
 			//img.setBackgroundColor(Color.rgb(179, 223, 252));
 			img.setDrawingCacheBackgroundColor(Color.rgb(179, 223, 252));
 			img.setScaleType(ScaleType.FIT_START);
 			img.setUrl(i);
 			layout.addView(img);
 		}

 		private void createSpoiler(String string) {
 			//Creates a new textview for spoiler
 			//Adds it to the overall layout
 			string = string.replace("<span>", "").replace("</span>", "").replace("&lt;", "<").replace("&gt;", ">");
 			Log.v("SpoilerRep", string);
 			
 			TextView tvPostBody = new TextView(DisplayTopic.this);
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
 			Header = new TextView(DisplayTopic.this);
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
			layout.addView(Header);
				
			for (String x : parts){
				TextView Post = new TextView(DisplayTopic.this);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
				params.setMargins(margin, 2, 0, 2);
				x = x.replace("<br />", "").replace("<span>", "").replace("</span>", "");
				
				Post.setBackgroundColor(Color.rgb(221, 227, 235));
	 			Post.setPadding(15, 0, 5, 0);
	 			Post.setLayoutParams(params);
	 			Post.setTextColor(Color.BLACK);
	 			
	 			Post.setText(x);
	 			Post.setAutoLinkMask(Linkify.ALL);
	 			
				layout.addView(Post);
			}
			
 			
 		}
 	}
}