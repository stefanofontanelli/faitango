package com.retis.faitango;

import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;

public class EventsAdapter extends SimpleCursorAdapter {
  static final String[] FROM = { DbHelper.C_CITY, DbHelper.C_DATE,
      DbHelper.C_TYPE, DbHelper.C_NAME, DbHelper.C_TIME };
  static final int[] TO = { R.id.textCity, R.id.textDate, R.id.textEventType,
	  R.id.textEventName, R.id.textTime };
  
 
  // Constructor
  public EventsAdapter(Context context, Cursor c) {
    super(context, R.layout.eventrow, c, FROM, TO);
  }

}
