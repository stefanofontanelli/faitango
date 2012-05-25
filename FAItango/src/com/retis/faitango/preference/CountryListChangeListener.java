package com.retis.faitango.preference;

import android.content.Context;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;

public class CountryListChangeListener implements OnPreferenceChangeListener {

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
		CharSequence[] entryValues = list.getEntryValues();
		String entryValue = (String) entryValues[i];
		region.load(context, entryValue);
		return true;
	}
}
