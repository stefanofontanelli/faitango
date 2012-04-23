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
import com.retis.faitango.remote.EventType;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
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
	private Context context;
	private SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate ...");
		addPreferencesFromResource(R.xml.preferences);
		context = getApplicationContext();
		syncType = (ListPreference) findPreference("syncType");
		syncPeriod = (ListPreference) findPreference("syncPeriod");
		syncTypeListener = new SyncTypeChangeListener(this);
		syncType.setOnPreferenceChangeListener(syncTypeListener);
		syncPeriodListener = new SyncPeriodChangeListener(this);
		syncPeriod.setOnPreferenceChangeListener(syncPeriodListener);
		country = (CountryList) findPreference("country");
		region = (RegionList) findPreference("region");
		province = (ProvinceList) findPreference("province");
		countryListener = new CountryListChangeListener(context, region);
		country.setOnPreferenceChangeListener(countryListener);
		regionListener = new RegionListChangeListener(context, province);
		region.setOnPreferenceChangeListener(regionListener);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		Log.d(TAG, "Context: " + this.getApplicationContext());
		
		EventFilter f = PreferenceHelper.getSearchParams(this);
		region.load(context, f.country);
		province.load(context, f.region);
	    
		if (f.country == null) {
			f.country = "";
		}
	    country.setDefaultValue(f.country);
	    country.setValue(f.country);
	    Log.d(TAG, "COUNTRY " + f.country);
	    
	    if (f.region == null) {
			f.region = "";
		}
	    region.setDefaultValue(f.region);
	    region.setValue(f.region);
	    Log.d(TAG, "REGION " + f.region);
	    
	    if (f.province == null) {
			f.province = "";
		}
	    province.setDefaultValue(f.province);
	    province.setValue(f.province);
	    Log.d(TAG, "PROVINCE " + f.province);
	    
	    for (EventType t : EventType.values()) {
	    	CheckBoxPreference p = (CheckBoxPreference) findPreference(t.name());
			if (f.types.contains(t)) {
				p.setChecked(true);
				Log.d(TAG, "Event type: " + t.name() + " -> TRUE");
			} else {
				p.setChecked(false);
				Log.d(TAG, "Event type: " + t.name() + " -> FALSE");
			}
		}
	}
}
