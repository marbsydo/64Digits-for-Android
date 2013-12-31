package com.marbs.sixtyfourdigits;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MainActivity extends Activity {

	ArrayList<FrontPageItemData> frontPageData;
	int page;
	ProgressDialog pd;
	public Context mainActivityContext = this;
	public MainActivity mainActivity = this;

	public MainActivity() {
		super();
		frontPageData = new ArrayList<FrontPageItemData>();
		page = 0;
	}
	
	public void addFrontPageItem(FrontPageItemData frontPageItem) {
		frontPageData.add(frontPageItem);
	}
	
	public void addFrontPageItemError(String errorMessage) {
		addFrontPageItem(new FrontPageItemData(FrontPageItemData.Type.ERROR, "Error!", errorMessage, -1, ""));
	}
	
	public void addFrontPageItemNext() {
		addFrontPageItem(new FrontPageItemData(FrontPageItemData.Type.NEXT, -1));
	}
	
	public void addFrontPageItemDivider(int page) {
		addFrontPageItem(new FrontPageItemData(FrontPageItemData.Type.DIVIDER, page));
	}
	
	public void removeLastFrontPageItem() {
		frontPageData.remove(frontPageData.size() - 1);
	}
	
	public void regenerateFrontPage() {
		final ListView listview = (ListView) findViewById(R.id.listview);
		final FrontPageItemAdapter adapter = new FrontPageItemAdapter(
				this, R.layout.frontpage_item, frontPageData);
		listview.setAdapter(adapter);
		
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
				if (frontPageData.get(position).IsNext()) {
					// Clicked the next button (which is always at the bottom)
					removeLastFrontPageItem();
					page++;
					addFrontPageItemDivider(page);
					new FrontPage(page).execute();
					
				}
			}
		});
	}
	
	// Data for each item on the front page
	public static class FrontPageItemData {
		
		private static enum Type {
			NORMAL,
			ERROR,
			NEXT,
			DIVIDER;
		}
		
		Type type;
		String imageUrl;
		String title;
		String excerpt;
		String author;
		int numComments;

		public FrontPageItemData(Type type, int page) {
			this.type = type;
			this.title = "";
			this.author = "";
			this.numComments = page;
			this.imageUrl = "";
		}
		
		public FrontPageItemData(Type type, String title,
				String author, int numComments, String imageUrl) {
			this.type = type;
			this.title = title;
			this.author = author;
			this.numComments = numComments;
			this.imageUrl = imageUrl;
		}
		
		public FrontPageItemData(String title, String author, int numComments,
				String imageUrl) {
			this.type = Type.NORMAL;
			this.title = title;
			this.author = author;
			this.numComments = numComments;
			this.imageUrl = imageUrl;
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

		new FrontPage(page).execute();
	}

	private class FrontPageItemAdapter extends ArrayAdapter<FrontPageItemData> {

		private class ViewHolder {
			private TextView textViewTitle;
			private TextView textViewAuthor;
			private ImageView imageViewAvatar;

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

				itemView.setTag(holder);
			} else {
				holder = (ViewHolder) itemView.getTag();
			}

			if (item.IsNormal()) {
				// Set the title text
				holder.textViewTitle.setText(item.GetTitle());
				
				// Set the author and number of comments text
				int num = item.GetNumComments();
				holder.textViewAuthor.setText(item.GetAuthor() + " (" + num
						+ " comment" + (num == 1 ? "" : "s") + ")");

				// Tell Picasso to load the avatar into the image view
				if (item.GetImageUrl().length() > 0) {
					holder.imageViewAvatar.setImageResource(android.R.color.white);
					Picasso.with(mainActivityContext).load(item.GetImageUrl())
							.into(holder.imageViewAvatar);
				}
			} else if (item.IsNext()) {
				// Set the title text
				holder.textViewTitle.setText("");
				holder.textViewAuthor.setText("Load next page...");
				holder.imageViewAvatar.setImageResource(android.R.color.transparent);
			} else if (item.IsDivider()) {
				// Set the title text
				holder.textViewTitle.setText("");
				holder.textViewAuthor.setText("Page #" + item.GetNumComments()); // Page is stored in numComments
				holder.imageViewAvatar.setImageResource(android.R.color.transparent);
			} else if (item.IsError()) {
				// Set the error title and message
				holder.textViewTitle.setText(item.GetTitle());
				holder.textViewAuthor.setText(item.GetAuthor());
				holder.imageViewAvatar.setImageResource(android.R.color.transparent);
			}

			return itemView;
		}
	}

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

								frontPageData.add(new FrontPageItemData(title,
										author, numComments, imageUrl));
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
				mainActivity.addFrontPageItem(frontPageData.get(i));
			}
			mainActivity.addFrontPageItemNext();
			mainActivity.regenerateFrontPage();

			pd.dismiss();
		}
	}
}