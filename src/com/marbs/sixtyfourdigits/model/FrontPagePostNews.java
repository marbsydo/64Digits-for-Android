package com.marbs.sixtyfourdigits.model;

public class FrontPagePostNews extends FrontPagePost {
	private String date;
	
	public FrontPagePostNews(String title, String queryCmd, String queryId, String queryUserid, String date) {
		super(title, queryCmd, queryId, queryUserid);
		this.date = date;
	}
	
	public String getDate() {
		return this.date;
	}
}