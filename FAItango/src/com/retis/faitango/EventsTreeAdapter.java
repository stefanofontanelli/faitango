package com.retis.faitango;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;
import com.retis.faitango.database.EventProvider;
import com.retis.faitango.database.EventTable;
import com.retis.faitango.remote.EventFilter;

public class EventsTreeAdapter extends SimpleCursorTreeAdapter {

	private static final String[] FROM = {
		EventTable.CITY, EventTable.DATE, EventTable.TYPE, EventTable.NAME, EventTable.TIME
	};
	private static final int[] TO = {
		R.id.textCity, R.id.textDate, R.id.textEventType,	R.id.textEventName, R.id.textTime
	};
	private Context context;
	// map used to associate an ID to a child position inside a parent group expanded list entry
	public static HashMap<Integer, Integer> childMap = new HashMap<Integer, Integer>();
	// map used to associate the group ID with the date String
	private Map<Integer, String> listMap = new HashMap<Integer, String>();
	private EventFilter filter = null;

	public EventsTreeAdapter(Cursor cursor, Context c, EventFilter f) {
		super(c, cursor, android.R.layout.simple_expandable_list_item_1,
				new String[] {EventTable.DATE}, new int[] {android.R.id.text1},
				R.layout.eventrow, FROM, TO);
		context = c;
		filter = f;
	}
	
	@Override
	public void notifyDataSetChanged() {
		// Prepare to display date as String in GroupView
		Cursor cursor = getCursor();
		cursor.moveToFirst();
		long date;
		while(cursor.isAfterLast() == false) {
			SimpleDateFormat sdf = new SimpleDateFormat("E dd/MM/yyyy", Locale.ITALIAN);
			date = cursor.getLong(cursor.getColumnIndexOrThrow(EventTable.DATE));
			String s = sdf.format(new Date(date));
			if (!listMap.containsValue(s))
				listMap.put(cursor.getPosition(), s);
			cursor.moveToNext();
		}
		cursor.moveToFirst();
		
		super.notifyDataSetChanged();
	}

	@Override
	protected void bindChildView(View view, Context context, Cursor cursor,	boolean isLastChild) {
		super.bindChildView(view, context, cursor, isLastChild);
		SimpleDateFormat sdf = new SimpleDateFormat("E dd/MM/yyyy", Locale.ITALIAN); 
		String s = sdf.format(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(EventTable.DATE))));
		TextView tv = (TextView) view.findViewById(R.id.textDate);
		tv.setText(s);
	}

	@Override
	public View getGroupView(int groupPos, boolean isExpanded, View convertView, ViewGroup parent) {
		LinearLayout linear = new LinearLayout(context);
		final LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		TextView text = new TextView(context);

		// Push the group name slightly to the right for drop down icon
		final String str = "\t\t\t\t"  + listMap.get(groupPos);

		linear = new LinearLayout(context);
		linear.setOrientation(LinearLayout.VERTICAL);

		text.setLayoutParams(params);
		text.setText(str);
		text.setTextSize(24.0f);
		linear.addView(text);

		return linear;
	}

	@Override
	protected Cursor getChildrenCursor(Cursor groupCursor) {
		Cursor cursor;
		// Since this function is called every time the user expands a group, 
		// we must be sure that the map is cleared and then filled again with actual IDs
		EventsTreeAdapter.childMap.clear();
		ContentResolver cr = context.getContentResolver();
		EventFilter f = (EventFilter)filter.clone();
		long time = groupCursor.getLong(groupCursor.getColumnIndexOrThrow(EventTable.DATE));
		f.dateFrom = new Date(time);
		f.dateTo = new Date(time);
		String where = f.getWhereClause(cr);

		
		cursor = cr.query(EventProvider.CONTENT_URI, null, where, null, null);
		cursor.moveToFirst();
		while(cursor.isAfterLast() == false) {
			//we associate an event ID with the position of a child
			//in an expandable list hierarchy
			EventsTreeAdapter.childMap.put(cursor.getPosition(),
										   cursor.getInt(cursor.getColumnIndex(EventTable._ID)));
			cursor.moveToNext();
		}
		cursor.moveToFirst();

		return cursor;
	}
	
	public void updateSearchFilter(EventFilter f) {
    	filter = f;
    }
}
