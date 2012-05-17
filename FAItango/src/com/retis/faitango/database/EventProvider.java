package com.retis.faitango.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class EventProvider extends ContentProvider {
	
	private static final String TAG = "EventProvider";
	private static final String baseDomain = "com.retis.provider.faitango.events";
	private static final String URI = "content://" + baseDomain + "/events";
	public static final Uri CONTENT_URI = Uri.parse(URI);
	private static final int EVENTS = 1;
	private static final int EVENT_ID = 2;
	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(baseDomain, "events", EVENTS);
		uriMatcher.addURI(baseDomain, "events/#", EVENT_ID);
	}
	private SQLiteDatabase db;
	private DatabaseHelper dbHelper;
	
	@Override
	public boolean onCreate() {
		Context context = getContext();
		dbHelper = new DatabaseHelper(context);
		db = dbHelper.getWritableDatabase();
		return (db == null) ? false : true;
	}
	
	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
			case EVENTS:
				return "vnd.android.cursor.dir/vnd.faitango.events";
			case EVENT_ID:
				return "vnd.android.cursor.item/vnd.faitango.events";
			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sort) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(EventTable.TABLE_NAME);
		qb.setDistinct(true);
		switch (uriMatcher.match(uri)) {
			case EVENT_ID:
				qb.appendWhere(EventTable._ID + "=" + uri.getPathSegments().get(1));
				break;
			default:
				break;
		}
		String orderBy;
		if (TextUtils.isEmpty(sort)) {
			orderBy = EventTable.NAME;
		} else {
			orderBy = sort;
		}
		Cursor c = qb.query(db, projection, selection, selectionArgs, "_id", null, orderBy);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}
	
	@Override
	public Uri insert(Uri _uri, ContentValues _initialValues) {
		long rowID = db.insert(EventTable.TABLE_NAME, null, _initialValues);
		if (rowID > 0) {
			Log.d(TAG, "Added the row: " + rowID + ", values: " + _initialValues);
			Uri uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
			getContext().getContentResolver().notifyChange(uri, null);
			return uri;
		}
		throw new SQLException("Failed to insert row into " + _uri);
	}
	
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		int count;
		switch (uriMatcher.match(uri)) {
			case EVENTS:
				count = db.delete(EventTable.TABLE_NAME, where, whereArgs);
			break;
			case EVENT_ID:
				String segment = uri.getPathSegments().get(1);
				count = db.delete(EventTable.TABLE_NAME, EventTable._ID + "="
						+ segment + (!TextUtils.isEmpty(where) ? " AND (" + where + ")" : ""), whereArgs);
			break;
			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
		int count;
		switch (uriMatcher.match(uri)) {
			case EVENTS:
				count = db.update(EventTable.TABLE_NAME, values, where, whereArgs);
				Log.d(TAG, "Update values: " + values);
			break;
			case EVENT_ID:
				String segment = uri.getPathSegments().get(1);
				count = db.update(EventTable.TABLE_NAME, values, EventTable._ID
						+ "=" + segment + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
				Log.d(TAG, "Update values: " + values + " in row: " + segment);
			break;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
}