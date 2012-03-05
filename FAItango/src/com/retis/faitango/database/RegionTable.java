package com.retis.faitango.database;

import android.provider.BaseColumns;

public interface RegionTable extends BaseColumns {
	String TABLE_NAME = "regions";
	String NAME = "name";
	String COUNTRY = "country";
	String[] COLUMNS = new String[] { _ID, NAME, COUNTRY };
}