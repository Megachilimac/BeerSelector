package com.chilimac.exitsix.beerselector;

import android.app.Application;

public class BeerSelectorApplication extends Application{
	
	private static BeerSelectorApplication sInstance;
	private BeerDBHandler dbHandler;
	
	public static BeerSelectorApplication getInstance()
	{
		return sInstance;
	}
	
	public BeerDBHandler getDatabaseHandler()
	{
		return dbHandler;
	}
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		sInstance = this;
		sInstance.initializeInstance();
	}
	
	protected void initializeInstance()
	{
		dbHandler = new BeerDBHandler(this);
	}
	
	
}
