package com.marbs.sixtyfourdigits;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class BlogActivity extends Activity {

	public BlogActivity blogActivity = this;
	
	TextView blogTextView;
	TextView blogTitleView;
	ProgressDialog pd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blog);
		// Show the Up button in the action bar.
		setupActionBar();
		
		blogTextView = (TextView) this.findViewById(R.id.blog_content);
		blogTextView.setText("Doing stuff...");
		blogTitleView = (TextView) this.findViewById(R.id.blog_title);
		blogTitleView.setText("No title yet...");
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String blogAuthor = extras.getString("blogAuthor");
			String blogId = extras.getString("blogId");
			
			(new Blog(blogAuthor, blogId)).execute();
		} else {
			blogTextView.setText("No blog author or ID supplied");
		}
		
		setTitle("Blog");
	}
	
	public void setBlogInfo(final String blogTitle, final String blogText) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				blogTextView.setText(Html.fromHtml(blogText));
				blogTitleView.setText(blogTitle);
			}
		});	
	}

	private class Blog extends AsyncTask<Void, Void, Void> {

		String blogAuthor;
		String blogId;
		
		String newBlogText;
		
		public Blog(String blogAuthor, String blogId) {
			this.blogAuthor = blogAuthor;
			this.blogId = blogId;
		}

		public String generateBlogUrl(String blogAuthor, String blogId) {
			return "http://www.64digits.com/users/index.php?userid=" + blogAuthor + "&cmd=comments&id=" + blogId;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(BlogActivity.this);
			pd.setTitle("Loading blog");
			pd.setMessage("Fetching bits and pieces...");
			pd.setIndeterminate(false);
			pd.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			
			boolean errorOccurred = false;
			String errorString = "";

			newBlogText = "This should not be shown";
			String blogText = "This should never be visible";
			String blogTitle = "No title yet";

			Connection.Response response;
			try {
				response = Jsoup
						.connect(generateBlogUrl(this.blogAuthor, this.blogId))
						.userAgent(
								"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
						.timeout(10000).execute();
			} catch (IOException e) {
				response = null;
				System.out.println("Error: could not connect");
				errorOccurred = true;
				errorString = "Could not connect";
				e.printStackTrace();
			}

			if (response != null) {
				int statusCode = -1;
				try {
					statusCode = response.statusCode();
				} catch (Exception e) {
					System.out.println("Could not get a status code");
				}

				if (statusCode == 200) { // OK
					Document doc;
					try {
						doc = response.parse();
					} catch (IOException e) {
						doc = null;
						System.out.println("Error: Could not parse data");
						errorOccurred = true;
						errorString = "Could not parse data";
						e.printStackTrace();
					}
					
					if (doc != null) {
						// Read blog
						try {
							//Elements blogWrappers = doc.select("div.blog_" + blogId + ".blog_wrapper");
							//Elements blogWrappers = doc.select("middlecontent");
							//for (Element blogWrapper : blogWrappers) {
							//	t = t + "<<<" + blogWrapper.text() + ">>>";
							//}
							//t = blogWrapper.text();
							//t = blogWrapper.select("div.fnt14").first().text();
							
							//t = doc.select("div.blog_wrapper").first().text();
							
							Element blogWrapper = doc.select("div.blog_wrapper").first();
							blogTitle = blogWrapper.select("span").get(0).text();
							blogText = blogWrapper.select("div").get(3).html();

						} catch (Exception e) {
							System.out.println("Error: Selecting threw error: "+ e);
							errorOccurred = true;
							errorString = "Could not parse blog";
						}
					}
				} else {
					System.out.println("Error: Received status code: "
							+ statusCode);
					errorOccurred = true;
					errorString = "Received status code " + statusCode;
				}
			}

			try {
				blogActivity.setBlogInfo(blogTitle, blogText);
			} catch (Exception e) {
				System.out.println("Error: Setting blog text threw error: "+ e);
			}
			
			if (errorOccurred) {
				newBlogText = errorString;
			} else {
				newBlogText = blogText;
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			pd.dismiss();
		}
	}
	
	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.blog, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
