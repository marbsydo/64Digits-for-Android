package com.marbs.sixtyfourdigits;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.marbs.sixtyfourdigits.model.FrontPageFeed;
import com.marbs.sixtyfourdigits.model.FrontPagePost;
import com.marbs.sixtyfourdigits.model.FrontPagePostBlog;
import com.marbs.sixtyfourdigits.model.FrontPagePostDivider;
import com.marbs.sixtyfourdigits.model.FrontPagePostNext;
import com.squareup.picasso.Picasso;

public class MainActivity extends ActionBarActivity {

	LayoutInflater inflater;
	FrontPageFeed frontPageFeed;
	
	int page;
	int scrollToAfterRegenerate = -1;
	ProgressDialog pd;
	public Context mainActivityContext = this;
	public MainActivity mainActivity = this;

	public MainActivity() {
		super();
		frontPageFeed = new FrontPageFeed();
		page = 0;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_refresh:
			refreshFrontPage();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void refreshFrontPage() {
		resetFrontPage();
		page = 0;
		scrollToAfterRegenerate = -1;
		new FrontPage(page).execute();
	}

	public void resetFrontPage() {
		//TODO
		//frontPageData.clear();
	}

	public void addFrontPageItem(FrontPageItemData frontPageItem, int type) {
		//FrontPagePost post;
		
		switch (type) {
		case FrontPageFeed.VIEWTYPE_BLOG:
			//TODO: Move this out
			FrontPagePost post;
			
			post = (FrontPagePost) new FrontPagePostBlog(
					frontPageItem.title,
					"comments",
					frontPageItem.blogId + "",
					frontPageItem.author,
					frontPageItem.numComments,
					frontPageItem.imageUrl);
			
			//TODO: Move this out
			frontPageFeed.addPost(post, type);
			break;
		case FrontPageFeed.VIEWTYPE_NEWS:
			//TODO
			break;
		case FrontPageFeed.VIEWTYPE_RECENT:
			//TODO
			break;
		default:
			//TODO
			break;
		}
		
		// frontPageFeed.addPost(post, type);
	}

	public void addFrontPageItemError(String errorMessage) {
		//TODO
		//addFrontPageItem(new FrontPageItemData(FrontPageItemData.Type.ERROR,
		//		"Error!", errorMessage, -1, "", -1));
	}

	public void addFrontPageItemNext() {
		FrontPagePost post = (FrontPagePost) new FrontPagePostNext("More");
		frontPageFeed.addPost(post, FrontPageFeed.VIEWTYPE_NEXT);
	}

	public void addFrontPageItemDivider(int page) {
		FrontPagePost post = (FrontPagePost) new FrontPagePostDivider("Page 3");
		frontPageFeed.addPost(post, FrontPageFeed.VIEWTYPE_DIVIDER);
	}

	public void removeLastFrontPageItem() {
		//TODO
		//frontPageData.remove(frontPageData.size() - 1);
	}

	public void regenerateFrontPage() {
		
		final ListView listview = (ListView) findViewById(R.id.listview);
		
		final FrontPageAdapter adapater = new FrontPageAdapter();
		listview.setAdapter(adapater);
		/*
		final FrontPageItemAdapter adapter = new FrontPageItemAdapter(this,
				R.layout.frontpage_item, frontPageData);
		listview.setAdapter(adapter);
		*/
		
		// This makes all the items clickable
		/*
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {
				if (frontPageData.get(position).IsNext()) {
					// Clicked the next button (which is always at the bottom)

					// Work out what position we should scroll to when the list
					// has been regenerated
					// This is done because when the list is regenerated, the
					// view scroll back to the top
					// TODO: It might be possible to avoid this messy solution
					// by regenerating the list
					// in a neater way that doesn't have the side effect of
					// shifting the view
					int visibleChildCount = (listview.getLastVisiblePosition() - listview
							.getFirstVisiblePosition()) - 1;
					scrollToAfterRegenerate = position - visibleChildCount;

					// Remove the last item, which is this button that was just
					// pressed
					removeLastFrontPageItem();

					// Create a divider and load the next page
					page++;
					addFrontPageItemDivider(page);
					new FrontPage(page).execute();

				} else if (frontPageData.get(position).IsNormal()) {
					// Go to the blog it points to
					Intent intentBlog = new Intent(getApplicationContext(), BlogActivity.class);
					intentBlog.putExtra("blogAuthor", frontPageData.get(position).GetAuthor());
					intentBlog.putExtra("blogId", frontPageData.get(position).GetBlogId() + ""); //TODO: We have converted blogId from string to int to string. Is this silly?
					startActivity(intentBlog);
				}
			}
		});
		*/

		// Scroll to the target position
		if (scrollToAfterRegenerate >= 0) {
			listview.setSelection(scrollToAfterRegenerate);
		}
		
	}

	// Data for each item on the front page
	public static class FrontPageItemData {

		private static enum Type {
			NORMAL, ERROR, NEXT, DIVIDER;
		}

		Type type;
		String imageUrl;
		String title;
		String excerpt;
		String author;
		int numComments;
		int blogId;

		public FrontPageItemData(Type type, int page) {
			this.type = type;
			this.title = "";
			this.author = "";
			this.numComments = page;
			this.imageUrl = "";
			this.blogId = -1;
		}

		public FrontPageItemData(Type type, String title, String author,
				int numComments, String imageUrl, int blogId) {
			this.type = type;
			this.title = title;
			this.author = author;
			this.numComments = numComments;
			this.imageUrl = imageUrl;
			this.blogId = blogId;
		}

		public FrontPageItemData(String title, String author, int numComments,
				String imageUrl, int blogId) {
			this.type = Type.NORMAL;
			this.title = title;
			this.author = author;
			this.numComments = numComments;
			this.imageUrl = imageUrl;
			this.blogId = blogId;
		}

		public String GetTitle() {
			return title;
		}

		public String GetAuthor() {
			return author;
		}

		public int GetNumComments() {
			return numComments;
		}

		public String GetImageUrl() {
			return imageUrl;
		}
		
		public int GetBlogId() {
			return blogId;
		}

		public boolean IsNormal() {
			return type == Type.NORMAL;
		}

		public boolean IsNext() {
			return type == Type.NEXT;
		}

		public boolean IsDivider() {
			return type == Type.DIVIDER;
		}

		public boolean IsError() {
			return type == Type.ERROR;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		refreshFrontPage();
	}

	/*
	private class FrontPageItemAdapter extends ArrayAdapter<FrontPageItemData> {

		private class ViewHolder {
			private TextView textViewTitle;
			private TextView textViewAuthor;
			private ImageView imageViewAvatar;
			private Button blogViewButton;

			public ViewHolder() {
				// Do nothing
			}
		}

		private final LayoutInflater inflater;

		public FrontPageItemAdapter(Context context, int textViewResourceId,
				List<FrontPageItemData> itemData) {
			super(context, textViewResourceId, itemData);

			this.inflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(final int position, final View convertView,
				final ViewGroup parent) {
			View itemView = convertView;
			ViewHolder holder = null;
			final FrontPageItemData item = getItem(position);
			if (null == itemView) {
				itemView = this.inflater.inflate(R.layout.frontpage_item,
						parent, false);

				holder = new ViewHolder();

				holder.textViewTitle = (TextView) itemView
						.findViewById(R.id.textTitle);
				holder.textViewAuthor = (TextView) itemView
						.findViewById(R.id.textAuthor);
				holder.imageViewAvatar = (ImageView) itemView
						.findViewById(R.id.imageAvatar);
				holder.blogViewButton = (Button) itemView
						.findViewById(R.id.buttonBlog);

				itemView.setTag(holder);
			} else {
				holder = (ViewHolder) itemView.getTag();
			}

			if (item.IsNormal()) {
				// Set the title text
				holder.textViewTitle.setText(item.GetTitle());
				
				// Set the author text
				holder.textViewAuthor.setText(item.GetAuthor());
				
				// Set the button text
				holder.blogViewButton.setText(item.GetNumComments() + "");

				// Tell Picasso to load the avatar into the image view
				if (item.GetImageUrl().length() > 0) {
					holder.imageViewAvatar
							.setImageResource(android.R.color.white);
					Picasso.with(mainActivityContext).load(item.GetImageUrl())
							.into(holder.imageViewAvatar);
				}
			} else if (item.IsNext()) {
				// Set the title text
				holder.textViewTitle.setText("");
				holder.textViewAuthor.setText("Load next page...");
				holder.imageViewAvatar
						.setImageResource(android.R.color.transparent);
			} else if (item.IsDivider()) {
				// Set the title text
				holder.textViewTitle.setText("");
				holder.textViewAuthor.setText("Page #" + item.GetNumComments()); // Page
																					// is
																					// stored
																					// in
																					// numComments
				holder.imageViewAvatar
						.setImageResource(android.R.color.transparent);
			} else if (item.IsError()) {
				// Set the error title and message
				holder.textViewTitle.setText(item.GetTitle());
				holder.textViewAuthor.setText(item.GetAuthor());
				holder.imageViewAvatar
						.setImageResource(android.R.color.transparent);
			}

			return itemView;
		}
	}
	*/
	
	class FrontPageAdapter extends BaseAdapter {
		
		public FrontPageAdapter() {
			inflater = LayoutInflater.from(MainActivity.this);
		}
		
		@Override
		public int getCount() {
			return frontPageFeed.size();
		}
		
		@Override
		public FrontPagePost getItem(int position) {
			return frontPageFeed.getPost(position);
		}
		
		public int getType(int position) {
			return frontPageFeed.getType(position);
		}
		
		@Override
		public long getItemId(int position) {
			//TODO: Is this needed?
			return 0;
		}
		
		@Override
		public int getItemViewType(int position) {
			return frontPageFeed.getType(position);
		}
		
		@Override
		public int getViewTypeCount() {
			// Three types: blog, news, recent
			return FrontPageFeed.VIEWTYPE_NUM;
		}
		
		public class ViewHolder {
			private TextView t1;
			private TextView t2;
			private ImageView i1;
			private Button b1;
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View itemView = convertView;
			ViewHolder holder = null;
			
			// Get data about the current item
			final FrontPagePost item = getItem(position);
			final int type = getType(position);
			
			if (null == itemView) {
				holder = new ViewHolder();
				
				switch (type) {
				case FrontPageFeed.VIEWTYPE_BLOG:
					itemView = inflater.inflate(R.layout.frontpage_item, parent, false);
					holder.t1 = (TextView) itemView.findViewById(R.id.textTitle);
					holder.t2 = (TextView) itemView.findViewById(R.id.textAuthor);
					holder.i1 = (ImageView) itemView.findViewById(R.id.imageAvatar);
					holder.b1 = (Button) itemView.findViewById(R.id.buttonBlog);
					break;
				case FrontPageFeed.VIEWTYPE_DIVIDER:
					itemView = inflater.inflate(R.layout.frontpage_divider, parent, false);
					holder.t1 = (TextView) itemView.findViewById(R.id.textDivider);
					break;
				case FrontPageFeed.VIEWTYPE_NEXT:
					itemView = inflater.inflate(R.layout.frontpage_next, parent, false);
					holder.t1 = (TextView) itemView.findViewById(R.id.textNext);
					break;
				}

				// Cache the holder in the tag
				// This increases performance by not repeatedly calling findViewById
				itemView.setTag(holder);
			} else {
				// Retrieve the holder from the tag
				holder = (ViewHolder) itemView.getTag();
			}

			switch (type) {
			case FrontPageFeed.VIEWTYPE_BLOG:
				FrontPagePostBlog itemBlog = (FrontPagePostBlog) item;
				holder.t1.setText(itemBlog.getTitle());
				holder.t2.setText(itemBlog.getQueryUserid());
				holder.b1.setText(itemBlog.getNumComments() + "");
				// Tell Picasso to load the avatar into the image view
				if (itemBlog.getImageUrl().length() > 0) {
					holder.i1.setImageResource(android.R.color.white);
					Picasso.with(mainActivityContext).load(itemBlog.getImageUrl()).into(holder.i1);
				}
				break;
			case FrontPageFeed.VIEWTYPE_DIVIDER:
				FrontPagePostDivider itemDivider = (FrontPagePostDivider) item;
				holder.t1.setText(itemDivider.getTitle());
				break;
			case FrontPageFeed.VIEWTYPE_NEXT:
				FrontPagePostNext itemNext = (FrontPagePostNext) item;
				holder.t1.setText(itemNext.getTitle());
				break;
			}

			/* else if (type == FrontPageFeed.) {
				// Set the title text
				holder.textViewTitle.setText("");
				holder.textViewAuthor.setText("Load next page...");
				holder.imageViewAvatar
						.setImageResource(android.R.color.transparent);
			} else if (item.IsDivider()) {
				// Set the title text
				holder.textViewTitle.setText("");
				holder.textViewAuthor.setText("Page #" + item.GetNumComments());
				holder.imageViewAvatar
						.setImageResource(android.R.color.transparent);
			}
			*/
			return itemView;
		}
		
	}

	// Scrapes data off the front page and add the data to the feed
	//TODO: Make this work with other types (news, recent)
	private class FrontPage extends AsyncTask<Void, Void, Void> {

		ArrayList<FrontPageItemData> frontPageData;
		int page;

		public FrontPage(int page) {
			this.page = page;
		}

		public String generateFrontPageUrl(int page) {
			return "http://www.64digits.com/index.php?id=0&cmd=&page=" + page;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(MainActivity.this);
			pd.setTitle("Retrieve 64D Front Page");
			pd.setMessage("Loading page " + this.page + "...");
			pd.setIndeterminate(false);
			pd.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			frontPageData = new ArrayList<FrontPageItemData>();

			boolean errorOccurred = false;
			String errorString = "";

			Connection.Response response;
			try {
				response = Jsoup
						.connect(generateFrontPageUrl(this.page))
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
				int statusCode = response.statusCode();
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
						//TODO: This should be in a try/catch
						Elements frontPageBlogs = doc
								.select("div.middlecontent div.fnt11.fntgrey");
						for (Element blog : frontPageBlogs) {
							try {
								// Title
								String title = blog
										.select("a.lnknodec.fntblue.fntbold.fnt15")
										.first().text();

								// Author
								String author = blog.select("a.fntblue").get(1)
										.text();

								// Number of comments
								String numCommentsString = blog
										.select("a.fntblue").get(2).text();
								int numComments = -1;
								Pattern p = Pattern.compile("\\d+");
								Matcher m = p.matcher(numCommentsString);
								if (m.find()) {
									numComments = Integer.parseInt(m.group());
								}

								// Image URL
								String imageUrl = blog.select("img").first()
										.absUrl("src");
								// Encode the URL so that e.g. spaces become
								// %20s
								URL url = new URL(imageUrl);
								URI uri = new URI(url.getProtocol(),
										url.getUserInfo(), url.getHost(),
										url.getPort(), url.getPath(),
										url.getQuery(), url.getRef());
								imageUrl = uri.toURL().toString();
								
								// BlogId
								String blogUrl = blog.select("a").get(2).absUrl("href");
								int blogId = -1;
								try {
									Uri blogUri = Uri.parse(blogUrl);
									blogId = Integer.parseInt(blogUri.getQueryParameter("id"));
								} catch (Exception e) {
									System.out.println("Error: Could not parse blog ID");
									errorOccurred = true;
									errorString = "Could not parse blog ID";
								}

								frontPageData.add(new FrontPageItemData(title,
										author, numComments, imageUrl, blogId));
							} catch (Exception e) {
								System.out
										.println("Error: Selecting threw error: "
												+ e);
								errorOccurred = true;
								errorString = "Could not select parsed data";
							}
						}
					}
				} else {
					System.out.println("Error: Received status code: "
							+ statusCode);
					errorOccurred = true;
					errorString = "Received status code " + statusCode;
				}
			}

			if (errorOccurred) {
				mainActivity.addFrontPageItemError(errorString);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			for (int i = 0; i < frontPageData.size(); ++i) {
				mainActivity.addFrontPageItem(frontPageData.get(i), FrontPageFeed.VIEWTYPE_BLOG);
			}
			
			mainActivity.addFrontPageItemNext();
			mainActivity.regenerateFrontPage();

			pd.dismiss();
		}
	}
}