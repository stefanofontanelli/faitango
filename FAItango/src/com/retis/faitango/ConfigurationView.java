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
        bgUpdaterCheckbox.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
        	public boolean onPreferenceChange(Preference pce, Object value) {
        		EventReaderAlarm alarm = new EventReaderAlarm(pce.getContext());
            	String s;
            	if (value.equals(true)) {
               		alarm.start();
               		s = "Starting background updater";
            	} else {
            		alarm.stop();
            		s = "Stopping background updater";
            	}
        		Toast.makeText(pce.getContext(), s, Toast.LENGTH_LONG).show();
        		return true;
        	}
        });

        // List setup
        bgUpdaterPeriodsList = (ListPreference) findPreference("listBgUpdaterPeriods");  
        bgUpdaterPeriodsList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
        	public boolean onPreferenceChange(Preference pce, Object value) {
        		EventReaderAlarm alarm = new EventReaderAlarm(pce.getContext());
        		// chris TODO: update alarm if necessary
            	String s = "UpdaterPerdiods = " + (String)value;
        		Toast.makeText(pce.getContext(), s, Toast.LENGTH_LONG).show();
        		return true;
        	}
        });    
    }
}
