package com.retis.faitango;

import java.util.ArrayList;
import android.graphics.drawable.Drawable;
import com.google.android.maps.OverlayItem;

@SuppressWarnings("rawtypes")
public class ItemizedOverlay extends com.google.android.maps.ItemizedOverlay {
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	
	public ItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);

	}

	@Override
	public int size() {
		return mOverlays.size();
	}
	
	public void addOverlay(OverlayItem overlay) {
	    mOverlays.add(overlay);
	    populate();
	}
}
