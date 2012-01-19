package com.retis.faitango;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import android.os.Parcel;
import android.os.Parcelable;

/** Search parameter container
 * 
 * This parcelable class holds the query parameters to be used by the EventReader when
 * fetching data from the remote server. 
 * The class is implements Parcelable in order to be included as extras to the Intent
 * passed to EventReader.  
 * 
 * @author chris
 */
public class EventFilter implements Parcelable {

	public Set<EventType> types;
	public Date dateFrom;
	public Date dateTo;
	public String country;
	public String region;
	public String area; // chris FIXME: in preference is called province. Shall we rename uniformly?
	public String title; 
	
	public EventFilter() {
		types = new HashSet<EventType>();
		dateFrom = null;
		dateTo = null;
		country = null;
		region = null;
		area = null;
		title = null;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

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
		out.writeString(area);
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
		area = in.readString();
		title = in.readString();
	}
}