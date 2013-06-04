package com.chilimac.exitsix.beerselector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

public class DataFeedService extends Service {

	private static final String TAG = "DATAFEED_SERVICE";
	List<Beer> theBeers = new ArrayList<Beer>();
	
	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       
		try {
			URL url = new URL("http://www.taplister.com/bars/4e308ab2aeb7f1133a023787");
			
		
			downloadTask dl = new downloadTask();
			dl.execute(url);
			
		} catch (MalformedURLException e) {
			
			e.printStackTrace();
		}
		
        return Service.START_FLAG_REDELIVERY;
    }

	private class downloadTask extends AsyncTask<URL, Void, Void>
	{
		
		@Override
		protected Void doInBackground(URL... params) {
	
		try
		{
			
			
			URLConnection connection = params[0].openConnection();
			HttpURLConnection httpConnection = (HttpURLConnection)connection;
			
			int responseCode = httpConnection.getResponseCode();
			if(responseCode == HttpURLConnection.HTTP_OK)
			{
				InputStream in = httpConnection.getInputStream();
				processStream(in);
				String PREFS_NAME = "e6beerlist";
				
			    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			    SharedPreferences.Editor editor = settings.edit();
			    int x = 1;  // simple index
			    for(Beer theBeer : theBeers)
			    {			    	
			    	editor.putString(Integer.toString(x),theBeer.getName());
			    	x++;
			    }

			    // Commit the edits!
			    editor.commit();
				
			}
		}
		catch (MalformedURLException e)
		{
			Log.d(TAG, "Malformed URL.");
		}
		catch(IOException e)
		{
			Log.d(TAG, "IO Exception.");
		}
		return null;
	
		
	}

private void processStream(InputStream in) {
		
		BeerDBHandler dbHandler = BeerSelectorApplication.getInstance().getDatabaseHandler();
		
		
		try {
			String StringFromInputStream = convertStreamToString(in);
			
			Document doc = Jsoup.parse(StringFromInputStream);
			Elements brewers = doc.select("div[id^=beer-]");
			
			for(Element brewer : brewers)
			{
				
				Element brewerName = brewer.select("h4").first();
				Log.d("BREWER: ",brewerName.text());
				
				Element beerName = brewer.select("h1.beer-name").first();
				Log.d("BEER: ",beerName.text());
				
				Beer parsedBeer = new Beer(beerName.text(),brewerName.text(),null,null,null);
				theBeers.add(parsedBeer);
				dbHandler.addBeer(parsedBeer);
			}
			
			
//			Elements beers = doc.select("h1.beer-name");
//			
//			if(!beers.isEmpty()){
//				for(Element beer : beers)
//				{
//				String info = beer.text();
//				beerList.add(info);
//				
//				Log.d(TAG,info);}
//			}
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}	
	

	}

private String convertStreamToString(InputStream is)
        throws IOException {
    //
    // To convert the InputStream to String we use the
    // Reader.read(char[] buffer) method. We iterate until the
    // Reader return -1 which means there's no more data to
    // read. We use the StringWriter class to produce the string.
    //
	String convertedStream = "";
    if (is != null) {
        StringWriter writer = new StringWriter();

        char[] buffer = new char[1024];
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } finally {
            is.close();
        }
        convertedStream = writer.toString();
    }
    
    return convertedStream;
}
	}


}
