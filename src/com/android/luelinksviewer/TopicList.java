package com.android.luelinksviewer;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
	private ArrayList<Topic> topicdata = new ArrayList<Topic>();
	private String address;
	private QuickActionWidget mGrid;
	
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Define Variables
		Bundle b;
		
		boolean canPost;
		
		//Initiate Variables
		b = getIntent().getExtras();
		address = b.getString("URL");
		canPost = b.getBoolean("postable");

		if(canPost) {
			addActionBarItem(getActionBar()
	                .newActionBarItem(NormalActionBarItem.class)
	                .setDrawable(new ActionBarDrawable(this, R.drawable.gd_action_bar_compose)), R.string.gd_compose);
		}
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
			//TODO: MAKE THIS DO IT IN THE BACKGRONUD SO I GET A LOADING SCREEN DAMNIT
			//AsyncTask<String, Integer, Boolean> GT = new GetTopics().execute("");
			//boolean result = GT.get();
			Helper topicRetrival = new Helper();
			topicdata = topicRetrival.ParseTopics(address);
		} catch (Exception e) {	e.printStackTrace(); }
		final ListView topicslist = (ListView) findViewById(R.id.Boardview);
		topicslist.setAdapter(new CustomBaseAdapter(TopicList.this, topicdata));
	}
	
	//Prepare actiongrid
    private void prepareQuickActionGrid() {
    	//TODO: Display proper actiongrid
        mGrid = new QuickActionGrid(this);
        mGrid.addQuickAction(new MyQuickAction(this, R.drawable.gd_action_bar_compose, R.string.gd_compose));
        mGrid.addQuickAction(new MyQuickAction(this, R.drawable.gd_action_bar_export, R.string.gd_export));
        mGrid.addQuickAction(new MyQuickAction(this, R.drawable.gd_action_bar_share, R.string.gd_share));
        mGrid.addQuickAction(new MyQuickAction(this, R.drawable.gd_action_bar_search, R.string.gd_search));
        mGrid.addQuickAction(new MyQuickAction(this, R.drawable.gd_action_bar_edit, R.string.gd_edit));
        mGrid.addQuickAction(new MyQuickAction(this, R.drawable.gd_action_bar_locate, R.string.gd_locate));

        mGrid.setOnQuickActionClickListener(mActionListener);
    }
    
    //ActionGrid item listener
    private OnQuickActionClickListener mActionListener = new OnQuickActionClickListener() {
        public void onQuickActionClicked(QuickActionWidget widget, int position) {
        	//TODO: Set Itemclick events
            Toast.makeText(TopicList.this, "Item " + position + " clicked", Toast.LENGTH_SHORT).show();
        }
    };
	
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent myIntent;
		Bundle b;
		//TODO: Open a topic up.
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

