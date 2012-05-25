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

public class ProvinceProvider extends ContentProvider {
	
	//private static final String TAG = "ProvinceProvider";
	private static final String baseDomain = "com.retis.provider.faitango.provinces";
	private static final String URI = "content://" + baseDomain + "/provinces";
	public static final Uri CONTENT_URI = Uri.parse(URI);
	private static final int PROVINCES = 1;
	private static final int PROVINCE_ID = 2;
	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(baseDomain, "provinces", PROVINCES);
		uriMatcher.addURI(baseDomain, "provinces/#", PROVINCE_ID);
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
			case PROVINCES:
				return "vnd.android.cursor.dir/vnd.faitango.provinces";
			case PROVINCE_ID:
				return "vnd.android.cursor.item/vnd.faitango.provinces";
			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sort) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(ProvinceTable.TABLE_NAME);
		switch (uriMatcher.match(uri)) {
			case PROVINCE_ID:
				qb.appendWhere(ProvinceTable._ID + "=" + uri.getPathSegments().get(1));
				break;
			default:
				break;
		}
		String orderBy;
		if (TextUtils.isEmpty(sort)) {
			orderBy = ProvinceTable.NAME;
		} else {
			orderBy = sort;
		}
		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}
	
	@Override
	public Uri insert(Uri _uri, ContentValues _initialValues) {
		long rowID = db.insert(ProvinceTable.TABLE_NAME, null, _initialValues);
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
			case PROVINCES:
				count = db.delete(ProvinceTable.TABLE_NAME, where, whereArgs);
			break;
			case PROVINCE_ID:
				String segment = uri.getPathSegments().get(1);
				count = db.delete(ProvinceTable.TABLE_NAME, ProvinceTable._ID + "="
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
			case PROVINCES:
				count = db.update(ProvinceTable.TABLE_NAME, values, where, whereArgs);
			break;
			case PROVINCE_ID:
				String segment = uri.getPathSegments().get(1);
				count = db.update(ProvinceTable.TABLE_NAME, values, ProvinceTable._ID
						+ "=" + segment + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
			break;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
}