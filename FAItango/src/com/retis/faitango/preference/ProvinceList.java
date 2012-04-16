package com.retis.faitango.preference;

import com.retis.faitango.database.ProvinceProvider;
import com.retis.faitango.database.ProvinceTable;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.util.Log;

public class ProvinceList extends ListPreference {
	
	private static final String TAG = "ProvinceList";
	private Cursor provinces;
	private String[] entries;
	private String[] entryValues;
	
	public ProvinceList(Context context, AttributeSet attrs) {
        super(context, attrs);
        load(context, null);
    }

    public ProvinceList(Context context) {
        this(context, null);
    }
    
    public void load(Context context, String region) {
    	ContentResolver cr = context.getContentResolver();
    	String where = null;
    	if (region != null && region != "") {
    		Log.d(TAG, "Where: " + where);
    		where = ProvinceTable.REGION + "='" + region +"'";
    	}
        provinces = cr.query(ProvinceProvider.CONTENT_URI, null, where, null, null);
        entries = new String[provinces.getCount() + 1];
        entryValues = new String[provinces.getCount() + 1];
        entries[0] = "All provinces";
        entryValues[0] = "";
        int i = 1;
        if (provinces.moveToFirst()) {
        	do {
        		entries[i] = provinces.getString(provinces.getColumnIndex(ProvinceTable.NAME));
        		entryValues[i] = provinces.getString(provinces.getColumnIndex(ProvinceTable.CODE));
        		Log.d(TAG, entries[i] + " = " + entryValues[i]);
        		i++;
        	} while(provinces.moveToNext());
        }
        setEntries(entries);
        setEntryValues(entryValues);
    }
}
