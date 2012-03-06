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

public class RegionProvider extends ContentProvider {
	
	private static final String TAG = "RegionProvider";
	private static final String baseDomain = "com.retis.provider.faitango.regions";
	private static final String URI = "content://" + baseDomain + "/regions";
	public static final Uri CONTENT_URI = Uri.parse(URI);
	private static final int REGIONS = 1;
	private static final int REGION_ID = 2;
	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(baseDomain, "regions", REGIONS);
		uriMatcher.addURI(baseDomain, "regions/#", REGION_ID);
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
			case REGIONS:
				return "vnd.android.cursor.dir/vnd.faitango.regions";
			case REGION_ID:
				return "vnd.android.cursor.item/vnd.faitango.regions";
			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sort) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(RegionTable.TABLE_NAME);
		switch (uriMatcher.match(uri)) {
			case REGION_ID:
				qb.appendWhere(RegionTable._ID + "=" + uri.getPathSegments().get(1));
				break;
			default:
				break;
		}
		String orderBy;
		if (TextUtils.isEmpty(sort)) {
			orderBy = RegionTable.NAME;
		} else {
			orderBy = sort;
		}
		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}
	
	@Override
	public Uri insert(Uri _uri, ContentValues _initialValues) {
		long rowID = db.insert(RegionTable.TABLE_NAME, null, _initialValues);
		if (rowID > 0) {
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
			case REGIONS:
				count = db.delete(RegionTable.TABLE_NAME, where, whereArgs);
			break;
			case REGION_ID:
				String segment = uri.getPathSegments().get(1);
				count = db.delete(RegionTable.TABLE_NAME, RegionTable._ID + "="
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
			case REGIONS:
				count = db.update(RegionTable.TABLE_NAME, values, where, whereArgs);
			break;
			case REGION_ID:
				String segment = uri.getPathSegments().get(1);
				count = db.update(RegionTable.TABLE_NAME, values, RegionTable._ID
						+ "=" + segment + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
			break;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
}