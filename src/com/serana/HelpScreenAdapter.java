package com.serana;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class HelpScreenAdapter extends BaseAdapter{
	
	Activity context;
	private LayoutInflater layoutInflater;
	private int static_images_ids[];
	public HelpScreenAdapter(Activity context, int static_images_ids[])
	{
		// TODO Auto-generated constructor stub
		this.context=context;
		this.static_images_ids= static_images_ids;
		layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return static_images_ids.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView = layoutInflater.inflate(R.layout.listitem,
				null);
		ImageView image=(ImageView)convertView.findViewById(R.id.image);
		//image.setBackgroundResource(static_images_ids[position]);
		image.setImageResource(static_images_ids[position]);
		return convertView;
	}

}
