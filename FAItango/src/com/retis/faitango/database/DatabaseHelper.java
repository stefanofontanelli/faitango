package com.retis.faitango.database;

import java.text.MessageFormat;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String TAG = "DatabaseHelper";
	private static final String DATABASE_NAME = "faitango.db";
	private static final String CREATE_COUNTRY_SQL = "CREATE TABLE {0} " + 
													 "({1} INTEGER PRIMARY KEY AUTOINCREMENT," +
													 " {2} TEXT NOT NULL);";
	private static final String CREATE_REGION_SQL = "CREATE TABLE {0} " + 
			  										"({1} INTEGER PRIMARY KEY AUTOINCREMENT," +
			  										" {2} TEXT NOT NULL," +
			  										" {3} INTEGER NOT NULL);";
	private static final String CREATE_PROVINCE_SQL = "CREATE TABLE {0} " + 
			 										  "({1} INTEGER PRIMARY KEY AUTOINCREMENT," +
			 										  " {2} TEXT NOT NULL," +
			 										  " {3} TEXT NOT NULL," +
			 										  " {4} INTEGER NOT NULL);";
	private static final String CREATE_EVENT_SQL = "CREATE TABLE {0} " + 
												   "({1} INTEGER PRIMARY KEY AUTOINCREMENT," +
												   " {2} TEXT NOT NULL" +
												   " {3} INTEGER NOT NULL" +
												   " {4} TEXT NOT NULL" +
												   " {5} TEXT NOT NULL" +
												   " {6} TEXT NOT NULL" +
												   ");";
	private static final String CREATE_EVENT_DETAIL_SQL = "CREATE TABLE {0} " + 
			   											  "({1} INTEGER PRIMARY KEY AUTOINCREMENT," +
			   											  " {2} TEXT NOT NULL" +
			   											  " {3} TEXT NOT NULL" +
			   											  " {4} INTEGER NOT NULL" +
			   											  " {5} TEXT NOT NULL" +
			   											  " {6} TEXT NOT NULL" +
			   											  " {7} INTEGER NOT NULL" +
			   											  " {8} TEXT NOT NULL" +
			   											  " {9} INTEGER NOT NULL" +
			   											  " {10} TEXT NOT NULL" +
			   											  " {11} TEXT NOT NULL" +
			   											  " {12} TEXT NOT NULL" +
			   											  ");";
	private static final String DROP_TABLE_SQL = "DROP TABLE IF EXISTS {0}";
	private static final int SCHEMA_VERSION = 1;
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, SCHEMA_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "onCreate START ...");
		
		db.execSQL(MessageFormat.format(CREATE_COUNTRY_SQL,
										CountryTable.TABLE_NAME,
										CountryTable._ID,
										CountryTable.NAME));
		db.execSQL(MessageFormat.format(CREATE_REGION_SQL,
										RegionTable.TABLE_NAME,
										RegionTable._ID,
										RegionTable.NAME,
										RegionTable.COUNTRY));
		db.execSQL(MessageFormat.format(CREATE_PROVINCE_SQL,
										ProvinceTable.TABLE_NAME,
										ProvinceTable._ID,
										ProvinceTable.NAME,
										ProvinceTable.CODE,
										ProvinceTable.REGION));
		db.execSQL(MessageFormat.format(CREATE_EVENT_SQL,
										EventTable.TABLE_NAME,
										EventTable._ID,
										EventTable.CITY,
										EventTable.DATE,
										EventTable.TIME,
										EventTable.TYPE,
										EventTable.NAME));
		db.execSQL(MessageFormat.format(CREATE_EVENT_DETAIL_SQL,
										EventDetailTable.TABLE_NAME,
										EventDetailTable._ID,
										EventDetailTable.TITLE,
										EventDetailTable.CITY,
										EventDetailTable.DATE,
										EventDetailTable.TIME,
										EventDetailTable.TYPE,
										EventDetailTable.EVENT,
										EventDetailTable.LINK,
										EventDetailTable.CREATED,
										EventDetailTable.AF,
										EventDetailTable.EMAIL,
										EventDetailTable.DESCRIPTION));
		
		insertCountry(db, 118, "Italia"); // The ID of Italy in FAITango events database.
		
		insertRegion(db, 1, "Abruzzo", 118);
		insertProvince(db, 1, "Chieti", "CH", 1);
		insertProvince(db, 2, "L'Aquila", "AQ", 1);
		insertProvince(db, 3, "Pescara", "PE", 1);
		insertProvince(db, 4, "Teramo", "TE", 1);
		
		insertRegion(db, 2, "Basilicata", 118);
		insertProvince(db, 5, "Matera", "MT", 2);
		insertProvince(db, 6, "Potenza", "PZ", 2);
		
		insertRegion(db, 3, "Calabria", 118);
		insertProvince(db, 7, "Catanzaro", "CZ", 3);
		insertProvince(db, 8, "Cosenza", "CS", 3);
		insertProvince(db, 9, "Crotone", "KR", 3);
		insertProvince(db, 10, "Reggio Calabria", "RC", 3);
		insertProvince(db, 11, "Vibo Valentia", "VV", 3);
		
		insertRegion(db, 4, "Campania", 118);
		insertProvince(db, 12, "Avellino", "AV", 4);
		insertProvince(db, 13, "Benevento", "BN", 4);
		insertProvince(db, 14, "Caserta", "CE", 4);
		insertProvince(db, 15, "Napoli", "NA", 4);
		insertProvince(db, 16, "Salerno", "SA", 4);
		
		insertRegion(db, 5, "Emilia-Romagna", 118);
		insertProvince(db, 17, "Bologna", "BO", 5);
		insertProvince(db, 18, "Ferrara", "FE", 5);
		insertProvince(db, 19, "Forl“-Cesena", "FC", 5);
		insertProvince(db, 20, "Modena", "MO", 5);
		insertProvince(db, 21, "Parma", "PR", 5);
		insertProvince(db, 22, "Piacenza", "PC", 5);
		insertProvince(db, 23, "Ravenna", "RA", 5);
		insertProvince(db, 24, "Reggio Emilia", "RE", 5);
		insertProvince(db, 25, "Rimini", "RN", 5);
		
		insertRegion(db, 6, "Friuli-Venezia Giulia", 118);
		insertProvince(db, 26, "Gorizia", "GO", 6);
		insertProvince(db, 27, "Pordenone", "PN", 6);
		insertProvince(db, 28, "Trieste", "TS", 6);
		insertProvince(db, 29, "Udine", "UD", 6);
		
		insertRegion(db, 7, "Lazio", 118);
		insertProvince(db, 30, "Frosinone", "FR", 7);
		insertProvince(db, 31, "Latina", "LT", 7);
		insertProvince(db, 32, "Rieti", "RI", 7);
		insertProvince(db, 33, "Roma", "RM", 7);
		insertProvince(db, 34, "Viterbo", "VT", 7);
		
		insertRegion(db, 8, "Liguria", 118);
		insertProvince(db, 35, "Genova", "GE", 8);
		insertProvince(db, 36, "Imperia", "IM", 8);
		insertProvince(db, 37, "La Spezia", "SP", 8);
		insertProvince(db, 38, "Savona", "SV", 8);
		
		insertRegion(db, 9, "Lombardia", 118);
		insertProvince(db, 39, "Bergamo", "BG", 9);
		insertProvince(db, 40, "Brescia", "BS", 9);
		insertProvince(db, 41, "Como", "CO", 9);
		insertProvince(db, 42, "Cremona", "CR", 9);
		insertProvince(db, 43, "Lecco", "LC", 9);
		insertProvince(db, 44, "Lodi", "LO", 9);
		insertProvince(db, 45, "Mantova", "MN", 9);
		insertProvince(db, 46, "Milano", "MI", 9);
		insertProvince(db, 47, "Monza e della Brianza", "MB", 9);
		insertProvince(db, 48, "Pavia", "PV", 9);
		insertProvince(db, 49, "Sondrio", "SO", 9);
		insertProvince(db, 50, "Varese", "VA", 9);
		
		insertRegion(db, 10, "Marche", 118);
		insertProvince(db, 51, "Ancona", "AN", 10);
		insertProvince(db, 52, "Ascoli Piceno", "AP", 10);
		insertProvince(db, 53, "Fermo", "FM", 10);
		insertProvince(db, 54, "Macerata", "MC", 10);
		insertProvince(db, 55, "Pesaro e Urbino", "PU", 10);
		
		insertRegion(db, 11, "Molise", 118);
		insertProvince(db, 56, "Campobasso", "CB", 11);
		insertProvince(db, 57, "Isernia", "IS", 11);
		
		insertRegion(db, 12, "Piemonte", 118);
		insertProvince(db, 58, "Alessandria", "AL", 12);
		insertProvince(db, 59, "Asti", "AT", 12);
		insertProvince(db, 60, "Biella", "BI", 12);
		insertProvince(db, 61, "Cuneo", "CN", 12);
		insertProvince(db, 62, "Novara", "NO", 12);
		insertProvince(db, 63, "Torino", "TO", 12);
		insertProvince(db, 64, "Verbano-Cusio-Ossola", "VB", 12);
		insertProvince(db, 65, "Vercelli", "VE", 12);
		
		insertRegion(db, 13, "Puglia", 118);
		insertProvince(db, 66, "Bari", "BA", 13);
		insertProvince(db, 67, "Barletta-Andria-Trani", "BT", 13);
		insertProvince(db, 68, "Brindisi", "BR", 13);
		insertProvince(db, 69, "Foggia", "FG", 13);
		insertProvince(db, 70, "Lecce", "LE", 13);
		insertProvince(db, 71, "Taranto", "TA", 13);
		
		insertRegion(db, 14, "Sardegna", 118);
		insertProvince(db, 72, "Cagliari", "CA", 14);
		insertProvince(db, 73, "Carbonia-Iglesias", "CI", 14);
		insertProvince(db, 74, "Nuoro", "NU", 14);
		insertProvince(db, 75, "Olbia-Tempio", "OT", 14);
		insertProvince(db, 76, "Oristano", "OR", 14);
		insertProvince(db, 77, "Medio Campidano", "VS", 14);
		insertProvince(db, 78, "Sassari", "SS", 14);
		insertProvince(db, 79, "Ogliastra", "OG", 14);
		
		insertRegion(db, 15, "Sicilia", 118);
		insertProvince(db, 80, "Agrigento", "AG", 15);
		insertProvince(db, 81, "Caltanissetta", "CL", 15);
		insertProvince(db, 82, "Catania", "CT", 15);
		insertProvince(db, 83, "Enna", "EN", 15);
		insertProvince(db, 84, "Messina", "ME", 15);
		insertProvince(db, 85, "Palermo", "PA", 15);
		insertProvince(db, 86, "Ragusa", "RA", 15);
		insertProvince(db, 87, "Siracusa", "SR", 15);
		insertProvince(db, 88, "Trapani", "TP", 15);
		
		insertRegion(db, 16, "Toscana", 118);
		insertProvince(db, 89, "Arezzo", "AR", 16);
		insertProvince(db, 90, "Firenze", "FI", 16);
		insertProvince(db, 91, "Grosseto", "GR", 16);
		insertProvince(db, 92, "Livorno", "LI", 16);
		insertProvince(db, 93, "Lucca", "LU", 16);
		insertProvince(db, 94, "Massa-Carrara", "MS", 16);
		insertProvince(db, 95, "Pisa", "PI", 16);
		insertProvince(db, 96, "Pistoia", "PT", 16);
		insertProvince(db, 97, "Prato", "PO", 16);
		insertProvince(db, 98, "Siena", "SI", 16);
		
		insertRegion(db, 17, "Trentino-Alto Adige", 118);
		insertProvince(db, 99, "Bolzano", "BZ", 17);
		insertProvince(db, 100, "Trento", "TN", 17);
		
		insertRegion(db, 18, "Umbria", 118);
		insertProvince(db, 101, "Perugia", "PG", 18);
		insertProvince(db, 102, "Terni", "TR", 18);
		
		insertRegion(db, 19, "Valle d'Aosta", 118);
		insertProvince(db, 103, "Aosta", "AO", 19);
		
		insertRegion(db, 20, "Veneto", 118);
		insertProvince(db, 104, "Belluno", "BL", 20);
		insertProvince(db, 105, "Padova", "PD", 20);
		insertProvince(db, 106, "Rovigo", "RO", 20);
		insertProvince(db, 107, "Treviso", "TV", 20);
		insertProvince(db, 108, "Venezia", "VE", 20);
		insertProvince(db, 109, "Verona", "VR", 20);
		insertProvince(db, 110, "Vicenza", "VI", 20);
		
		Log.d(TAG, "onCreate END!");
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, "onUpgrade START ...");
		db.execSQL(MessageFormat.format(DROP_TABLE_SQL, CountryTable.TABLE_NAME));
		db.execSQL(MessageFormat.format(DROP_TABLE_SQL, RegionTable.TABLE_NAME));
		db.execSQL(MessageFormat.format(DROP_TABLE_SQL, ProvinceTable.TABLE_NAME));
		db.execSQL(MessageFormat.format(DROP_TABLE_SQL, EventTable.TABLE_NAME));
		db.execSQL(MessageFormat.format(DROP_TABLE_SQL, EventDetailTable.TABLE_NAME));
		onCreate(db);
		Log.d(TAG, "onUpgrade END!");
	}
	
	private void insertCountry(SQLiteDatabase db, Integer id, String name) {
		ContentValues value = new ContentValues();
		value.put(CountryTable._ID, id);
		value.put(CountryTable.NAME, name);
		db.insert(CountryTable.TABLE_NAME, null, value);
	}
	
	private void insertRegion(SQLiteDatabase db, Integer id, String name, Integer country) {
		ContentValues value = new ContentValues();
		value.put(RegionTable._ID, id);
		value.put(RegionTable.NAME, name);
		value.put(RegionTable.COUNTRY, country);
		db.insert(RegionTable.TABLE_NAME, null, value);
	}
	
	private void insertProvince(SQLiteDatabase db, Integer id, String name, String code, Integer region) {
		ContentValues value = new ContentValues();
		value.put(ProvinceTable._ID, id);
		value.put(ProvinceTable.NAME, name);
		value.put(ProvinceTable.CODE, code);
		value.put(ProvinceTable.REGION, region);
		db.insert(ProvinceTable.TABLE_NAME, null, value);
	}
	
	public void insertEvent(SQLiteDatabase db, 
							Integer id, String name, String city, Integer date, String time, String type) {
		ContentValues value = new ContentValues();
		value.put(EventTable._ID, id);
		value.put(EventTable.NAME, name);
		value.put(EventTable.CITY, city);
		value.put(EventTable.DATE, date);
		value.put(EventTable.TIME, time);
		value.put(EventTable.TYPE, type);
		db.insert(EventTable.TABLE_NAME, null, value);
	}
	
	public void insertEventDetail(SQLiteDatabase db, Integer id, String title,
								  String city, Integer date, String time,
								  String type, Integer event, String link,
								  Integer created, String email, String description) {
		ContentValues value = new ContentValues();
		value.put(EventDetailTable._ID, id);
		value.put(EventDetailTable.TITLE, title);
		value.put(EventDetailTable.CITY, city);
		value.put(EventDetailTable.DATE, date);
		value.put(EventDetailTable.TIME, time);
		value.put(EventDetailTable.TYPE, type);
		value.put(EventDetailTable.EVENT, event);
		value.put(EventDetailTable.LINK, link);
		value.put(EventDetailTable.CREATED, created);
		value.put(EventDetailTable.EMAIL, email);
		value.put(EventDetailTable.DESCRIPTION, description);
		db.insert(EventDetailTable.TABLE_NAME, null, value);
	}
}