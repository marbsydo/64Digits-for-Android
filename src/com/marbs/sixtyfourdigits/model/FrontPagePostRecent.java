package com.marbs.sixtyfourdigits.model;

public class FrontPagePostRecent extends FrontPagePost {
	private String howLongAgo;
	
	public FrontPagePostRecent(String title, String queryCmd, String queryId, String queryUserid, String howLongAgo) {
		super(title, queryCmd, queryId, queryUserid);
		this.howLongAgo = howLongAgo;
	}
	
	public String getHowLongAgo() {
		return this.howLongAgo;
	}
}