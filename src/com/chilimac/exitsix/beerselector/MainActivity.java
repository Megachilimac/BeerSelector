package com.chilimac.exitsix.beerselector;
 
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.analytics.tracking.android.EasyTracker;

/**
 *  * 
 * @author Jason McKee
 * @version 2.0
 * 
 *
 */
public class MainActivity extends Activity {
	
	ArrayList<String> beerList = new ArrayList<String>();
	    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initializeEverybody();
        configureView();
        EasyTracker.getInstance().activityStart(this);         
        startService(new Intent(this,DataFeedService.class));
    
    }

	@Override
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(this);
	}
		

	
	private void configureView() {
        
	
	}

	private void initializeEverybody() {


	}
	
    
	
	public void listBeers(View view)
	{
		EasyTracker.getTracker().sendEvent("ui_action", "button_press", "list_beers", (long) 1);
		Intent intent = new Intent(this,ViewListActivity.class);
		startActivity(intent);
	}


}