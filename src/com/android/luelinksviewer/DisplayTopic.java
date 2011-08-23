package com.android.luelinksviewer;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
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
	private String LOG = "DisplayTopic";
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
    			Log.v(LOG, Integer.toString(position));
	    		switch (position) {
	    			case 0:
	    				Log.v(LOG, Integer.toString(position));
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
		Log.v(LOG, "Display Entered");
		try {
			Log.v(LOG, "Displaying: " +  address + "&page=" + Integer.toString(page));
			new LoadMessageList().execute(address + "&page=" + Integer.toString(page));
		} catch (Exception e) {	e.printStackTrace(); }
		prepareQuickActionGrid();
	}
	
	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
		
    }

    public void onScrollStateChanged(AbsListView listView, int scrollState) {
    	Log.v(LOG, "MessageList: " + messagelist.toString());
    	Log.v(LOG, "listView: " + listView.toString());
        if (messagelist == listView) {
        	Log.v(LOG, "GREAT SUCCSES");
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
 			Log.v(LOG, "Pagecount: " + pc.text());
 			pagecount = Integer.parseInt(pc.text());
 			
			try {
				Elements postList = doc.select("#u0_1").select(".message-container");
				for (Element msg : postList){
					parseMessage(msg);
				}
			}catch (NullPointerException e){ }
			
			scrollview.addView(layout);
			pd.dismiss();
			//ba.notifyDataSetChanged();
		}
 		private void parseMessage(Element msg) {
 			//Declare Variables
 			String author, date, avatarurl;
 			Elements body;
 			Element header;
 			Message messageData;
 			
 			//Initiate Variables
 			author = null;
 			date = null;
 			avatarurl = null;
 			messageData = new Message();
 			
 			header = msg.select("div.message-top").first();
	    	// Parse Out Header Data
	    	String split = "[|]";
	    	String[] splithead = header.text().split(split);
	    	String posthead = splithead[0].trim();
	    	String datehead = splithead[1].trim();
	    	datehead = datehead.substring(8);
	    	author = posthead.substring(6);
	    	date = datehead.substring(0, datehead.length()-6);
	    	//messageData.setAuthor(author);
			//messageData.setDate(date);
			
			//Get body HTML
			body = msg.select(".message");
			messageData.setMessage(body);
			
			//Get Avatar
			Elements avatar = msg.select(".userpic-holder").select("script");
			
			if (!avatar.isEmpty()) {
				String splitter = "[,]";
				String datas[] = avatar.toString().split(splitter);
				String a = datas[1].replace("\\" , "").replace("\"" , "");
				avatarurl = "http:" + a.trim();
				messageData.setAvatarUrl(avatarurl);
			}
			GetHeader(author, date, avatarurl);
			GetBody(body);
			//MessageList.add(messageData);
 		}
 		private void GetHeader(String a, String d, String avatarurl) {
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
 			avalayout = new RelativeLayout.LayoutParams(100, 100);
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
 			
 			//Set Layouts
 			header.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
 			avatar.setLayoutParams(avalayout);
 			author.setLayoutParams(authlayout);
 			date.setLayoutParams(datelayout);
 			
 			//Make the Views
 			avatar.setUrl(avatarurl);
 			author.setText(a);
 			date.setText(d);
 			
 			//Add Views
 			header.addView(avatar);
 			header.addView(author);
 			header.addView(date);
 			layout.addView(header);
 			
 		}
 		private void GetBody(Elements msg) {
 			ArrayList<String> images = new ArrayList<String>();
 			try {
 				//Removes quotes from the main message
 				Elements quote = msg.select(".quoted-message");
 				if (!quote.isEmpty()){
 					//Get quotes and add them to the Layout
 					Whitelist wlist = new Whitelist();
 					wlist.addTags("br", "img");
 					String clean = Jsoup.clean(quote.html(), wlist);
 					String[] parts = clean.split("we t43un t5sunsrnu4sy63abt32vf2w3r9u2-=p2vo.t");
 					for (String x : parts){
 						Log.v("Quoted String parts", x + " ");
 						//Add Quoted TextView
 						createQuotePost(x.replace("<br />", "").replace("<span>", "").replace("</span>", ""));
 					}
 					msg.select(".quoted-message").remove();
 				}
 				
 			}catch (NullPointerException e) { e.printStackTrace(); }
 			
 			
 			Whitelist wlist = new Whitelist();
 			wlist.addTags("br", "img", "a").addAttributes(":all", "imgsrc");
 			
 			String clean = Jsoup.clean(msg.html(), wlist);
 			
 			images.clear();
 			for (Element i : msg.select("a[imgsrc]")){
 				//Get the images in order they were posted
 				images.add(i.attr("imgsrc"));
 				Log.v("Image url", i.attr("src"));
 			}		
 			
 			String[] parts = clean.split("<br />");
 			
 			int y = 0;
 			for (String post : parts){
 				
 				String text = post.replace("<br />", "").replace("<span>", "")
 						.replace("</span>", "").replace("&lt;", "<").replace("&gt;", ">")
 						.replace("<a>", "").replace("</a>", "").replace("&quot;", "''").trim();
 				
 				if (text.contains("<a imgsrc")){
 					//If an image is here, post it from the list of captured image urls
 					Log.v("FOUND", "Image Found");
 					createImage(images.get(y));
 					y++;
 					
 				}else if (post.contains("<span>") && post.contains("&lt;") && post.contains("&gt;")){
 					//If thre's a spoiler in the text
 					
 					Log.v("FOUND", "Spoiler Found");
 					
 					createPost(text);
 					
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
 			Log.v("createImage", i);
 			
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
 		
 		private void createQuotePost(String string) {
 			//Creates a new textview for the quote post body text
 			//Adds it to the overall layout
 			
 			TextView tvQuoteBody = new TextView(DisplayTopic.this);
 			tvQuoteBody.setText(string);
 			tvQuoteBody.setAutoLinkMask(Linkify.ALL);
 			tvQuoteBody.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
 			tvQuoteBody.setBackgroundColor(Color.rgb(169, 169, 169));
 			tvQuoteBody.setTextColor(Color.BLACK);
 			tvQuoteBody.setPadding(15, 5, 5, 5);
 			layout.addView(tvQuoteBody);
 			
 		}
 	}
}