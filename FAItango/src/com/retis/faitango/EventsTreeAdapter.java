package com.retis.faitango;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.SimpleCursorTreeAdapter.ViewBinder;
import android.widget.TextView;

import com.retis.faitango.DbHelper;

public class EventsTreeAdapter extends SimpleCursorTreeAdapter {
	static final String[] FROM = { DbHelper.C_CITY, DbHelper.C_DATE,
	      DbHelper.C_TYPE, DbHelper.C_NAME, DbHelper.C_TIME };
	  static final int[] TO = { R.id.textCity, R.id.textDate, R.id.textEventType,
		  R.id.textEventName, R.id.textTime };
	  
	  private final String TAG = "EventsTreeAdapter";
	  private SQLiteDatabase db;
	  private Context context;
	  /*
	   *  this map is used to associate an ID to a child
	   *  position inside a parent group expanded list entry
	   */
	  static HashMap<Integer, Integer> childMap = new HashMap<Integer, Integer>();
	  private Map<Integer, String> listMap = new HashMap<Integer, String>();
	  

	  // Constructor
	  public EventsTreeAdapter(Cursor cursor, Context c, SQLiteDatabase dataBase) {
	    super(c, cursor, android.R.layout.simple_expandable_list_item_1,
	    		new String[] {DbHelper.C_DATE}, new int[] {android.R.id.text1},
	    		R.layout.eventrow, FROM, TO);
	    db = dataBase;
	    context = c;
		cursor.moveToFirst();
		while(cursor.isAfterLast() == false) {
			SimpleDateFormat sdf = new SimpleDateFormat("E dd/MM/yyyy", Locale.ITALIAN); 
			String s = sdf.format(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(DbHelper.C_DATE))));
			listMap.put(cursor.getPosition(), s);
			cursor.moveToNext();
		}
		cursor.moveToFirst();
	  }

 
	  @Override
	  protected void bindChildView(View view, Context context, Cursor cursor,	boolean isLastChild) {
		  super.bindChildView(view, context, cursor, isLastChild);
		  SimpleDateFormat sdf = new SimpleDateFormat("E dd/MM/yyyy", Locale.ITALIAN); 
		  String s = sdf.format(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(DbHelper.C_DATE))));
		  TextView tv = (TextView) view.findViewById(R.id.textDate);
		  tv.setText(s);
		  Log.d(TAG, "PROVIAMOLO :" + s);
	  }
	  
	  @Override
	  public View getGroupView(int groupPos, boolean isExpanded, View convertView, ViewGroup parent) 	  {
	      LinearLayout linear = new LinearLayout(context);
	      final LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
	      TextView text = new TextView(context);

	      // Push the group name slightly to the right for drop down icon
	      final String str = "\t\t\t\t"  + listMap.get(groupPos);

	      linear = new LinearLayout(context);
	      linear.setOrientation(LinearLayout.VERTICAL);

	      text.setLayoutParams(params);
	      text.setText(str);
	      text.setTextSize(32.0f);
	      linear.addView(text);

	      return linear;
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
				groupCursor.getLong(groupCursor.getColumnIndexOrThrow(DbHelper.C_DATE)) + "'",
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
