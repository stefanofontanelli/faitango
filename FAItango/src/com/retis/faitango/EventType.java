package com.retis.faitango;

public enum EventType {
	
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

	EventType (int id) {
		resId = id;
		enumId = Counter.nextValue;
		Counter.nextValue += 1;
	}

	EventType(int id, int enumInitId) {
		resId = id;
		enumId = enumInitId;
		Counter.nextValue = enumInitId + 1;
	}
}
