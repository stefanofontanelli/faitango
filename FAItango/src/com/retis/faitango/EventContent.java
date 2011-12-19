package com.retis.faitango;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class EventContent extends Activity {
	private static String TAG = "EventContent";
	private DbHelper dbHelper;
	private SQLiteDatabase db;
	private Cursor cursor;
	private String eventID;
	private TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eventcont);
		/*
		 * retrieve the event ID and use it later (onResume)
		 * to query the DB for event info
		 */
		Bundle extras = getIntent().getExtras();
		eventID = extras.getString("id");
		Log.d(TAG, eventID);
		
        dbHelper = new DbHelper(this);
        db = dbHelper.getReadableDatabase();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		db.close();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		cursor = db.query(DbHelper.TABLE, null, DbHelper.C_ID + "=" + eventID,
				null, null, null, null);
		startManagingCursor(cursor);
		Log.d(TAG, Integer.toString(cursor.getCount()));
		Log.d(TAG, Integer.toString(cursor.getColumnCount()));
		Log.d(TAG, cursor.getColumnName(cursor.getColumnIndex(DbHelper.C_TYPE)));
		cursor.moveToFirst();
		
		if (cursor.getCount() > 0) {
			textView = (TextView) findViewById(R.id.textDetEventType);
			textView.setText(cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.C_TYPE)));
			
			textView = (TextView) findViewById(R.id.textDetCity);
			textView.setText(cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.C_CITY)));
			
			textView = (TextView) findViewById(R.id.textDetDate);
			textView.setText(cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.C_DATE)));
			
			textView = (TextView) findViewById(R.id.textDetEventName);
			textView.setText(cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.C_NAME)));
			
			textView = (TextView) findViewById(R.id.textDetTime);
			textView.setText(cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.C_TIME)));
		}
	}

}
