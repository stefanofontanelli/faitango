package com.retis.faitango.preference;

import android.content.Context;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;

public class RegionListChangeListener implements OnPreferenceChangeListener {
	
	private static final String TAG = "RegionListChangeListener";
	private final Context context;
	private final ProvinceList province;
	
	public RegionListChangeListener(Context c, ProvinceList lp) {
		super();
		context = c;
		province = lp;
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
		province.load(context, entryValue);
		return true;
	}
}
