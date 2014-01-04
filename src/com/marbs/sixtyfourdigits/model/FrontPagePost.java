package com.marbs.sixtyfourdigits.model;

public class FrontPagePost {
	
	private String title;
	private String queryCmd;
	private String queryId;
	private String queryUserid;
	
	public FrontPagePost(String title, String queryCmd, String queryId, String queryUserid) {
		this.title = title;
		this.queryCmd = queryCmd;
		this.queryId = queryId;
		this.queryUserid = queryUserid;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public String getQueryCmd() {
		return this.queryCmd;
	}
	
	public String getQueryId() {
		return this.queryId;
	}
	
	public String getQueryUserid() {
		return this.queryUserid;
	}
}