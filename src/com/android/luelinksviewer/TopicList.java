package com.android.luelinksviewer;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
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
	private ArrayList<Topic> topicdata = new ArrayList<Topic>();
	private String address;
	private String LOG = "TopicList";
	private QuickActionWidget mGrid;
	private int page, pagecount;
	private boolean canPost, ToM;
	
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
		Log.v(LOG, "ToM: " + new Boolean(ToM).toString());
		
		//TODO: Make a proper title
		setTitle("BOARD - " + Integer.toString(page));
		addActionBarItem(getActionBar()
			.newActionBarItem(NormalActionBarItem.class)
            .setDrawable(new ActionBarDrawable(this, R.drawable.gd_action_bar_compose)), R.string.gd_compose);
		
		//Prepare view
		setContentView(R.layout.topiclist);
		addActionBarItem(Type.Edit);
		try {
			Display();	
			prepareQuickActionGrid();
		} catch (Exception e) {	e.printStackTrace();}
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
		try {
			Helper topicRetrival = new Helper();
			if(!ToM) {
				pagecount = topicRetrival.GetPageCount(address);
				if(canPost)
					topicdata = topicRetrival.ParseTopics(address + "&page=" + Integer.toString(page));
				else
					topicdata = topicRetrival.ParseTopics(address + "?page=" + Integer.toString(page));
			}
			topicdata = topicRetrival.ParseTopics(address);
		} catch (Exception e) {	e.printStackTrace(); }
		final ListView topicslist = (ListView) findViewById(R.id.Boardview);
		topicslist.setAdapter(new CustomBaseAdapter(TopicList.this, topicdata));
	}
	
	//Prepare actiongrid
    private void prepareQuickActionGrid() {
        mGrid = new QuickActionGrid(this);
        //Check to ToM. If true, set Refresh and return
        if(ToM) {
        	mGrid.addQuickAction(new MyQuickAction(this, R.drawable.gd_action_bar_refresh, R.string.refresh));
        	return;
        }
        //Check for not-posting board, if true: add proper buttons
        if(canPost) {
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
        	//Declare Variables
        	Intent myIntent;
        	Bundle b;
        	
        	//Set Values
        	b = new Bundle();
        	myIntent = new Intent(TopicList.this, TopicList.class);
        	
        	if(!canPost)
        		position = position + 3; // Set the non-posting topic positions to match canPost
            switch (position) {
            case 0:
            	if(ToM) {
            		//Refresh page
            		b.putString("URL", address);
            		b.putBoolean("postable", canPost);
            		b.putInt("page", page);
            		myIntent.putExtras(b);
            		startActivity(myIntent);
            		break;
            	}
            	break;
            case 1:
            	//TODO: Add Board List
            	break;
            case 2:
            	//TODO: Add Search
            	break;
            case 3:
            	if(page == 1) {
            		//Refresh page
            		b.putString("URL", address);
            		b.putBoolean("postable", canPost);
            		b.putInt("page", page);
            		myIntent.putExtras(b);
            		startActivity(myIntent);
            		break;
            	}
            	//Previous page
        		b.putString("URL", address);
        		b.putBoolean("postable", canPost);
        		b.putInt("page", page-1);
        		myIntent.putExtras(b);
        		startActivity(myIntent);
            	break;
            case 4:
        		if(page != pagecount) {
        			//Next Page
            		b.putString("URL", address);
            		b.putBoolean("postable", canPost);
            		b.putInt("page", page+1);
            		myIntent.putExtras(b);
            		startActivity(myIntent);
        			break;
        		}
        		if(page == pagecount && page != 1) {
        			//Previous Page
            		b.putString("URL", address);
            		b.putBoolean("postable", canPost);
            		b.putInt("page", page-1);
            		myIntent.putExtras(b);
            		startActivity(myIntent);
        			break;
        		}
        		//TODO: Add Goto Page
        		final EditText input = new EditText(TopicList.this);
        		final String pageget;

        		new AlertDialog.Builder(TopicList.this)
        		    .setTitle("Go to Page")
        		    .setMessage("Enter page number")
        		    .setView(input)
        		    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
        		         public void onClick(DialogInterface dialog, int whichButton) {
        		             pageget = input.getText(); 
        		             // deal with the editable
        		         }
        		    })
        		    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        		         public void onClick(DialogInterface dialog, int whichButton) {
        		                // Do nothing.
        		         }
        		    }).show();


            	break;
            case 5:
            	//TODO: Add Goto Page
            	break;
            }
        }
    };
	
	protected void onListItemClick(ListView l, View v, int position, long id) {
		//TODO: Open a topic up.
		Log.v(LOG, "Topic Clicked");
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
	
}

