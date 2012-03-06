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

public class CountryProvider extends ContentProvider {
	
	private static final String TAG = "CountryProvider";
	private static final String baseDomain = "com.retis.provider.faitango.countries";
	private static final String URI = "content://" + baseDomain + "/countries";
	public static final Uri CONTENT_URI = Uri.parse(URI);
	private static final int COUNTRIES = 1;
	private static final int COUNTRY_ID = 2;
	private static final UriMatcher uriMatcher;
	// Populate the UriMatcher object, where a URI ending in ÔitemsÕ will correspond to a request for all items, 
	// and Ôitems/[rowID]Õ represents a single row.
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(baseDomain, "countries", COUNTRIES);
		uriMatcher.addURI(baseDomain, "countries/#", COUNTRY_ID);
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
			case COUNTRIES:
				return "vnd.android.cursor.dir/vnd.faitango.countries";
			case COUNTRY_ID:
				return "vnd.android.cursor.item/vnd.faitango.coutries";
			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sort) {
		
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(CountryTable.TABLE_NAME);
		
		switch (uriMatcher.match(uri)) {
			case COUNTRY_ID:
				qb.appendWhere(CountryTable._ID + "=" + uri.getPathSegments().get(1));
				break;
			default:
				break;
		}
		// If no sort order is specified sort by name.
		String orderBy;
		if (TextUtils.isEmpty(sort)) {
			orderBy = CountryTable.NAME;
		} else {
			orderBy = sort;
		}
		// Apply the query to the underlying database.
		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
		// Register the contexts ContentResolver to be notified if 
		// the cursor result set changes.
		c.setNotificationUri(getContext().getContentResolver(), uri);
		// Return a cursor to the query result.
		return c;
	}
	
	@Override
	public Uri insert(Uri _uri, ContentValues _initialValues) {
		// Insert the new row, will return the row number if successful.
		long rowID = db.insert(CountryTable.TABLE_NAME, null, _initialValues);
		// Return a URI to the newly inserted row on success.
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
			case COUNTRIES:
				count = db.delete(CountryTable.TABLE_NAME, where, whereArgs);
			break;
			case COUNTRY_ID:
				String segment = uri.getPathSegments().get(1);
				count = db.delete(CountryTable.TABLE_NAME, CountryTable._ID + "="
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
			case COUNTRIES:
				count = db.update(CountryTable.TABLE_NAME, values, where, whereArgs);
			break;
			case COUNTRY_ID:
				String segment = uri.getPathSegments().get(1);
				count = db.update(CountryTable.TABLE_NAME, values, CountryTable._ID
						+ "=" + segment + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
			break;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
}