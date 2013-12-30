package com.marbs.sixtyfourdigits;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {

	ProgressDialog pd;
	Context context = this;
	
	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);
	    
	    new FrontPage().execute();
	  }

	  private class StableArrayAdapter extends ArrayAdapter<String> {

	    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

	    public StableArrayAdapter(Context context, int textViewResourceId,
	        List<String> objects) {
	      super(context, textViewResourceId, objects);
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

	    private class FrontPage extends AsyncTask<Void, Void, Void> {
	    	
	    	ArrayList<String> list;
	    	
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
	    	    
	    	    
	    	    list = new ArrayList<String>();
	    	    
	    		try {
	    			Document doc = Jsoup.connect("http://www.64digits.com").timeout(3000).get();
	    			Elements divs = doc.select("div.middlecontent div.fnt11.fntgrey");
	    			for (Element div : divs) {
	    				list.add(div.text());
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
	    	    final StableArrayAdapter adapter = new StableArrayAdapter(context,
	    	    		android.R.layout.simple_list_item_1, list);
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