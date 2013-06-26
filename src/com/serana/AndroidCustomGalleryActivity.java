package com.serana;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import com.serena.connection.Network_Available;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;

@SuppressLint({ "NewApi", "WorldWriteableFiles", "WorldReadableFiles" })
public class AndroidCustomGalleryActivity extends Activity {
	public ImageAdapter imageAdapter;
	public GridView imagegrid;
	ProgressDialog dialog;
	Button selectBtn;
	boolean flag = false;
	int value = 0;
	String selectImages = "";
	long selection;
	long selection11;
	int size = 0;
	private SharedPreferences user_details;
	private String prefname = "SERENA";
	public static boolean isfirstEntry;
	String TAG = "Serena";
	boolean isXlarge = false;

	protected void onStart() {
		super.onStart();
		Log.i(TAG, "onStart called");

		Log.i(TAG, "Checking configuration");

		Configuration myConfig = new Configuration();
		myConfig.setToDefaults();
		myConfig = getResources().getConfiguration();

		switch (myConfig.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) {
		case Configuration.SCREENLAYOUT_SIZE_XLARGE: {
			isXlarge = true;
			Log.v(TAG, "Screen size is extra large");
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			break;
		}
		case Configuration.SCREENLAYOUT_SIZE_LARGE: {
			Log.v(TAG, "Screen size is large");
			break;
		}
		case Configuration.SCREENLAYOUT_SIZE_NORMAL: {
			Log.v(TAG, "Screen size is normal");
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

			break;
		}
		case Configuration.SCREENLAYOUT_SIZE_SMALL: {
			Log.v(TAG, "Screen size is small");
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

			break;
		}
		case Configuration.SCREENLAYOUT_SIZE_UNDEFINED: {
			Log.v(TAG, "Screen size is undefined");
			break;
		}
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		onStart();
		setContentView(R.layout.main);
		selectBtn = (Button) findViewById(R.id.selectBtn);
		isfirstEntry = false;
		imageAdapter = new ImageAdapter(AndroidCustomGalleryActivity.this);
		imagegrid = (GridView) findViewById(R.id.PhoneImageGrid);
		value = getIntent().getIntExtra("value", value);
		System.out.println("value is in custom class is:" + value);
		new getDatainBackground().execute();

		selectBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				final int len = imageAdapter.images.size();
				for (int i = 0; i < len; i++) {
					if (imageAdapter.images.get(i).selection) {
						selectImages = selectImages
								+ imageAdapter.images.get(i).id + ",";
						selection = (long) imageAdapter.images.get(i).id;
						QueueItem queueItem = new QueueItem();
						queueItem.media_id = selection;
						queueItem.bitmap = MediaStore.Images.Thumbnails
								.getThumbnail(getContentResolver(),
										queueItem.media_id,
										MediaStore.Images.Thumbnails.MINI_KIND,
										null);
						/*
						 * Bitmap bit =
						 * MediaStore.Images.Thumbnails.getThumbnail(
						 * getContentResolver(), queueItem.media_id,
						 * MediaStore.Images.Thumbnails.MINI_KIND, null); Matrix
						 * matrix = new Matrix(); matrix.postRotate(90);
						 * queueItem.bitmap= Bitmap.createBitmap(bit , 0, 0, bit
						 * .getWidth(), bit .getHeight(), matrix, true);
						 */
						switch (value) {
						case 1:
							Catalog_webview.queueItemForAttachment
									.add(queueItem);
							break;
						case 2:
							Request_webview.queueItemAdapterforRequest
									.add(queueItem);
							break;
						case 3:
							Approval_webview.queueItemAdapterforApproval
									.add(queueItem);
							break;
						case 4:
							Request.queueItemforRequest.add(queueItem);
							break;
						case 5:
							Approval.queueItemforApproval.add(queueItem);
							break;
						case 6:
							search_webview.queueItemForAttachment
									.add(queueItem);
							break;

						default:
							break;
						}
					}
				}

				switch (value) {
				case 1:
					size = Catalog_webview.queueItemForAttachment.size();
					break;
				case 2:
					size = Request_webview.queueItemAdapterforRequest.size();
					break;
				case 3:
					size = Approval_webview.queueItemAdapterforApproval.size();
					break;
				case 4:
					size = Request.queueItemforRequest.size();
					break;
				case 5:
					size = Approval.queueItemforApproval.size();
					break;
				case 6:
					size = search_webview.queueItemForAttachment.size();
					break;

				default:
					break;
				}
				Intent cint = new Intent();
				cint.putExtra("returnValue", size);
				setResult(RESULT_OK, cint);
				onBackPressed();
				AndroidCustomGalleryActivity.this.finish();
			}
		});
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// super.onBackPressed();
		System.gc();
	}

	@Override
	protected void onStop() {
		super.onStop();
		backgroundActivity();
		this.finish();
	}

	private void backgroundActivity() {
		// TODO Auto-generated method stub
		MyApplication.setlastTime((long) System.currentTimeMillis());
		long movedBackground = (long) System.currentTimeMillis();
		user_details = getSharedPreferences(prefname, MODE_WORLD_WRITEABLE);
		SharedPreferences.Editor editor = user_details.edit();
		editor.putString("name", MyApplication.getUserID());
		editor.putString("password", MyApplication.getPassWord());
		editor.putString("url", MyApplication.getServer());
		editor.putLong("backgroundtime", movedBackground);
		editor.putBoolean("backgroundSave", true);
		editor.commit();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (isfirstEntry) {
			SharedPreferences e1 = getSharedPreferences("SERENA",
					MODE_WORLD_READABLE);
			long lasttime = e1.getLong("backgroundtime", 0);

			long millisecond = (long) System.currentTimeMillis();
			System.out.println("millisecond is" + millisecond);
			System.out.println("last millisecond is:" + lasttime);
			long f = millisecond - lasttime;
			int sec = (int) TimeUnit.MILLISECONDS.toMinutes(f);

			if (!Network_Available
					.hasConnection(AndroidCustomGalleryActivity.this)
					&& sec > 60) {
				Intent m = new Intent(AndroidCustomGalleryActivity.this,
						MainSerenaActivity.class);
				m.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(m);
				finish();
			} else if (Network_Available
					.hasConnection(AndroidCustomGalleryActivity.this)
					&& sec > 60) {
				Intent m = new Intent(AndroidCustomGalleryActivity.this,
						SplashScreen.class);
				m.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(m);
				finish();

			} else if (!Network_Available
					.hasConnection(AndroidCustomGalleryActivity.this)
					&& sec < 60) {
				makediallog("No network connection",
						"You must be connected to the internet to use this app");

			} else if (Network_Available
					.hasConnection(AndroidCustomGalleryActivity.this)
					&& sec < 60) {
				// don't do any thing let user stays in same screen with the
				// data loaded previously
			}
		}
		isfirstEntry = true;

	}

	public void makediallog(String titlte, String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				AndroidCustomGalleryActivity.this);
		builder.setTitle(titlte);
		builder.setMessage(msg);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		AlertDialog dialog = builder.create();
		dialog.show();
	}

	class getDatainBackground extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			imageAdapter.initialize();
			return null;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					dialog = new ProgressDialog(
							AndroidCustomGalleryActivity.this);
					dialog.setMessage("Loading images..");
					dialog.setIndeterminate(true);
					dialog.setCancelable(false);
					dialog.show();
				}
			});
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			imagegrid.setAdapter(imageAdapter);
			dialog.cancel();
		}
	}
}