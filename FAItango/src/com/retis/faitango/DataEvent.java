package com.retis.faitango;

public final class DataEvent {
	
	public enum Types {
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

		Types (int id) {
			resId = id;
			enumId = Counter.nextValue;
			Counter.nextValue += 1;
		}
		
		Types (int id, int enumInitId) {
			resId = id;
			enumId = enumInitId;
			Counter.nextValue = enumInitId + 1;
		}
	};   
		
	// chris NOTE:Fields taken from the JSON file they gave us
	public String id;
	public String text;
	public String date;
	public String city;
	public String type;
	public String af; // chris TODO: what is this? give a better name
}
