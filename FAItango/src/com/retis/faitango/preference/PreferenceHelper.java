package com.retis.faitango.preference;

import java.util.Calendar;

import com.retis.faitango.remote.EventFilter;
import com.retis.faitango.remote.EventType;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;


public final class PreferenceHelper {

	private static final String TAG = "PreferenceHelper";
	private static final String remoteServer = "remoteHTTPServer";
	private static final String syncType = "syncType";
	private static final String syncPeriod = "syncPeriod";
	private static final String country = "country";
	private static final String region = "region";
	private static final String province = "province";
	private static final String eventSearchPeriod = "eventSearchPeriod";

	public static String getRemoteServer(Context c) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c.getApplicationContext());
		return prefs.getString(remoteServer, "");
	}

	public static boolean isSyncPeriodic(Context c) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c.getApplicationContext());
		Log.d(TAG, "SyncType is: " + prefs.getString(syncType, ""));
		if (prefs.getString(syncType, "").contentEquals("2")) {
			return true;
		}
		return false;
	}
	
	public static boolean isSyncAtStartup(Context c) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c.getApplicationContext());
		if (prefs.getString(syncType, "").contentEquals("1")) {
			return true;
		}
		return false;
	}

	public static long getSyncPeriod(Context c) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c.getApplicationContext());
		return Long.decode(prefs.getString(syncPeriod, "0"));
	}

	public static EventFilter getSearchParams(Context c) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c.getApplicationContext());
		EventFilter filter = new EventFilter();
		filter.country = prefs.getString(country, "");
		filter.region = prefs.getString(region, "");
		filter.province = prefs.getString(province, "");
		// Add all event types to the filter
		for (EventType t : EventType.values())
			if (Boolean.parseBoolean(prefs.getString(t.name(), "false"))) {
				filter.types.add(t);
			}
		Calendar now = Calendar.getInstance();
		filter.dateFrom = now.getTime();
		now.add(Calendar.DAY_OF_MONTH,
				Integer.parseInt(prefs.getString(eventSearchPeriod, "1")));
		filter.dateTo = now.getTime();
		return filter;
	}
}
