package com.retis.faitango;

import com.retis.faitango.R;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.Toast;

public class ConfigurationView extends PreferenceActivity {

	private ListPreference autoSyncType;
	private ListPreference periodicAutoSyncPeriods;
	private boolean periodicAutoSync;
	
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
        			// NOTE: The period and offset params (0 and 0) are not important, we just need
        			//       to call the alarm.stop() 
        			EventReaderAlarm alarm = new EventReaderAlarm(pce.getContext(), 0, 0);
    				alarm.stop();
        			periodicAutoSync = false;
        		} else if (type == 2) {
        			EventReaderAlarm alarm = new EventReaderAlarm(pce.getContext());
    				alarm.start();
        			periodicAutoSync = true;
        		} else {
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
        			// Update alarm if necessary
        			long period =  Long.decode((String)value);
        			if (period != 0) {
        				period = period * 1000; // In milliseconds
        				EventReaderAlarm alarm = new EventReaderAlarm(pce.getContext(), period, period);
        				alarm.stop();  // Cancel previous alarm (if any)
        				alarm.start(); // Start the new alarm
        			}
        		}
            	String s = "UpdaterPerdiods = " + (String)value;
        		Toast.makeText(pce.getContext(), s, Toast.LENGTH_SHORT).show();
        		int idx = periodicAutoSyncPeriods.findIndexOfValue((String)value);
        		pce.setSummary(periodicAutoSyncPeriods.getEntries()[idx]);
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
