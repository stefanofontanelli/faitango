package com.retis.faitango;

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
		
		Log.d("EventsTreeAdapter", DbHelper.C_DATE + " = " +
				groupCursor.getString(groupCursor.getColumnIndexOrThrow(DbHelper.C_DATE)));
		cursor = db.query(DbHelper.TABLE, null,
				DbHelper.C_DATE + " = '" +
				groupCursor.getString(groupCursor.getColumnIndexOrThrow(DbHelper.C_DATE)) + "'",
				null, null, null, null);
		Log.d(TAG, Integer.toString(cursor.getCount()));
		//startManagingCursor(cursor);
		return cursor;
	}

}
