package com.retis.faitango.remote;

import com.retis.faitango.R;
import android.os.Parcel;
import android.os.Parcelable;

/** Event-type enumeration <br><br> 
 * 
 * This enumeration describes the available event types.
 * Each entry holds a resource ID to a valid event-type string defined in the <code>string.xml</code> file.
 * The enumeration is also parcelable.
 * 
 * @author Christian Nastasi
 */
public enum EventType implements Parcelable {

	CONCERT(R.string.eventTypeCONCERT),
	PARTY(R.string.eventTypePARTY),
	FESTIVAL(R.string.eventTypeFESTIVAL),
	MARATHON(R.string.eventTypeMARATHON),
	MILONGA(R.string.eventTypeMILONGA),
	SHOW(R.string.eventTypeSHOW),
	STAGE(R.string.eventTypeSTAGE),
	VACATION(R.string.eventTypeVACATION);

	public final int resId;
	public final int enumId;

	private static class Counter {
		private static int nextValue = 0;
	}

	EventType (int id) {
		resId = id;
		enumId = Counter.nextValue;
		Counter.nextValue += 1;
	}

	EventType(int id, int enumInitId) {
		resId = id;
		enumId = enumInitId;
		Counter.nextValue = enumInitId + 1;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(ordinal());
	}

	public static final Parcelable.Creator<EventType> CREATOR = 
			new Parcelable.Creator<EventType>() {
		public EventType createFromParcel(Parcel in) {
			return EventType.values()[in.readInt()];
		}

		public EventType[] newArray(int size) {
			return new EventType[size];
		}
	};
}
