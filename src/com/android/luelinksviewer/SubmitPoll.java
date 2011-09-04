package com.android.luelinksviewer;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import greendroid.app.GDActivity;

public class SubmitPoll extends GDActivity{
	private ProgressDialog pd;
	private LinearLayout layout;
	private ScrollView scrollview;
	private RadioGroup radiogroup;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Setup views
        layout = new LinearLayout(this);
        scrollview = new ScrollView(this);
        radiogroup = new RadioGroup(this);
        
        layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        layout.setOrientation(1);
        scrollview.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        radiogroup.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        
		new GetPoll().execute("http://www.endoftheinter.net/main.php");
		setActionBarContentView(scrollview);
	}
	
	private class GetPoll extends AsyncTask <String, Integer, Document > {
		@Override
		protected void onPreExecute(){
			//UI Thread, run before executing
			pd = ProgressDialog.show(SubmitPoll.this, "Loading", "Getting Poll");	//opens progress dialog
			layout.removeAllViews();
			scrollview.removeAllViews();
			radiogroup.removeAllViews();
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
			setTitle("Poll of the Day");
			Elements poll;
			try {
				poll = doc.select("div.poll");
				SetTitle(poll.select("b").first().text());
				Elements options = poll.select("label");
				for(Element opt : options) {
					SetSelection(opt.text());
				}
				Elements inputs = poll.select("input");
				Element ID = inputs.get(inputs.size() - 2);
				String id = ID.toString();
				int a = id.lastIndexOf("value=\"") + 7;
				int b = id.lastIndexOf("\"");
				Log.v("POLL", id.substring(a, b));
				Log.v("POLL", ID.toString());
				SetSubmit(id.substring(a, b));
			}catch (NullPointerException e){
				Log.v("POLL", "HOLY SHIT AN ERROR");
			}
			
			layout.addView(radiogroup);
			scrollview.addView(layout);
			pd.dismiss();
		}
	}
	private void SetTitle(String t) {
		//Declare
		TextView title;
		LayoutParams lp;
		
		//Initiate
		title = new TextView(SubmitPoll.this);
		lp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		
		//Set Layout
		title.setLayoutParams(lp);
		
		//Set Text
		title.setText(t);
		layout.addView(title);
		
	}
	private void SetSelection(String sel) {
		RadioButton selection;
		LayoutParams lp;
		
		selection = new RadioButton(SubmitPoll.this);
		lp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		
		selection.setText(sel);
		radiogroup.addView(selection);
	}
	
	private void SetSubmit(final String PollId) {
		Button submit;
		LayoutParams lp;
		
		submit = new Button(SubmitPoll.this);
		lp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		
		submit.setText("SUBMIT");
		
		layout.addView(submit);
		
		submit.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
    			try {
    				int rbid = radiogroup.getCheckedRadioButtonId();
    				View rb = radiogroup.findViewById(rbid);
    				int idx = radiogroup.indexOfChild(rb) + 1;
    				Helper helper = new Helper();
    				helper.SubmitPoll(idx, PollId);
    				new DisplayResults().execute("http://www.endoftheinter.net/poll.php");
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    		}
        });
	}
	
	private class DisplayResults extends AsyncTask <String, Integer, Document > {
		@Override
		protected void onPreExecute(){
			//UI Thread, run before executing
			pd = ProgressDialog.show(SubmitPoll.this, "Loading", "Getting Poll Results");	//opens progress dialog
			layout.removeAllViews();
			scrollview.removeAllViews();
			radiogroup.removeAllViews();
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
			//Declare Variables
			Elements poll;
			TableLayout ResultsTable;
			TextView title;
			
			//Initiate Variables
			title = new TextView(SubmitPoll.this);
			ResultsTable = new TableLayout(SubmitPoll.this);
			
			//Set Table View
			ResultsTable.setColumnStretchable(1, true);
			try {
				poll = doc.select("table.poll");
				
				//Set Title
				title.setText(doc.select("h2").first().text());
				title.setTextSize(15);
				title.setGravity(17); //Center
				layout.addView(title);
				
				//Get Results. Remove and hold Totals
				Elements results = poll.select("tr");
				Element totalvotes = results.get(results.size() - 1);
				results.remove(results.size() - 1);
				
				for(Element res : results) {
					//Declare Variables
					TableRow result;
					TextView ResultTitle, ResultCount;
					String entry, votes;
					TableRow.LayoutParams resulttitle;
					
					//Initiate Variables
					result = new TableRow(SubmitPoll.this);
					ResultTitle = new TextView(SubmitPoll.this);
					ResultCount = new TextView(SubmitPoll.this);
					resulttitle = new TableRow.LayoutParams();
					
					//Set Views
					resulttitle.column = 1;
					ResultTitle.setLayoutParams(resulttitle);
					
					//Get Result Data
					entry = res.select("td").first().text();
					votes = res.select("td").get(3).text() + " | " + res.select("td").get(1).text();
					
					//Set Text
					ResultTitle.setText(entry);
					ResultCount.setText(votes);
					
					//Format Text
					ResultTitle.setTypeface(null, Typeface.BOLD);
					
					result.addView(ResultTitle);
					result.addView(ResultCount);
					
					ResultsTable.addView(result);
				}
				layout.addView(ResultsTable);
				TextView Totals = new TextView(SubmitPoll.this);
				Totals.setText("TOTAL VOTES: " + totalvotes.select("td").get(1).text());
				layout.addView(Totals);
			}catch (NullPointerException e){
				Log.v("POLL", "HOLY SHIT AN ERROR");
			}
			scrollview.addView(layout);
			pd.dismiss();
		}
	}
}
