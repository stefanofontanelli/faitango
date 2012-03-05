package com.retis.faitango.database;

import android.provider.BaseColumns;

public interface EventTable extends BaseColumns {
	String TABLE_NAME = "events";
	String NAME = "name";
	String CITY = "city";
	String DATE = "date";
	String TIME = "time";
	String TYPE = "type";
	String[] COLUMNS = new String[] { _ID, NAME, CITY, DATE, TIME, TYPE };
}