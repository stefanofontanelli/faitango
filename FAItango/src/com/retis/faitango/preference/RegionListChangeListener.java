package com.retis.faitango.preference;

import android.content.Context;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;

public class RegionListChangeListener implements OnPreferenceChangeListener {

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
		CharSequence[] entryValues = list.getEntryValues();
		String entryValue = (String) entryValues[i];
		province.load(context, entryValue);
		return true;
	}
}
