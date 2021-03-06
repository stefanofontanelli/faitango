package com.retis.faitango;

import android.content.ContentResolver;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

import com.retis.faitango.database.CountryProvider;
import com.retis.faitango.database.CountryTable;
import com.retis.faitango.preference.PreferenceHelper;
import com.retis.faitango.remote.EventFilter;

public class CountrySpinnerChangeListener implements OnItemSelectedListener {

	private static final String TAG = "CountrySpinnerChangeListener";
	private MainView context;
	private Spinner region;
	private ContentResolver cr;

	public CountrySpinnerChangeListener(MainView activity, Spinner r) {
		super();
		context = activity;
		region = r;
		cr = context.getContentResolver();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {

		EventFilter searchPrefs = PreferenceHelper.getSearchParams(context);
		String value = parent.getItemAtPosition(pos).toString();
		if (value.equals(MainView.ALL_COUNTRIES_LABEL)) {
			region.setSelection(0);
		} else {
			String where = CountryTable.NAME + "='" + value + "'";
			Log.d(TAG, "WHERE: " + where);
			Cursor countryCursor = cr.query(CountryProvider.CONTENT_URI, null, where, null, null);
			if (countryCursor.moveToFirst()) {
				do {
					context.setRegionSpinner(region,
							countryCursor.getString(countryCursor.getColumnIndex(CountryTable._ID)),
							searchPrefs.region);
				} while(countryCursor.moveToNext());
			}
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// Do nothing.
	}
}
