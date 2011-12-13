package com.retis.faitango;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import android.util.Log;

public class DataEventParserJSON extends DataEventParser {
	
	/* Register the concrete Product's constructor to the Factory */
	static { DataEventParser.Factory.register("json", DataEventParserJSON.class); }
	
	private String PIPPO; // chris TODO: remove PIPPO and its usage
	private String jsonData;
	 
	public DataEventParserJSON() {
		PIPPO = "Testata... ";
		jsonData = "";
		// chris TODO: check the jsonData string before using it!
	}
	
	@Override
	public void parse(String input) {
		jsonData = input;
		PIPPO = "... sul cranio.\n\n";
		PIPPO += "JSON='" + jsonData.substring(0, 10) + "    ...'\n\n";
		jsonParsingString();
	}

	@Override
	String getTestString() {
		return PIPPO;
	}

	private void jsonParsingString() {
		try {
			JSONArray jArray = new JSONArray(jsonData);
			int len = jArray.length();
			PIPPO += "jArray.lenght = " + len + "\n";
			for (int i = 0; i < len; i++) {
				JSONObject obj = jArray.getJSONObject(i);
				DataEvent ev = new DataEvent();
				ev.id = obj.getString("id");
				ev.text = obj.getString("tx");
				ev.date = obj.getString("dt");
				ev.city = obj.getString("citta");
				ev.type = obj.getString("type");
				events.add(ev);
				// ev.af = obj.getString("af"); // chris NOTE: commented for now (is null in the file, why?)
				PIPPO += "id = " + ev.id + "\n";
				PIPPO += "text = " + ev.text + "\n";
				PIPPO += "date = " + ev.date + "\n";
				PIPPO += "city = " + ev.city + "\n";
				PIPPO += "type = " + ev.type + "\n";
			}
		} catch (JSONException e) {
			PIPPO += "EXCEPTION: " + e.toString() + "\n";
			Log.d("chris", PIPPO, e);
		}
	}
}
