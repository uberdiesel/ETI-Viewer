package com.android.luelinksviewer;

import java.util.ArrayList;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CustomBaseAdapter extends BaseAdapter {
	 private static ArrayList<Topic> topicList;
	 
	 private LayoutInflater mInflater;

	 public CustomBaseAdapter(Context context, ArrayList<Topic> results) {
		 topicList = results;
		 mInflater = LayoutInflater.from(context);
	 }

	 public int getCount() {
		 return topicList.size();
	 }

	 public Object getItem(int position) {
		 return topicList.get(position);
	 }

	 public long getItemId(int position) {
		 return position;
	 }

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.topicrow, null);
			holder = new ViewHolder();
			holder.txtName = (TextView) convertView.findViewById(R.id.topictitle);
			holder.txtPoster = (TextView) convertView.findViewById(R.id.topicposter);
			holder.txtPostcount = (TextView) convertView.findViewById(R.id.topiccount);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (topicList.get(position).getisSticky())
		{
			SpannableString spannableString = new SpannableString(topicList.get(position).getTitle());
			spannableString.setSpan(
					new UnderlineSpan()/*OBJECT*/
					          , 0/*STARTING POSITION*/
					          , spannableString.length()/*ENDING POSITION*/
					          , 0/*FLAGS*/);
			holder.txtName.setText(spannableString);
		}
		else {
			holder.txtName.setText(topicList.get(position).getTitle());
		}
			
		holder.txtPoster.setText(topicList.get(position).getPoster());
		if(topicList.get(position).getisBookmark()) {
			String postcount;
			postcount = topicList.get(position).getPostcount();
			postcount = postcount + "(+" + topicList.get(position).getPostBookmark() + ")";
			holder.txtPostcount.setText(postcount);

		}
		else {
			holder.txtPostcount.setText(topicList.get(position).getPostcount());
		}
		return convertView;
	 }
	 static class ViewHolder {
		 TextView txtName;
		 TextView txtPoster;
		 TextView txtPostcount;
	}
}
