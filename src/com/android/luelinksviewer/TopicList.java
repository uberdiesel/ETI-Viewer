package com.android.luelinksviewer;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;
import greendroid.app.GDListActivity;
import greendroid.widget.ItemAdapter;

public class TopicList extends GDListActivity{
	Topic topicdata = new Topic(); 
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Define Variables
		Bundle b;
		String address;
		Helper TopicParser;
		
		//Initiate Variables
		b = getIntent().getExtras();
		address = b.getString("URL");
		TopicParser = new Helper();
		topicdata = TopicParser.ParseTopic(address);
		setTitle(topicdata.getTitle());
	}
	
	
	private class DisplayTopics extends AsyncTask <String, Integer, Boolean > {
		ProgressDialog pd;
		@Override
		protected void onPreExecute(){
			//UI Thread, run before executing
			pd = ProgressDialog.show(TopicList.this, "Loading", "Loading Topics...");	//opens progress dialog
		}
		
		@Override
		protected Boolean doInBackground(String... url) {
			return true;
		}
		
		protected void onPostExecute(Boolean result){
			//UI Thread, what to do after
			pd.cancel();
			if (result){
				finish();
				
			}else {
				Toast.makeText(TopicList.this, "Please try again", Toast.LENGTH_LONG).show();
			}
			
		}
		
	}
}

