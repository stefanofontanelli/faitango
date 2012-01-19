package com.retis.faitango;

import java.util.Calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public final class PreferenceHelper {
	
	private static final String keyRemoteServer = "remoteHTTPServer";
	private static final String keyAutoSyncType = "autoSyncType";
	private static final String keyPeriodicAutoSyncPeriod = "periodicAutoSyncPeriods";
	private static final String keySearchParamsCountry = "autoSyncSearchCountry";
	private static final String keySearchParamsRegion = "autoSyncSearchRegion";
	private static final String keySearchParamsArea = "autoSyncSearchProvince";

	public static String getRemoteServer(Context c) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c.getApplicationContext());
		//if (!prefs.contains(keyRemoteServer))
			//; //TODO: throw some exception!
		return prefs.getString(keyRemoteServer, "");
	}
	
	public static boolean hasPeriodicAutoSync(Context c) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c.getApplicationContext());
		//if (!prefs.contains(keyAutoSyncType))
			//; //TODO: throw some exception!
		if (prefs.getString(keyAutoSyncType, "").contentEquals("2"))
			return true;
		return false;
	}
	
	public static long getPeriodicAutoSyncPeriod(Context c) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c.getApplicationContext());
		//if (!prefs.contains(keyPeriodicAutoSyncPeriod))
			//; //TODO: throw some exception!
		return Long.decode(prefs.getString(keyPeriodicAutoSyncPeriod, "0"));
	}
	
	public static EventFilter getSearchParams(Context c) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c.getApplicationContext());
		//if (!prefs.contains(keySearchParamsCountry))
			//; //TODO: throw some exception!
		String country = prefs.getString(keySearchParamsCountry, "");
		String region = prefs.getString(keySearchParamsRegion, "");
		String area = prefs.getString(keySearchParamsArea, "");
		
		EventFilter filter = new EventFilter();
		if (!country.isEmpty())
			filter.country = country;
		if (!region.isEmpty())
			filter.region = region;
		if (!area.isEmpty())
			filter.area = area;
		// Add all event types to the filter
		for (EventType t : EventType.values())
			filter.types.add(t);
		Calendar now = Calendar.getInstance();
		filter.dateFrom = now.getTime();
		now.add(Calendar.MONTH, 1);
		filter.dateTo = now.getTime();
		//filter.dateFrom = (new GregorianCalendar(2011, Calendar.JANUARY, 1)).getTime();
		//filter.dateTo = (new GregorianCalendar(2012, Calendar.DECEMBER, 31)).getTime();
		return filter;
	}
}
