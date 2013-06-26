package com.serana;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SortSpinnerAdapter extends BaseAdapter {
	private String[] names;
	@SuppressWarnings("unused")
	private Context context;
	private LayoutInflater layoutInflater;

	SortSpinnerAdapter(Context c, String[] s) {
		this.names = s;
		this.context = c;
	}

	public int getCount() {
		return names.length;
	}

	public Object getItem(int position)	{
		return names[position];
	}

	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("unused")
	public View getView(int position, View grid, ViewGroup parent) {

		grid = layoutInflater.inflate(R.layout.sortspinner, null);
		TextView text = (TextView) grid.findViewById(R.id.sort_item_name);
		return grid;
	}
}