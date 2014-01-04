package com.marbs.sixtyfourdigits.model;

import java.util.ArrayList;
import java.util.List;

public class FrontPageFeed {

	// Types of frontpage items
	public static final int VIEWTYPE_NUM = 5;
	public static final int VIEWTYPE_BLOG = 0;
	public static final int VIEWTYPE_NEWS = 1;
	public static final int VIEWTYPE_RECENT = 2;
	public static final int VIEWTYPE_NEXT = 3;
	public static final int VIEWTYPE_DIVIDER = 4;
	
	// Info about the post, including its type
	public class FrontPagePostInfo {
		private FrontPagePost post;
		private int type;
		
		public FrontPagePostInfo(FrontPagePost post, int type) {
			this.post = post;
			this.type = type;
		}
		
		public FrontPagePost getPost() {
			return post;
		}
		
		public int getType() {
			return type;
		}
	}

	private List<FrontPagePostInfo> posts;

	public FrontPageFeed() {
		this.posts = new ArrayList<FrontPagePostInfo>();
	}

	public void addPost(FrontPagePost post, int type) {
		this.posts.add(new FrontPagePostInfo(post, type));
	}

	public FrontPagePost getPost(int position) {
		return this.posts.get(position).getPost();
	}
	
	public void removeLastPost() {
		this.posts.remove(this.posts.size() - 1);
	}

	public int getType(int position) {
		return this.posts.get(position).getType();
	}

	public int size() {
		return posts.size();
	}
}