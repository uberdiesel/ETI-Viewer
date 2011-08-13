package com.android.luelinksviewer;

public class Topic {
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

	 public void setTitle(String title) {
		 this.title = title;
	 }
	 public String getTitle() {
		 return title;
	 }
	 public void setPoster(String poster) {
		 this.poster = poster;
	 }

	 public String getPoster() {
		 return poster;
	 }

	 public void setPostcount(String postcount) {
		 this.postcount = postcount;
	 }
	 public String getPostcount() {
		 return postcount;
	 }
	 public void setPostBookmark(String bookmark) {
		  this.postBookmark = bookmark;
	 }
	 public String getPostBookmark() {
		 return postBookmark;
	 }
	 public void setDate(String date) {
		  this.date = date;
	 }
	 public String getDate() {
		 return date;
	 }
	 
	 public void setisBookmark(Boolean bool) {
		  this.isBookmark = bool;
	 }
	 public Boolean getisBookmark() {
		 return isBookmark;
	 }
	 public void setisTagged(Boolean bool) {
		  this.isTagged = bool;
	 }

	 public Boolean getisTagged() {
		 return isTagged;
	 }
	 public void setbookmarkAddress(String address) {
		 this.bookmarkAddress = address;
	 }

	 public String getbookmarkAddress() {
		 return bookmarkAddress;
	 }
	 public void setAddress(String uri) {
		 this.address = uri;
	 }
	 public String getAddress() {
		 return address;
	 }
	 public void setPage(int value) {
		 this.page = value;
	 }
	 public int getPage() {
		 return page;
	 }
}

