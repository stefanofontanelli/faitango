package com.retis.faitango.remote;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;


import android.content.Context;
import android.util.Log;

public class JSONEventParser extends EventParser {

	/* Register the concrete Product's constructor to the Factory */
	static {
		try {
			EventParser.Factory.register("json", JSONEventParser.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}
	
	private static final String TAG = "DataEventParserJSON";
	
	public JSONEventParser(Context context) {
		super(context);
	}
	
	@Override
	public void parseEventList(String input) { 
		try {
			JSONArray jArray = new JSONArray(input);
			int len = jArray.length();
			for (int i = 0; i < len; i++) {
				JSONObject obj = jArray.getJSONObject(i);
				DataEvent ev = new DataEvent();
				ev.id = obj.getLong("id");
				ev.text = obj.getString("tx");
                // ugly.. but web site JSON doesn't have a proper date field :-/
                String[] datetmp = obj.getString("dt").split(" ");
                String[] dateFields = datetmp[1].split("/");
                ev.date = (new GregorianCalendar(Integer.parseInt(dateFields[2]), 
                		Integer.parseInt(dateFields[1]) - (12 - Calendar.DECEMBER), 
                		Integer.parseInt(dateFields[0]))).getTime();
				ev.city = obj.getString("citta");
				ev.type = getEventTypeFromString(obj.getString("type"));
				events.add(ev);
			}
		} catch (JSONException e) {
			Log.e(TAG, "Failure: got exception in JSON: " + e.toString());
			// chris TODO: manage JSON parsing exceptions?
		}
	}

	@Override
	public void parseEventDetail(String input) {
		try {
			JSONArray jArray = new JSONArray(input);
			int len = jArray.length();
			for (int i = 0; i < len; i++) {
				JSONObject obj = jArray.getJSONObject(i);
				DataEventDetail ev = new DataEventDetail();
				ev.title = obj.getString("tx");
                // ugly.. but web site JSON doesn't have a proper date field :-/
                String[] datetmp = obj.getString("dt").split(" ");
                String[] dateFields = datetmp[1].split("/");
                ev.date = (new GregorianCalendar(Integer.parseInt(dateFields[2]), 
                		Integer.parseInt(dateFields[1]) - (12 - Calendar.DECEMBER), 
                		Integer.parseInt(dateFields[0]))).getTime();
                datetmp = obj.getString("dt_ins").split(" ");
                dateFields = datetmp[1].split("/");
                ev.created = (new GregorianCalendar(Integer.parseInt(dateFields[2]), 
                			  Integer.parseInt(dateFields[1]) - (12 - Calendar.DECEMBER), 
                			  Integer.parseInt(dateFields[0]))).getTime();
				ev.city = obj.getString("citta");
				ev.type = getEventTypeFromString(obj.getString("type"));
				ev.email = obj.getString("email");
				ev.description = obj.getString("descr");
				ev.link = obj.getString("Link");
				eventsDetails.add(ev);
			}
		} catch (JSONException e) {
			Log.e(TAG, "Failure: got exception in JSON: " + e.toString());
		}
	}
}
