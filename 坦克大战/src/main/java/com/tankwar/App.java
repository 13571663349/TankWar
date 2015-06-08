package com.tankwar;

import java.util.HashMap;
import java.util.LinkedList;

import android.app.Activity;
import android.app.Application;
import android.widget.Toast;

final public class App extends Application
{
	private static App instance							= null;
	private static HashMap<String, Object> sharedData	= new HashMap<String, Object>();

	public static String DS	= "/";

	public final void onCreate() {
		super.onCreate();
		initialize();
	}


	private final void initialize() {
		instance = this;
	}


	public final static void setData(String key, Object obj) {
		sharedData.put(key, obj);
	}


	public final static Object getData(String key) {
		return sharedData.get(key);
	}


	public final static void toast(String s) {
		Toast.makeText(App.getInstance().getBaseContext(), s, Toast.LENGTH_SHORT).show();
	}


	public final static App getInstance() {
		return instance;
	}
}
