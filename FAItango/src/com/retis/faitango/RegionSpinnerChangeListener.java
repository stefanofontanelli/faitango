package com.retis.faitango;

import com.retis.faitango.database.CountryProvider;
import com.retis.faitango.database.CountryTable;
import com.retis.faitango.database.ProvinceProvider;
import com.retis.faitango.database.ProvinceTable;
import com.retis.faitango.database.RegionProvider;
import com.retis.faitango.database.RegionTable;
import com.retis.faitango.preference.PreferenceHelper;
import com.retis.faitango.remote.EventFilter;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class RegionSpinnerChangeListener implements OnItemSelectedListener {
	
	private static final String TAG = "RegionSpinnerChangeListener";
	private MainView context;
	private Spinner province;
	private ContentResolver cr;
	
	public RegionSpinnerChangeListener(MainView activity, Spinner p) {
		super();
		context = activity;
		province = p;
		cr = context.getContentResolver();
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
		
		EventFilter searchPrefs = PreferenceHelper.getSearchParams(context);
		String value = parent.getItemAtPosition(pos).toString();
		if (value.equals(context.ALL_REGIONS_LABEL)) {
			province.setSelection(0);
		} else {
			context.setProvinceSpinner(province, value, searchPrefs.province);
		}
    }
	
	@Override
	public void onNothingSelected(AdapterView parent) {
        // Do nothing.
      }
}
