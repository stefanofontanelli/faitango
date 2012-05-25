package com.retis.faitango.remote;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.retis.faitango.MainView;
import com.retis.faitango.database.EventTable;
import com.retis.faitango.database.ProvinceProvider;
import com.retis.faitango.database.ProvinceTable;
import com.retis.faitango.database.RegionProvider;
import com.retis.faitango.database.RegionTable;


import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/** Search parameter container <br><br>
 * 
 * This parcelable class holds the query parameters to be used by the EventReader when
 * fetching data from the remote server. 
 * The class is implements Parcelable in order to be included as extras to the Intent
 * passed to EventReader.  
 * 
 * @author Christian Nastasi
 */
public class EventFilter implements Parcelable, Cloneable {

	private static final String TAG = "EventFilter";
	public Set<EventType> types;
	public Date dateFrom;
	public Date dateTo;
	public String country;
	public String region;
	public String province;
	public String title; 

	public EventFilter() {
		types = new HashSet<EventType>();
		dateFrom = null;
		dateTo = null;
		country = null;
		region = null;
		province = null;
		title = null;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	/** Serializer <br><br>
	 * 
	 * Write the parcelable object to the output <code>out</code> parcel.
	 * The order of serialization is the following:
	 * <ul>
	 * <li> {@code int}: the length of the types in the {@link #types} set;
	 * <li> {@code TypedArray}: an EventType array representing the {@link #types} set;
	 * <li> {@code long}: {@literal 0} or {@link #dateFrom} as milliseconds from 1970 (see {@link Date#getTime()});
	 * <li> {@code long}: {@literal 0} or {@link #dateTo} as milliseconds from 1970 (see {@link Date#getTime()});
	 * <li> {@code String}: the {@link #country} value;
	 * <li> {@code String}: the {@link #region} value;
	 * <li> {@code String}: the {@link #area} value;
	 * <li> {@code String}: the {@link #title} value;
	 * </ul> 
	 * 
	 * @param out	The output parcel object
	 * @param flags	The parcelling flags
	 */
	@Override
	public void writeToParcel(Parcel out, int flags) {
		EventType t[] = new EventType[types.size()];
		types.toArray(t);
		out.writeInt(t.length);
		out.writeTypedArray(t, 0);
		if (dateFrom != null)
			out.writeLong(dateFrom.getTime());
		else 
			out.writeLong(0);
		if (dateTo != null)
			out.writeLong(dateTo.getTime());
		else
			out.writeLong(0);
		out.writeString(country);
		out.writeString(region);
		out.writeString(province);
		out.writeString(title);
	}

	public static final Parcelable.Creator<EventFilter> CREATOR = 
			new Parcelable.Creator<EventFilter>() {
		public EventFilter createFromParcel(Parcel in) {
			return new EventFilter(in);
		}

		public EventFilter[] newArray(int size) {
			return new EventFilter[size];
		}
	};

	/** Deserializer <br><br>
	 * 
	 * This constructor is called by the {@link #CREATOR} object to construct the EventFilter
	 * from the input parcel.
	 * The order of deserialization is the same order of serialization specified in {@link #writeToParcel(Parcel, int)}.
	 * 
	 * @param in The input parcel object
	 */
	private EventFilter(Parcel in) {
		types = new HashSet<EventType>();
		EventType[] t = new EventType[in.readInt()]; 
		in.readTypedArray(t, EventType.CREATOR);
		for (EventType i : t) {
			types.add(i);
		}
		long time;
		time = in.readLong();
		if (time != 0)
			dateFrom = new Date(time);
		time = in.readLong();
		if (time != 0)
			dateTo = new Date(time);
		country = in.readString();
		region = in.readString();
		province = in.readString();
		title = in.readString();
	}
	
	public String getWhereClause(ContentResolver cr) {
		String where = null;
		
		if (province != null && !province.equals("") && !province.equals(MainView.ALL_PROVINCES_LABEL)) {
			where = getWhereForProvince(province);
		} else if (region != null && !region.equals("") && !region.equals(MainView.ALL_REGIONS_LABEL)) {
			where = getWhereForRegion(cr, region);
    	} else if (country != null && !country.equals("") && !country.equals(MainView.ALL_COUNTRIES_LABEL)) {
    		where = getWhereForCountry(cr, country);
    	}
		if (dateFrom != null) {
			if (where == null) {
				where = "";
			}
			if (!where.equals("")) {
				where += " AND ";
			}
			where += EventTable.DATE + " >= " + dateFrom.getTime() + " ";
		}
		if (dateTo != null) {
			if (where == null) {
				where = "";
			}
			if (!where.equals("")) {
				where += " AND ";
			}
			where += EventTable.DATE + " <= " + dateTo.getTime() + " ";
		}
		Log.d(TAG, "WHERE: " + where);
		return where;
	}
	
	protected String getWhereForProvince(String code) {
		return "upper(" + EventTable.CITY + ") LIKE '%" + code + "%' ";
	}
	
	protected String getWhereForRegion(ContentResolver cr, String name) {
		
		String where = "";
		Cursor cursor = cr.query(ProvinceProvider.CONTENT_URI,
		 		  				 null,
		 		  				 ProvinceTable.REGION + "='" + name + "' ",
		 		  				 null,
		 		  				 null);
        if (cursor.moveToFirst()) {
     		where += "(";
        	do {
        		if (!where.equals("(")) {
        			where += " OR ";
        		}
        		where += getWhereForProvince(cursor.getString(cursor.getColumnIndex(ProvinceTable.CODE)));
        	} while(cursor.moveToNext());
        	where += ")";
        }
        cursor.close();
		return where;
	}
	
	public String getWhereForCountry(ContentResolver cr, String id) {
		
		String where = null;
  		Cursor r = cr.query(RegionProvider.CONTENT_URI,
 		  		  			null,
 		  		  			RegionTable.COUNTRY + "='" + id + "' ",
 		  		  			null,
 		  		  			null);
  		if (r.moveToFirst()) {
        	do {
        		if (where == null) {
        			where = "";
        		}
        		if (!where.equals("")) {
        			where += " OR ";
        		}
        		where += getWhereForRegion(cr, r.getString(r.getColumnIndex(RegionTable.NAME)));
        	} while(r.moveToNext());
        }
  		r.close();
		return where;
	}
	
	public boolean isEmpty() {
		if (province != null || region != null || country != null || 
			dateFrom != null || dateTo != null || !types.isEmpty()) {
			return false;
		}
		return true;
	}

	public Object clone()  {
		EventFilter f = new EventFilter();
		for (EventType e : types)
			f.types.add(e);
		if (dateFrom != null)
			f.dateFrom = (Date)dateFrom.clone();
		else
			f.dateFrom = null;
		if (dateTo != null)
			f.dateTo = (Date)dateTo.clone();
		else 
			f.dateTo = null;
		f.country = country;
		f.region = region;
		f.province = province;
		f.title = title;
		return f;
	}
}