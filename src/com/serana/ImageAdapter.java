package com.serana;

import java.io.File;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

@SuppressLint("NewApi")
public class ImageAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	public ArrayList<ImageItem> images = new ArrayList<ImageItem>();
	Activity context;
	long lastId;

	public ImageAdapter(Activity context) {
		this.context = context;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void initialize() {
		images.clear();
		final String[] columns = { MediaStore.Images.Thumbnails._ID,
				MediaStore.Images.Media.TITLE };

		final String orderBy = MediaStore.Images.Media._ID;
		Cursor imagecursor = context.managedQuery(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
				null, orderBy);
		if (imagecursor != null) {
			int image_column_index = imagecursor
					.getColumnIndex(MediaStore.Images.Media._ID);
			int image_column_index_title = imagecursor
					.getColumnIndex(MediaStore.Images.Media.TITLE);
			int count = imagecursor.getCount();
			for (int i = 0; i < count; i++) {
				imagecursor.moveToPosition(i);
				int id = imagecursor.getInt(image_column_index);
				ImageItem imageItem = new ImageItem();
				imageItem.id = id;
				lastId = id;
				imageItem.title = imagecursor
						.getString(image_column_index_title);
				System.out.println("title is:" + imageItem.title);
				imageItem.img = MediaStore.Images.Thumbnails.getThumbnail(
						context.getContentResolver(), id,
						MediaStore.Images.Thumbnails.MICRO_KIND, null);
				images.add(imageItem);
			}
			imagecursor.close();
		}
		notifyDataSetChanged();
	}

	public int getCount() {
		return images.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.galleryitem, null);
			holder.imageview = (ImageView) convertView
					.findViewById(R.id.thumbImage);
			holder.checkbox = (CheckBox) convertView
					.findViewById(R.id.itemCheckBox);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ImageItem item = images.get(position);
		holder.checkbox.setId(position);
		holder.imageview.setId(position);
		holder.checkbox.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				CheckBox cb = (CheckBox) v;
				int id = cb.getId();
				if (images.get(id).selection) {
					cb.setChecked(false);
					images.get(id).selection = false;
				} else {
					cb.setChecked(true);
					images.get(id).selection = true;
				}
			}
		});
		holder.imageview.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("unused")
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int id = v.getId();
				ImageItem item = images.get(id);
				final String[] columns = { MediaStore.Images.Media.DATA };
				Cursor imagecursor = context.managedQuery(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
						MediaStore.Images.Media._ID + " = " + item.id, null,
						MediaStore.Images.Media._ID);
				if (imagecursor != null && imagecursor.getCount() > 0) {
					imagecursor.moveToPosition(0);
					String path = imagecursor.getString(imagecursor
							.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
					File file = new File(path);
					imagecursor.close();
				}
			}
		});
		holder.imageview.setImageBitmap(item.img);
		holder.checkbox.setChecked(item.selection);
		return convertView;
	}
}

	class ViewHolder {
		ImageView imageview;
		CheckBox checkbox;
	}

	class ImageItem {
		boolean selection;
		String title;
		int id;
		Bitmap img;
		String path;
	}