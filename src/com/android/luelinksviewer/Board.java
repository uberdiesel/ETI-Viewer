package com.android.luelinksviewer;

public class Board {
	public class DataTopic {
		 private String title = "";
		 private String boardid = "";
		 private int page = 1;

		 public void setTitle(String title) {
			 this.title = title;
		 }
		 public String getBoardid() {
			 return boardid;
		 }
		 public void setBoardid(String id) {
			 this.boardid = id;
		 }
		 public String getTitle() {
			 return title;
		 }
		 public void setPage(int value) {
			 this.page = value;
		 }
		 public int getPage() {
			 return page;
		 }
	}
}
