package com.retis.faitango.database;

import android.provider.BaseColumns;

public interface CountryTable extends BaseColumns {
	String TABLE_NAME = "countries";
	String NAME = "name";
	String[] COLUMNS = new String[] { _ID, NAME };
}