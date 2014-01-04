package com.marbs.sixtyfourdigits.model;

public class FrontPagePostBlog extends FrontPagePost {
	private int numComments;
	private String imageUrl;
	
	public FrontPagePostBlog(String title, String queryCmd, String queryId, String queryUserid, int numComments, String imageUrl) {
		super(title, queryCmd, queryId, queryUserid);
		this.numComments = numComments;
		this.imageUrl = imageUrl;
	}
	
	public int getNumComments() {
		return this.numComments;
	}
	
	public String getImageUrl() {
		return this.imageUrl;
	}
}