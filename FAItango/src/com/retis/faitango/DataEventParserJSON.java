package com.retis.faitango;

import org.json.JSONTokener;

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
		PIPPO = "... sul cranio.\n";
		PIPPO += " JSON='" + jsonData.substring(0, 10) + "'";
		//PIPPO += " JSON='" + jsonData.length() + "'";
	}
}
