package com.retis.faitango.preference;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.util.Log;

import com.retis.faitango.MainView;
import com.retis.faitango.database.RegionProvider;
import com.retis.faitango.database.RegionTable;

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
    	if (country != null && country != "") {
    		Log.d(TAG, "WHERE: " + where);
    		where = RegionTable.COUNTRY + "=" + country;
    	}
        regions = cr.query(RegionProvider.CONTENT_URI, null, where, null, null);
        entries = new String[regions.getCount() + 1];
        entryValues = new String[regions.getCount() + 1];
        entries[0] = MainView.ALL_REGIONS_LABEL;
        entryValues[0] = "";
        int i = 1;
        if (regions.moveToFirst()) {
        	do {
        		entries[i] = regions.getString(regions.getColumnIndex(RegionTable.NAME));
        		entryValues[i] = regions.getString(regions.getColumnIndex(RegionTable.NAME));
        		i++;
        	} while(regions.moveToNext());
        }
        setEntries(entries);
        setEntryValues(entryValues);
        regions.close();
    }
}
