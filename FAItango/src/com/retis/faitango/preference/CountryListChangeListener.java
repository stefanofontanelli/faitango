package com.retis.faitango.preference;

import android.content.Context;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;

public class CountryListChangeListener implements OnPreferenceChangeListener {
	
	private static final String TAG = "CountryListChangeListener";
	private final Context context;
	private final RegionList region;
	
	public CountryListChangeListener(Context c, RegionList lp) {
		super();
		context = c;
		region = lp;
	}
	
	@Override
	public boolean onPreferenceChange(Preference p, Object v) {
		ListPreference list = (ListPreference) p;
		String value = (String) v;
		int i = list.findIndexOfValue(value);
		CharSequence[] entries = list.getEntries();
		CharSequence[] entryValues = list.getEntryValues();
		String entry = (String) entries[i];
		String entryValue = (String) entryValues[i];
		//Log.d(TAG, entry);
		//Log.d(TAG, entryValue);
		region.load(context, entryValue);
		return true;
	}
}
