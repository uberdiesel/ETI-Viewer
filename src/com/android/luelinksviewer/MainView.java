package com.android.luelinksviewer;

import greendroid.app.GDListActivity;
import greendroid.graphics.drawable.ActionBarDrawable;
import greendroid.widget.ActionBarItem;
import greendroid.widget.ItemAdapter;
import greendroid.widget.NormalActionBarItem;
import greendroid.widget.item.TextItem;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class MainView extends GDListActivity {
	LuelinksViewer LueApp;
	static String LOG = "MainView";

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LueApp = (LuelinksViewer)getApplicationContext();
        setTitle("Main View");
        ItemAdapter adapter = new ItemAdapter(this);
        adapter.add(createTextItem(R.string.board_list));
        adapter.add(createTextItem(R.string.topicsofmoment));
        adapter.add(createTextItem(R.string.posted_messages));
        adapter.add(createTextItem(R.string.tagged_topics));
        adapter.add(createTextItem(R.string.drama_links));
        adapter.add(createTextItem(R.string.private_messages));
        adapter.add(createTextItem(R.string.poll));
        adapter.add(createTextItem(R.string.profile));
        setListAdapter(adapter);
        
        addActionBarItem(getActionBar()
                .newActionBarItem(NormalActionBarItem.class)
                .setDrawable(new ActionBarDrawable(this, R.drawable.ic_action_settings)), R.id.action_view_settings);
	}
	
	private TextItem createTextItem(int stringId) {
        final TextItem textItem = new TextItem(getString(stringId));
        return textItem;
    }
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent myIntent;
		Bundle b;
		//TODO: Add each item's respective activity and link to said activity.
        switch (position) {
	 		case 0:
	 			myIntent = new Intent(this, BoardList.class);
				startActivity(myIntent);
	 			break;
	 		case 1:
	 			myIntent = new Intent(this, TopicList.class);
				b = new Bundle();
				b.putString("URL",getString(R.string.moment_uri));
				b.putBoolean("postable", false);
				Log.v(LOG, getString(R.string.moment_uri)); 
				myIntent.putExtras(b);
				startActivity(myIntent);
	 			break;
	 		case 2:
	 			//TODO: Add an activity to open posted topics
	 			break;
	 		case 3:
	 			myIntent = new Intent(this, TopicList.class);
				b = new Bundle();
				b.putString("URL",getString(R.string.tagged_uri));
				b.putBoolean("postable", false);
				myIntent.putExtras(b);
				startActivity(myIntent);
	 			break;
	 		case 4:
	 			//TODO: Add an activity to open drama
	 			break;
	 		case 5:
	 			//TODO: Add an activity to open the PM
	 			break;
	 		case 6:
	 			//TODO: Add an activity to open the poll
	 			break;
	 		case 7:
	 			//TODO: Add an activity to open the profle
	 			break;
        }
	 }
	 @Override
	 public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
	 	switch (item.getItemId()) {
	 		case R.id.action_view_settings:
	 			Log.v(LOG, "Clicked Settings");
	 			//TODO: Add an activity to modify any applicaton settings (theme?) 
	 			return true;
            default:
                return super.onHandleActionBarItemClick(item, position);
        }
    }
}