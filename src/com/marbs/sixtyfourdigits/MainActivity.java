package com.marbs.sixtyfourdigits;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

	ProgressDialog pd;
	Context context = this;
	
	// Data for each item on the front page
	public class FrontPageItemData {
		//String image;
		String title;
		String excerpt;
		String author;
		int numComments;
		
		public FrontPageItemData(String title, String author) {
			this.title = title;
			this.author = author;
		}
		
		public String GetTitle() {
			return title;
		}
		
		public String GetAuthor() {
			return author;
		}
	}
	
	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);
	    
	    new FrontPage().execute();
	  }

	  private static class FrontPageItemAdapter extends ArrayAdapter<FrontPageItemData> {
		  
		  private static class ViewHolder {
			  private TextView text1;
			  private TextView text2;
			  
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
				  
				  holder.text1 = (TextView)itemView.findViewById(R.id.firstLine);
				  holder.text2 = (TextView)itemView.findViewById(R.id.secondLine);
				  
				  itemView.setTag(holder);
			  } else {
				  holder = (ViewHolder)itemView.getTag();
			  }
			  
			  holder.text1.setText(item.GetTitle());
			  holder.text2.setText(item.GetAuthor());
			  
			  return itemView;
		  }
	  }
	  
	  /*
	  private class StableArrayAdapter extends ArrayAdapter<String> {

	    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

	    public StableArrayAdapter(Context context, int textViewResourceId, int asdf,
		        List<String> objects) {
		      super(context, textViewResourceId, asdf, objects);
		      for (int i = 0; i < objects.size(); ++i) {
		        mIdMap.put(objects.get(i), i);
		      }
		    }

	    @Override
	    public long getItemId(int position) {
	      String item = getItem(position);
	      return mIdMap.get(item);
	    }

	    @Override
	    public boolean hasStableIds() {
	      return true;
	    }

	  }
	  */

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
	    	    
	    		try {
	    			Document doc = Jsoup.connect("http://www.64digits.com").timeout(3000).get();
	    			Elements frontPageBlogs = doc.select("div.middlecontent div.fnt11.fntgrey");
	    			for (Element blog : frontPageBlogs) {
	    				// Read author
	    				Element author = blog.select("div.fnt10.floatright").first();
	    				//authors.add(author.text());
	    				
	    				// Read blog preview
	    				Element title = blog.select("div.fnt12.fntgrey").first();
	    				//blogPreviews.add(blogPreview.text());
	    				frontPageData.add(new FrontPageItemData(title.text(), author.text()));
	    			}
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		}

	    	    /*
	    	    final StableArrayAdapter adapter = new StableArrayAdapter(context,
	    	    		android.R.layout.simple_list_item_1, list);
	    	    listview.setAdapter(adapter);
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
	    	    //final StableArrayAdapter adapter = new StableArrayAdapter(context,
	    	    //		android.R.layout.simple_list_item_1, list);
	    	    //final StableArrayAdapter adapter = new StableArrayAdapter(context,
	    	    //		R.layout.frontpage_item, R.id.secondLine, authors);
	    	    //listview.setAdapter(adapter);
	    	    
	    	    //listview.setAdapter(new StableArrayAdapter(context, R.layout.frontpage_item, R.id.firstLine, blogPreviews));
	    	    
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