package com.serana;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;

import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;

public class QueueAdapter extends BaseAdapter {
	private LayoutInflater mInflater;

	Activity context;
	String[] arrIds;
	int value;

	public QueueAdapter(Activity context, int value) {
		this.context = context;
		this.value = value;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		switch (value) {
		case 1:
			return Catalog_webview.queueItemForAttachment.size();

		case 2:
			return Request_webview.queueItemAdapterforRequest.size();

		case 3:
			return Approval_webview.queueItemAdapterforApproval.size();
		case 4:
			return Request.queueItemforRequest.size();

		case 5:
			return Approval.queueItemforApproval.size();

		case 6:
			return search_webview.queueItemForAttachment.size();

		default:
			return 0;
		}

	}

	public boolean deletValue(long item_id) {
		switch (value) {
		case 1:
			for (int i = 0; i < Catalog_webview.queueItemForAttachment.size(); i++) {
				if (Catalog_webview.queueItemForAttachment.get(i).media_id == item_id) {
					Catalog_webview.queueItemForAttachment.remove(i);
				}
			}
			notifyDataSetChanged();
			return true;
		case 2:
			for (int i = 0; i < Request_webview.queueItemAdapterforRequest
					.size(); i++) {
				if (Request_webview.queueItemAdapterforRequest.get(i).media_id == item_id) {
					Request_webview.queueItemAdapterforRequest.remove(i);
				}
			}
			notifyDataSetChanged();
			return true;

		case 3:
			for (int i = 0; i < Approval_webview.queueItemAdapterforApproval
					.size(); i++) {
				if (Approval_webview.queueItemAdapterforApproval.get(i).media_id == item_id) {
					Approval_webview.queueItemAdapterforApproval.remove(i);
				}
			}
			notifyDataSetChanged();
			return true;
		case 4:
			for (int i = 0; i < Request.queueItemforRequest.size(); i++) {
				if (Request.queueItemforRequest.get(i).media_id == item_id) {
					Request.queueItemforRequest.remove(i);
				}
			}
			notifyDataSetChanged();
			return true;

		case 5:
			for (int i = 0; i < Approval.queueItemforApproval.size(); i++) {
				if (Approval.queueItemforApproval.get(i).media_id == item_id) {
					Approval.queueItemforApproval.remove(i);
				}
			}
			notifyDataSetChanged();
			return true;

		case 6:
			for (int i = 0; i < search_webview.queueItemForAttachment.size(); i++) {
				if (search_webview.queueItemForAttachment.get(i).media_id == item_id) {
					search_webview.queueItemForAttachment.remove(i);
				}
			}
			notifyDataSetChanged();
			return true;

		default:
			return false;
		}
	}

	public QueueItem getItem(int position) {
		System.out.println("get item called");
		if (value == 1) {
			return Catalog_webview.queueItemForAttachment.get(position);
		} else if (value == 2) {
			return Request_webview.queueItemAdapterforRequest.get(position);
		} else if (value == 3) {
			return Approval_webview.queueItemAdapterforApproval.get(position);
		} else if (value == 4) {
			return Request.queueItemforRequest.get(position);
		} else if (value == 5) {
			return Approval.queueItemforApproval.get(position);
		} else {
			return search_webview.queueItemForAttachment.get(position);
		}
	}

	public long getItemId(int position) {
		return position;
	}

	@TargetApi(Build.VERSION_CODES.ECLAIR)
	public View getView(int position, View convertView, ViewGroup parent) {
		SelectedViewHolder holder;
		if (convertView == null) {
			holder = new SelectedViewHolder();
			convertView = mInflater.inflate(R.layout.selectedgalleryitem, null);
			holder.imageview = (ImageView) convertView
					.findViewById(R.id.selectedthumbImage);
			holder.delete = (Button) convertView.findViewById(R.id.deleteImage);
			convertView.setTag(holder);
		} else {
			holder = (SelectedViewHolder) convertView.getTag();
		}
		final QueueItem item = getItem(position);
		final String[] columns = { MediaStore.Images.Media.DATA };
		final String orderBy = MediaStore.Images.Media._ID;
		Cursor imagecursor = context.managedQuery(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
				MediaStore.Images.Media._ID + " = " + item.media_id + "", null,
				orderBy);
		int count = imagecursor.getCount();
		for (int i = 0; i < count; i++) {
			imagecursor.moveToPosition(i);
			holder.imageview.setImageBitmap(MediaStore.Images.Thumbnails
					.getThumbnail(context.getContentResolver(), item.media_id,
							MediaStore.Images.Thumbnails.MICRO_KIND, null));
		}

		holder.delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Log.v("Message", Long.toString(item.media_id));
				// SelectOption.deleteItem(item.media_id);
				deletValue(item.media_id);
			}
		});
		// imagecursor.close();
		return convertView;
	}
}

class SelectedViewHolder {
	ImageView imageview;
	Button delete;
}

class QueueItem {
	String path;
	long media_id;
	String caption;
	Bitmap bitmap;
	int uploaded;
}
