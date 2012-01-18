package com.retis.faitango;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public final class PreferenceHelper {
	
	private static final String keyAutoSyncType = "autoSyncType";
	private static final String keyPeriodicAutoSyncPeriod = "periodicAutoSyncPeriods";
	private static final String keySearchParamsCountry = "autoSyncSearchCountry";
	private static final String keySearchParamsRegion = "autoSyncSearchRegion";
	private static final String keySearchParamsArea = "autoSyncSearchProvince";

	public static boolean hasPeriodicAutoSync(Context c) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
		//if (!prefs.contains(keyAutoSyncType))
			//; //TODO: throw some exception!
		if (prefs.getString(keyAutoSyncType, "").contentEquals("2"))
			return true;
		return false;
	}
	
	public static long getPeriodicAutoSyncPeriod(Context c) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
		//if (!prefs.contains(keyPeriodicAutoSyncPeriod))
			//; //TODO: throw some exception!
		return Long.decode(prefs.getString(keyPeriodicAutoSyncPeriod, "0"));
	}
	
	public static EventFilter getSearchParams(Context c) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
		//if (!prefs.contains(keySearchParamsCountry))
			//; //TODO: throw some exception!
		String country = prefs.getString(keySearchParamsCountry, "");
		String region = prefs.getString(keySearchParamsRegion, "");
		String area = prefs.getString(keySearchParamsArea, "");
		
		EventFilter f = new EventFilter();
		if (!country.isEmpty())
			f.country = country;
		if (!region.isEmpty())
			f.region = region;
		if (!area.isEmpty())
			f.area = area;
		return f;
	}
}
