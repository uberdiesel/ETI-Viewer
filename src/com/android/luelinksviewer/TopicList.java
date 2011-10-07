package com.android.luelinksviewer;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import greendroid.app.GDActivity;
import greendroid.graphics.drawable.ActionBarDrawable;
import greendroid.widget.ActionBarItem;
import greendroid.widget.NormalActionBarItem;
import greendroid.widget.QuickAction;
import greendroid.widget.QuickActionGrid;
import greendroid.widget.QuickActionWidget;
import greendroid.widget.ActionBarItem.Type;
import greendroid.widget.QuickActionWidget.OnQuickActionClickListener;

public class TopicList extends GDActivity{
	LuelinksViewer LueApp;
	ProgressDialog pd;
	ListView lv;
	ArrayList<Topic> TopicList = new ArrayList<Topic>();
	TopicCustomBaseAdapter ba;
	ListView topicslist;
	private String address;
	private String LOG = "TopicList";
	private QuickActionWidget mGrid;
	int page, pagecount;
	private boolean canPost, ToM;
	private int topictype;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Define Variables
		Bundle b;
		
		//Initiate Variables
		b = getIntent().getExtras();
		address = b.getString("URL");
		canPost = b.getBoolean("postable");
		page = b.getInt("page");
		ToM = b.getBoolean("ToM");
		topictype = b.getInt("type");
		Log.v(LOG, Integer.toString(topictype));

		addActionBarItem(getActionBar()
		.newActionBarItem(NormalActionBarItem.class)
		            .setDrawable(new ActionBarDrawable(this, R.drawable.gd_action_bar_compose)), R.string.gd_compose);
		
		//Prepare view
		setContentView(R.layout.topiclist);
		addActionBarItem(Type.Edit);
		topicslist = (ListView) findViewById(R.id.Topicview);
		ba = new TopicCustomBaseAdapter(TopicList.this, TopicList);
		topicslist.setAdapter(ba);
		try {
			Display();
		} catch (InterruptedException e) { e.printStackTrace();	} catch (ExecutionException e) { e.printStackTrace();}
		
		
        topicslist.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> a, View v, int position, long id) {
        		Intent myIntent = new Intent(TopicList.this, DisplayTopic.class);;
    	        Bundle b = new Bundle(); 
    	        b.putString("URL", TopicList.get(position).getAddress());
    	        b.putInt("page", TopicList.get(position).getPage());
    	        b.putString("title", TopicList.get(position).getTitle());
    	        myIntent.putExtras(b);
    	        Log.v(LOG, TopicList.get(position).getAddress());
    	        startActivity(myIntent);
        	}
        });
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
    
	private void Display() throws InterruptedException, ExecutionException {
		prepareQuickActionGrid();
	try {
		if(topictype > 0) {
			if(topictype > 2)
				new LoadTopicList().execute(address + "&page=" + Integer.toString(page));
			else
				new LoadTopicList().execute(address + "?page=" + Integer.toString(page));
		}
		else
			new LoadTopicList().execute(address);
	} catch (Exception e) { e.printStackTrace(); }
	
	}

//Prepare actiongrid
    private void prepareQuickActionGrid() {
        mGrid = new QuickActionGrid(this);
        //Check to ToM. If true, set Refresh and return
        if(topictype == 0) {
         mGrid.addQuickAction(new MyQuickAction(this, R.drawable.gd_action_bar_refresh, R.string.refresh));
         return;
        }
        //Check for not-posting board, if true: add proper buttons
        if(topictype > 2) {
         mGrid.addQuickAction(new MyQuickAction(this, R.drawable.gd_action_bar_compose, R.string.post_topic));
         mGrid.addQuickAction(new MyQuickAction(this, R.drawable.gd_action_bar_list, R.string.board_list));
         mGrid.addQuickAction(new MyQuickAction(this, R.drawable.gd_action_bar_search, R.string.search));

        }
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
    
    //ActionGrid item listener
    private OnQuickActionClickListener mActionListener = new OnQuickActionClickListener() {
    	public void onQuickActionClicked(QuickActionWidget widget, int position) {
    		try {
	    		if(topictype < 3)
	    			position = position + 3; // Set the non-posting topic positions to match canPost
	    		switch (position) {
	    			case 0:
	    				if(topictype == 0) {
	    					//Refresh page
							Display();
	    					break;
	    				}
	    				//TODO: Post Topic
	    				break;
	    			case 1:
	    				//Board List
	    		        startActivity(new Intent(TopicList.this, BoardList.class));
	    				break;
	    			case 2:
	    				//TODO: Add Search
	    				break;
	    			case 3:
	    				if(page == 1) {
	    					//Refresh page
	    					Display();
	    					break;
	    				}
	    				//Previous page
	    				page--;
	    				Display();
	             		break;
	    			case 4:
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
	    				GetPage();
	    				break;
	    			case 5:
	    				//Goto Page
	    				GetPage();
	    				break;
	            }
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
        }
    };
    private void GetPage() {
        final EditText input = new EditText(TopicList.this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        new AlertDialog.Builder(TopicList.this)
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
						} catch (InterruptedException e) {e.printStackTrace();} 
        				catch (ExecutionException e) {e.printStackTrace();	}
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

    //AsyncTask <input, progress, results>		LOAD TOPIC LIST
 	private class LoadTopicList extends AsyncTask <String, Integer, Document > {
 		@Override
 		protected void onPreExecute(){
 			//UI Thread, run before executing
 			pd = ProgressDialog.show(TopicList.this, "Loading", "Topic List");	//opens progress dialog
 			TopicList.clear();		
 			ba.notifyDataSetChanged();
 		}
 		
 		@Override
 		protected Document doInBackground(String... addr) {
 			Document doc = null;
 			try {
 				doc = Helper.GetPage(addr[0]);
 			} catch (Exception e) {
 				e.printStackTrace();
 			
 			}
 			pd.dismiss();
 			return doc;
 		}
 		
 		protected void onPostExecute(Document doc){
 			//UI Thread, what to do after
 			String extraUrl = "//boards.endoftheinter.net";
 			try {
 				//Declare Variables
 				Elements tables, topics, infobar;
 				Element pc;
 				
 				if(topictype == 0)
 					setTitle("Topics of the Moment");
 				else
 					setTitle(Integer.toString(page) + ": " + doc.select("h1").text());
 				if(topictype > 0) {
 					//Check for a sub-board info-bar and remove it.
 					infobar = doc.select("div.infobar");
 					int y = 0;
 					//pc = infobar.get(y).select("span").first();
 					while (!infobar.get(y).toString().contains("span")) {
 						y++;
 					}
 					pc = infobar.get(y).select("span").first();
 	 	 			pagecount = Integer.parseInt(pc.text());
 	 			}
 				
 				//Check for a sub board and remove it.
 				tables = doc.select("table");
 				if (tables.size() > 1) {
 					Log.v("boolean", "IM IN MART");
 					tables.remove(0); 
 				}
 				topics = tables.select("tr");
 				
 				//Remove the header from each table
 				topics.remove(0);
 				if(topictype == 0)
 					topics.remove(0); //Remove the ToM header from being added as well.
 				for (Element td : topics){
 					Topic topicinfo = new Topic();
 					
 					//Set Topic Address
 					//if (topictype == 2)
 						//td.select("a").eq(0).remove();
 					topicinfo.setAddress("http:" + td.select("a").eq(0).attr("href").toString());
 					topicinfo.setTitle(td.select("a").eq(0).text());
 					if(td.select("b").eq(0).hasText()) {
 						topicinfo.setisSticky(true);
 					}
 						
 					//topic.put("author_link", td.select("a").eq(1).attr("href").replace(extraURL, ""));
 					if (topictype == 1)
 						td.select("a").eq(1).remove();
 					if (topictype == 2)
 						topicinfo.setPoster(td.select("td").first().text());
 					else
 						topicinfo.setPoster(td.select("a").eq(1).text());
 					topicinfo.setPostcount(td.select("td").eq(2).text());
 					
 					
 					String split = "[ ]+";
 			    	String[] splitpostcount = td.select("td").eq(2).text().split(split);
 			    	topicinfo.setPostcount(splitpostcount[0]);
 			    	
 			    	
 			    	//Add bookmarking
 			    	if (splitpostcount.length > 1) {
 			    		int startsplit = splitpostcount[1].lastIndexOf("+") + 1;
 			    		int endsplit = splitpostcount[1].lastIndexOf(")");
 			    		String bookmarkcount = splitpostcount[1].substring(startsplit, endsplit);
 			    		topicinfo.setPostBookmark(bookmarkcount); // Add number of "new" messages
 			    		topicinfo.setisBookmark(true);
 			    		
 			    		//Set new address for topic
 			    		int posts = Integer.parseInt(splitpostcount[0]);
 			    		int newposts = Integer.parseInt(bookmarkcount);
 			    		if((posts-newposts) > 50) {
 			    			int page = (((posts-newposts) + 49) / 50 );
 			    			topicinfo.setPage(page);
 			    		}
 			    	}
 				TopicList.add(topicinfo);
 				ba.notifyDataSetChanged();
 				}
 				
 			}catch (NullPointerException e){
 				Log.v(LOG, "Must Relog");
 				Toast.makeText(getApplicationContext(), "Not logged in", Toast.LENGTH_SHORT).show();
 				
     			Intent intent = new Intent(TopicList.this, Login.class);
     			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
     			startActivity(intent);
 			}catch (IndexOutOfBoundsException e) {
 				Log.v(LOG, "Must Relog");
 				Toast.makeText(getApplicationContext(), "Not logged in", Toast.LENGTH_SHORT).show();
 				
     			Intent intent = new Intent(TopicList.this, Login.class);
     			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
     			startActivity(intent);
 			}
 			

 		}
 		
 	}
}