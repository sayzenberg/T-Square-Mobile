package com.teammeh.t_squaremobile;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
 
public class MyAdapter extends ArrayAdapter<Items> {
 
        private final Context context;
        private final ArrayList<Items> itemsArrayList;
 
        public MyAdapter(Context context, ArrayList<Items> itemsArrayList) {
 
            super(context, R.layout.listview_assignments, itemsArrayList);
 
            this.context = context;
            this.itemsArrayList = itemsArrayList;
        }
 
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
 
            // 1. Create inflater 
            LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
            // 2. Get rowView from inflater
            View rowView = inflater.inflate(R.layout.listview_assignments, parent, false);
 
            // 3. Get the two text view from the rowView
            TextView labelView = (TextView) rowView.findViewById(R.id.label);
            TextView valueView = (TextView) rowView.findViewById(R.id.value);
 
            // 4. Set the text for textView 
            labelView.setText(itemsArrayList.get(position).getTitle());
            valueView.setText(itemsArrayList.get(position).getDescription());
 
            // 5. return rowView
            return rowView;
        }
        
        @Override
        public void notifyDataSetChanged() // Create this function in your adapter class
        {
            this.notifyDataSetChanged();
        }
}
