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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MainActivity extends Activity {

	public final static String SITE_PATH = "http://www.64digits.com/index.php?id=0&cmd=&page=0";
	
	ProgressDialog pd;
	public Context context = this;
	
	// Data for each item on the front page
	public class FrontPageItemData {
		String imageUrl;
		String title;
		String excerpt;
		String author;
		int numComments;
		
		public FrontPageItemData(String title, String author, int numComments, String imageUrl) {
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
	}
	
	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);
	    
	    new FrontPage().execute();
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
		  
		  public FrontPageItemAdapter(Context context, int textViewResourceId, List<FrontPageItemData> itemData) {
			  super(context, textViewResourceId, itemData);
			  
			  this.inflater = LayoutInflater.from(context);
		  }
		  
		  @Override
		  public View getView(final int position, final View convertView, final ViewGroup parent) {
			  View itemView = convertView;
			  ViewHolder holder = null;
			  final FrontPageItemData item = getItem(position);
			  if (null == itemView) {
				  itemView = this.inflater.inflate(R.layout.frontpage_item, parent, false);
				  
				  holder = new ViewHolder();
				  
				  holder.textViewTitle = (TextView)itemView.findViewById(R.id.textTitle);
				  holder.textViewAuthor = (TextView)itemView.findViewById(R.id.textAuthor);
				  holder.imageViewAvatar = (ImageView)itemView.findViewById(R.id.imageAvatar);
				  
				  itemView.setTag(holder);
			  } else {
				  holder = (ViewHolder)itemView.getTag();
			  }

			  // Set the title text
			  holder.textViewTitle.setText(item.GetTitle());
			  
			  // Set the author and number of comments text
			  int num = item.GetNumComments();
			  holder.textViewAuthor.setText(item.GetAuthor() + " (" + num + " comment" + (num == 1 ? "" : "s") + ")");
			  
			  // Tell Picasso to load the avatar into the image view
			  Picasso.with(context).load(item.GetImageUrl()).into(holder.imageViewAvatar);
			  
			  return itemView;
		  }
	  }

	    private class FrontPage extends AsyncTask<Void, Void, Void> {
	    	
	    	//ArrayList<String> authors;
	    	//ArrayList<String> blogPreviews;
	    	ArrayList<FrontPageItemData> frontPageData;
	    	
	    	@Override
	    	protected void onPreExecute() {
	    		super.onPreExecute();
	    		pd = new ProgressDialog(MainActivity.this);
	    		pd.setTitle("Retrieve 64D Front Page");
	    		pd.setMessage("Loading...");
	    		pd.setIndeterminate(false);
	    		pd.show();
	    	}
	    	
	    	@Override
	    	protected Void doInBackground(Void... params) {
	    	    
	    	    
	    	    //authors = new ArrayList<String>();
	    	    //blogPreviews = new ArrayList<String>();
	    		frontPageData = new ArrayList<FrontPageItemData>();
	    	    
	    		boolean errorOccurred = false;
	    		String errorString = "";
	    		
	    		Connection.Response response;
	    		try {
	    			response = Jsoup.connect(SITE_PATH)
							.userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
							.timeout(10000)
							.execute();
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
	    	    			Elements frontPageBlogs = doc.select("div.middlecontent div.fnt11.fntgrey");
	    	    			for (Element blog : frontPageBlogs) {
	    	    				try {
	    	    					// Title
	    	    					String title = blog.select("a.lnknodec.fntblue.fntbold.fnt15").first().text();
	    	    					
	    	    					// Author
	    	    					String author = blog.select("a.fntblue").get(1).text();
	    	    					
	    	    					// Number of comments
	    	    					String numCommentsString = blog.select("a.fntblue").get(2).text();
	    	    					int numComments = -1;
	    	    					Pattern p = Pattern.compile("\\d+");
	    	    					Matcher m = p.matcher(numCommentsString);
	    	    					if (m.find()) {
	    	    						numComments = Integer.parseInt(m.group());
	    	    					}
	    	    					
	    	    					// Image URL
	    	    					String imageUrl = blog.select("img").first().absUrl("src");
	    	    					// Encode the URL so that e.g. spaces become %20s
	    	    					URL url = new URL(imageUrl);
	    	    					URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
	    	    					imageUrl = uri.toURL().toString();
	    	    					
	    	    					frontPageData.add(new FrontPageItemData(title, author, numComments, imageUrl));
	    	    				} catch (Exception e) {
	    	    					System.out.println("Error: Selecting threw error: " + e);
	    	    					errorOccurred = true;
	    	    					errorString = "Could not select parsed data";
	    	    				}
	    	    			}
	    				}
	    			} else {
	    				System.out.println("Error: Received status code: " + statusCode);
						errorOccurred = true;
						errorString = "Received status code " + statusCode;
	    			}
	    		}
	    		
	    		if (errorOccurred) {
	    			frontPageData.add(new FrontPageItemData("Error!", errorString, -1, ""));
	    		}
	    		
	    		/*
	    		try {
	    			Document doc = Jsoup.connect("http://www.64digits.com").timeout(3000).get();
	    			Elements frontPageBlogs = doc.select("div.middlecontent div.fnt11.fntgrey");
	    			for (Element blog : frontPageBlogs) {
	    				Element title = blog.select("div.lnknodec.fntblue.fntbold.fnt15").first();
	    				Element author = blog.select("div.fnt10.floatright").first();
	    				frontPageData.add(new FrontPageItemData(title.text(), author.text()));
	    			}
	    		} catch (IOException e) {
	    			//frontPageData.add(new FrontPageItemData("Error", "more errors!"));
	    			e.printStackTrace();
	    		}
	    		*/
	    		
	    	    /*
	    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

	      @Override
	      public void onItemClick(AdapterView<?> parent, final View view,
	          int position, long id) {
	        final String item = (String) parent.getItemAtPosition(position);
	        view.animate().setDuration(2000).alpha(0)
	            .withEndAction(new Runnable() {
	              @Override
	              public void run() {
	                list.remove(item);
	                adapter.notifyDataSetChanged();
	                view.setAlpha(1);
	              }
	            });
	      }

	    });
	    	     */
	    		return null;
	    	}
	    	
	    	@Override
	    	protected void onPostExecute(Void result) {
	    		/*
	    		Button titleButton = (Button) findViewById(R.id.get_title);
	    		titleButton.setText(title);
	    		EditText editText = (EditText) findViewById(R.id.edit_message);
	    		editText.setText(desc);
	    		*/
	    		final ListView listview = (ListView) findViewById(R.id.listview);
	    		final FrontPageItemAdapter adapter = new FrontPageItemAdapter(context, R.layout.frontpage_item, frontPageData);
	    		listview.setAdapter(adapter);
	    		
	    	    pd.dismiss();
	    	}
	    }
	  
	}

/*
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
*/