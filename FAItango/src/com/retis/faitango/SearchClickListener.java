package com.retis.faitango;

import java.util.Date;

import com.retis.faitango.remote.EventFilter;
import com.retis.faitango.remote.EventType;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;


public class SearchClickListener implements OnClickListener {

	private static final String TAG = "SearchClickListener";
	private MainView context;
	private View view;
	
	public SearchClickListener(MainView activity, View v) {
		super();
		context = activity;
		view = v;
	}
	
	public void onClick(DialogInterface dialog, int id) {
		Log.d(TAG, "onClick: setting MainView search filters...");
		EventFilter f = new EventFilter();
		Spinner countrySpinner = (Spinner) view.findViewById(R.id.searchCountrySpinner);
		f.country = countrySpinner.getSelectedItem().toString();
		Log.d(TAG, "Country: " + f.country);
        Spinner regionSpinner = (Spinner) view.findViewById(R.id.searchRegionSpinner);
        f.region = regionSpinner.getSelectedItem().toString();
        Log.d(TAG, "Region: " + f.region);
        Spinner provinceSpinner = (Spinner) view.findViewById(R.id.searchProvinceSpinner);
        f.province = provinceSpinner.getSelectedItem().toString();
        Log.d(TAG, "Province: " + f.province);
        DatePicker from = (DatePicker) view.findViewById(R.id.searchFromDatePicker);
        f.dateFrom = new Date();
        f.dateFrom.setDate(from.getDayOfMonth());
        f.dateFrom.setMonth(from.getMonth());
        f.dateFrom.setYear(from.getYear() - 1900);
        Log.d(TAG, "From: " + f.dateFrom.toString());
    	DatePicker to = (DatePicker) view.findViewById(R.id.searchToDatePicker);
    	f.dateTo = new Date();
        f.dateTo.setDate(to.getDayOfMonth());
        f.dateTo.setMonth(to.getMonth());
        f.dateTo.setYear(to.getYear() - 1900);
        Log.d(TAG, "To: " + f.dateTo.toString());
    	for (EventType t : EventType.values()) {
        	int rid = context.getResources().getIdentifier(t.name(), "id", context.getPackageName());
        	CheckBox cb = (CheckBox) view.findViewById(rid);
        	if (cb.isChecked()) {
        		f.types.add(t);
        		Log.d(TAG, "Type: " + t.name());
        	}
        }
    	context.setSearchFilters(f);
		context.updateEventsList();
        dialog.cancel();
   }
	
}
