package com.retis.faitango;

import com.retis.faitango.R;
import com.retis.faitango.preference.CountryList;
import com.retis.faitango.preference.CountryListChangeListener;
import com.retis.faitango.preference.ProvinceList;
import com.retis.faitango.preference.RegionList;
import com.retis.faitango.preference.RegionListChangeListener;
import com.retis.faitango.preference.SyncPeriodChangeListener;
import com.retis.faitango.preference.SyncTypeChangeListener;

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
	}

	/*
	private void configureAutoSyncType() {
		// List setup
		syncType = (ListPreference) findPreference("autoSyncType");  
		syncType.setSummary(syncType.getEntry());
		syncType.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference pce, Object value) {
				long type = Long.decode((String)value);
				// chris FIXME: treating type0 and type1 as the SAME!
				if (type == 0 || type == 1) {
					Log.d(Setting.TAG, "Changing preference autoSyncType to NEVER/ON-STARTUP");
					EventReaderAlarm.instance(getApplicationContext()).stop();
					periodicAutoSync = false;
				} else if (type == 2) {
					Log.d(Setting.TAG, "Changing preference autoSyncType to ON-BOOT");
					EventFilter filter = PreferenceHelper.getSearchParams(getApplicationContext());
		        	long period =  PreferenceHelper.getPeriodicAutoSyncPeriod(getApplicationContext());
		        	period = period * 1000;
		        	EventReaderAlarm alarm = EventReaderAlarm.instance(getApplicationContext());
		        	alarm.update(filter, period, period);
		        	alarm.start();
					periodicAutoSync = true;
				} else {
					Log.w(Setting.TAG, "Changing preference autoSyncType to invalid value=" + 
							(String)value + ". Check code against string.xml file");
					// TODO: generate some error?
				}
				int idx = autoSyncType.findIndexOfValue((String)value);
				pce.setSummary(autoSyncType.getEntries()[idx]);
				return true;
			}
		});
	}

	private void configurePeriodicAutoSyncPeriods() {
		// List setup
		periodicAutoSyncPeriods = (ListPreference) findPreference("periodicAutoSyncPeriods");  
		periodicAutoSyncPeriods.setSummary(periodicAutoSyncPeriods.getEntry());
		periodicAutoSyncPeriods.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference pce, Object value) {
				if (periodicAutoSync) {
					Log.d(Setting.TAG, "Changing preference autoSync period to " + (String)value);
					// Update alarm if necessary
					long period =  Long.decode((String)value);
					period = period * 1000; // In milliseconds
					EventFilter filter = PreferenceHelper.getSearchParams(getApplicationContext());
					EventReaderAlarm alarm = EventReaderAlarm.instance(getApplicationContext());
					alarm.update(filter, period, period);
					alarm.start(); // Start the new alarm
				}
				int idx = periodicAutoSyncPeriods.findIndexOfValue((String)value);
				CharSequence valueText = periodicAutoSyncPeriods.getEntries()[idx]; 
				pce.setSummary(valueText);
				String s = "Automatic Synchronization every " + valueText;
				Toast.makeText(pce.getContext(), s, Toast.LENGTH_LONG).show();
				return true;
			}
		});    
	}

	private void configureSearchParams() {

		// FIXME: TEMPORARY SOLUTION: treat each change as the same
		// TODO:  The Country, Region, Province preference should be influenced each other according
		//        to the following hierarchical order: Country->Region->Province!!!
		ListPreference country = (ListPreference) findPreference("autoSyncSearchCountry");  
		ListPreference region = (ListPreference) findPreference("autoSyncSearchRegion");
		ListPreference province = (ListPreference) findPreference("autoSyncSearchProvince");
		addSearchPreference(country);
		addSearchPreference(region);
		addSearchPreference(province);
	}

	private void addSearchPreference(ListPreference pref) {
		pref.setSummary(pref.getEntry());
		pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference pce, Object value) {
				ListPreference lp = (ListPreference) pce;
				int idx = lp.findIndexOfValue((String)value);
				pce.setSummary(lp.getEntries()[idx]);
				return true;
			}
		});
	}   */ 
}
