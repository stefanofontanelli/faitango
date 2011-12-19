package com.retis.faitango;

import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.SimpleCursorTreeAdapter;
import com.retis.faitango.DbHelper;

public class EventsTreeAdapter extends SimpleCursorTreeAdapter {
	static final String[] FROM = { DbHelper.C_CITY, DbHelper.C_DATE,
	      DbHelper.C_TYPE, DbHelper.C_NAME, DbHelper.C_TIME };
	  static final int[] TO = { R.id.textCity, R.id.textDate, R.id.textEventType,
		  R.id.textEventName, R.id.textTime };
	  
	  private final String TAG = "EventsTreeAdapter";
	  private SQLiteDatabase db;
	  /*
	   *  this map is used to associate an ID to a child
	   *  position inside a parent group expanded list entry
	   */
	  static HashMap<Integer, Integer> childMap = new HashMap<Integer, Integer>();
 
	 
	  // Constructor
	  public EventsTreeAdapter(Cursor cursor, Context context, SQLiteDatabase dataBase) {
	    super(context, cursor, android.R.layout.simple_expandable_list_item_1,
	    		new String[] {DbHelper.C_DATE}, new int[] {android.R.id.text1},
	    		R.layout.eventrow, FROM, TO);
	    db = dataBase;
	  }


	@Override
	protected Cursor getChildrenCursor(Cursor groupCursor) {
		Cursor cursor;
		/* 
		 * since this function is called every time the user
		 * expands a group we must be sure that the map is
		 * cleared and then filled again with actual IDs
		 */
		EventsTreeAdapter.childMap.clear();
		
		Log.d("EventsTreeAdapter", DbHelper.C_DATE + " = " +
				groupCursor.getString(groupCursor.getColumnIndexOrThrow(DbHelper.C_DATE)));
		cursor = db.query(DbHelper.TABLE, null,
				DbHelper.C_DATE + " = '" +
				groupCursor.getString(groupCursor.getColumnIndexOrThrow(DbHelper.C_DATE)) + "'",
				null, null, null, null);
		Log.d(TAG, Integer.toString(cursor.getCount()));
		cursor.moveToFirst();
		while(cursor.isAfterLast() == false) {
			Log.d(TAG, Integer.toString(cursor.getPosition()));
			Log.d(TAG, Integer.toString(cursor.getInt(cursor.getColumnIndex(DbHelper.C_ID))));
			/*
			 * we associate an event ID with the position of a child
			 * in an expandable list hierarchy
			 */
			EventsTreeAdapter.childMap.put(cursor.getPosition(),
					cursor.getInt(cursor.getColumnIndex(DbHelper.C_ID)));
			Log.d(TAG, Integer.toString(EventsTreeAdapter.childMap.get(cursor.getPosition())));
			cursor.moveToNext();
		}
		cursor.moveToFirst();
		
		return cursor;
	}

}
