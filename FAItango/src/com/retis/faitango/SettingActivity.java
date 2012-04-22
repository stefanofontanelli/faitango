package com.retis.faitango;

import com.retis.faitango.R;
import com.retis.faitango.preference.CountryList;
import com.retis.faitango.preference.CountryListChangeListener;
import com.retis.faitango.preference.PreferenceHelper;
import com.retis.faitango.preference.ProvinceList;
import com.retis.faitango.preference.RegionList;
import com.retis.faitango.preference.RegionListChangeListener;
import com.retis.faitango.preference.SyncPeriodChangeListener;
import com.retis.faitango.preference.SyncTypeChangeListener;
import com.retis.faitango.remote.EventFilter;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.util.Log;


public class SettingActivity extends PreferenceActivity {
	
	private static final String TAG = "SettingActivity";
	private ListPreference syncType;
	private ListPreference syncPeriod;
	private CountryList country;
	private RegionList region;
	private ProvinceList province;
	private SyncTypeChangeListener syncTypeListener;
	private SyncPeriodChangeListener syncPeriodListener;
	private CountryListChangeListener countryListener;
	private RegionListChangeListener regionListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate ...");
		addPreferencesFromResource(R.xml.preferences);
		syncType = (ListPreference) findPreference("syncType");
		syncPeriod = (ListPreference) findPreference("syncPeriod");
		syncTypeListener = new SyncTypeChangeListener(this);
		syncType.setOnPreferenceChangeListener(syncTypeListener);
		syncPeriodListener = new SyncPeriodChangeListener(this);
		syncPeriod.setOnPreferenceChangeListener(syncPeriodListener);
		country = (CountryList) findPreference("country");
		region = (RegionList) findPreference("region");
		province = (ProvinceList) findPreference("province");
		countryListener = new CountryListChangeListener(this.getApplicationContext(), region);
		country.setOnPreferenceChangeListener(countryListener);
		regionListener = new RegionListChangeListener(this.getApplicationContext(), province);
		region.setOnPreferenceChangeListener(regionListener);
		EventFilter f = PreferenceHelper.getSearchParams(this);
		region.load(this.getApplicationContext(), f.country);
		province.load(this.getApplicationContext(), f.region);
	} 
}
