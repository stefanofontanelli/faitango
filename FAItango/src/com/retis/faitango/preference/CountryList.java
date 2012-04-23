package com.retis.faitango.preference;

import com.retis.faitango.MainView;
import com.retis.faitango.database.CountryProvider;
import com.retis.faitango.database.CountryTable;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.util.Log;

public class CountryList extends ListPreference {
	
	private static final String TAG = "CountryList";
	private final Cursor countries;
	private final String[] entries;
	private final String[] entryValues;
	
	public CountryList(Context context, AttributeSet attrs) {
        super(context, attrs);
        ContentResolver cr = context.getContentResolver();
        countries = cr.query(CountryProvider.CONTENT_URI, null, null, null, null);
        entries = new String[countries.getCount() + 1];
        entryValues = new String[countries.getCount() + 1];
        
        entries[0] = MainView.ALL_COUNTRIES_LABEL;
        entryValues[0] = "";
        
        int i = 1;
        if (countries.moveToFirst()) {
        	do {
        		entries[i] = countries.getString(countries.getColumnIndex(CountryTable.NAME));
        		entryValues[i] = countries.getString(countries.getColumnIndex(CountryTable._ID));
        		//Log.d(TAG, entries[i] + " = " + entryValues[i]);
        		i++;
        	} while(countries.moveToNext());
        }
        setEntries(entries);
        setEntryValues(entryValues);
    }

    public CountryList(Context context) {
        this(context, null);
    }
}
