package com.retis.faitango;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import android.content.Context;
import android.util.Log;

public class DataEventParserJSON extends DataEventParser {

	/* Register the concrete Product's constructor to the Factory */
	static { DataEventParser.Factory.register("json", DataEventParserJSON.class); }
	
	public DataEventParserJSON(Context context) {
		super(context);
	}
	
	@Override
	public void parseEventList(String input) { 
	
		try {
			// chris TODO: manage JSON parsing exceptions!
			JSONArray jArray = new JSONArray(input);
			int len = jArray.length();
			for (int i = 0; i < len; i++) {
				JSONObject obj = jArray.getJSONObject(i);
				DataEvent ev = new DataEvent();
				ev.id = obj.getLong("id");
				ev.text = obj.getString("tx");
				ev.date = obj.getString("dt");
				ev.city = obj.getString("citta");
				ev.type = getFromString(obj.getString("type"));
				events.add(ev);
			}
		} catch (JSONException e) {
			Log.d("chris", "EXCEPTION in JSON: " + e.toString());
		}
	}

	@Override
	public void parseEventDetail(String input) {
		// TODO Auto-generated method stub
		// chris FIXME: DO IT!
		
	}
}
