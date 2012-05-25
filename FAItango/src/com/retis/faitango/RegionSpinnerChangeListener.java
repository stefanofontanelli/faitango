package com.retis.faitango;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

import com.retis.faitango.preference.PreferenceHelper;
import com.retis.faitango.remote.EventFilter;

public class RegionSpinnerChangeListener implements OnItemSelectedListener {

	private MainView context;
	private Spinner province;

	public RegionSpinnerChangeListener(MainView activity, Spinner p) {
		super();
		context = activity;
		province = p;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {

		EventFilter searchPrefs = PreferenceHelper.getSearchParams(context);
		String value = parent.getItemAtPosition(pos).toString();
		if (value.equals(MainView.ALL_REGIONS_LABEL)) {
			province.setSelection(0);
		} else {
			context.setProvinceSpinner(province, value, searchPrefs.province);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// Do nothing.
	}
}
