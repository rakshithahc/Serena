package com.serana;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import com.serena.connection.Network_Available;

import android.R.anim;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.provider.MediaStore;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint({ "SetJavaScriptEnabled", "WorldWriteableFiles",
		"WorldReadableFiles", "ResourceAsColor" })
public class Catalog_webview extends Activity {

	public static boolean isfirstEntry;
	WebView wb;
	RelativeLayout actionBar;
	public static int disDialog = 0;
	CustomDialog dialog;
	private TextView head;
	private ImageView logo;
	public static ArrayList<QueueItem> queueItemForAttachment;
	Bitmap bitmap;
	Button attachment;
	private Context catWebCtx;
	private SharedPreferences user_details;
	String[] selectedImageLength;
	private String prefname = "SERENA";
	String TAG = "Catalog_weview";
	boolean isxLarge = false;
	private static final int PICK_IMAGE = 1;
	String final_req_url;
	Calendar cal;
	ImageView selectedImage1, selectedImage2, selectedImage3;
	boolean click_flag = false;

	@Override
	protected void onStart() {
		super.onStart();
		Log.i(TAG, "onStart called and Checking configuration");

		Configuration myConfig = new Configuration();
		myConfig.setToDefaults();
		myConfig = getResources().getConfiguration();

		switch (myConfig.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) {
		case Configuration.SCREENLAYOUT_SIZE_XLARGE:
			Log.v(TAG, "Screen size is extra large");
			isxLarge = true;
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			break;

		case Configuration.SCREENLAYOUT_SIZE_LARGE:
			Log.v(TAG, "Screen size is large");
			break;

		case Configuration.SCREENLAYOUT_SIZE_NORMAL:
			Log.v(TAG, "Screen size is normal");
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			break;

		case Configuration.SCREENLAYOUT_SIZE_SMALL:
			Log.v(TAG, "Screen size is small");
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			break;
		case Configuration.SCREENLAYOUT_SIZE_UNDEFINED:
			Log.v(TAG, "Screen size is undefined");
			break;
		}
	}

	@SuppressLint("NewApi")
	@Override
	protected void onResume() {
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
			// checks network availability and session expire stuff
			if (!Network_Available.hasConnection(catWebCtx) && sec > 60) {
				Intent m = new Intent(Catalog_webview.this,
						MainSerenaActivity.class);
				m.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(m);
				finish();
			} else if (Network_Available.hasConnection(catWebCtx) && sec > 60) {
				Intent m = new Intent(Catalog_webview.this, SplashScreen.class);
				m.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(m);
				finish();

			} else if (!Network_Available.hasConnection(catWebCtx) && sec < 60) {
				if (disDialog == 0) {
					disDialog = 5;
					makediallog("No network connection",
							"You must be connected to the internet to use this app");
				}
			} else if (Network_Available.hasConnection(catWebCtx) && sec < 60) {
				// don't do any thing let user stays in same screen with the
				// data loaded previously
			}

		}
		isfirstEntry = true;
	}

	@Override
	public void onBackPressed() {

	}

	@Override
	protected void onStop() {
		super.onStop();
		// Calendar cal = Calendar.getInstance();
		// int searchtime= cal.get(Calendar.HOUR) * 60 * 60 +
		// cal.get(Calendar.MINUTE) * 60+ cal.get(Calendar.SECOND) * 1000;
		long millisecond = System.currentTimeMillis();
		MyApplication.setlastTime(millisecond);
		// the following for when app goes to background and is killed by user
		// then to maintain auto re-log in
		long movedBackground = millisecond;
		user_details = getSharedPreferences(prefname, MODE_WORLD_WRITEABLE);
		SharedPreferences.Editor editor = user_details.edit();
		// Log.v("the details",Uname+Pass+FirstloggedTime+Url);
		editor.putString("name", MyApplication.getUserID());
		editor.putString("password", MyApplication.getPassWord());
		// editor.putLong("lasttime",FirstloggedTime);
		editor.putString("url", MyApplication.getServer());
		editor.putLong("backgroundtime", movedBackground);
		editor.commit();
		wb.clearCache(true);

	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.catalog_webview);
		queueItemForAttachment = new ArrayList<QueueItem>();
		encode = new ArrayList<QueueString>();
		isfirstEntry = false;
		cal = Calendar.getInstance();
		catWebCtx = Catalog_webview.this;
		actionBar = (RelativeLayout) findViewById(R.id.webActionBar);
		head = (TextView) findViewById(R.id.headings);
		logo = (ImageView) findViewById(R.id.webImage);
		wb = (WebView) findViewById(R.id.plan_web);

		selectedImage1 = (ImageView) findViewById(R.id.selectImage1);
		selectedImage2 = (ImageView) findViewById(R.id.selectImage2);
		selectedImage3 = (ImageView) findViewById(R.id.selectImage3);

		String colour = getIntent().getStringExtra("actColour");
		actionBar.setBackgroundColor(Color.parseColor(colour));
		String headingweb = getIntent().getStringExtra("heading");
		head.setText(headingweb);

		Bitmap bitmap = (Bitmap) this.getIntent().getParcelableExtra("image");
		logo.setImageBitmap(bitmap);

		WebSettings webSettings = wb.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setDomStorageEnabled(true);
		webSettings.setPluginsEnabled(true);

		if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.HONEYCOMB) {
			wb.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}

		attachment = (Button) findViewById(R.id.attachment);

		final_req_url = getIntent().getStringExtra("finurl");
		Log.v("Catalog URL NEW TIME", final_req_url);
		wb.setWebViewClient(new WebViewClient() {

			@Override
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				handler.proceed();
				// super.onReceivedSslError(view, handler, error);
			}

			private int webViewPreviousState;
			private final int PAGE_STARTED = 0x1;
			private final int PAGE_REDIRECTED = 0x2;

			@Override
			public boolean shouldOverrideUrlLoading(WebView view,
					String urlNewString) {
				webViewPreviousState = PAGE_REDIRECTED;
				Log.v("THE WEB DATA", urlNewString);
				if (urlNewString
						.equalsIgnoreCase("closeWebView://form=catalog")) {

					Catalog_webview.queueItemForAttachment.clear();
					// onBackPressed();
					overridePendingTransition(R.anim.push_left_in,
							R.anim.push_left_out);
					finish();
				}

				if (urlNewString.contains("xcloseWebView")) {
					// get data for the images create a hash map
					String[] url = urlNewString.split("://");
					attachdataonserver(url[1]);
					try {
						Catalog_webview.queueItemForAttachment.clear();
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					overridePendingTransition(R.anim.push_left_in,
							R.anim.push_left_out);
					finish();
				}
				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				webViewPreviousState = PAGE_STARTED;
				if (CustomDialog.notShowing())
					CustomDialog.showProgressDialog(catWebCtx, "", false);
				new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface arg0) {
					}
				};
			}

			@Override
			public void onPageFinished(WebView view, String url) {

				if (webViewPreviousState == PAGE_STARTED) {
					CustomDialog.removeDialog();
					attachment.setEnabled(true);
				}
			}
		});

		wb.setWebChromeClient(new WebChromeClient() {

			@Override
			public boolean onJsAlert(WebView view, String url, String message,
					final JsResult result) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(Catalog_webview.this)
						.setTitle("Alert")
						.setMessage(message)
						.setPositiveButton(android.R.string.ok,
								new AlertDialog.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										result.confirm();
									}
								}).setCancelable(false).create().show();

				return true;
			}

			@Override
			public boolean onJsConfirm(WebView view, String url,
					String message, final JsResult result) {
				new AlertDialog.Builder(Catalog_webview.this)
						.setTitle("javaScript dialog")
						.setMessage(message)
						.setPositiveButton(android.R.string.ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										result.confirm();
									}
								})
						.setNegativeButton(android.R.string.cancel,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										result.cancel();
									}
								}).create().show();
				return true;
			};

			@Override
			public void onReceivedTitle(WebView view, String title) {
				setTitle(title);
				super.onReceivedTitle(view, title);
			}

		});
		wb.loadUrl(final_req_url);
		attachmentthings();
	}

	protected void attachdataonserver(String urlNewString) {
		// TODO Auto-generated method stub
		switch (index) {
		case 1:
			dotheattachmentpart(1, urlNewString);
			break;
		case 2:
			dotheattachmentpart(2, urlNewString);
			break;
		case 3:
			dotheattachmentpart(3, urlNewString);
			break;

		default:
			// dotheattachmentpart(0, urlNewString);
			break;
		}
	}

	private void dotheattachmentpart(int value, String urlNewString) {
		// TODO Auto-generated method stub

		for (int i = 0; i < value; i++) {
			System.out.println("attachment" + i);
			JSONObject object = new JSONObject();
			try {
				object.put("filename", "Attachment");
				object.put("displayname", "Attachment");
				object.put("showAsImage", true);
				object.put("isUnrestricted", false);
				object.put("content", encode.get(i).path);
				// doin background thread for request
				int j = DBAdpter.sendRequestforAttachment(object, urlNewString,
						MyApplication.getToken());
				if (j == 5) {
					Log.v("Results", "Successfully Uploaded");
				} else if (j == 1) {
					Log.v("Results", "Error");
					makediallog("Error", "CannotUpload the file");
				} else {
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		encode.clear();
		queueItemForAttachment.clear();
		index = 0;
	}

	private void attachmentthings() {
		// TODO Auto-generated method stub

		attachment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.v("Catalog Webview", "attachment button clicked");
				Intent intent = new Intent(Catalog_webview.this,
						SelectOption.class);
				intent.putExtra("value", 1);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
						| Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
				startActivityForResult(intent, PICK_IMAGE);
			}
		});
	}

	public void makediallog(String titlte, String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(catWebCtx);
		builder.setTitle(titlte);
		builder.setMessage(msg);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (disDialog == 5) {
					disDialog = 0;
				}
				dialog.cancel();
			}
		});

		AlertDialog dialog = builder.create();
		dialog.show();
	}

	String selectedImages;
	int index = 0;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case PICK_IMAGE:
			if (resultCode == Activity.RESULT_OK) {
				try {
					System.out
							.println("activity result called for catalog webview class");
					// new accesstheimagesfromUri().execute();
					System.out.println("Catalog list size is"
							+ queueItemForAttachment.size());
					index = queueItemForAttachment.size();
					if (index > 0) {
						callbitmapmethod();
					} else {
						selectedImage1.setImageDrawable(null);
						selectedImage2.setImageDrawable(null);
						selectedImage3.setImageDrawable(null);

					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
			break;
		}
	}

	Bitmap[] imagebitmap = null;
	ArrayList<QueueString> encode;

	private void callbitmapmethod() {
		// TODO Auto-generated method stub
		try {
			encode.clear();
			for (int i = 0; i < queueItemForAttachment.size(); i++) {
				/**/
				QueueString en = new QueueString();
				if (queueItemForAttachment.get(i).bitmap != null) {
					System.out.println("bitmap is:"
							+ queueItemForAttachment.get(i).bitmap);
					en.path = convertintobase64(queueItemForAttachment.get(i).bitmap);
					System.out.println("path is :" + en.path);
					Bitmap bitmap = decodeBase64(en.path, i);
					switch (i) {
					case 0:
						selectedImage1.setImageBitmap(bitmap);
						selectedImage2.setImageDrawable(null);
						selectedImage3.setImageDrawable(null);
						break;
					case 1:
						selectedImage2.setImageBitmap(bitmap);
						selectedImage3.setImageDrawable(null);
						break;
					case 2:
						selectedImage3.setImageBitmap(bitmap);
						break;

					default:
						selectedImage1.setImageDrawable(null);
						selectedImage2.setImageDrawable(null);
						selectedImage3.setImageDrawable(null);

						break;
					}
				} else {
					System.out.println("bitmap is null");
				}
				encode.add(en);
			}

			try {
				if (isxLarge) {
				//	setbitmaponImageView();
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	private String convertintobase64(Bitmap bitmap2) {
		// TODO Auto-generated method stub
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap2.compress(Bitmap.CompressFormat.PNG, 100, baos);
		byte[] b = baos.toByteArray();
		String temp = Base64.encodeToString(b, Base64.DEFAULT | Base64.NO_WRAP);
		System.out.println("encoded string is:" + temp);

		Log.e("LOOK", temp);
		// temp="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";
		return temp;
	}

	public static Bitmap decodeBase64(String input, int i) {
		byte[] decodedByte = Base64.decode(input, 0);
		return BitmapFactory
				.decodeByteArray(decodedByte, 0, decodedByte.length);

	}

	private void setbitmaponImageView() {
		// TODO Auto-generated method stub
		try {
			if (queueItemForAttachment.get(0).bitmap != null) {
				selectedImage1
						.setImageBitmap(queueItemForAttachment.get(0).bitmap);
			}
			if (queueItemForAttachment.get(1).bitmap != null) {
				selectedImage2
						.setImageBitmap(queueItemForAttachment.get(1).bitmap);
			}
			if (queueItemForAttachment.get(2).bitmap != null) {
				selectedImage3
						.setImageBitmap(queueItemForAttachment.get(2).bitmap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		if (cursor != null) {
			// HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
			// THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} else
			return null;
	}

}
