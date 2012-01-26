package com.retis.faitango;

import com.retis.faitango.R;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;

public class ConfigurationView extends PreferenceActivity {
	
	private ListPreference autoSyncType;
	private ListPreference periodicAutoSyncPeriods;
	private boolean periodicAutoSync;
	private static final String TAG = "ConfigurationView";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);
		configureAutoSyncType();
		configurePeriodicAutoSyncPeriods();
		configureSearchParams();
	}

	private void configureAutoSyncType() {
		// List setup
		autoSyncType = (ListPreference) findPreference("autoSyncType");  
		autoSyncType.setSummary(autoSyncType.getEntry());
		autoSyncType.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference pce, Object value) {
				long type = Long.decode((String)value);
				// chris FIXME: treating type0 and type1 as the SAME!
				if (type == 0 || type == 1) {
					Log.d(ConfigurationView.TAG, "Changing preference autoSyncType to NEVER/ON-STARTUP");
					EventReaderAlarm.instance(getApplicationContext()).stop();
					periodicAutoSync = false;
				} else if (type == 2) {
					Log.d(ConfigurationView.TAG, "Changing preference autoSyncType to ON-BOOT");
					EventFilter filter = PreferenceHelper.getSearchParams(getApplicationContext());
		        	long period =  PreferenceHelper.getPeriodicAutoSyncPeriod(getApplicationContext());
		        	period = period * 1000;
		        	EventReaderAlarm alarm = EventReaderAlarm.instance(getApplicationContext());
		        	alarm.update(filter, period, period);
		        	alarm.start();
					periodicAutoSync = true;
				} else {
					Log.w(ConfigurationView.TAG, "Changing preference autoSyncType to invalid value=" + 
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
					Log.d(ConfigurationView.TAG, "Changing preference autoSync period to " + (String)value);
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
	}    
}
