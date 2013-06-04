package com.chilimac.exitsix.beerselector;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.database.sqlite.SQLiteOpenHelper;

public class BeerDBHandler extends SQLiteOpenHelper {
	
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_TABLE_NAME = "BEER";
	private static final String KEY_ID = "_id";
	private static final String KEY_NAME = "BEER_NAME";
	private static final String KEY_BREWER = "BREWER";
	private static final String KEY_IBU = "IBU";
	private static final String KEY_ABV = "ABV";
	private static final String KEY_RATING = "RATING";
	
    private static final String DATABASE_TABLE_CREATE =
                "CREATE TABLE " + DATABASE_TABLE_NAME + " (" +
                KEY_ID + " integer primary key autoincrement, " +
                KEY_NAME + " TEXT, " +
                KEY_BREWER + " TEXT, " +
                KEY_IBU + " TEXT, " +
                KEY_ABV + " TEXT, " +
                KEY_RATING + " TEXT);";
    
	public BeerDBHandler(Context context) {
		super(context, DATABASE_TABLE_NAME, null, DATABASE_VERSION);
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_TABLE_CREATE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DATABASE_TABLE_CREATE);
		
	}
	

	public void addBeer(Beer theBeer) {
		 SQLiteDatabase db = this.getWritableDatabase();
		 
		    ContentValues values = new ContentValues();
		    values.put(KEY_NAME, theBeer.getName()); 
		    values.put(KEY_BREWER, theBeer.getBrewer());
		    values.put(KEY_IBU, theBeer.getIBU());
		    values.put(KEY_ABV, theBeer.getABV());
		    values.put(KEY_RATING, theBeer.getRating());
		 
		    db.insert(DATABASE_TABLE_NAME, null, values);
		    db.close(); 
	}
	 

	public Beer getBeer(int id) { 
		SQLiteDatabase db = this.getReadableDatabase();
		 
	    Cursor cursor = db.query(DATABASE_TABLE_NAME, new String[] { KEY_ID,
	            KEY_NAME, KEY_BREWER, KEY_IBU, KEY_ABV, KEY_RATING }, KEY_ID + "=?",
	            new String[] { String.valueOf(id) }, null, null, null, null);
	    if (cursor != null)
	        cursor.moveToFirst();
	 
	    Beer theBeer = new Beer(cursor.getInt(0), cursor.getString(1),
	    						cursor.getString(2), cursor.getString(3), 
	    						cursor.getString(4),cursor.getString(5));

	    return theBeer;
	}
	 

	public List<Beer> getAllBeers() {
		List<Beer> beerList = new ArrayList<Beer>();
		
		String getAllQuery = "SELECT * FROM " + DATABASE_TABLE_NAME;
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.rawQuery(getAllQuery,null);
		
		if(cursor.moveToFirst())
		{
			do
			{
				Beer theBeer = new Beer(cursor.getInt(0), cursor.getString(1),
						cursor.getString(2), cursor.getString(3), 
						cursor.getString(4),cursor.getString(5));
				
				beerList.add(theBeer);
			}
			while (cursor.moveToNext());
		}
		
		
		return beerList;
		
	}
	 

	public int getBeerCount() {
		String getCount = "SELECT * FROM " + DATABASE_TABLE_NAME;
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.rawQuery(getCount,null);
		
		return cursor.getCount();
		
	}

}
