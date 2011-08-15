package com.android.luelinksviewer;

import android.os.Parcel;
import android.os.Parcelable;

public class Topic implements Parcelable{
	 private String title = "";
	 private String poster = "";
	 private String postcount = "";
	 private String postBookmark = "";
	 private String date = "";
	 private String bookmarkAddress = "";
	 private String address;
	 private int page = 1;
	 
	 private Boolean isTagged = false;
	 private Boolean isBookmark = false;
	 private Boolean isSticky = false;

	 public void setTitle(String title) {	this.title = title; }
	 public String getTitle() {	return title;	 }
	 public void setPoster(String poster) {	 this.poster = poster;	 }
	 public String getPoster() {	return poster; }
	 public void setPostcount(String postcount) { this.postcount = postcount;	 }
	 public String getPostcount() {	 return postcount;	 }
	 public void setPostBookmark(String bookmark) {	  this.postBookmark = bookmark;	 }
	 public String getPostBookmark() {	 return postBookmark; }
	 public void setDate(String date) {	  this.date = date;	 }
	 public String getDate() {	 return date; }
	 public void setisBookmark(Boolean bool) {  this.isBookmark = bool;	 }
	 public Boolean getisBookmark() { return isBookmark; }
	 public void setisTagged(Boolean bool) {  this.isTagged = bool;	 }
	 public Boolean getisTagged() {	 return isTagged;	 }
	 public void setisSticky(Boolean bool) {  this.isSticky = bool;	 }
	 public Boolean getisSticky() {	 return isSticky;	 }
	 public void setbookmarkAddress(String address) { this.bookmarkAddress = address; }
	 public String getbookmarkAddress() { return bookmarkAddress; }
	 public void setAddress(String uri) { this.address = uri; }
	 public String getAddress() { return address; }
	 public void setPage(int value) { this.page = value; }
	 public int getPage() {	 return page; }
	 
	 //Methods to pass class
	 public int describeContents() {
		 return 0;
	 }
	 // write your object's data to the passed-in Parcel
	 public void writeToParcel(Parcel out, int flags) {
		 out.writeString(title);
		 out.writeString(poster);
		 out.writeString(postcount);
		 out.writeString(postBookmark);
		 out.writeString(date);
		 out.writeString(bookmarkAddress);
		 out.writeString(address);
		 out.writeInt(page);
		 boolean[] bool = new boolean[3];
		 	bool[0] = isTagged;
		 	bool[1] = isBookmark;
		 	bool[2] = isSticky;
	 	out.writeBooleanArray(bool);
	 }
	 public static final Parcelable.Creator<Topic> CREATOR = new Parcelable.Creator<Topic>() {
        public Topic createFromParcel(Parcel in) {
            return new Topic(in);
        }
        public Topic[] newArray(int size) {
        	return new Topic[size];
        }
	 };
	    // example constructor that takes a Parcel and gives you an object populated with it's values
	 private Topic(Parcel in) {
		 title = in.readString();
		 poster = in.readString();
		 postcount = in.readString();
		 postBookmark = in.readString();
		 date = in.readString();
		 bookmarkAddress = in.readString();
		 address = in.readString();
		 page = in.readInt();
		 boolean[] bool = new boolean[3];
		 bool = in.createBooleanArray();
		 	isTagged = bool[0];
		 	isBookmark = bool[1];
		 	isSticky = bool[2];
	 }
	public Topic() {

	}
}


