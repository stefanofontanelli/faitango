package com.retis.faitango.database;

import android.provider.BaseColumns;

public interface EventDetailTable extends BaseColumns {
	String TABLE_NAME = "eventdetails";
	String EVENT = "event";
	String TITLE = "title";
	String CITY = "city";
	String DATE = "date";
	String TIME = "time";
	String TYPE = "type";
	String CREATED = "created";
	String AF = "af";
	String EMAIL = "email";
	String DESCRIPTION = "description";
	String LINK = "link";
	String[] COLUMNS = new String[] { _ID, EVENT, TITLE, CITY, 
									  DATE, TIME, TYPE, CREATED, 
									  AF, EMAIL, DESCRIPTION, LINK };
}