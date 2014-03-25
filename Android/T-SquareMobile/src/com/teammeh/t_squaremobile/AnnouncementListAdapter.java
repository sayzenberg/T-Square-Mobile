package com.teammeh.t_squaremobile;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AnnouncementListAdapter extends ArrayAdapter {

	private Context context;

	public AnnouncementListAdapter(Context context, int layout, ArrayList<Announcement> items) {
		super(context, layout, items);
		this.context = context;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		Announcement announcement = (Announcement)getItem(position);
		View viewToUse = null;

		// This block exists to inflate the settings list item conditionally based on whether
		// we want to support a grid or list view.
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		//       if (convertView == null) {
		viewToUse = mInflater.inflate(R.layout.list_item, null);
		holder = new ViewHolder();
		holder.titleText = (TextView)viewToUse.findViewById(R.id.titleTextView);
		viewToUse.setTag(holder);
		//       } else {
		//           viewToUse = convertView;
		//           holder = (ViewHolder) viewToUse.getTag();
		//       }

		holder.titleText.setText(announcement.getTitle());
		System.out.println(holder.titleText.getText());
		return viewToUse;
	}

	/**
	 * Holder for the list items.
	 */
	private class ViewHolder{
		TextView titleText;
	}
	
}
