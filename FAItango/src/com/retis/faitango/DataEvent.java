package com.retis.faitango;

import java.util.Date;

public final class DataEvent {
			
	// chris NOTE:Fields taken from the JSON file they gave us
	
	public long id;
	public EventType type;
	public String text;
	public Date date;
	public String city;
	public String af; // chris TODO: what is this? give a better name
	
	public String details;
}
