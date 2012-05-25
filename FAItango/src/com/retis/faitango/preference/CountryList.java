package com.retis.faitango.preference;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.preference.ListPreference;
import android.util.AttributeSet;

import com.retis.faitango.MainView;
import com.retis.faitango.database.CountryProvider;
import com.retis.faitango.database.CountryTable;

public class CountryList extends ListPreference {
	
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
        		i++;
        	} while(countries.moveToNext());
        }
        setEntries(entries);
        setEntryValues(entryValues);
        countries.close();
    }

    public CountryList(Context context) {
        this(context, null);
    }
}
