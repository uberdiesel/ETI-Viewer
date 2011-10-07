package com.android.luelinksviewer;

import org.jsoup.select.Elements;

import android.graphics.Bitmap;
import android.widget.LinearLayout;

public class Message {
	private String author;
	private String date;
	private String avatarUrl;
	private Bitmap avatar;
	private LinearLayout message;
	
	
	public void setAuthor (String a) {this.author = a; }
	public String getAuthor() {	return this.author; }
	public void setDate (String d) {this.date = d; }
	public String getDate() {return this.date; }
	public void setAvatarUrl (String u) {	this.avatarUrl = u; }
	public String getAvatarUrl() { return this.avatarUrl; }
	public void setAvatar (Bitmap a) { this.avatar = a; }
	public Bitmap getAvatar() {	return this.avatar;	}
}
