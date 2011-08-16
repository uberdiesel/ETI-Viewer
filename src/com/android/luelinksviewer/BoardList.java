package com.android.luelinksviewer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import greendroid.app.GDListActivity;
import greendroid.widget.ItemAdapter;


public class BoardList extends GDListActivity{
	static String LOG = "BoardList";
    private String[] boardvalues;
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        ItemAdapter adapter = new ItemAdapter(this);
        setTitle("Board List");
	    boardvalues = getResources().getStringArray(R.array.boardvalues);
	    try {
			adapter = ItemAdapter.createFromXml(this, R.layout.board);
		} 
	    catch (Exception e) {
			e.printStackTrace();
		}
        setListAdapter(adapter);
	}
	protected void onListItemClick(ListView l, View v, int position, long id) {
		int boardid = position;
		Intent myIntent = new Intent(this, TopicList.class);
		Bundle b = new Bundle();
		
		//set boardid and remove the separater items
		boardid = boardid - 1;
		if (boardid > 4)
			boardid = boardid-1;
		if (boardid > 9)
			boardid = boardid-1;
		
		b.putString("URL",boardvalues[boardid]);
		b.putBoolean("postable", true);
		b.putInt("page", 1);
		myIntent.putExtras(b);
		startActivity(myIntent);
	}
}
