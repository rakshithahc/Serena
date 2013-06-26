package com.serana;

import java.io.File;
import java.util.concurrent.TimeUnit;

import com.serena.connection.Network_Available;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

@SuppressLint({ "NewApi", "WorldWriteableFiles", "WorldReadableFiles" })
public class SelectOption extends Activity implements OnClickListener {

	GridView gridviewfor_images;
	Button gallery, camera, ok;
	private static final int Gallery_Item = 0;
	private static final int Camera_Capture = 1;
	String[] imagelength;
	QueueAdapter imageAdapter;
	public static int disDialog = 0;
	String Files;
	static int value = 0;
	long id;
	Uri imageUri;
	int count = 0;
	@SuppressWarnings("unused")
	private Bitmap bitmap;
	private SharedPreferences user_details;
	public static boolean isfirstEntry;
	private String prefname = "SERENA";
	String TAG="Serena";
	boolean isXlarge= false;
	
	
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onCreate(savedInstanceState);
		setContentView(R.layout.option);
		isfirstEntry = false;
		initializeIds();
		if (returnValue > 0) {
			ok.setBackgroundResource(R.drawable.custombutton01);
			ok.setText(" Done ");
		}
		adaptersetting_list();
	}

	private void checkforthestatus(int value2) {
		// TODO Auto-generated method stub
		switch (value2) {
		case 1:
			returnValue = Catalog_webview.queueItemForAttachment.size();
			break;
		case 2:
			returnValue = Request_webview.queueItemAdapterforRequest.size();
			break;
		case 3:
			returnValue = Approval_webview.queueItemAdapterforApproval.size();
			break;
		case 4:
			returnValue = Request.queueItemforRequest.size();
			break;
		case 5:
			returnValue = Approval.queueItemforApproval.size();
			break;
		case 6:
			returnValue = search_webview.queueItemForAttachment.size();
			break;
		default:
			break;
		}
	}

	private void initializeIds() {
		// TODO Auto-generated method stub
		gallery = (Button) findViewById(R.id.gallery);
		camera = (Button) findViewById(R.id.capture);
		ok = (Button) findViewById(R.id.ok);
		gridviewfor_images = (GridView) findViewById(R.id.selectedImageGrid);
		value = getIntent().getIntExtra("value", value);
		System.out.println("value is:" + value);
		imageAdapter = new QueueAdapter(SelectOption.this, value);
		gallery.setOnClickListener(SelectOption.this);
		camera.setOnClickListener(SelectOption.this);
		ok.setOnClickListener(SelectOption.this);
		checkforthestatus(value);
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
		System.gc();
		backgroundActivity();
	}

	private void backgroundActivity() {
		// TODO Auto-generated method stub
		MyApplication.setlastTime(System.currentTimeMillis());
		// the following for when app goes to background and is killed my user
		// then to maintain auto reloggin
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

			if (!Network_Available.hasConnection(SelectOption.this) && sec > 60) {
				Intent m = new Intent(SelectOption.this,
						MainSerenaActivity.class);
				m.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(m);
				finish();
			} else if (Network_Available.hasConnection(SelectOption.this)
					&& sec > 60) {
				Intent m = new Intent(SelectOption.this, SplashScreen.class);
				m.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(m);
				finish();

			} else if (!Network_Available.hasConnection(SelectOption.this)
					&& sec < 60) {
				makediallog("No network connection",
						"You must be connected to the internet to use this app");

			} else if (Network_Available.hasConnection(SelectOption.this)
					&& sec < 60) {
				// don't do any thing let user stays in same screen with the
				// data loaded previously
			}
		}
		isfirstEntry = true;
	}

	public void makediallog(String titlte, String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(SelectOption.this);
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.capture:
			callcameraIntent();
			break;

		case R.id.gallery:
			callIntent();
			break;

		case R.id.ok:
			int count = 0;
			switch (value) {
			case 1:
				count = Catalog_webview.queueItemForAttachment.size();
				break;
			case 2:
				count = Request_webview.queueItemAdapterforRequest.size();
				break;
			case 3:
				count = Approval_webview.queueItemAdapterforApproval.size();
				break;
			case 4:
				count = Request.queueItemforRequest.size();
				break;
			case 5:
				count = Approval.queueItemforApproval.size();
				break;
				
			case 6:
				count = search_webview.queueItemForAttachment.size();
				break;
			}
			if (count > 3) {
				AlertDialog.Builder alert = new AlertDialog.Builder(
						SelectOption.this);
				alert.setTitle("Warning");
				alert.setMessage("You cannot upload more than 3 images");
				alert.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						});
				AlertDialog dialog = alert.create();
				dialog.show();
			} else {
				System.out.println("call catalog webview");
				Intent intent = new Intent();
				intent.setFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				setResult(RESULT_OK, intent);
				onBackPressed();
				System.gc();
				SelectOption.this.finish();
			}

			break;

		default:
			break;
		}

	}

	private void callcameraIntent() {
		// TODO Auto-generated method stub
		String fileName = "new-photo-name_" + count + ".jpg";
		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.TITLE, fileName);
		values.put(MediaStore.Images.Media.DESCRIPTION,
				"Image capture by camera");
		imageUri = getContentResolver().insert(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		// create new Intent
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		startActivityForResult(intent, Camera_Capture);
	}

	private void callIntent() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, AndroidCustomGalleryActivity.class);
		intent.putExtra("value", value);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivityForResult(intent, Gallery_Item);
	}

	int returnValue = 0;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		Uri selectedImageUri = null;

		String filePath = null;
		System.out.println("on activity result call");
		switch (requestCode) {
		case Gallery_Item:
			if (resultCode == Activity.RESULT_OK) {
				System.out.println("result is ok");
				returnValue = data.getIntExtra("returnValue", returnValue);
				if (returnValue > 0) {
					ok.setBackgroundResource(R.drawable.custombutton01);
					ok.setText(" Done ");
				}
				adaptersetting_list();
			}
			break;

		case Camera_Capture:
			if (resultCode == RESULT_OK) {
				count = count + 1;
				selectedImageUri = imageUri;
			}

			break;
		}

		if (selectedImageUri != null) {
			try {
				String filemanagerstring = selectedImageUri.getPath();

				// MEDIA GALLERY
				String selectedImagePath = getPath(selectedImageUri);

				if (selectedImagePath != null) {
					filePath = selectedImagePath;
					System.out.println("file path is:" + filePath);					
				} else if (filemanagerstring != null) {
					filePath = filemanagerstring;
					System.out.println("file path is:" + filePath);
				} else {
					Toast.makeText(getApplicationContext(), "Unknown path",
							Toast.LENGTH_LONG).show();
					Log.e("Bitmap", "Unknown path");
				}

				if (filePath != null) {
					decodeFile(filePath);
				} else {
					bitmap = null;
				}
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), "Internal error",
						Toast.LENGTH_LONG).show();
				Log.e(e.getClass().getName(), e.getMessage(), e);
			}
		}
	}

	private void adaptersetting_list() {
		// TODO Auto-generated method stub

		if (returnValue > 0) {
			ok.setBackgroundResource(R.drawable.custombutton01);
			ok.setText(" Done ");
		}
		gridviewfor_images.setAdapter(imageAdapter);
		imageAdapter.notifyDataSetChanged();
	}

	public String getPath(Uri uri) {
		try {
			String[] projection = { MediaStore.Images.Media.DATA,
					MediaStore.Images.Thumbnails._ID };
			Cursor cursor = managedQuery(uri, projection, null, null, null);
			if (cursor != null) {
				// HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
				// THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE
				// MEDIA
				int column_index = cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				int column_media_id = cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
				cursor.moveToFirst();
				id = cursor.getInt(column_media_id);
				String filename=cursor.getString(column_index);
			
				System.out.println("media id for clicked image is:" + id);
				QueueItem queueItem = new QueueItem();
				queueItem.media_id = id;
				/*queueItem.bitmap= MediaStore.Images.Thumbnails.getThumbnail(
						getContentResolver(), queueItem.media_id,
						MediaStore.Images.Thumbnails.MINI_KIND, null);*/
				Bitmap bit = MediaStore.Images.Thumbnails.getThumbnail(
						getContentResolver(), queueItem.media_id,
						MediaStore.Images.Thumbnails.MINI_KIND, null);
				Matrix matrix = new Matrix();
				matrix.postRotate(90);
				queueItem.bitmap=  Bitmap.createBitmap(bit , 0, 0, bit .getWidth(), bit .getHeight(), matrix, true);
				 int rotateImage = getCameraPhotoOrientation(SelectOption.this, uri, filename);
				
				switch (value) {
				case 1:
					Catalog_webview.queueItemForAttachment.add(queueItem);
					returnValue = Catalog_webview.queueItemForAttachment.size();
					break;
				case 2:
					Request_webview.queueItemAdapterforRequest.add(queueItem);
					returnValue = Request_webview.queueItemAdapterforRequest
							.size();
					break;
				case 3:
					Approval_webview.queueItemAdapterforApproval.add(queueItem);
					returnValue = Approval_webview.queueItemAdapterforApproval
							.size();
					break;
				case 4:
					Request.queueItemforRequest.add(queueItem);
					returnValue = Request.queueItemforRequest.size();
					break;
				case 5:
					Approval.queueItemforApproval.add(queueItem);
					returnValue = Approval.queueItemforApproval.size();
					break;
					
				case 6:
					search_webview.queueItemForAttachment.add(queueItem);
					returnValue = search_webview.queueItemForAttachment.size();
					break;

				default:
					break;
				}

				adaptersetting_list();
				return filename;

			} else
				return null;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}

	public int getCameraPhotoOrientation(Activity context, Uri imageUri, String imagePath){
	    int rotate = 0;
	    try {
	        context.getContentResolver().notifyChange(imageUri, null);
	        File imageFile = new File(imagePath);

	        ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
	        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

	        switch (orientation) {
	        case ExifInterface.ORIENTATION_ROTATE_270:
	            rotate = 270;
	            break;
	        case ExifInterface.ORIENTATION_ROTATE_180:
	            rotate = 180;
	            break;
	        case ExifInterface.ORIENTATION_ROTATE_90:
	            rotate = 90;
	            break;
	        }

	        Log.i("RotateImage", "Exif orientation: " + orientation);
	        Log.i("RotateImage", "Rotate value: " + rotate);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return rotate;
	}

	public void decodeFile(String filePath) {
		// Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, o);

		// The new size we want to scale to
		final int REQUIRED_SIZE = 1024;

		// Find the correct scale value. It should be the power of 2.
		int width_tmp = o.outWidth, height_tmp = o.outHeight;
		int scale = 1;
		while (true) {
			if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
				break;
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}

		// Decode with inSampleSize
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		bitmap = BitmapFactory.decodeFile(filePath, o2);
		// imgView.setImageBitmap(bitmap);
	}
}
