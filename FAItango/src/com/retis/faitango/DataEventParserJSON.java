package com.retis.faitango;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import android.util.Log;

public class DataEventParserJSON extends DataEventParser {
	
	/* Register the concrete Product's constructor to the Factory */
	static { DataEventParser.Factory.register("json", DataEventParserJSON.class); }
	
	private String jsonData;
	 
	public DataEventParserJSON() {
		jsonData = "";
	}
	
	@Override
	public void parse(String input) {
		jsonData = input;
		jsonParsingString();
	}

	private void jsonParsingString() {
		try {
			// chris TODO: manage JSON parsing exceptions!
			JSONArray jArray = new JSONArray(jsonData);
			int len = jArray.length();
			for (int i = 0; i < len; i++) {
				JSONObject obj = jArray.getJSONObject(i);
				DataEvent ev = new DataEvent();
				ev.id = obj.getString("id");
				ev.text = obj.getString("tx");
				ev.date = obj.getString("dt");
				ev.city = obj.getString("citta");
				ev.type = obj.getString("type");
				events.add(ev);
			}
		} catch (JSONException e) {
			Log.d("chris", "EXCEPTION in JSON: " + e.toString());
		}
	}
}
