package com.retis.faitango.preference;

import com.retis.faitango.database.RegionProvider;
import com.retis.faitango.database.RegionTable;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.util.Log;

public class RegionList extends ListPreference {
	
	private static final String TAG = "RegionList";
	private Cursor regions;
	private String[] entries;
	private String[] entryValues;
	
	public RegionList(Context context, AttributeSet attrs) {
        super(context, attrs);
        load(context, null);
    }

    public RegionList(Context context) {
        this(context, null);
    }
    
    public void load(Context context, String country) {
    	ContentResolver cr = context.getContentResolver();
    	String where = null;
    	if (country != null || country != "") {
    		where = RegionTable.COUNTRY + "=" + country;
    	}
        regions = cr.query(RegionProvider.CONTENT_URI, null, where, null, null);
        entries = new String[regions.getCount()];
        entryValues = new String[regions.getCount()];
        int i = 0;
        if (regions.moveToFirst()) {
        	do {
        		entries[i] = regions.getString(regions.getColumnIndex(RegionTable.NAME));
        		entryValues[i] = regions.getString(regions.getColumnIndex(RegionTable.NAME));
        		Log.d(TAG, entries[i] + " = " + entryValues[i]);
        		i++;
        	} while(regions.moveToNext());
        }
        setEntries(entries);
        setEntryValues(entryValues);
    }
}
