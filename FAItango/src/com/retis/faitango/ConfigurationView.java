package com.retis.faitango;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.Toast;

public class ConfigurationView extends PreferenceActivity {

	private CheckBoxPreference bgUpdaterCheckbox;
	private ListPreference bgUpdaterPeriodsList;
	private boolean bgUpdaterOn;
	
	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	     // Load the preferences from an XML resource
		 addPreferencesFromResource(R.xml.preferences);
		 createBgUpdaterLayout();
	 }
	    
    /*****************************************/
    /*** Background Updater configuration ***/
    /*****************************************/    
    private void createBgUpdaterLayout() {
    	
    	// CheckBox setup
        bgUpdaterCheckbox = (CheckBoxPreference) findPreference("checkboxBgUpdater");
        bgUpdaterOn = bgUpdaterCheckbox.isChecked();
        bgUpdaterCheckbox.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
        	public boolean onPreferenceChange(Preference pce, Object value) {
        		EventReaderAlarm alarm = new EventReaderAlarm(pce.getContext());
            	String s;
            	bgUpdaterOn = value.equals(true); 
            	if (bgUpdaterOn) {
               		alarm.start();
               		s = "Starting background updater";
            	} else {
            		alarm.stop();
            		s = "Stopping background updater";
            	}
        		Toast.makeText(pce.getContext(), s, Toast.LENGTH_SHORT).show();
        		return true;
        	}
        });

        // List setup
        bgUpdaterPeriodsList = (ListPreference) findPreference("listBgUpdaterPeriods");  
        bgUpdaterPeriodsList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
        	public boolean onPreferenceChange(Preference pce, Object value) {
        		if (bgUpdaterOn) {
        			// Update alarm if necessary
        			long period =  Long.decode((String)value);
                	period = period * 1000; // In milliseconds
        			EventReaderAlarm alarm = new EventReaderAlarm(pce.getContext(), period, period);
        			alarm.start();
        		}
            	String s = "UpdaterPerdiods = " + (String)value;
        		Toast.makeText(pce.getContext(), s, Toast.LENGTH_SHORT).show();
        		return true;
        	}
        });    
    }
}
