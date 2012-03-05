package com.retis.faitango.database;

import android.provider.BaseColumns;

public interface ProvinceTable extends BaseColumns {
	String TABLE_NAME = "provinces";
	String NAME = "name";
	String CODE = "code";
	String REGION = "region";
	String[] COLUMNS = new String[] { _ID, NAME, CODE, REGION };
}