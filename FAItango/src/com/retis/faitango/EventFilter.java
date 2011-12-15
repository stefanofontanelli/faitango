package com.retis.faitango;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class EventFilter {

	public Set<EventType> types;
	public Date dateFrom;
	public Date dateTo;
	public String country;
	public String region;
	public String area; // provincia
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
}
