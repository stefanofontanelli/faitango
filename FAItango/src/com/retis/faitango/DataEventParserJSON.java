package com.retis.faitango;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import android.util.Log;

public class DataEventParserJSON extends DataEventParser {

	public String PIPPO; // chris TODO: remove PIPPO and its usage
	private String jsonData;
	 
	public DataEventParserJSON (String json) {
		PIPPO = "Testata... ";
		jsonData = json;
		// chris TODO: check the jsonData string before using it!
	}
	
	@Override
	void parse() {
		PIPPO = "... sul cranio.\n\n";
		PIPPO += "JSON='" + jsonData.substring(0, 10) + "    ...'\n\n";
		jsonParsingString();
	}

	private void jsonParsingString() {
		try {
			JSONArray jArray = new JSONArray(jsonData);
			int len = jArray.length();
			PIPPO += "jArray.lenght = " + len + "\n";
			for (int i = 0; i < len; i++) {
				JSONObject obj = jArray.getJSONObject(i);
				id = obj.getString("id");
				text = obj.getString("tx");
				date = obj.getString("dt");
				city = obj.getString("citta");
				type = obj.getString("type");
				// af = obj.getString("af"); // chris NOTE: commented for now (is null in the file, why?)
				PIPPO += "id = " + id + "\n";
				PIPPO += "text = " + text + "\n";
				PIPPO += "date = " + date + "\n";
				PIPPO += "city = " + city + "\n";
				PIPPO += "type = " + type + "\n";
			}
		} catch (JSONException e) {
			PIPPO += "EXCEPTION: " + e.toString() + "\n";
			Log.d("chris", PIPPO, e);
		}
	}
}