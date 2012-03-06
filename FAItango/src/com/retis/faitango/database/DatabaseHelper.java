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
			 										  " {4} TEXT NOT NULL);";
	private static final String CREATE_EVENT_SQL = "CREATE TABLE {0} " + 
												   "({1} INTEGER PRIMARY KEY AUTOINCREMENT," +
												   " {2} TEXT NOT NULL," +
												   " {3} INTEGER NOT NULL," +
												   " {4} TEXT NOT NULL," +
												   " {5} TEXT NOT NULL," +
												   " {6} TEXT NOT NULL" +
												   ");";
	private static final String CREATE_EVENT_DETAIL_SQL = "CREATE TABLE {0} " + 
			   											  "({1} INTEGER PRIMARY KEY AUTOINCREMENT," +
			   											  " {2} TEXT NOT NULL," +
			   											  " {3} TEXT NOT NULL," +
			   											  " {4} INTEGER NOT NULL," +
			   											  " {5} TEXT NOT NULL," +
			   											  " {6} TEXT NOT NULL," +
			   											  " {7} INTEGER NOT NULL," +
			   											  " {8} TEXT NOT NULL," +
			   											  " {9} INTEGER NOT NULL," +
			   											  " {10} TEXT NOT NULL," +
			   											  " {11} TEXT NOT NULL," +
			   											  " {12} TEXT NOT NULL" +
			   											  ");";
	private static final String DROP_TABLE_SQL = "DROP TABLE IF EXISTS {0}";
	private static final int SCHEMA_VERSION = 2;
	
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
		insertProvince(db, 1, "Chieti", "CH", "Abruzzo");
		insertProvince(db, 2, "L'Aquila", "AQ", "Abruzzo");
		insertProvince(db, 3, "Pescara", "PE", "Abruzzo");
		insertProvince(db, 4, "Teramo", "TE", "Abruzzo");
		
		insertRegion(db, 2, "Basilicata", 118);
		insertProvince(db, 5, "Matera", "MT", "Basilicata");
		insertProvince(db, 6, "Potenza", "PZ", "Basilicata");
		
		insertRegion(db, 3, "Calabria", 118);
		insertProvince(db, 7, "Catanzaro", "CZ", "Calabria");
		insertProvince(db, 8, "Cosenza", "CS", "Calabria");
		insertProvince(db, 9, "Crotone", "KR", "Calabria");
		insertProvince(db, 10, "Reggio Calabria", "RC", "Calabria");
		insertProvince(db, 11, "Vibo Valentia", "VV", "Calabria");
		
		insertRegion(db, 4, "Campania", 118);
		insertProvince(db, 12, "Avellino", "AV", "Campania");
		insertProvince(db, 13, "Benevento", "BN", "Campania");
		insertProvince(db, 14, "Caserta", "CE", "Campania");
		insertProvince(db, 15, "Napoli", "NA", "Campania");
		insertProvince(db, 16, "Salerno", "SA", "Campania");
		
		insertRegion(db, 5, "Emilia-Romagna", 118);
		insertProvince(db, 17, "Bologna", "BO", "Emilia-Romagna");
		insertProvince(db, 18, "Ferrara", "FE", "Emilia-Romagna");
		insertProvince(db, 19, "Forl“-Cesena", "FC", "Emilia-Romagna");
		insertProvince(db, 20, "Modena", "MO", "Emilia-Romagna");
		insertProvince(db, 21, "Parma", "PR", "Emilia-Romagna");
		insertProvince(db, 22, "Piacenza", "PC", "Emilia-Romagna");
		insertProvince(db, 23, "Ravenna", "RA", "Emilia-Romagna");
		insertProvince(db, 24, "Reggio Emilia", "RE", "Emilia-Romagna");
		insertProvince(db, 25, "Rimini", "RN", "Emilia-Romagna");
		
		insertRegion(db, 6, "Friuli-Venezia Giulia", 118);
		insertProvince(db, 26, "Gorizia", "GO", "Friuli-Venezia Giulia");
		insertProvince(db, 27, "Pordenone", "PN", "Friuli-Venezia Giulia");
		insertProvince(db, 28, "Trieste", "TS", "Friuli-Venezia Giulia");
		insertProvince(db, 29, "Udine", "UD", "Friuli-Venezia Giulia");
		
		insertRegion(db, 7, "Lazio", 118);
		insertProvince(db, 30, "Frosinone", "FR", "Lazio");
		insertProvince(db, 31, "Latina", "LT", "Lazio");
		insertProvince(db, 32, "Rieti", "RI", "Lazio");
		insertProvince(db, 33, "Roma", "RM", "Lazio");
		insertProvince(db, 34, "Viterbo", "VT", "Lazio");
		
		insertRegion(db, 8, "Liguria", 118);
		insertProvince(db, 35, "Genova", "GE", "Liguria");
		insertProvince(db, 36, "Imperia", "IM", "Liguria");
		insertProvince(db, 37, "La Spezia", "SP", "Liguria");
		insertProvince(db, 38, "Savona", "SV", "Liguria");
		
		insertRegion(db, 9, "Lombardia", 118);
		insertProvince(db, 39, "Bergamo", "BG", "Lombardia");
		insertProvince(db, 40, "Brescia", "BS", "Lombardia");
		insertProvince(db, 41, "Como", "CO", "Lombardia");
		insertProvince(db, 42, "Cremona", "CR", "Lombardia");
		insertProvince(db, 43, "Lecco", "LC", "Lombardia");
		insertProvince(db, 44, "Lodi", "LO", "Lombardia");
		insertProvince(db, 45, "Mantova", "MN", "Lombardia");
		insertProvince(db, 46, "Milano", "MI", "Lombardia");
		insertProvince(db, 47, "Monza e della Brianza", "MB", "Lombardia");
		insertProvince(db, 48, "Pavia", "PV", "Lombardia");
		insertProvince(db, 49, "Sondrio", "SO", "Lombardia");
		insertProvince(db, 50, "Varese", "VA", "Lombardia");
		
		insertRegion(db, 10, "Marche", 118);
		insertProvince(db, 51, "Ancona", "AN", "Marche");
		insertProvince(db, 52, "Ascoli Piceno", "AP", "Marche");
		insertProvince(db, 53, "Fermo", "FM", "Marche");
		insertProvince(db, 54, "Macerata", "MC", "Marche");
		insertProvince(db, 55, "Pesaro e Urbino", "PU", "Marche");
		
		insertRegion(db, 11, "Molise", 118);
		insertProvince(db, 56, "Campobasso", "CB", "Molise");
		insertProvince(db, 57, "Isernia", "IS", "Molise");
		
		insertRegion(db, 12, "Piemonte", 118);
		insertProvince(db, 58, "Alessandria", "AL", "Piemonte");
		insertProvince(db, 59, "Asti", "AT", "Piemonte");
		insertProvince(db, 60, "Biella", "BI", "Piemonte");
		insertProvince(db, 61, "Cuneo", "CN", "Piemonte");
		insertProvince(db, 62, "Novara", "NO", "Piemonte");
		insertProvince(db, 63, "Torino", "TO", "Piemonte");
		insertProvince(db, 64, "Verbano-Cusio-Ossola", "VB", "Piemonte");
		insertProvince(db, 65, "Vercelli", "VE", "Piemonte");
		
		insertRegion(db, 13, "Puglia", 118);
		insertProvince(db, 66, "Bari", "BA", "Puglia");
		insertProvince(db, 67, "Barletta-Andria-Trani", "BT", "Puglia");
		insertProvince(db, 68, "Brindisi", "BR", "Puglia");
		insertProvince(db, 69, "Foggia", "FG", "Puglia");
		insertProvince(db, 70, "Lecce", "LE", "Puglia");
		insertProvince(db, 71, "Taranto", "TA", "Puglia");
		
		insertRegion(db, 14, "Sardegna", 118);
		insertProvince(db, 72, "Cagliari", "CA", "Sardegna");
		insertProvince(db, 73, "Carbonia-Iglesias", "CI", "Sardegna");
		insertProvince(db, 74, "Nuoro", "NU", "Sardegna");
		insertProvince(db, 75, "Olbia-Tempio", "OT", "Sardegna");
		insertProvince(db, 76, "Oristano", "OR", "Sardegna");
		insertProvince(db, 77, "Medio Campidano", "VS", "Sardegna");
		insertProvince(db, 78, "Sassari", "SS", "Sardegna");
		insertProvince(db, 79, "Ogliastra", "OG", "Sardegna");
		
		insertRegion(db, 15, "Sicilia", 118);
		insertProvince(db, 80, "Agrigento", "AG", "Sicilia");
		insertProvince(db, 81, "Caltanissetta", "CL", "Sicilia");
		insertProvince(db, 82, "Catania", "CT", "Sicilia");
		insertProvince(db, 83, "Enna", "EN", "Sicilia");
		insertProvince(db, 84, "Messina", "ME", "Sicilia");
		insertProvince(db, 85, "Palermo", "PA", "Sicilia");
		insertProvince(db, 86, "Ragusa", "RA", "Sicilia");
		insertProvince(db, 87, "Siracusa", "SR", "Sicilia");
		insertProvince(db, 88, "Trapani", "TP", "Sicilia");
		
		insertRegion(db, 16, "Toscana", 118);
		insertProvince(db, 89, "Arezzo", "AR", "Toscana");
		insertProvince(db, 90, "Firenze", "FI", "Toscana");
		insertProvince(db, 91, "Grosseto", "GR", "Toscana");
		insertProvince(db, 92, "Livorno", "LI", "Toscana");
		insertProvince(db, 93, "Lucca", "LU", "Toscana");
		insertProvince(db, 94, "Massa-Carrara", "MS", "Toscana");
		insertProvince(db, 95, "Pisa", "PI", "Toscana");
		insertProvince(db, 96, "Pistoia", "PT", "Toscana");
		insertProvince(db, 97, "Prato", "PO", "Toscana");
		insertProvince(db, 98, "Siena", "SI", "Toscana");
		
		insertRegion(db, 17, "Trentino-Alto Adige", 118);
		insertProvince(db, 99, "Bolzano", "BZ", "Trentino-Alto Adige");
		insertProvince(db, 100, "Trento", "TN", "Trentino-Alto Adige");
		
		insertRegion(db, 18, "Umbria", 118);
		insertProvince(db, 101, "Perugia", "PG", "Umbria");
		insertProvince(db, 102, "Terni", "TR", "Umbria");
		
		insertRegion(db, 19, "Valle d'Aosta", 118);
		insertProvince(db, 103, "Aosta", "AO", "Valle d'Aosta");
		
		insertRegion(db, 20, "Veneto", 118);
		insertProvince(db, 104, "Belluno", "BL", "Veneto");
		insertProvince(db, 105, "Padova", "PD", "Veneto");
		insertProvince(db, 106, "Rovigo", "RO", "Veneto");
		insertProvince(db, 107, "Treviso", "TV", "Veneto");
		insertProvince(db, 108, "Venezia", "VE", "Veneto");
		insertProvince(db, 109, "Verona", "VR", "Veneto");
		insertProvince(db, 110, "Vicenza", "VI", "Veneto");
		
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
	
	private void insertProvince(SQLiteDatabase db, Integer id, String name, String code, String region) {
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