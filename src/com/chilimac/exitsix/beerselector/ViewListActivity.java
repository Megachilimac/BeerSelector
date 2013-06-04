package com.chilimac.exitsix.beerselector;

import java.util.ArrayList;
import java.util.List;
import com.google.analytics.tracking.android.EasyTracker;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ViewListActivity extends Activity {

	private ListView myBeerList;
	private BeerDBHandler dbHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_list);
	    EasyTracker.getInstance().activityStart(this); 
		
	    dbHandler = new BeerDBHandler(this);     
		myBeerList = (ListView) findViewById(R.id.listView1);
		displayListView();
	}

	private void displayListView()
	{
	
		List<Beer> beerList = new ArrayList<Beer>();
		
		beerList = dbHandler.getAllBeers();
		
		ArrayAdapter<Beer> arrayAdapter =
				new ArrayAdapter<Beer>(this,R.layout.mylist,beerList);  			

		myBeerList.setAdapter(arrayAdapter);
		
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
	
	public void closeList(View view)
	{
		EasyTracker.getInstance().activityStop(this);	
		finish();
	
	}

}
