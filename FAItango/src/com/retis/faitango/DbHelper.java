package com.retis.faitango;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper{
	private static final String TAG = "DbHelper";
	public static final String DB_NAME = "events.db";
	public static final int DB_VERSION = 3;
	public static final String TABLE ="events";
	public static final String C_ID = BaseColumns._ID;
	public static final String C_CITY = "city";
	public static final String C_DATE = "date";
	public static final String C_TIME = "time";
	public static final String C_TYPE = "type";
	public static final String C_NAME = "name";

	public DbHelper(Context c) {
		super(c, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table " + TABLE + " (" + C_ID + " int primary key, "
				+ C_CITY + " text, " + C_DATE + " integer, " + C_TIME + " text, "
				+ C_TYPE + " text, " + C_NAME + " text)";
		db.execSQL(sql);
		Log.d(TAG, "onCreated sql: " + sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Called whenever newVersion != oldVersion
		db.execSQL("drop table if exists " + TABLE);
		onCreate(db);
		Log.d(TAG, "onUpgraded");
	}
}
