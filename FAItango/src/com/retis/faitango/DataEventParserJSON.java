package com.retis.faitango;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import android.content.Context;
import android.util.Log;

public class DataEventParserJSON extends DataEventParser {

	/* Register the concrete Product's constructor to the Factory */
	static { DataEventParser.Factory.register("json", DataEventParserJSON.class); }
	
	private static final String TAG = "DataEventParserJSON";
	
	public DataEventParserJSON(Context context) {
		super(context);
	}
	
	@Override
	public void parseEventList(String input) { 
	
		try {
			// chris TODO: manage JSON parsing exceptions?
			JSONArray jArray = new JSONArray(input);
			int len = jArray.length();
			for (int i = 0; i < len; i++) {
				JSONObject obj = jArray.getJSONObject(i);
				DataEvent ev = new DataEvent();
				ev.id = obj.getLong("id");
				ev.text = obj.getString("tx");
				 /*
                 * ugly.. but web site database doesn't have
                 * a proper date field :-/
                 */
                String[] datetmp = obj.getString("dt").split(" ");
                String[] dateFields = datetmp[1].split("/");
                ev.date = (new GregorianCalendar(Integer.parseInt(dateFields[2]), 
                		Integer.parseInt(dateFields[1]) - (12 - Calendar.DECEMBER), 
                		Integer.parseInt(dateFields[0]))).getTime();
				ev.city = obj.getString("citta");
				ev.type = getFromString(obj.getString("type"));
				events.add(ev);
			}
		} catch (JSONException e) {
			Log.d(TAG, "Failure: got exception in JSON: " + e.toString());
		}
	}

	@Override
	public void parseEventDetail(String input) {
		// chris FIXME: DO IT!
	}
}
