package com.retis.faitango.preference;

import java.util.Calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.preference.PreferenceManager;
import android.util.Log;

import com.retis.faitango.R;
import com.retis.faitango.remote.EventFilter;
import com.retis.faitango.remote.EventType;


public final class PreferenceHelper {

	private static final String TAG = "PreferenceHelper";
	private static final String remoteServer = "remoteHTTPServer";
	private static final String syncType = "syncType";
	private static final String syncPeriod = "syncPeriod";
	private static final String country = "country";
	private static final String region = "region";
	private static final String province = "province";
	private static final String eventSearchPeriod = "eventSearchPeriod";
	public static final String eventSearchPeriodChanged = "eventSearchPeriodChanged";
	
	public static void installPreferences(Context c){
		 PreferenceManager.setDefaultValues(c, R.xml.preferences, false);
	}
	
	public static boolean isFirstRun(Context c) {
		boolean ret = false;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c.getApplicationContext());
		PackageInfo pInfo;
    	try {
    		pInfo = c.getPackageManager().getPackageInfo(c.getPackageName(), PackageManager.GET_META_DATA);
    		if (prefs.getLong( "lastRunVersionCode", 0) < pInfo.versionCode ) {
    			android.content.SharedPreferences.Editor editor = prefs.edit();
    			editor.putLong("lastRunVersionCode", pInfo.versionCode);
    			editor.commit();
    			ret = true;
    		}
    	} catch (NotFoundException e) {
    		e.printStackTrace();
    	} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
    	return ret;
	}

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
		if (filter.country.equals("")) {
			filter.country = null;
		}
		Log.d(TAG, "Preferred country:" + filter.country);
		filter.region = prefs.getString(region, "");
		if (filter.region.equals("")) {
			filter.region = null;
		}
		Log.d(TAG, "Preferred region:" + filter.region);
		filter.province = prefs.getString(province, "");
		if (filter.province.equals("")) {
			filter.province = null;
		}
		Log.d(TAG, "Preferred province:" + filter.province);
		// Add all event types to the filter
		for (EventType t : EventType.values()) {
			if (prefs.getBoolean(t.name(), false)) {
				filter.types.add(t);
			} else {
				Log.d(TAG, "Type " + t.name() + " not selected in " + prefs);
			}
		}
		Calendar now = Calendar.getInstance();
		now.set(Calendar.HOUR_OF_DAY, 1);
		filter.dateFrom = now.getTime();
		now.set(Calendar.HOUR_OF_DAY, 1);
		
		now.add(Calendar.DAY_OF_MONTH, Integer.parseInt(prefs.getString(eventSearchPeriod, "1")));
		now.set(Calendar.HOUR_OF_DAY, 23);
		filter.dateTo = now.getTime();
		return filter;
	}
	
	public static void setSearchParams(Context c, EventFilter filter) {
		Log.d(TAG, "Context: " + c.getApplicationContext());
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c.getApplicationContext());
		SharedPreferences.Editor editor = prefs.edit();
		
		editor.putString(country, filter.country);
		Log.d(TAG, "Updated preferred country: " + filter.country);
		editor.putString(region, filter.region);
		Log.d(TAG, "Updated preferred region: " + filter.region);
		editor.putString(province, filter.province);
		Log.d(TAG, "Updated preferred province: " + filter.province);
		
		for (EventType t : EventType.values()) {
			if (filter.types.contains(t)) {
				editor.putBoolean(t.name(), true);
				Log.d(TAG, "Updated preferred event type: " + t.name() + " -> TRUE");
			} else {
				editor.putBoolean(t.name(), false);
				Log.d(TAG, "Updated preferred event type: " + t.name() + " -> FALSE");
			}
		}
		long period = (filter.dateTo.getTime() - filter.dateFrom.getTime()) / (24 * 60 * 60 * 1000);
		if (period <= 1) {
			period = 1;
		} else if (period <= 7) {
			period = 7;
		} else if (period <= 15) {
			period = 15;
		} else if (period <= 31) {
			period = 31;
		} else if (period <= 186) {
			period = 186;
		} else { //if (period <= 372) {
			period = 372;
		}
		editor.putString(eventSearchPeriod, "" + period);
		editor.commit();
	}
}
